package com.marrybye.neorism.api.entity;

/**
 * Interface implemented by all vampire mobs and vampire-like entities.
 */
public interface IVampireEntity extends IFactionEntity {

    /**
     * @return Whether this entity is currently receiving damage from sunlight.
     */
    boolean isGettingSundamage();

    /**
     * @return Whether this entity is immune or currently ignoring sunlight damage.
     */
    boolean isIgnoringSundamage();

    /**
     * @return Whether this vampire entity desires blood.
     */
    boolean wantsBlood();
}
