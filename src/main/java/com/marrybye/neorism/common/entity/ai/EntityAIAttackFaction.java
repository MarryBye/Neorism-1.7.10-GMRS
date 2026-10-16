package com.marrybye.neorism.common.entity.ai;

import com.marrybye.neorism.api.entity.IBiteableEntity;
import com.marrybye.neorism.api.entity.IFactionEntity;
import com.marrybye.neorism.api.entity.IHunterEntity;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.entity.ExtendedCreature;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;

/**
 * AI target selector for faction-based hostility between Vampires, Hunters, and Prey.
 */
public class EntityAIAttackFaction extends EntityAINearestAttackableTarget {

    private final EntityCreature attacker;
    private final IFaction faction;

    public EntityAIAttackFaction(EntityCreature creature, IFaction faction, boolean checkSight) {
        super(creature, EntityLivingBase.class, 0, checkSight, false, new IEntitySelector() {
            @Override
            public boolean isEntityApplicable(Entity entity) {
                if (!(entity instanceof EntityLivingBase)) return false;
                EntityLivingBase living = (EntityLivingBase) entity;
                if (!living.isEntityAlive()) return false;

                if (living instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) living;
                    if (p.capabilities.isCreativeMode) return false;
                    if (faction == ModFactions.VAMPIRE) {
                        return Helper.isHunter(p);
                    } else if (faction == ModFactions.HUNTER) {
                        return Helper.isVampire(p);
                    }
                }

                if (faction == ModFactions.VAMPIRE) {
                    if (living instanceof IHunterEntity) return true;
                    // Check if vampire is hungry for biteable prey
                    if (creature instanceof IVampireEntity && ((IVampireEntity) creature).wantsBlood()) {
                        ExtendedCreature ext = ExtendedCreature.get(living);
                        if (ext != null && ext.canBeBitten((IVampireEntity) creature)) return true;
                        if (living instanceof IBiteableEntity && ((IBiteableEntity) living).canBeBitten((IVampireEntity) creature)) return true;
                    }
                } else if (faction == ModFactions.HUNTER) {
                    if (living instanceof IVampireEntity) return true;
                }

                return false;
            }
        });
        this.attacker = creature;
        this.faction = faction;
    }
}
