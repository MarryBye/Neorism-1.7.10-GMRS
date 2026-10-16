package com.marrybye.neorism.common.tileentity;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.Entity;

/**
 * TileEntity for Fog Diffusers generating dark artificial mist that protects vampires from sun damage.
 */
public class TileEntityFogDiffuser extends TileEntityNeorism {

    public static final Set<TileEntityFogDiffuser> ACTIVE_DIFFUSERS = new HashSet<TileEntityFogDiffuser>();
    public static final double RADIUS = 16.0D;

    @Override
    public void validate() {
        super.validate();
        ACTIVE_DIFFUSERS.add(this);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        ACTIVE_DIFFUSERS.remove(this);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (worldObj.isRemote && worldObj.rand.nextInt(3) == 0) {
            double rx = (worldObj.rand.nextDouble() - 0.5D) * RADIUS * 1.5D;
            double rz = (worldObj.rand.nextDouble() - 0.5D) * RADIUS * 1.5D;
            worldObj.spawnParticle("smoke", xCoord + 0.5D + rx, yCoord + 0.5D + worldObj.rand.nextDouble() * 2.0D, zCoord + 0.5D + rz, 0, 0.02D, 0);
        }
    }

    public static boolean isEntityProtected(Entity entity) {
        if (entity == null || entity.worldObj == null) return false;
        for (TileEntityFogDiffuser diffuser : ACTIVE_DIFFUSERS) {
            if (diffuser != null && diffuser.worldObj == entity.worldObj && !diffuser.isInvalid()) {
                double distSq = entity.getDistanceSq(diffuser.xCoord + 0.5D, diffuser.yCoord + 0.5D, diffuser.zCoord + 0.5D);
                if (distSq <= RADIUS * RADIUS) {
                    return true;
                }
            }
        }
        return false;
    }
}
