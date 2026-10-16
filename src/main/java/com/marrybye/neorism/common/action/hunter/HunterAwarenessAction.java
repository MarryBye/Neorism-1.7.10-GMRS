package com.marrybye.neorism.common.action.hunter;

import java.util.List;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;

/**
 * Hunter Vampire Awareness ability detecting nearby undead and vampire threats through walls.
 */
public class HunterAwarenessAction extends ActionBase {

    public HunterAwarenessAction() {
        super("awareness_hunter", 600, 300, false, 0);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        int count = detectVampires(player, false);
        if (!player.worldObj.isRemote) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.hunter_awareness_detected", count));
        }
        return true;
    }

    @Override
    public void onUpdate(EntityPlayer player, int activeTicksLeft) {
        if (player.worldObj.isRemote && activeTicksLeft % 10 == 0) {
            detectVampires(player, true);
        }
    }

    private int detectVampires(EntityPlayer player, boolean spawnClientParticles) {
        double r = 24.0D;
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
            player.posX - r, player.posY - r, player.posZ - r,
            player.posX + r, player.posY + r, player.posZ + r
        );
        List list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        int count = 0;

        for (Object obj : list) {
            if (obj instanceof EntityLivingBase && obj != player) {
                EntityLivingBase target = (EntityLivingBase) obj;
                boolean isVampire = Helper.isVampire(target) ||
                    (target instanceof IVampireEntity) ||
                    target.getClass().getName().toLowerCase().contains("vampire");

                if (isVampire || target.isEntityUndead()) {
                    count++;
                    if (spawnClientParticles) {
                        player.worldObj.spawnParticle("flame", target.posX, target.posY + target.height * 0.5D, target.posZ, 0, 0.05D, 0);
                    }
                }
            }
        }
        return count;
    }
}
