package com.marrybye.neorism.common.entity.ai;

import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.entity.projectile.EntityQuarrel;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;

/**
 * AI task for hunter marksmen using crossbows to maintain tactical distance and shoot quarrels.
 */
public class EntityAICrossbowAttack extends EntityAIBase {

    private final EntityCreature hunter;
    private final double moveSpeedAmp;
    private int attackCooldown;
    private final float maxAttackDistance;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public EntityAICrossbowAttack(EntityCreature hunter, double speed, float maxDistance) {
        this.hunter = hunter;
        this.moveSpeedAmp = speed;
        this.maxAttackDistance = maxDistance;
        this.attackCooldown = 30;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = hunter.getAttackTarget();
        if (target == null || !target.isEntityAlive()) {
            return false;
        }
        ItemStack held = hunter.getHeldItem();
        return held != null && held.getItem() instanceof com.marrybye.neorism.common.items.ItemCrossbow;
    }

    @Override
    public boolean continueExecuting() {
        return shouldExecute();
    }

    @Override
    public void resetTask() {
        this.seeTime = 0;
        this.attackCooldown = 30;
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = hunter.getAttackTarget();
        if (target == null) return;

        double distSq = hunter.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
        boolean canSee = hunter.getEntitySenses().canSee(target);

        if (canSee) {
            this.seeTime++;
        } else {
            this.seeTime = 0;
        }

        // Tactical positioning
        if (distSq <= (double) (this.maxAttackDistance * this.maxAttackDistance) && this.seeTime >= 10) {
            hunter.getNavigator().clearPathEntity();
        } else {
            hunter.getNavigator().tryMoveToEntityLiving(target, this.moveSpeedAmp);
        }

        hunter.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);

        if (--this.attackCooldown <= 0) {
            if (canSee) {
                // Shoot crossbow quarrel
                shootQuarrel(target);
                this.attackCooldown = 35 + hunter.getRNG().nextInt(15);
            }
        }
    }

    private void shootQuarrel(EntityLivingBase target) {
        EntityQuarrel quarrel = new EntityQuarrel(hunter.worldObj, hunter, target, 1.8F, 3.0F);
        hunter.worldObj.playSoundAtEntity(hunter, "random.bow", 1.0F, 1.0F / (hunter.getRNG().nextFloat() * 0.4F + 0.8F));
        hunter.worldObj.spawnEntityInWorld(quarrel);
        hunter.swingItem();

        ModLogger.ai("Hunter marksman %s fired quarrel at %s (distance=%.1f)",
            hunter.getCommandSenderName(), target.getCommandSenderName(), Math.sqrt(hunter.getDistanceSqToEntity(target)));
    }
}
