package com.marrybye.neorism.common.integration;

import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.combatbackport.combat.WeaponRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

import java.util.List;
import java.util.UUID;

/**
 * Integration with Combat Backport mod (1.9+ combat mechanics).
 * Supports:
 * - Registering custom weapon attack speeds (Heartseeker, Heartstriker, Hunter Axe).
 * - Sweeping Edge / area sweep attacks for Heartseeker and Heartstriker.
 */
public class CombatBackportIntegration {

    public static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    public static void init() {
        try {
            // Register custom weapon speeds for 1.9 combat balance
            WeaponRegistry.CUSTOM_SPEEDS.put("neorism:heart_seeker", 1.6F);
            WeaponRegistry.CUSTOM_SPEEDS.put("neorism:heart_striker", 1.6F);
            WeaponRegistry.CUSTOM_SPEEDS.put("neorism:hunter_axe", 1.0F);

            // Register sweeping edge weapons
            WeaponRegistry.CUSTOM_SWEEP_ITEMS.add("neorism:heart_seeker");
            WeaponRegistry.CUSTOM_SWEEP_ITEMS.add("neorism:heart_striker");

            ModLogger.debug("Combat Backport integration active: Registered weapon speeds and sweeping weapons");
        } catch (Throwable t) {
            ModLogger.error("Error initializing Combat Backport integration: %s", t.getMessage());
        }
    }

    /**
     * Executes sweep attack when a player attacks with Heartseeker or Heartstriker.
     */
    public static void triggerSweepAttack(EntityPlayer player, EntityLivingBase target, float baseDamage) {
        if (player == null || target == null || player.worldObj.isRemote) return;

        ItemStack held = player.getHeldItem();
        if (held == null) return;

        boolean isSweepBlade = held.getItem() == ModItems.heartSeeker || held.getItem() == ModItems.heartStriker;
        if (!isSweepBlade) return;

        // Sweep attack area (1.0 block around target)
        AxisAlignedBB sweepBox = target.boundingBox.expand(1.0D, 0.25D, 1.0D);
        List<EntityLivingBase> list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, sweepBox);

        float sweepDamage = Math.max(1.0F, baseDamage * 0.5F);

        for (EntityLivingBase entity : list) {
            if (entity != player && entity != target && !player.isOnSameTeam(entity)) {
                entity.attackEntityFrom(DamageSource.causePlayerDamage(player), sweepDamage);
                entity.knockBack(player, 0.4F, player.posX - entity.posX, player.posZ - entity.posZ);
            }
        }

        // Sweep particle effect
        player.worldObj.spawnParticle("crit", target.posX, target.posY + target.height * 0.5D, target.posZ, 0.0D, 0.0D, 0.0D);
    }
}
