package com.marrybye.neorism.common.entity.ai;

import java.util.List;

import com.marrybye.neorism.api.entity.IHunterEntity;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.entity.converted.EntityConvertedVillager;
import com.marrybye.neorism.common.util.Helper;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.world.ICaptureAttributes;
import de.teamlapen.faction.api.world.entities.IVillageCaptureEntity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

/**
 * Canonical port of TeamLapen's AttackVillageGoal & DefendVillageGoal.
 * Guides raid attackers and village defenders toward the village totem and forces target acquisition
 * against hostile defenders/attackers inside the village bounding box.
 */
public class EntityAIVillageCapture extends EntityAIBase {

    private final EntityCreature creature;
    private final IVillageCaptureEntity captureEntity;
    private EntityLivingBase targetMob;

    public EntityAIVillageCapture(EntityCreature creature) {
        this.creature = creature;
        this.captureEntity = (IVillageCaptureEntity) creature;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.captureEntity.isAttackingVillage() && !this.captureEntity.isDefendingVillage()) {
            return false;
        }
        AxisAlignedBB area = this.captureEntity.getTargetVillageArea();
        if (area == null) {
            return false;
        }
        this.targetMob = findVillageRaidTarget(area);
        return this.targetMob != null;
    }

    @Override
    public boolean continueExecuting() {
        if (!this.captureEntity.isAttackingVillage() && !this.captureEntity.isDefendingVillage()) {
            return false;
        }
        return this.targetMob != null && this.targetMob.isEntityAlive() && this.creature.getDistanceSqToEntity(this.targetMob) <= 2304.0D;
    }

    @Override
    public void startExecuting() {
        this.creature.setAttackTarget(this.targetMob);
        ICaptureAttributes info = this.captureEntity.getCaptureInfo();
        if (info != null && this.creature.getNavigator().noPath()) {
            this.creature.getNavigator().tryMoveToXYZ(info.getX() + 0.5D, info.getY(), info.getZ() + 0.5D, 1.0D);
        }
    }

    @Override
    public void resetTask() {
        this.targetMob = null;
    }

    @SuppressWarnings("unchecked")
    private EntityLivingBase findVillageRaidTarget(AxisAlignedBB area) {
        ICaptureAttributes info = this.captureEntity.getCaptureInfo();
        if (info == null) return null;

        boolean attacking = this.captureEntity.isAttackingVillage();
        IPlayableFaction myFaction = attacking ? info.getAttackingFaction() : info.getDefendingFaction();
        if (myFaction == null) return null;

        List<EntityLivingBase> list = this.creature.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, area);
        EntityLivingBase closest = null;
        double closestDist = Double.MAX_VALUE;

        for (EntityLivingBase candidate : list) {
            if (candidate == this.creature || !candidate.isEntityAlive()) continue;
            if (candidate instanceof EntityPlayer && ((EntityPlayer) candidate).capabilities.isCreativeMode) continue;

            if (isValidRaidTarget(candidate, myFaction, info, attacking)) {
                double d = this.creature.getDistanceSqToEntity(candidate);
                if (d < closestDist) {
                    closestDist = d;
                    closest = candidate;
                }
            }
        }
        return closest;
    }

    private boolean isValidRaidTarget(EntityLivingBase candidate, IPlayableFaction myFaction, ICaptureAttributes info, boolean attacking) {
        if (myFaction == ModFactions.VAMPIRE) {
            if (candidate instanceof IHunterEntity || Helper.isHunter(candidate)) {
                return true;
            }
            if (candidate instanceof EntityIronGolem && info.getDefendingFaction() != ModFactions.VAMPIRE) {
                return true;
            }
            if (attacking && candidate instanceof EntityVillager && !(candidate instanceof EntityConvertedVillager)) {
                return true;
            }
        } else if (myFaction == ModFactions.HUNTER) {
            if (candidate instanceof IVampireEntity || Helper.isVampire(candidate)) {
                return true;
            }
        }
        return false;
    }
}
