package com.marrybye.neorism.common.entity.converted;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.World;

/**
 * Converted vampiric sheep with glowing red eyes, allied to vampires.
 */
public class EntityConvertedSheep extends EntitySheep implements IVampireEntity {

    public EntityConvertedSheep(World world) {
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
    public EntitySheep createChild(EntityAgeable ageable) {
        return new EntityConvertedSheep(this.worldObj);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        super.dropFewItems(recentlyHit, looting);
        if (this.rand.nextInt(3) == 0) {
            this.dropItem(ModItems.vampireFang, 1);
        }
    }
}
