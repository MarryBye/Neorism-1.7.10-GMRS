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
