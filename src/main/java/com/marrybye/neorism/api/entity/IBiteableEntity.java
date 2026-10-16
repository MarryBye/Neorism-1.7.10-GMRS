package com.marrybye.neorism.api.entity;

import net.minecraft.entity.EntityLivingBase;

/**
 * Interface implemented by entities that can be bitten by vampires for blood consumption.
 */
public interface IBiteableEntity {

    /**
     * Called when a vampire tries to bite this entity.
     *
     * @param biter The vampire entity attempting to bite.
     * @return True if the entity can currently be bitten.
     */
    boolean canBeBitten(IVampireEntity biter);

    /**
     * @return Current blood relative to maximum (0.0F to 1.0F).
     */
    float getBloodLevelRelative();

    /**
     * @return Saturation multiplier of this entity's blood (default 1.0F).
     */
    float getBloodSaturation();

    /**
     * Called when a vampire bites this entity.
     * Drains blood and returns the amount drained.
     *
     * @param biter The vampire entity biting.
     * @return Amount of blood units drained.
     */
    int onBite(IVampireEntity biter);

    /**
     * Checks if this amount of blood can be drained (e.g. by syringe).
     */
    boolean canDrain(int amount);

    /**
     * Drains blood via syringe or item tool.
     */
    int onSyringeUse(int amount);

    /**
     * @return Whether this entity can be infected with Sanguinare Vampiris.
     */
    boolean canBeInfected(IVampireEntity vampire);

    /**
     * Attempts to infect this entity.
     *
     * @return True if successfully infected.
     */
    boolean tryInfect(IVampireEntity vampire);

    /**
     * @return The underlying EntityLivingBase.
     */
    EntityLivingBase asEntity();
}
