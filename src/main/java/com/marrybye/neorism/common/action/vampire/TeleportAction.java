package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.core.ModSounds;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * Vampire Teleport ability (blinks forward up to 16 blocks).
 */
public class TeleportAction extends ActionBase {

    public TeleportAction() {
        super("teleport", 200, 0, false, 5);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        Vec3 start = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 look = player.getLookVec();
        double dist = 14.0D;
        Vec3 end = Vec3.createVectorHelper(start.xCoord + look.xCoord * dist, start.yCoord + look.yCoord * dist, start.zCoord + look.zCoord * dist);

        MovingObjectPosition mop = player.worldObj.rayTraceBlocks(start, end);
        double targetX = end.xCoord;
        double targetY = end.yCoord;
        double targetZ = end.zCoord;

        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            targetX = mop.blockX + 0.5D;
            targetY = mop.blockY + 1.0D;
            targetZ = mop.blockZ + 0.5D;
        }

        // Particle and sound at departure
        ModSounds.playAt(player.worldObj, player.posX, player.posY, player.posZ, ModSounds.TELEPORT_AWAY, 1.0F, 1.0F);

        // Teleport
        player.setPositionAndUpdate(targetX, targetY, targetZ);
        player.fallDistance = 0.0F;

        // Particle and sound at arrival
        ModSounds.playAt(player.worldObj, targetX, targetY, targetZ, ModSounds.TELEPORT_HERE, 1.0F, 1.0F);

        return true;
    }
}
