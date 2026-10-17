package com.marrybye.neorism.common.entity.minion.ai;

import com.marrybye.neorism.common.entity.minion.EntityMinionBase;
import com.marrybye.neorism.common.entity.minion.tasks.ProtectLordTask;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;

/**
 * AI target goal causing minion to retaliate against attackers of the Lord player.
 */
public class DefendLordGoal extends EntityAITarget {

    private final EntityMinionBase minion;
    private EntityLivingBase attacker;

    public DefendLordGoal(EntityMinionBase minion) {
        super(minion, false);
        this.minion = minion;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        IMinionTask.IMinionTaskDesc taskDesc = minion.getCurrentTask();
        if (taskDesc == null || !(taskDesc.getTask() instanceof ProtectLordTask)) {
            return false;
        }
        EntityPlayer lord = minion.getLordPlayerEntity();
        if (lord == null) return false;

        this.attacker = lord.getAITarget();
        if (this.attacker != null && this.isSuitableTarget(this.attacker, false)) {
            return true;
        }
        this.attacker = lord.getLastAttacker();
        return this.attacker != null && this.isSuitableTarget(this.attacker, false);
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.attacker);
        super.startExecuting();
    }
}
