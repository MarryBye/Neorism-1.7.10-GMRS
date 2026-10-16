package com.marrybye.neorism.common.vampire;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.entity.ExtendedCreature;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.network.packets.SyncBloodPacket;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Extended entity property managing vampire blood stats, bite feeding mechanics, and level progression.
 */
public class VampirePlayer implements IExtendedEntityProperties, IVampireEntity {

    public static final String PROPERTY_ID = "neorism:vampire_player";
    public static final int FEED_TIMER = 20;

    private final EntityPlayer player;
    private final BloodStats bloodStats;
    private int feed_victim = -1;
    private int feedBiteTickCounter = 0;

    public VampirePlayer(EntityPlayer player) {
        this.player = player;
        this.bloodStats = new BloodStats(player);
    }

    public static VampirePlayer get(EntityPlayer player) {
        if (player == null) return null;
        return (VampirePlayer) player.getExtendedProperties(PROPERTY_ID);
    }

    public static void register(EntityPlayer player) {
        if (player != null && player.getExtendedProperties(PROPERTY_ID) == null) {
            player.registerExtendedProperties(PROPERTY_ID, new VampirePlayer(player));
        }
    }

    @Override
    public void init(Entity entity, World world) {
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        bloodStats.writeToNBT(tag);
        tag.setInteger("feedVictim", feed_victim);
        compound.setTag("NeorismVampirePlayer", tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey("NeorismVampirePlayer")) {
            NBTTagCompound tag = compound.getCompoundTag("NeorismVampirePlayer");
            bloodStats.readFromNBT(tag);
            this.feed_victim = -1;
        }
    }

    public void copyFrom(VampirePlayer old) {
        if (old != null) {
            this.bloodStats.copyFrom(old.bloodStats);
        }
    }

    public BloodStats getBloodStats() {
        return bloodStats;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public IFaction getFaction() {
        return ModFactions.VAMPIRE;
    }

    @Override
    public EntityLivingBase asEntity() {
        return player;
    }

    @Override
    public boolean isGettingSundamage() {
        if (player.worldObj == null || !Helper.isVampire(player)) return false;
        return Helper.canSunBurn(player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
    }

    @Override
    public boolean isIgnoringSundamage() {
        return player.isPotionActive(ModPotions.sunscreen);
    }

    @Override
    public boolean wantsBlood() {
        return bloodStats.needsBlood();
    }

    public void drinkBlood(int amount, float saturationMod) {
        bloodStats.addBlood(amount, saturationMod);
        syncBlood();
    }

    public void onLevelChanged(int newLevel) {
        int maxBlood = 20;
        if (newLevel > 13) {
            maxBlood = 40;
        } else if (newLevel > 9) {
            maxBlood = 34;
        } else if (newLevel > 6) {
            maxBlood = 30;
        } else if (newLevel > 3) {
            maxBlood = 26;
        }
        bloodStats.setMaxBlood(maxBlood);
        syncBlood();
    }

    public boolean startFeeding(EntityLivingBase target) {
        if (!Helper.isVampire(player) || target == null || !target.isEntityAlive()) {
            return false;
        }
        if (player.getDistanceToEntity(target) > 3.5D) {
            return false;
        }

        ExtendedCreature creature = ExtendedCreature.get(target);
        if (creature == null || !creature.canBeBitten(this)) {
            return false;
        }

        this.feed_victim = target.getEntityId();
        this.feedBiteTickCounter = 0;

        // Apply pinning slowness effect to both entities
        target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 25, 6, false));
        player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 25, 3, false));

        ModSounds.playAtEntity(player, ModSounds.VAMPIRE_BITE, 0.8F, 1.0F);
        ModLogger.combat("Player %s started continuous feeding on entity %s (id: %d)",
            player.getCommandSenderName(), target.getCommandSenderName(), target.getEntityId());

        return true;
    }

    public void updateFeeding() {
        if (feed_victim == -1) return;

        Entity targetEntity = player.worldObj.getEntityByID(feed_victim);
        if (!(targetEntity instanceof EntityLivingBase) || !targetEntity.isEntityAlive()) {
            endFeeding();
            return;
        }

        EntityLivingBase target = (EntityLivingBase) targetEntity;
        if (player.getDistanceToEntity(target) > 3.5D) {
            endFeeding();
            return;
        }

        // Reapply slowness hold
        target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 25, 6, false));
        player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 25, 3, false));

        ExtendedCreature creature = ExtendedCreature.get(target);
        if (creature != null && creature.canBeBitten(this)) {
            int drained = creature.onBite(this);
            if (drained > 0) {
                drinkBlood(drained, creature.getBloodSaturation());
                ModSounds.playAtEntity(player, ModSounds.VAMPIRE_BITE, 0.7F, 1.0F);
                ModSounds.playAtEntity(player, ModSounds.BLOOD_DRIP, 0.5F, 1.0F);
            } else {
                endFeeding();
            }
        } else {
            endFeeding();
        }
    }

    public void endFeeding() {
        if (feed_victim != -1) {
            ModLogger.combat("Player %s ended feeding", player.getCommandSenderName());
            feed_victim = -1;
            feedBiteTickCounter = 0;
            if (player.isPotionActive(Potion.moveSlowdown)) {
                player.removePotionEffect(Potion.moveSlowdown.id);
            }
        }
    }

    public void onUpdate() {
        if (!Helper.isVampire(player)) return;

        bloodStats.onUpdate();

        if (!player.worldObj.isRemote) {
            if (feed_victim != -1) {
                if (++feedBiteTickCounter >= FEED_TIMER) {
                    updateFeeding();
                    feedBiteTickCounter = 0;
                }
            }

            if (bloodStats.isChanged()) {
                syncBlood();
                bloodStats.setChanged(false);
            }
        } else {
            // Client side low-blood heartbeat sound (below 20% blood)
            if (bloodStats.getBloodLevel() > 0 && bloodStats.getBloodLevel() <= bloodStats.getMaxBlood() * 0.2F) {
                if (player.ticksExisted % 40 == 0) {
                    ModSounds.playAtEntity(player, ModSounds.HEARTBEAT, 0.8F, 1.0F);
                }
            }
        }
    }

    public void syncBlood() {
        if (!player.worldObj.isRemote && player instanceof EntityPlayerMP) {
            SyncBloodPacket packet = new SyncBloodPacket(
                player.getEntityId(),
                bloodStats.getBloodLevel(),
                bloodStats.getMaxBlood(),
                bloodStats.getBloodSaturation(),
                bloodStats.getBloodExhaustion()
            );
            PacketHandler.sendTo(packet, (EntityPlayerMP) player);
        }
    }
}
