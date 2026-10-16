package com.marrybye.neorism.common.entity.vampire;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

/**
 * Vampire Baroness miniboss variant.
 */
public class EntityVampireBaroness extends EntityVampireBaron {

    public EntityVampireBaroness(World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.33D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(140.0D);
    }
}
