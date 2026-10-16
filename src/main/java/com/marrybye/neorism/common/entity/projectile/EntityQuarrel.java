package com.marrybye.neorism.common.entity.projectile;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;

/**
 * Quarrel bolt fired from Hunter Crossbows.
 * Features higher velocity, flatter trajectory, and bonus damage against vampires.
 */
public class EntityQuarrel extends EntityArrow {

    private boolean heavy = false;

    public EntityQuarrel(World world) {
        super(world);
    }

    public EntityQuarrel(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityQuarrel(World world, EntityLivingBase shooter, float velocity, boolean heavy) {
        super(world, shooter, velocity);
        this.heavy = heavy;
        if (heavy) {
            this.setDamage(this.getDamage() * 1.5D);
        }
    }

    public EntityQuarrel(World world, EntityLivingBase shooter, EntityLivingBase target, float velocity, float inaccuracy) {
        super(world, shooter, target, velocity, inaccuracy);
    }

    public boolean isHeavy() {
        return heavy;
    }

    public void setHeavy(boolean heavy) {
        this.heavy = heavy;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.worldObj.isRemote && !this.onGround) {
            this.worldObj.spawnParticle("crit", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }
}
