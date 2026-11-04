package com.marrybye.neorism.common.events;

import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.LevelAttributeModifier;
import com.marrybye.neorism.common.vampire.VampireLeveling;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * Event handler managing combat interactions for Vampires:
 * - Sanguinare Vampiris transmission to neutral players from vampire attacks.
 * - Increased fire vulnerability for vampires based on level.
 * - Diagnostic combat logging.
 */
public class VampireCombatEventHandler {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote) return;

        EntityLivingBase target = event.entityLiving;
        DamageSource source = event.source;

        // 1. Vampire Fire Vulnerability: Vampires take increased damage from fire
        if (target instanceof EntityPlayer && Helper.isVampire((EntityPlayer) target)) {
            if (source.isFireDamage() && source != ModDamageSource.SUN_DAMAGE) {
                int level = Helper.getLevel((EntityPlayer) target);
                double extraMod = LevelAttributeModifier.calculateModifierValue(level, VampireLeveling.MAX_LEVEL, 0.5D, 0.5D);
                float oldAmt = event.ammount;
                event.ammount *= (float) (1.0D + extraMod);
                ModLogger.combat("Vampire player %s took increased fire damage: %.1f -> %.1f (+%.0f%%)",
                    target.getCommandSenderName(), oldAmt, event.ammount, extraMod * 100.0D);
            }

            // Half Invulnerable skill: cap incoming hits exceeding 40% max HP
            com.marrybye.neorism.common.action.ActionHandler ah = com.marrybye.neorism.common.action.ActionHandler.get((EntityPlayer) target);
            if (ah != null && ah.isActive(com.marrybye.neorism.common.action.ActionRegistry.getAction("half_invulnerable"))) {
                float threshold = target.getMaxHealth() * 0.40F;
                if (event.ammount >= threshold && event.ammount < 999.0F) {
                    com.marrybye.neorism.common.vampire.VampirePlayer vp = com.marrybye.neorism.common.vampire.VampirePlayer.get((EntityPlayer) target);
                    if (vp != null && vp.getBloodStats().getBloodLevel() >= 4) {
                        vp.getBloodStats().consumeBlood(4);
                        event.ammount = Math.min(event.ammount, threshold);
                        ModSounds.playAtEntity(target, ModSounds.BEAM_ENTER, 0.8F, 1.2F);
                        ModLogger.combat("Vampire %s mitigated large hit via Half Invulnerable: capped at %.1f",
                            target.getCommandSenderName(), event.ammount);
                    } else {
                        ah.deactivateAction(com.marrybye.neorism.common.action.ActionRegistry.getAction("half_invulnerable"));
                    }
                }
            }
        }

        // Shield blocking check (Et-Futurum shield mitigation)
        if (com.marrybye.neorism.common.integration.IntegrationManager.isEtFuturumLoaded()) {
            if (com.marrybye.neorism.common.integration.EtFuturumIntegration.isBlockingWithShield(target)) {
                if (source.isProjectile() || source.damageType.equals("stake")) {
                    event.ammount *= 0.25F; // 75% damage mitigation on shield block
                    ModLogger.combat("Entity %s blocked attack with shield: remaining damage %.1f", target.getCommandSenderName(), event.ammount);
                }
            }
        }

        // Crossbow Technique Hunter skill bonus damage
        if (source.getSourceOfDamage() instanceof net.minecraft.entity.projectile.EntityArrow && source.getEntity() instanceof EntityPlayer) {
            EntityPlayer shooter = (EntityPlayer) source.getEntity();
            if (Helper.isHunter(shooter)) {
                com.marrybye.neorism.common.skills.SkillHandler sh = com.marrybye.neorism.common.skills.SkillHandler.get(shooter);
                if (sh != null && sh.isSkillUnlocked("crossbow_technique")) {
                    float oldAmt = event.ammount;
                    event.ammount *= 1.25F;
                    ModLogger.combat("Hunter %s hit %s with Crossbow Technique (+25%% damage: %.1f -> %.1f)",
                        shooter.getCommandSenderName(), target.getCommandSenderName(), oldAmt, event.ammount);
                }
            }
        }

        // Sweeping attack for Heartseeker / Heartstriker & Sword Finisher / Stake2
        if (source.getEntity() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) source.getEntity();
            com.marrybye.neorism.common.skills.SkillHandler sh = com.marrybye.neorism.common.skills.SkillHandler.get(attacker);
            if (sh != null) {
                if (sh.isSkillUnlocked("sword_finisher") && target.getHealth() <= target.getMaxHealth() * 0.25F) {
                    float oldAmt = event.ammount;
                    event.ammount *= 1.50F;
                    ModLogger.combat("Attacker %s executed Sword Finisher on %s (%.1f -> %.1f)",
                        attacker.getCommandSenderName(), target.getCommandSenderName(), oldAmt, event.ammount);
                }
                if (sh.isSkillUnlocked("stake2") && Helper.isVampire(target)) {
                    if (attacker.getHeldItem() != null && attacker.getHeldItem().getItem() instanceof com.marrybye.neorism.common.items.ItemStake) {
                        event.ammount *= 1.40F;
                    }
                }
            }
            if (com.marrybye.neorism.common.integration.IntegrationManager.isCombatBackportLoaded()) {
                com.marrybye.neorism.common.integration.CombatBackportIntegration.triggerSweepAttack(attacker, target, event.ammount);
            }
        }

        // 2. Sanguinare Vampiris infection transmission to neutral players
        if (target instanceof EntityPlayer && source.getEntity() instanceof EntityLivingBase) {
            EntityPlayer targetPlayer = (EntityPlayer) target;
            EntityLivingBase attacker = (EntityLivingBase) source.getEntity();

            boolean attackerIsVampire = Helper.isVampire(attacker) ||
                (attacker.getClass().getName().toLowerCase().contains("vampire"));

            if (attackerIsVampire) {
                FactionPlayerHandler targetFaction = FactionPlayerHandler.get(targetPlayer);
                if (targetFaction != null && targetFaction.canJoin(ModFactions.VAMPIRE)) {
                    if (!targetPlayer.isPotionActive(ModPotions.sanguinare)) {
                        // 15% chance per vampire strike to contract Sanguinare Vampiris
                        if (target.worldObj.rand.nextInt(7) == 0) {
                            // 24000 ticks = 20 minutes (1 in-game Minecraft day)
                            targetPlayer.addPotionEffect(new PotionEffect(ModPotions.sanguinare.id, 24000, 0));
                            targetPlayer.addChatMessage(new ChatComponentTranslation("chat.neorism.contracted_sanguinare"));
                            ModSounds.playAtEntity(targetPlayer, ModSounds.HEARTBEAT, 1.0F, 1.0F);
                            ModLogger.combat("Neutral player %s contracted Sanguinare Vampiris from %s",
                                targetPlayer.getCommandSenderName(), attacker.getCommandSenderName());
                        }
                    }
                }
            }
        }
    }
}
