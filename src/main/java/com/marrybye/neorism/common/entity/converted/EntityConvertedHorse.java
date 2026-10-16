package com.marrybye.neorism.common.entity.converted;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.world.World;

/**
 * Converted vampiric horse with enhanced speed and jump, allied to vampires.
 */
public class EntityConvertedHorse extends EntityHorse implements IVampireEntity {

    public EntityConvertedHorse(World world) {
        super(world);
        this.setHorseType(0); // Regular horse with converted overlay
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
    public EntityAgeable createChild(EntityAgeable ageable) {
        return new EntityConvertedHorse(this.worldObj);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        super.dropFewItems(recentlyHit, looting);
        if (this.rand.nextInt(2) == 0) {
            this.dropItem(ModItems.vampireFang, 1);
        }
    }
}
