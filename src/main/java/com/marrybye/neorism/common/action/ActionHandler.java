package com.marrybye.neorism.common.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.marrybye.neorism.api.action.IAction;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.network.packets.SyncActionsPacket;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Extended entity property tracking active action timers, cooldowns, and network synchronization.
 */
public class ActionHandler implements IExtendedEntityProperties {

    public static final String PROPERTY_ID = "neorism:action_handler";

    private final EntityPlayer player;
    private final Map<String, Integer> cooldowns = new HashMap<String, Integer>();
    private final Map<String, Integer> activeActions = new HashMap<String, Integer>();
    private boolean dirty = false;

    public ActionHandler(EntityPlayer player) {
        this.player = player;
    }

    public static ActionHandler get(EntityPlayer player) {
        if (player == null) return null;
        return (ActionHandler) player.getExtendedProperties(PROPERTY_ID);
    }

    public static void register(EntityPlayer player) {
        if (player != null && player.getExtendedProperties(PROPERTY_ID) == null) {
            player.registerExtendedProperties(PROPERTY_ID, new ActionHandler(player));
        }
    }

    @Override
    public void init(Entity entity, World world) {
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound cdTag = new NBTTagCompound();
        for (Map.Entry<String, Integer> entry : cooldowns.entrySet()) {
            cdTag.setInteger(entry.getKey(), entry.getValue());
        }
        tag.setTag("cooldowns", cdTag);
        compound.setTag("NeorismActions", tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey("NeorismActions")) {
            NBTTagCompound tag = compound.getCompoundTag("NeorismActions");
            NBTTagCompound cdTag = tag.getCompoundTag("cooldowns");
            this.cooldowns.clear();
            for (Object keyObj : cdTag.func_150296_c()) {
                String key = (String) keyObj;
                this.cooldowns.put(key, cdTag.getInteger(key));
            }
        }
    }

    public void copyFrom(ActionHandler old) {
        if (old != null) {
            this.cooldowns.clear();
            this.cooldowns.putAll(old.cooldowns);
        }
    }

    public boolean isOnCooldown(IAction action) {
        if (action == null) return false;
        Integer cd = cooldowns.get(action.getId());
        return cd != null && cd > 0;
    }

    public int getCooldownRemaining(IAction action) {
        if (action == null) return 0;
        Integer cd = cooldowns.get(action.getId());
        return cd != null ? cd : 0;
    }

    public boolean isActive(IAction action) {
        if (action == null) return false;
        return activeActions.containsKey(action.getId());
    }

    public int getActiveTicksLeft(IAction action) {
        if (action == null) return 0;
        Integer ticks = activeActions.get(action.getId());
        return ticks != null ? ticks : 0;
    }

    public boolean executeAction(String actionId) {
        IAction action = ActionRegistry.getAction(actionId);
        if (action == null) return false;

        // Lasting/toggle action: if already active, deactivate it (canonical FactionAPI toggle behaviour)
        if (isActive(action)) {
            deactivateAction(action);
            return true;
        }

        if (!action.canUse(player)) {
            return false;
        }

        // Consume blood cost for vampires
        int bloodCost = action.getBloodCost(player);
        if (bloodCost > 0 && Helper.isVampire(player)) {
            VampirePlayer vp = VampirePlayer.get(player);
            if (vp != null) {
                vp.getBloodStats().consumeBlood(bloodCost);
            }
        }

        // Activate
        boolean success = action.onActivated(player);
        if (success) {
            int duration = action.getDuration(player);
            int cd = action.getCooldown(player);
            if (duration > 0 || action.isToggle()) {
                // Canonical: lasting actions do not start their cooldown until they deactivate
                activeActions.put(action.getId(), duration > 0 ? duration : Integer.MAX_VALUE - 1);
            } else if (cd > 0) {
                cooldowns.put(action.getId(), cd);
            }
            dirty = true;
            ModLogger.combat("Player %s activated action '%s' (duration: %d, cooldown: %d)",
                player.getCommandSenderName(), action.getId(), duration, cd);
            sync();
        }
        return success;
    }

    public void deactivateAction(IAction action) {
        if (action == null) return;
        // Server authoritative: clients only mirror state through SyncActionsPacket
        if (player.worldObj.isRemote) return;
        Integer removed = activeActions.remove(action.getId());
        if (removed != null) {
            startLastingCooldown(action, removed);
            action.onDeactivated(player);
            dirty = true;
            ModLogger.combat("Player %s deactivated action '%s'", player.getCommandSenderName(), action.getId());
            sync();
        }
    }

