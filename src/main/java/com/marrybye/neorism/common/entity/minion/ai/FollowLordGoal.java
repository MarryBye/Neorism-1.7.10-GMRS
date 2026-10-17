package com.marrybye.neorism.common.entity.minion.ai;

import com.marrybye.neorism.common.entity.minion.EntityMinionBase;
import com.marrybye.neorism.common.entity.minion.tasks.FollowLordTask;
import com.marrybye.neorism.common.entity.minion.tasks.ProtectLordTask;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * AI goal causing the minion to follow the Lord player when Follow or Protect task is active.
 */
public class FollowLordGoal extends EntityAIBase {

    private final EntityMinionBase minion;
    private final double speed;
    private EntityPlayer lord;
    private int timeToRecalcPath;

    public FollowLordGoal(EntityMinionBase minion, double speed) {
        this.minion = minion;
        this.speed = speed;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        IMinionTask.IMinionTaskDesc taskDesc = minion.getCurrentTask();
        if (taskDesc == null || (taskDesc.getTask() != null && !(taskDesc.getTask() instanceof FollowLordTask || taskDesc.getTask() instanceof ProtectLordTask))) {
            return false;
        }
        EntityPlayer lordPlayer = minion.getLordPlayerEntity();
        if (lordPlayer == null || minion.getDistanceSqToEntity(lordPlayer) < 16.0D) {
            return false;
        }
        this.lord = lordPlayer;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return lord != null && !minion.getNavigator().noPath() && minion.getDistanceSqToEntity(lord) > 9.0D;
    }

    @Override
    public void startExecuting() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void resetTask() {
        this.lord = null;
        this.minion.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        if (lord == null) return;
        this.minion.getLookHelper().setLookPositionWithEntity(lord, 10.0F, this.minion.getVerticalFaceSpeed());

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.minion.getNavigator().tryMoveToEntityLiving(lord, this.speed)) {
                if (minion.getDistanceSqToEntity(lord) > 256.0D) {
                    int x = (int) (lord.posX + (minion.getRNG().nextDouble() - 0.5D) * 4.0D);
                    int z = (int) (lord.posZ + (minion.getRNG().nextDouble() - 0.5D) * 4.0D);
                    int y = (int) lord.posY;
                    minion.setLocationAndAngles(x + 0.5D, y, z + 0.5D, minion.rotationYaw, minion.rotationPitch);
                    minion.getNavigator().clearPathEntity();
                }
            }
        }
    }
}
