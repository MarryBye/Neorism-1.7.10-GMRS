package com.marrybye.neorism.common.vampire;

import com.marrybye.neorism.api.entity.player.IBloodStats;
import com.marrybye.neorism.common.core.ModAttributes;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;

import java.lang.reflect.Field;

/**
 * Handles vampire blood hunger, capacity, saturation, exhaustion, and healing formulas.
 */
public class BloodStats implements IBloodStats {

    private static Field foodExhaustionField;

    static {
        try {
            foodExhaustionField = FoodStats.class.getDeclaredField("foodExhaustionLevel");
            foodExhaustionField.setAccessible(true);
        } catch (NoSuchFieldException e1) {
            try {
                // Obfuscated SRG field name for foodExhaustionLevel in 1.7.10
                foodExhaustionField = FoodStats.class.getDeclaredField("field_75126_c");
                foodExhaustionField.setAccessible(true);
            } catch (Exception e2) {
                ModLogger.error("Failed to access foodExhaustionLevel field in FoodStats: %s", e2.getMessage());
            }
        }
    }

    private final EntityPlayer player;
    private int maxBlood = 20;
    private int bloodLevel = 20;
    private float bloodSaturationLevel = 5.0F;
    private float bloodExhaustionLevel = 0.0F;
    private int bloodTimer = 0;
    private int prevBloodLevel = 20;
    private boolean changed = false;