    /**
     * Canonical FactionAPI ActionHandler#deactivateAction cooldown formula:
     * cooldown -= cooldown * (leftTime / duration / 2)
     */
    private void startLastingCooldown(IAction action, int leftTime) {
        if (cooldowns.containsKey(action.getId())) return;
        int cooldown = action.getCooldown(player);
        int duration = action.getDuration(player);
        if (duration > 0 && leftTime > 0) {
            cooldown -= (int) (cooldown * (leftTime / (float) duration / 2.0F));
        }
        if (cooldown > 0) {
            cooldowns.put(action.getId(), Math.max(cooldown, 1));
        }
    }

    public void onUpdate() {
        // 1. Tick cooldowns
        if (!cooldowns.isEmpty()) {
            Iterator<Map.Entry<String, Integer>> it = cooldowns.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Integer> entry = it.next();
                int remaining = entry.getValue() - 1;
                if (remaining <= 0) {
                    it.remove();
                    dirty = true;
                } else {
                    entry.setValue(remaining);
                }
            }
        }

        // 2. Tick active actions (snapshot keys: actions may deactivate themselves inside onUpdate)
        if (!activeActions.isEmpty()) {
            for (String key : new java.util.ArrayList<String>(activeActions.keySet())) {
                Integer current = activeActions.get(key);
                if (current == null) continue;
                IAction action = ActionRegistry.getAction(key);
                int ticksLeft = current - 1;
                activeActions.put(key, ticksLeft);

                if (action != null) {
                    action.onUpdate(player, ticksLeft);
                }
                if (!activeActions.containsKey(key)) continue; // deactivated during update

                if (ticksLeft <= 0) {
                    activeActions.remove(key);
                    if (action != null) {
                        startLastingCooldown(action, 0);
                        action.onDeactivated(player);
                    }
                    dirty = true;
                }
            }
        }

        // 3. Periodic synchronization
        if (dirty || (player.ticksExisted % 20 == 0 && (!cooldowns.isEmpty() || !activeActions.isEmpty()))) {
            sync();
            dirty = false;
        }
    }

    /**
     * Deactivates every running lasting action (logout / death), so transient state such as bat flight does not leak.
     */
    public void deactivateAll() {
        if (player.worldObj.isRemote) return;
        for (String key : new java.util.ArrayList<String>(activeActions.keySet())) {
            IAction action = ActionRegistry.getAction(key);
            if (action != null) {
                deactivateAction(action);
            } else {
                activeActions.remove(key);
            }
        }
    }

    /**
     * Canonical FactionAPI ActionHandler#resetTimers():
     * Deactivates all active actions without starting cooldowns, clears all cooldowns and active timers, and syncs.
     */
    public void resetTimers() {
        if (player.worldObj.isRemote) return;
        for (String key : new java.util.ArrayList<String>(activeActions.keySet())) {
            IAction action = ActionRegistry.getAction(key);
            if (action != null) {
                action.onDeactivated(player);
            }
        }
        activeActions.clear();
        cooldowns.clear();
        dirty = false;
        ModLogger.combat("Reset all action timers and cooldowns for %s", player.getCommandSenderName());
        sync();
    }

    public void setClientData(Map<String, Integer> cds, Map<String, Integer> actives) {
        this.cooldowns.clear();
        if (cds != null) this.cooldowns.putAll(cds);

        // Notify deactivations
        for (String oldKey : this.activeActions.keySet()) {
            if (actives == null || !actives.containsKey(oldKey)) {
                IAction action = ActionRegistry.getAction(oldKey);
                if (action != null) {
                    action.onDeactivated(player);
                }
            }
        }

        // Notify activations
        if (actives != null) {
            for (String newKey : actives.keySet()) {
                if (!this.activeActions.containsKey(newKey)) {
                    IAction action = ActionRegistry.getAction(newKey);
                    if (action != null) {
                        action.onActivated(player);
                    }
                }
            }
        }

        this.activeActions.clear();
        if (actives != null) this.activeActions.putAll(actives);
    }

    public Map<String, Integer> getCooldowns() {
        return cooldowns;
    }

    public Map<String, Integer> getActiveActions() {
        return activeActions;
    }

    public void sync() {
        if (!player.worldObj.isRemote && player instanceof EntityPlayerMP) {
            SyncActionsPacket packet = new SyncActionsPacket(player.getEntityId(), cooldowns, activeActions);
            PacketHandler.sendTo(packet, (EntityPlayerMP) player);
            PacketHandler.sendToAllAround(packet, new cpw.mods.fml.common.network.NetworkRegistry.TargetPoint(
                player.dimension, player.posX, player.posY, player.posZ, 64.0D
            ));
        }
    }
}
