package com.marrybye.neorism.common.entity.ai;

import com.marrybye.neorism.api.entity.IBiteableEntity;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.entity.ExtendedCreature;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * AI task that allows vampires to latch onto living creatures and drain their blood to heal.
 */
public class EntityAIBiteLiving extends EntityAIBase {

    private final EntityCreature attacker;
    private final IVampireEntity vampire;
    private final double speedTowardsTarget;
    private int biteCooldown = 0;

    public EntityAIBiteLiving(EntityCreature attacker, double speed) {
        this.attacker = attacker;
        this.vampire = attacker instanceof IVampireEntity ? (IVampireEntity) attacker : null;
        this.speedTowardsTarget = speed;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (vampire == null || !vampire.wantsBlood() || attacker.getAttackTarget() == null) {
            return false;
        }

        EntityLivingBase target = attacker.getAttackTarget();
        if (!target.isEntityAlive()
            || target instanceof net.minecraft.entity.player.EntityPlayer
            || target instanceof com.marrybye.neorism.api.entity.IHunterEntity
            || target instanceof IVampireEntity) {
            return false;
        }

        // Check if target has blood to drink
        if (target instanceof IBiteableEntity) {
            return ((IBiteableEntity) target).canBeBitten(vampire);
        }

        ExtendedCreature ext = ExtendedCreature.get(target);
        return ext != null && ext.canBeBitten(vampire);
    }

    @Override
    public boolean continueExecuting() {
        if (vampire == null || !vampire.wantsBlood()) {
            return false;
        }
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive()
            || target instanceof net.minecraft.entity.player.EntityPlayer
            || target instanceof com.marrybye.neorism.api.entity.IHunterEntity) {
            return false;
        }

        ExtendedCreature ext = ExtendedCreature.get(target);
        boolean hasBlood = ext != null ? ext.canBeBitten(vampire) :
            (target instanceof IBiteableEntity && ((IBiteableEntity) target).canBeBitten(vampire));

        return hasBlood && !attacker.getNavigator().noPath();
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null) return;

        attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        double distSq = attacker.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);

        if (biteCooldown > 0) {
            biteCooldown--;
        }

        if (distSq <= 4.0D) {
            attacker.getNavigator().clearPathEntity();
            if (biteCooldown <= 0) {
                biteCooldown = 25; // 1.25s between bites

                int bloodGained = 0;
                if (target instanceof IBiteableEntity) {
                    bloodGained = ((IBiteableEntity) target).onBite(vampire);
                } else {
                    ExtendedCreature ext = ExtendedCreature.get(target);
                    if (ext != null) {
                        bloodGained = ext.onBite(vampire);
                    }
                }

                if (bloodGained > 0) {
                    attacker.heal(bloodGained * 1.5F);
                    if (attacker instanceof com.marrybye.neorism.common.entity.vampire.EntityVampireBase) {
                        ((com.marrybye.neorism.common.entity.vampire.EntityVampireBase) attacker).onDrinkBlood(bloodGained, 1.0F);
                    }
                    attacker.swingItem();
                    ModSounds.playAtEntity(attacker, ModSounds.BITE, 1.0F, 1.0F);

                    // Spawn blood drip particles
                    for (int i = 0; i < 7; ++i) {
                        double px = target.posX + (attacker.getRNG().nextDouble() - 0.5D) * target.width;
                        double py = target.posY + target.getEyeHeight() - 0.2D;
                        double pz = target.posZ + (attacker.getRNG().nextDouble() - 0.5D) * target.width;
                        attacker.worldObj.spawnParticle("reddust", px, py, pz, 0.0D, 0.0D, 0.0D);
                    }

                    ModLogger.combat("Vampire mob %s bit %s, gained %d blood (healed +%.1f HP)",
                        attacker.getCommandSenderName(), target.getCommandSenderName(), bloodGained, bloodGained * 1.5F);
                }
            }
        } else {
            attacker.getNavigator().tryMoveToEntityLiving(target, speedTowardsTarget);
        }
    }
}
