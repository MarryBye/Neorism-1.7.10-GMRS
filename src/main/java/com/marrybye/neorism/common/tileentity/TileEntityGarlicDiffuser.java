package com.marrybye.neorism.common.tileentity;

import java.util.List;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;

/**
 * TileEntity for the Garlic Diffuser protecting areas from vampires.
 */
public class TileEntityGarlicDiffuser extends TileEntityNeorism {

    public static final double RADIUS = 16.0D;

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote) {
            if (worldObj.rand.nextInt(4) == 0) {
                double rx = (worldObj.rand.nextDouble() - 0.5D) * 4.0D;
                double rz = (worldObj.rand.nextDouble() - 0.5D) * 4.0D;
                worldObj.spawnParticle("happyVillager", xCoord + 0.5D + rx, yCoord + 0.5D, zCoord + 0.5D + rz, 0, 0.05D, 0);
            }
        } else {
            if (worldObj.getTotalWorldTime() % 40 == 0) {
                AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                    xCoord - RADIUS, yCoord - RADIUS, zCoord - RADIUS,
                    xCoord + RADIUS, yCoord + RADIUS, zCoord + RADIUS
                );
                List list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
                for (Object obj : list) {
                    if (obj instanceof EntityLivingBase) {
                        EntityLivingBase target = (EntityLivingBase) obj;
                        boolean isVampire = Helper.isVampire(target) ||
                            (target instanceof IVampireEntity) ||
                            target.getClass().getName().toLowerCase().contains("vampire");

                        if (isVampire) {
                            target.attackEntityFrom(ModDamageSource.GARLIC, 2.0F);
                            target.addPotionEffect(new PotionEffect(Potion.weakness.id, 100, 1));
                            target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 1));
                            target.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 120, 0));
                            ModSounds.playAtEntity(target, ModSounds.SCREECH, 0.7F, 1.2F);
                        }
                    }
                }
            }
        }
    }
}
