package com.marrybye.neorism.common.integration;

import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Integration with Et-Futurum-Requiem backport mod.
 * Supports:
 * - Shared arrow & quarrel compatibility with tipped/spectral arrows.
 * - Crossbow identification.
 * - Shield blocking mitigation against stakes and holy damage.
 * - Banner patterns for Vampire and Hunter factions.
 */
public class EtFuturumIntegration {

    private static Item tippedArrow = null;
    private static Item spectralArrow = null;
    private static Item shield = null;
    private static Item crossbow = null;

    public static void init() {
        try {
            tippedArrow = GameRegistry.findItem("etfuturum", "tipped_arrow");
            spectralArrow = GameRegistry.findItem("etfuturum", "spectral_arrow");
            shield = GameRegistry.findItem("etfuturum", "shield");
            crossbow = GameRegistry.findItem("etfuturum", "crossbow");

            // Register Vampire and Hunter banner patterns if BannerPatternHelper is present
            registerBannerPatterns();

            ModLogger.debug("Et-Futurum-Requiem integration loaded: [tippedArrow: %b, spectralArrow: %b, shield: %b, crossbow: %b]",
                tippedArrow != null, spectralArrow != null, shield != null, crossbow != null);
        } catch (Throwable t) {
            ModLogger.error("Error during EtFuturum integration: %s", t.getMessage());
        }
    }

    private static void registerBannerPatterns() {
        try {
            ganymedes01.etfuturum.api.BannerPatternHelper.addPattern("vampire", "vam", new ItemStack(ModItems.vampireFang));
            ganymedes01.etfuturum.api.BannerPatternHelper.addPattern("hunter", "hnt", new ItemStack(ModItems.hunterIntel));
            ModLogger.debug("Registered Et-Futurum Vampire and Hunter banner patterns");
        } catch (Throwable t) {
            ModLogger.debug("BannerPatternHelper not available or patterns already present: %s", t.getMessage());
        }
    }

    public static boolean isArrow(ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        return item == tippedArrow || item == spectralArrow;
    }

    public static boolean isShield(ItemStack stack) {
        if (stack == null) return false;
        return stack.getItem() == shield;
    }

    public static boolean isCrossbow(ItemStack stack) {
        if (stack == null) return false;
        return stack.getItem() == crossbow;
    }

    /**
     * Checks if a target is actively blocking with an Et-Futurum shield.
     */
    public static boolean isBlockingWithShield(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isBlocking()) {
                ItemStack item = player.getItemInUse();
                return isShield(item);
            }
        }
        return false;
    }
}
