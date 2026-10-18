package com.marrybye.neorism.common.entity.ai;

import java.util.List;

import com.marrybye.neorism.api.entity.IHunterEntity;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.entity.hunter.EntityHunterBase;
import com.marrybye.neorism.common.entity.hunter.EntityHunterTrainer;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaron;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBase;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.AxisAlignedBB;

/**
 * Canonical port of HunterHurtByTargetGoal and VampireHurtByTargetGoal.
 * Prevents friendly fire infighting between same-faction NPCs (e.g. Hunter crossbow hits)
 * and alerts all nearby Basic and Advanced allies of the same faction when attacked by an enemy.
 */
public class EntityAIHurtByTargetFaction extends EntityAITarget {

    private final IFaction faction;
    private int revengeTimerOld;

    public EntityAIHurtByTargetFaction(EntityCreature creature, IFaction faction) {
        super(creature, false);
        this.faction = faction;
        this.setMutexBits(1);
    }

    public static boolean isSameFactionMob(EntityLivingBase self, EntityLivingBase other, IFaction faction) {
        if (self == null || other == null) return false;
        if (self == other) return true;
        if (faction == ModFactions.HUNTER) {
            return other instanceof IHunterEntity;
        }
        if (faction == ModFactions.VAMPIRE) {
            // Canonical VampireBaronEntity: Barons can duel other Barons, but normal vampires never fight vampires
            if (self instanceof EntityVampireBaron && other instanceof EntityVampireBaron) {
                return false;
            }
            return other instanceof IVampireEntity;
        }
        return false;
    }

    @Override
    public boolean shouldExecute() {
        int timer = this.taskOwner.func_142015_aE();
        EntityLivingBase attacker = this.taskOwner.getAITarget();
        if (timer == this.revengeTimerOld || attacker == null || !attacker.isEntityAlive()) {
            return false;
        }
        // Ignore friendly fire from fellow faction NPCs
        if (isSameFactionMob(this.taskOwner, attacker, this.faction)) {
            this.taskOwner.setRevengeTarget(null);
            return false;
        }
        return this.isSuitableTarget(attacker, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void startExecuting() {
        EntityLivingBase attacker = this.taskOwner.getAITarget();
        this.revengeTimerOld = this.taskOwner.func_142015_aE();
        if (attacker == null || isSameFactionMob(this.taskOwner, attacker, this.faction)) {
            return;
        }

        this.taskOwner.setAttackTarget(attacker);
        double dist = this.getTargetDistance();
        AxisAlignedBB alertBox = AxisAlignedBB.getBoundingBox(
            this.taskOwner.posX - dist, this.taskOwner.posY - 10.0D, this.taskOwner.posZ - dist,
            this.taskOwner.posX + dist, this.taskOwner.posY + 10.0D, this.taskOwner.posZ + dist);

        if (this.faction == ModFactions.HUNTER) {
            // Canonical HunterHurtByTargetGoal: alert all Basic and Advanced Hunters (excluding HunterTrainer)
            List<EntityHunterBase> allies = this.taskOwner.worldObj.getEntitiesWithinAABB(EntityHunterBase.class, alertBox);
            for (EntityHunterBase ally : allies) {
                if (ally != this.taskOwner
                    && !(ally instanceof EntityHunterTrainer)
                    && ally.getAttackTarget() == null
                    && !isSameFactionMob(ally, attacker, this.faction)) {
                    ally.setAttackTarget(attacker);
                }
            }
        } else if (this.faction == ModFactions.VAMPIRE) {
            // Canonical VampireHurtByTargetGoal: alert all Basic and Advanced Vampires (excluding Barons)
            List<EntityVampireBase> allies = this.taskOwner.worldObj.getEntitiesWithinAABB(EntityVampireBase.class, alertBox);
            for (EntityVampireBase ally : allies) {
                if (ally != this.taskOwner
                    && !(ally instanceof EntityVampireBaron)
                    && ally.getAttackTarget() == null
                    && !isSameFactionMob(ally, attacker, this.faction)) {
                    ally.setAttackTarget(attacker);
                }
            }
        }

        super.startExecuting();
    }
}