    public BloodStats(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public int getBloodLevel() {
        return bloodLevel;
    }

    @Override
    public void setBloodLevel(int level) {
        this.bloodLevel = Math.max(0, Math.min(level, maxBlood));
        this.changed = true;
    }

    @Override
    public int getMaxBlood() {
        return maxBlood;
    }

    @Override
    public void setMaxBlood(int maxBlood) {
        this.maxBlood = Math.max(1, maxBlood);
        if (this.bloodLevel > this.maxBlood) {
            this.bloodLevel = this.maxBlood;
        }
        this.changed = true;
    }

    @Override
    public int getPrevBloodLevel() {
        return prevBloodLevel;
    }

    @Override
    public float getBloodSaturation() {
        return bloodSaturationLevel;
    }

    @Override
    public float getBloodExhaustion() {
        return bloodExhaustionLevel;
    }

    @Override
    public boolean needsBlood() {
        return bloodLevel < maxBlood;
    }

    public int addBlood(int amount) {
        return addBlood(amount, 0.5F);
    }

    @Override
    public int addBlood(int amount, float saturationModifier) {
        int add = Math.min(amount, maxBlood - bloodLevel);
        bloodLevel += add;
        bloodSaturationLevel = Math.min(this.bloodSaturationLevel + (float) add * saturationModifier * 2.0F, (float) bloodLevel);
        changed = true;
        ModLogger.combat("Player %s consumed %d blood (level: %d/%d, sat: %.1f)",
            player.getCommandSenderName(), add, bloodLevel, maxBlood, bloodSaturationLevel);
        return amount - add;
    }

    @Override
    public boolean removeBlood(int amount, boolean allowPartial) {
        if (bloodLevel >= amount) {
            bloodLevel -= amount;
            changed = true;
            return true;
        } else if (allowPartial) {
            bloodLevel = 0;
            changed = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean consumeBlood(int amount) {
        return removeBlood(amount, false);
    }

    @Override
    public void addExhaustion(float amount) {
        double mod = 1.0D;
        if (player.getAttributeMap().getAttributeInstance(ModAttributes.BLOOD_EXHAUSTION) != null) {
            mod = player.getAttributeMap().getAttributeInstance(ModAttributes.BLOOD_EXHAUSTION).getAttributeValue();
        }
        amount *= (float) mod;
        this.bloodExhaustionLevel = Math.min(bloodExhaustionLevel + amount, 40.0F);
    }

    @Override
    public void onUpdate() {
        if (player == null || player.worldObj == null) return;

        FoodStats foodStats = player.getFoodStats();
        if (foodStats != null) {
            // Lock food level to 10 (half bar)
            foodStats.setFoodLevel(10);
            if (foodExhaustionField != null) {
                try {
                    float exhaustion = foodExhaustionField.getFloat(foodStats);
                    if (exhaustion > 0.0F) {
                        foodExhaustionField.setFloat(foodStats, 0.0F);
                        addExhaustion(exhaustion);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        this.prevBloodLevel = bloodLevel;
        EnumDifficulty difficulty = player.worldObj.difficultySetting;

        // Biome exhaustion gate: 6.0F in Vampire Biome, 4.0F outside
        float exhaustionGate = Helper.isEntityInVampireBiome(player) ? 6.0F : 4.0F;
        if (this.bloodExhaustionLevel > exhaustionGate) {
            this.bloodExhaustionLevel -= exhaustionGate;
            if (bloodSaturationLevel > 0.0F) {
                bloodSaturationLevel = Math.max(bloodSaturationLevel - 1.0F, 0.0F);
            } else if (difficulty != EnumDifficulty.PEACEFUL) {
                this.bloodLevel = Math.max(bloodLevel - 1, 0);
            }
        }

        if (!player.worldObj.isRemote) {
            boolean naturalRegen = player.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration");
            float healMod = getHealModifier();

            if (naturalRegen && this.bloodSaturationLevel > 0.0F && player.shouldHeal() && this.bloodLevel >= maxBlood) {
                ++this.bloodTimer;
                if (this.bloodTimer >= 10) {
                    float f = Math.min(this.bloodSaturationLevel, 6.0F);
                    player.heal((f / 6.0F) * healMod);
                    this.addExhaustion(f);
                    this.bloodTimer = 0;
                }
            } else if (naturalRegen && this.bloodLevel > 0 && player.shouldHeal()) {
                ++this.bloodTimer;
                boolean fastHeal = this.bloodLevel >= 18 && this.bloodTimer >= 80;
                boolean regularHeal = this.bloodTimer >= 300;
                if (fastHeal || regularHeal) {
                    player.heal(fastHeal ? healMod : 0.5F * healMod);
                    this.addExhaustion(fastHeal ? 6.0F : 3.0F);
                    this.bloodTimer = 0;
                }
            } else if (this.bloodLevel <= 0 && !player.capabilities.isCreativeMode) {
                ++this.bloodTimer;
                if (this.bloodTimer >= 80) {
                    if (player.getHealth() > 10.0F || difficulty == EnumDifficulty.HARD || (player.getHealth() > 1.0F && difficulty == EnumDifficulty.NORMAL)) {
                        player.addPotionEffect(new PotionEffect(ModPotions.noBlood.id, 150, 0));
                    }
                    this.bloodTimer = 0;
                }
            } else {
                this.bloodTimer = 0;
            }

            if (this.prevBloodLevel != this.bloodLevel) {
                this.changed = true;
            }
        }
    }

    public float getHealModifier() {
        int level = Helper.getLevel(player);
        return 1.0F + (level / 14.0F) * 0.5F;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void setClientData(int bloodLevel, int maxBlood, float saturation) {
        this.bloodLevel = bloodLevel;
        this.maxBlood = maxBlood;
        this.bloodSaturationLevel = saturation;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("bloodLevel", bloodLevel);
        tag.setInteger("maxBlood", maxBlood);
        tag.setFloat("bloodSaturation", bloodSaturationLevel);
        tag.setFloat("bloodExhaustion", bloodExhaustionLevel);
        tag.setInteger("bloodTimer", bloodTimer);
        compound.setTag("NeorismBlood", tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("NeorismBlood")) {
            NBTTagCompound tag = compound.getCompoundTag("NeorismBlood");
            this.bloodLevel = tag.getInteger("bloodLevel");
            this.maxBlood = tag.getInteger("maxBlood");
            if (this.maxBlood <= 0) this.maxBlood = 20;
            this.bloodSaturationLevel = tag.getFloat("bloodSaturation");
            this.bloodExhaustionLevel = tag.getFloat("bloodExhaustion");
            this.bloodTimer = tag.getInteger("bloodTimer");
        }
    }

    public void copyFrom(BloodStats other) {
        if (other != null) {
            this.bloodLevel = other.bloodLevel;
            this.maxBlood = other.maxBlood;
            this.bloodSaturationLevel = other.bloodSaturationLevel;
            this.bloodExhaustionLevel = other.bloodExhaustionLevel;
        }
    }
}
