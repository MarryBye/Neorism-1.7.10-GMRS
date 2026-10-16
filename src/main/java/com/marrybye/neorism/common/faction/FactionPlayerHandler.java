package com.marrybye.neorism.common.faction;

import com.marrybye.neorism.api.faction.IFactionPlayer;
import com.marrybye.neorism.api.faction.IPlayableFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.network.packets.SyncFactionPacket;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Extended entity property managing player faction membership, levels, lord ranks, and disguise state.
 */
public class FactionPlayerHandler implements IExtendedEntityProperties, IFactionPlayer {

    public static final String PROPERTY_ID = "neorism:faction_player";

    private final EntityPlayer player;
    private IPlayableFaction currentFaction = ModFactions.NEUTRAL;
    private int currentLevel = 0;
    private int lordLevel = 0;
    private boolean disguised = false;

    public FactionPlayerHandler(EntityPlayer player) {
        this.player = player;
    }

    public static FactionPlayerHandler get(EntityPlayer player) {
        if (player == null) return null;
        return (FactionPlayerHandler) player.getExtendedProperties(PROPERTY_ID);
    }

    public static void register(EntityPlayer player) {
        if (player.getExtendedProperties(PROPERTY_ID) == null) {
            player.registerExtendedProperties(PROPERTY_ID, new FactionPlayerHandler(player));
        }
    }

    @Override
    public void init(Entity entity, World world) {
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("faction", currentFaction != null ? currentFaction.getId() : "neutral");
        tag.setInteger("level", currentLevel);
        tag.setInteger("lordLevel", lordLevel);
        tag.setBoolean("disguised", disguised);
        compound.setTag("NeorismFaction", tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey("NeorismFaction")) {
            NBTTagCompound tag = compound.getCompoundTag("NeorismFaction");
            this.currentFaction = ModFactions.getFaction(tag.getString("faction"));
            this.currentLevel = tag.getInteger("level");
            this.lordLevel = tag.getInteger("lordLevel");
            this.disguised = tag.getBoolean("disguised");
        }
    }

    public void copyFrom(FactionPlayerHandler oldHandler) {
        if (oldHandler != null) {
            this.currentFaction = oldHandler.currentFaction;
            this.currentLevel = oldHandler.currentLevel;
            this.lordLevel = oldHandler.lordLevel;
            this.disguised = oldHandler.disguised;
        }
    }

    public IPlayableFaction getFaction() {
        return currentFaction;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getLordLevel() {
        return lordLevel;
    }

    public boolean isDisguised() {
        return disguised;
    }

    public boolean isVampire() {
        return ModFactions.VAMPIRE.equals(currentFaction);
    }

    public boolean isHunter() {
        return ModFactions.HUNTER.equals(currentFaction);
    }

    public boolean canJoin(IPlayableFaction faction) {
        if (faction == null || faction.isNeutral()) return true;
        // Players can only join a faction if currently neutral
        return currentFaction.isNeutral();
    }

    public boolean canLeaveFaction() {
        return !currentFaction.isNeutral();
    }

    public void setFaction(IPlayableFaction faction, int level) {
        this.currentFaction = faction != null ? faction : ModFactions.NEUTRAL;
        this.currentLevel = level;
        if (this.currentFaction.isNeutral()) {
            this.lordLevel = 0;
            this.disguised = false;
        }
        ModLogger.leveling("Player %s faction changed to %s (level %d)",
            player.getCommandSenderName(), this.currentFaction.getId(), this.currentLevel);
        sync();
    }

    public void setLevel(int level) {
        int max = currentFaction.getMaxLevel();
        this.currentLevel = Math.max(0, Math.min(level, max));
        ModLogger.leveling("Player %s level updated to %d (faction: %s)",
            player.getCommandSenderName(), this.currentLevel, this.currentFaction.getId());
        sync();
    }

    public void setLordLevel(int lordLevel) {
        int max = currentFaction.getMaxLordLevel();
        this.lordLevel = Math.max(0, Math.min(lordLevel, max));
        ModLogger.leveling("Player %s lord rank updated to %d (faction: %s)",
            player.getCommandSenderName(), this.lordLevel, this.currentFaction.getId());
        sync();
    }

    public void setDisguised(boolean disguised) {
        this.disguised = disguised;
        ModLogger.debug("Player %s disguise state: %b", player.getCommandSenderName(), disguised);
        sync();
    }

    public void setClientData(IPlayableFaction faction, int level, int lordLevel, boolean disguised) {
        this.currentFaction = faction;
        this.currentLevel = level;
        this.lordLevel = lordLevel;
        this.disguised = disguised;
    }

    public void sync() {
        if (!player.worldObj.isRemote && player instanceof EntityPlayerMP) {
            SyncFactionPacket packet = new SyncFactionPacket(
                player.getEntityId(),
                currentFaction.getId(),
                currentLevel,
                lordLevel,
                disguised
            );
            // Send to the player
            PacketHandler.sendTo(packet, (EntityPlayerMP) player);
            // Send to tracking players nearby
            PacketHandler.sendToAllAround(packet, new NetworkRegistry.TargetPoint(
                player.dimension, player.posX, player.posY, player.posZ, 64.0D
            ));
        }
    }

    @Override
    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public EntityLivingBase asEntity() {
        return player;
    }

    @Override
    public int getLevel() {
        return currentLevel;
    }

    @Override
    public int getMaxLevel() {
        return currentFaction.getMaxLevel();
    }

    @Override
    public void leaveFaction() {
        setFaction(ModFactions.NEUTRAL, 0);
    }

    @Override
    public String getLevelDisplay() {
        return StatCollector.translateToLocal(currentFaction.getUnlocalizedName()) + " " + currentLevel;
    }
}
