package com.marrybye.neorism.common.entity;

import com.marrybye.neorism.api.entity.IBiteableEntity;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Extended entity property tracking blood capacity, current blood, and infection state for biteable creatures.
 */
public class ExtendedCreature implements IExtendedEntityProperties, IBiteableEntity {

    public static final String PROPERTY_ID = "neorism:extended_creature";

    private final EntityLivingBase entity;
    private int maxBlood;
    private int blood;
    private int poisonousBlood = 0;
    private boolean canBecomeVampire = false;

    public ExtendedCreature(EntityLivingBase entity) {
        this.entity = entity;
        this.maxBlood = calculateMaxBlood(entity);
        this.blood = this.maxBlood;
        this.canBecomeVampire = entity instanceof EntityVillager;
    }

    public static ExtendedCreature get(Entity entity) {
        if (entity == null) return null;
        return (ExtendedCreature) entity.getExtendedProperties(PROPERTY_ID);
    }

    public static void register(EntityLivingBase entity) {
        if (entity != null && entity.getExtendedProperties(PROPERTY_ID) == null) {
            entity.registerExtendedProperties(PROPERTY_ID, new ExtendedCreature(entity));
        }
    }

    private static int calculateMaxBlood(EntityLivingBase e) {
        if (e == null || e instanceof IMob) return 0;
        if (e.getMaxHealth() > 50.0F) return 0;

        if (e instanceof EntityVillager) return 15;
        if (e instanceof EntityCow) return 10;
        if (e instanceof EntityMooshroom) return 10;
        if (e instanceof EntityHorse) return 13;
        if (e instanceof EntityPig) return 5;
        if (e instanceof EntitySheep) return 5;
        if (e instanceof EntityWolf) return 5;
        if (e instanceof EntityOcelot) return 4;
        if (e instanceof EntityChicken || e instanceof EntityBat) return 0;

        // General fallback calculation based on bounding box volume
        AxisAlignedBB bb = e.boundingBox;
        if (bb != null) {
            double v = (bb.maxX - bb.minX) * (bb.maxY - bb.minY) * (bb.maxZ - bb.minZ);
            if (e instanceof EntityAgeable && ((EntityAgeable) e).isChild()) {
                v *= 8.0D;
            }
            if (v >= 0.3D) {
                int calc = (int) (v * 7.0D);
                return Math.min(15, Math.max(1, calc));
            }
        }

        return 0;
    }

    @Override
    public void init(Entity entity, World world) {
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("blood", blood);
        tag.setInteger("maxBlood", maxBlood);
        tag.setInteger("poisonousBlood", poisonousBlood);
        compound.setTag("NeorismCreature", tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey("NeorismCreature")) {
            NBTTagCompound tag = compound.getCompoundTag("NeorismCreature");
            this.blood = tag.getInteger("blood");
            this.maxBlood = tag.getInteger("maxBlood");
            this.poisonousBlood = tag.getInteger("poisonousBlood");
        }
    }

    @Override
    public boolean canBeBitten(IVampireEntity biter) {
        return blood > 0 && maxBlood > 0;
    }

    @Override
    public float getBloodLevelRelative() {
        return maxBlood > 0 ? (float) blood / (float) maxBlood : 0.0F;
    }

    @Override
    public float getBloodSaturation() {
        return entity instanceof EntityVillager ? 1.0F : 0.7F;
    }

    @Override
    public int onBite(IVampireEntity biter) {
        if (blood <= 0 || maxBlood <= 0) return 0;

        int amt = Math.max(1, maxBlood / 6);
        if (amt >= blood) {
            amt = blood;
        }
        blood -= amt;

        if (entity instanceof EntityCreature && biter != null) {
            ((EntityCreature) entity).setTarget(biter.asEntity());
        }

        // Child creatures provide reduced blood
        if (entity instanceof EntityAgeable && ((EntityAgeable) entity).isChild()) {
            amt = Math.max(1, Math.round(amt / 3.0F));
        }

        ModLogger.combat("Vampire %s bit creature %s for %d blood (remaining: %d/%d)",
            biter != null ? biter.asEntity().getCommandSenderName() : "unknown",
            entity.getCommandSenderName(), amt, blood, maxBlood);

        // Creature starved of blood dies
        if (blood <= 0) {
            ModLogger.combat("Creature %s died from complete blood loss", entity.getCommandSenderName());
            entity.attackEntityFrom(ModDamageSource.NO_BLOOD, 1000.0F);
        } else if (entity instanceof EntityVillager) {
            // 20% chance to contract Sanguinare Vampiris
            if (entity.worldObj.rand.nextInt(5) == 0 && biter != null) {
                tryInfect(biter);
            }
        }

        return amt;
    }

    @Override
    public boolean canDrain(int amount) {
        int avail = blood;
        if (entity instanceof EntityAgeable && ((EntityAgeable) entity).isChild()) {
            avail /= 3;
        }
        return avail > amount;
    }

    @Override
    public int onSyringeUse(int amount) {
        if (!canDrain(amount)) return 0;
        boolean child = entity instanceof EntityAgeable && ((EntityAgeable) entity).isChild();
        blood -= amount / (child ? 3 : 1);
        return amount;
    }

    @Override
    public boolean canBeInfected(IVampireEntity vampire) {
        return canBecomeVampire && poisonousBlood <= 0 && !entity.isPotionActive(ModPotions.sanguinare);
    }

    @Override
    public boolean tryInfect(IVampireEntity vampire) {
        if (canBeInfected(vampire)) {
            entity.addPotionEffect(new PotionEffect(ModPotions.sanguinare.id, 2400, 0));
            ModLogger.combat("Villager %s contracted Sanguinare Vampiris from vampire bite",
                entity.getCommandSenderName());
            return true;
        }
        return false;
    }

    @Override
    public EntityLivingBase asEntity() {
        return entity;
    }

    public void updateLiving() {
        // Natural blood replenishment over time (1 in 20 chance every 40 ticks = ~2 seconds)
        if (blood < maxBlood && entity.ticksExisted % 40 == 0) {
            if (entity.worldObj.rand.nextInt(20) == 0) {
                blood++;
            }
        }
    }

    public int getBlood() {
        return blood;
    }

    public int getMaxBlood() {
        return maxBlood;
    }
}
