package com.marrybye.neorism.common.entity.vampire;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

/**
 * Vampire Baroness miniboss variant.
 */
public class EntityVampireBaroness extends EntityVampireBaron {

    public EntityVampireBaroness(World world) {
        super(world);
        this.setLady(true);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.33D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(140.0D);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setLady(true);
        return data;
    }
}
