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

    public EntityAIAttackFaction(final EntityCreature creature, final IFaction faction, boolean checkSight) {
        super(creature, EntityLivingBase.class, 0, checkSight, false, new IEntitySelector() {
            @Override
            public boolean isEntityApplicable(Entity entity) {
                if (!(entity instanceof EntityLivingBase) || entity == creature) return false;
                EntityLivingBase living = (EntityLivingBase) entity;
                if (!living.isEntityAlive()) return false;

                boolean isBaronOrBoss = creature instanceof com.marrybye.neorism.common.entity.vampire.EntityVampireBaron
                    || creature instanceof com.marrybye.neorism.common.entity.vampire.EntityDracula
                    || creature instanceof com.marrybye.neorism.common.entity.vampire.EntityVampireMother;

                if (living instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) living;
                    if (p.capabilities.isCreativeMode) return false;
                    if (faction == ModFactions.VAMPIRE) {
                        // Canonical VampireBaronEntity / Dracula / Mother attack all players;
                        // Basic & Advanced Vampires (HOSTILE_TOWARDS_NEUTRAL) attack Hunter and Neutral players
                        if (isBaronOrBoss) {
                            return true;
                        }
                        return !Helper.isVampire(p);
                    } else if (faction == ModFactions.HUNTER) {
                        // Canonical BasicHunterEntity / AdvancedHunterEntity (.notNeutral()) attack Vampire players
                        return Helper.isVampire(p);
                    }
                    return false;
                }

                if (faction == ModFactions.VAMPIRE) {
                    if (living instanceof IHunterEntity) return true;
                    // Canonical BasicVampireEntity: biteable non-vampire creatures when hungry for blood
                    if (!isBaronOrBoss && creature instanceof IVampireEntity && ((IVampireEntity) creature).wantsBlood()) {
                        if (living instanceof IVampireEntity) return false;
                        ExtendedCreature ext = ExtendedCreature.get(living);
                        if (ext != null && ext.canBeBitten((IVampireEntity) creature)) return true;
                        if (living instanceof IBiteableEntity && ((IBiteableEntity) living).canBeBitten((IVampireEntity) creature)) return true;
                    }
                } else if (faction == ModFactions.HUNTER) {
                    if (living instanceof IVampireEntity && !(living instanceof com.marrybye.neorism.common.entity.converted.EntityConvertedVillager)) {
                        return true;
                    }
                    if (living instanceof net.minecraft.entity.monster.EntityZombie) {
                        return true;
                    }
                }

                return false;
            }
        });
        this.attacker = creature;
        this.faction = faction;
    }
}
