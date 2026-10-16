package com.marrybye.neorism.common.entity.converted;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.world.World;

/**
 * Converted vampiric cow with glowing red eyes, allied to vampires.
 */
public class EntityConvertedCow extends EntityCow implements IVampireEntity {

    public EntityConvertedCow(World world) {
        super(world);
    }

    @Override
    public IFaction getFaction() {
        return ModFactions.VAMPIRE;
    }

    @Override
    public EntityLivingBase asEntity() {
        return this;
    }

    @Override
    public boolean isGettingSundamage() {
        return false;
    }

    @Override
    public boolean isIgnoringSundamage() {
        return true;
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    @Override
    public EntityCow createChild(EntityAgeable ageable) {
        return new EntityConvertedCow(this.worldObj);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        super.dropFewItems(recentlyHit, looting);
        if (this.rand.nextInt(3) == 0) {
            this.dropItem(ModItems.vampireFang, 1);
        }
    }
}
