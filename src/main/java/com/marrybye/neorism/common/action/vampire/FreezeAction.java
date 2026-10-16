package com.marrybye.neorism.common.action.vampire;

import java.util.List;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;

/**
 * Vampire Freeze spell immobilizing nearby non-vampire entities in ice.
 */
public class FreezeAction extends ActionBase {

    public FreezeAction() {
        super("freeze", 500, 0, false, 6);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        double r = 8.0D;
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
            player.posX - r, player.posY - r, player.posZ - r,
            player.posX + r, player.posY + r, player.posZ + r
        );
        List list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

        for (Object obj : list) {
            if (obj instanceof EntityLivingBase && obj != player) {
                EntityLivingBase target = (EntityLivingBase) obj;
                boolean isVampire = Helper.isVampire(target) ||
                    (target instanceof IVampireEntity) ||
                    target.getClass().getName().toLowerCase().contains("vampire");

                if (!isVampire) {
                    target.addPotionEffect(new PotionEffect(ModPotions.freeze.id, 100, 1));
                }
            }
        }

        ModSounds.playAtEntity(player, ModSounds.FREEZE, 1.0F, 1.0F);
        return true;
    }
}
