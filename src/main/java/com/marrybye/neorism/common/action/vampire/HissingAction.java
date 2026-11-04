package com.marrybye.neorism.common.action.vampire;

import java.util.List;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.core.ModSounds;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

/**
 * Vampire Hissing ability.
 * Canonical port of TeamLapen's HissingAction:
 * Emits a terrifying vampire screech that causes all nearby hostile mobs targeting the player to stop and lose target.
 */
public class HissingAction extends ActionBase {

    public HissingAction() {
        super("hissing", 1200, 0, false, 2);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        ModSounds.playAtEntity(player, ModSounds.VAMPIRE_SCREAM, 1.0F, 1.0F);

        if (!player.worldObj.isRemote) {
            double r = 10.0D;
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                player.posX - r, player.posY - r, player.posZ - r,
                player.posX + r, player.posY + r, player.posZ + r
            );
            List list = player.worldObj.getEntitiesWithinAABB(EntityLiving.class, aabb);
            for (Object obj : list) {
                if (obj instanceof EntityLiving) {
                    EntityLiving mob = (EntityLiving) obj;
                    if (mob.getAttackTarget() == player) {
                        mob.setAttackTarget(null);
                        if (mob instanceof EntityCreature) {
                            ((EntityCreature) mob).setTarget(null);
                        }
                    }
                }
            }
        }
        return true;
    }
}
