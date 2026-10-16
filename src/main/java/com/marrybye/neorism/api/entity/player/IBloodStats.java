package com.marrybye.neorism.api.entity.player;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Blood statistics interface managing blood level, capacity, saturation, and hunger exhaustion for vampires.
 */
public interface IBloodStats {

    float LOW_SATURATION = 0.3F;
    float MEDIUM_SATURATION = 0.7F;
    float HIGH_SATURATION = 1.0F;

    /**
     * @return Current blood units.
     */
    int getBloodLevel();

    /**
     * @return Maximum blood capacity (base 20, up to 40).
     */
    int getMaxBlood();

    /**
     * @return Blood level in previous tick.
     */
    int getPrevBloodLevel();

    /**
     * @return Blood saturation level.
     */
    float getBloodSaturation();

    /**
     * @return Current blood exhaustion level.
     */
    float getBloodExhaustion();

    /**
     * Adds blood and saturation.
     *
     * @param amount Units of blood to add.
     * @param saturationModifier Multiplier for saturation added.
     * @return Excess blood that could not fit.
     */
    int addBlood(int amount, float saturationModifier);

    /**
     * Consumes blood.
     *
     * @param amount Units of blood to consume.
     * @param allowPartial If true, consumes whatever is available up to amount.
     * @return True if blood was consumed.
     */
    boolean removeBlood(int amount, boolean allowPartial);

    /**
     * Consumes exact blood amount.
     *
     * @param amount Units of blood to consume.
     * @return True if player had enough blood and it was consumed.
     */
    default boolean consumeBlood(int amount) {
        return removeBlood(amount, false);
    }

    /**
     * @return True if current blood is less than maximum.
     */
    boolean needsBlood();

    void setBloodLevel(int level);

    void setMaxBlood(int maxBlood);

    void addExhaustion(float amount);

    void onUpdate();

    void writeToNBT(NBTTagCompound compound);

    void readFromNBT(NBTTagCompound compound);
}
