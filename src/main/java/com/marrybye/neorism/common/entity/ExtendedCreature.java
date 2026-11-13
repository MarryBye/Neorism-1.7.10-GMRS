package com.marrybye.neorism.common.entity;

import com.marrybye.neorism.api.entity.IBiteableEntity;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.entity.converted.EntityConvertedCow;
import com.marrybye.neorism.common.entity.converted.EntityConvertedHorse;
import com.marrybye.neorism.common.entity.converted.EntityConvertedSheep;
import com.marrybye.neorism.common.entity.converted.EntityConvertedVillager;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
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
    public static final int POISONOUS_BLOOD_DOSE_DURATION = 12000;

    private final EntityLivingBase entity;
    private int maxBlood = -1;
    private int blood = -1;
    private int poisonousBlood = 0;
    private boolean canBecomeVampire = false;
    private boolean initialized = false;

    public ExtendedCreature(EntityLivingBase entity) {
        this.entity = entity;
        this.canBecomeVampire = !(entity instanceof IVampireEntity)
            && (entity instanceof EntityVillager || entity instanceof EntityHorse
                || entity instanceof EntityCow || entity instanceof EntitySheep);
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

    private void ensureInitialized() {
        if (!initialized) {
            initialized = true;
            if (this.maxBlood < 0) {
                this.maxBlood = calculateMaxBlood(this.entity);
            }
            if (this.blood < 0) {
                this.blood = this.maxBlood;
            }
        }
    }

    private static int calculateMaxBlood(EntityLivingBase e) {
        if (e == null || e instanceof IMob) return 0;

        try {
            IAttributeInstance maxHealth = e.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            if (maxHealth != null && maxHealth.getAttributeValue() > 50.0D) {
                return 0;
            }
        } catch (Exception ignored) {
        }

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
        ensureInitialized();
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
            this.initialized = true;
        }
    }

    @Override
    public boolean canBeBitten(IVampireEntity biter) {
        ensureInitialized();
        return blood > 0 && maxBlood > 0;
    }

    @Override
    public float getBloodLevelRelative() {
        ensureInitialized();
        return maxBlood > 0 ? (float) blood / (float) maxBlood : 0.0F;
    }

    @Override
    public float getBloodSaturation() {
        return entity instanceof EntityVillager ? 1.0F : 0.7F;
    }

    @Override
    public int onBite(IVampireEntity biter) {
        ensureInitialized();
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

        // Hunter resistance to vampire drain
        if (entity instanceof net.minecraft.entity.player.EntityPlayer) {
            com.marrybye.neorism.common.hunter.HunterPlayer hp =
                com.marrybye.neorism.common.hunter.HunterPlayer.get((net.minecraft.entity.player.EntityPlayer) entity);
            if (hp != null && hp.isHunter()) {
                float res = hp.getDrainResistance();
                if (res > 0.0F) {
                    amt = Math.max(1, Math.round(amt * (1.0F - res)));
                    ModLogger.combat("Hunter %s resisted blood drain (resistance: %.0f%%, actual drain: %d)",
                        entity.getCommandSenderName(), res * 100.0F, amt);
                }
            }
        }

        ModLogger.combat("Vampire %s bit creature %s for %d blood (remaining: %d/%d)",
            biter != null ? biter.asEntity().getCommandSenderName() : "unknown",
            entity.getCommandSenderName(), amt, blood, maxBlood);

        if (poisonousBlood > 0 && biter != null) {
            biter.asEntity().attackEntityFrom(ModDamageSource.GARLIC, 6.0F);
            biter.asEntity().addPotionEffect(new PotionEffect(ModPotions.garlic.id, 200, 1));
            ModLogger.combat("Vampire %s bit creature with poisonous blood and suffered garlic burns",
                biter.asEntity().getCommandSenderName());
        }

        // Creature starved of blood dies
        if (blood <= 0) {
            ModLogger.combat("Creature %s died from complete blood loss", entity.getCommandSenderName());
            entity.attackEntityFrom(ModDamageSource.NO_BLOOD, 1000.0F);
        } else if (canBecomeVampire) {
            // 20% chance to contract Sanguinare Vampiris when bitten
            if (entity.worldObj.rand.nextInt(5) == 0 && biter != null) {
                tryInfect(biter);
            }
        }

        return amt;
    }

    @Override
    public boolean canDrain(int amount) {
        ensureInitialized();
        int avail = blood;
        if (entity instanceof EntityAgeable && ((EntityAgeable) entity).isChild()) {
            avail /= 3;
        }
        return avail > amount;
    }

    @Override
    public int onSyringeUse(int amount) {
        ensureInitialized();
        if (!canDrain(amount)) return 0;
        boolean child = entity instanceof EntityAgeable && ((EntityAgeable) entity).isChild();
        blood -= amount / (child ? 3 : 1);
        return amount;
    }

    public int drainBlood(int amount, float saturation) {
        return onSyringeUse(amount);
    }

    @Override
    public boolean canBeInfected(IVampireEntity vampire) {
        return canBecomeVampire && poisonousBlood <= 0 && !entity.isPotionActive(ModPotions.sanguinare);
    }

    @Override
    public boolean tryInfect(IVampireEntity vampire) {
        if (canBeInfected(vampire)) {
            entity.addPotionEffect(new PotionEffect(ModPotions.sanguinare.id, 2400, 0));
            ModLogger.combat("Creature %s contracted Sanguinare Vampiris from vampire bite",
                entity.getCommandSenderName());
            return true;
        }
        return false;
    }

    /**
     * Converts this creature into its canonical converted vampire counterpart when Sanguinare completes.
     */
    public EntityLivingBase makeVampire() {
        if (!canBecomeVampire || entity == null || entity.worldObj == null || entity.worldObj.isRemote || entity.isDead) {
            return null;
        }
        this.blood = -1;
        EntityLivingBase converted = null;
        if (entity instanceof EntityVillager) {
            EntityConvertedVillager cv = new EntityConvertedVillager(entity.worldObj);
            cv.setProfession(((EntityVillager) entity).getProfession());
            converted = cv;
        } else if (entity instanceof EntityHorse) {
            EntityHorse src = (EntityHorse) entity;
            EntityConvertedHorse ch = new EntityConvertedHorse(entity.worldObj);
            ch.setHorseVariant(src.getHorseVariant());
            ch.setHorseTamed(src.isTame());
            if (src.func_152119_ch() != null && !src.func_152119_ch().isEmpty()) {
                ch.func_152120_b(src.func_152119_ch());
            }
            ch.setHorseSaddled(src.isHorseSaddled());
            converted = ch;
        } else if (entity instanceof EntityCow) {
            converted = new EntityConvertedCow(entity.worldObj);
        } else if (entity instanceof EntitySheep) {
            EntityConvertedSheep cs = new EntityConvertedSheep(entity.worldObj);
            cs.setFleeceColor(((EntitySheep) entity).getFleeceColor());
            converted = cs;
        }

        if (converted != null) {
            converted.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            converted.rotationYawHead = entity.rotationYawHead;
            if (entity instanceof EntityAgeable && converted instanceof EntityAgeable) {
                ((EntityAgeable) converted).setGrowingAge(((EntityAgeable) entity).getGrowingAge());
            }
            if (entity instanceof EntityLiving && converted instanceof EntityLiving) {
                EntityLiving srcLiving = (EntityLiving) entity;
                if (srcLiving.hasCustomNameTag()) {
                    ((EntityLiving) converted).setCustomNameTag(srcLiving.getCustomNameTag());
                }
            }
            entity.setDead();
            entity.worldObj.spawnEntityInWorld(converted);
            ModLogger.spawn("Converted %s into %s at (%.1f, %.1f, %.1f)",
                entity.getCommandSenderName(), converted.getCommandSenderName(),
                converted.posX, converted.posY, converted.posZ);
        }
        return converted;
    }

    @Override
    public EntityLivingBase asEntity() {
        return entity;
    }

    public void updateLiving() {
        ensureInitialized();
        if (poisonousBlood > 0) {
            poisonousBlood--;
        }
        // Natural blood replenishment over time (1 in 20 chance every 40 ticks = ~2 seconds)
        if (blood < maxBlood && entity.ticksExisted % 40 == 0) {
            if (entity.worldObj.rand.nextInt(20) == 0) {
                blood++;
            }
        }
    }

    public void setPoisonousBlood(int duration) {
        ensureInitialized();
        this.poisonousBlood = Math.max(this.poisonousBlood, duration);
    }

    public boolean hasPoisonousBlood() {
        ensureInitialized();
        return this.poisonousBlood > 0;
    }

    public int getBlood() {
        ensureInitialized();
        return blood;
    }

    public int getMaxBlood() {
        ensureInitialized();
        return maxBlood;
    }
}
