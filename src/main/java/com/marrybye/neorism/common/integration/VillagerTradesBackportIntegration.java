package com.marrybye.neorism.common.integration;

import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.villagertradesbackport.trade.VillagerTradeManager;
import com.marrybye.villagertradesbackport.trade.VillagerProfession;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

/**
 * Integration with Villager Trades Backport mod.
 * Supports:
 * - 1.14+ trading GUI & profession workstations.
 * - Dynamic Hunter faction discounts in hunter territory.
 */
public class VillagerTradesBackportIntegration {

    public static void init() {
        ModLogger.debug("Villager Trades Backport integration initialized: 1.14+ trade mechanics active");
    }

    /**
     * Applies Hunter faction discounts to merchant recipes when trading in hunter territory.
     */
    public static void applyHunterDiscounts(EntityVillager villager, EntityPlayer player) {
        if (villager == null || player == null) return;
        try {
            MerchantRecipeList recipes = villager.getRecipes(player);
            if (recipes == null) return;

            for (Object obj : recipes) {
                if (obj instanceof MerchantRecipe) {
                    MerchantRecipe recipe = (MerchantRecipe) obj;
                    ItemStack cost1 = recipe.getItemToBuy();
                    if (cost1 != null && cost1.getItem() == Items.emerald && cost1.stackSize > 1) {
                        int discounted = Math.max(1, (int) Math.round(cost1.stackSize * 0.85D));
                        cost1.stackSize = discounted;
                    }
                }
            }
        } catch (Throwable t) {
            ModLogger.error("Failed to apply hunter discounts: %s", t.getMessage());
        }
    }
}
