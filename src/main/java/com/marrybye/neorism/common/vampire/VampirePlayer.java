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
    public static final int SAFE_SUN_TICKS = 100;

    private final EntityPlayer player;
    private final BloodStats bloodStats;
    private int feed_victim = -1;
    private int feedBiteTickCounter = 0;
    private int ticksInSun = 0;

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
        tag.setInteger("ticksInSun", ticksInSun);
        compound.setTag("NeorismVampirePlayer", tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey("NeorismVampirePlayer")) {
            NBTTagCompound tag = compound.getCompoundTag("NeorismVampirePlayer");
            bloodStats.readFromNBT(tag);
            this.ticksInSun = tag.getInteger("ticksInSun");
            this.feed_victim = -1;
        }
    }

    public void copyFrom(VampirePlayer old) {
        if (old != null) {
            this.bloodStats.copyFrom(old.bloodStats);
            this.ticksInSun = old.ticksInSun;
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
        return SundamageRegistry.instance().isGettingSundamage(player);
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
        LevelAttributeModifier.applyVampireModifiers(player, newLevel, VampireLeveling.MAX_LEVEL);
        ModLogger.leveling("Applied vampire level %d attribute modifiers to %s (maxBlood: %d)",
            newLevel, player.getCommandSenderName(), maxBlood);
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
        handleSunDamage();
        handleGarlic();

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

    private void handleSunDamage() {
        boolean inSun = SundamageRegistry.instance().isGettingSundamage(player);
        if (inSun) {
            if (ticksInSun < SAFE_SUN_TICKS) {
                ticksInSun++;
            }
            if (player.isPotionActive(ModPotions.sunscreen)) {
                if (ticksInSun > 50) {
                    ticksInSun = 50;
                }
            }
            if (!player.worldObj.isRemote) {
                int level = Helper.getLevel(player);
                if (level >= 1 && ticksInSun > 50 && !player.isPotionActive(ModPotions.sunscreen)) {
                    if (player.ticksExisted % 300 == 1) {
                        player.addPotionEffect(new PotionEffect(Potion.confusion.id, 180));
                    }
                    if (player.ticksExisted % 150 == 3) {
                        player.addPotionEffect(new PotionEffect(Potion.weakness.id, 152, 0));
                    }
                }
                if (level >= 1 && ticksInSun >= SAFE_SUN_TICKS) {
                    if (player.ticksExisted % 40 == 5) {
                        float baseDmg = 3.0F + (level * 0.5F);
                        player.attackEntityFrom(com.marrybye.neorism.common.core.ModDamageSource.SUN_DAMAGE, baseDmg);
                        player.setFire(3);
                        ModSounds.playAtEntity(player, ModSounds.SCREECH, 0.6F, 1.2F);
                        ModLogger.combat("Player %s took %.1f sun damage (level: %d, ticksInSun: %d)",
                            player.getCommandSenderName(), baseDmg, level, ticksInSun);
                    }
                }
            }
        } else {
            if (ticksInSun > 0) {
                ticksInSun--;
            }
        }
    }

    private void handleGarlic() {
        if (player.worldObj.isRemote) return;
        com.marrybye.neorism.common.world.garlic.EnumStrength strength =
            com.marrybye.neorism.common.world.garlic.GarlicHandler.instance().getGarlicStrength(
                player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ
            );
        if (strength != com.marrybye.neorism.common.world.garlic.EnumStrength.NONE) {
            endFeeding();
            int amp = Math.max(0, strength.getStrength() - 1);
            player.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 60, amp));
            if (strength == com.marrybye.neorism.common.world.garlic.EnumStrength.STRONG && player.ticksExisted % 40 == 0) {
                player.attackEntityFrom(com.marrybye.neorism.common.core.ModDamageSource.GARLIC, 2.0F);
                ModSounds.playAtEntity(player, ModSounds.SCREECH, 0.5F, 1.1F);
                ModLogger.combat("Player %s taking garlic damage from strong garlic zone", player.getCommandSenderName());
            }
        }
        if (com.marrybye.neorism.common.world.garlic.GarlicHandler.instance().isHoldingGarlic(player) && player.ticksExisted % 20 == 0) {
            player.attackEntityFrom(com.marrybye.neorism.common.core.ModDamageSource.GARLIC, 1.0F);
            ModSounds.playAtEntity(player, ModSounds.VAMPIRE_BITE, 0.5F, 1.5F);
            ModLogger.combat("Player %s burned by holding garlic item", player.getCommandSenderName());
        }
    }

    public void onSanguinareFinished() {
        if (player.worldObj.isRemote) return;
        com.marrybye.neorism.common.faction.FactionPlayerHandler fHandler =
            com.marrybye.neorism.common.faction.FactionPlayerHandler.get(player);
        if (fHandler != null && fHandler.canJoin(ModFactions.VAMPIRE)) {
            fHandler.setFaction(ModFactions.VAMPIRE, 1);
            player.removePotionEffect(ModPotions.sanguinare.id);
            ModSounds.playAtEntity(player, ModSounds.SCREECH, 1.0F, 1.0F);
            player.addChatMessage(new net.minecraft.util.ChatComponentTranslation("chat.neorism.became_vampire"));
            player.triggerAchievement(com.marrybye.neorism.common.achievement.ModAchievements.becomeVampire);
            ModLogger.leveling("Player %s transformed into Vampire Level 1 through Sanguinare Vampiris",
                player.getCommandSenderName());
        }
    }

    public int getTicksInSun() {
        return ticksInSun;
    }

    public void setTicksInSun(int ticksInSun) {
        this.ticksInSun = ticksInSun;
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
