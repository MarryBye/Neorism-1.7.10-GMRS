package com.marrybye.neorism.common.core;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Crafting recipes for Neorism items and equipment.
 */
public class ModRecipes {

    public static void init() {
        // Wooden Stake (2 stakes per log + stick)
        GameRegistry.addRecipe(new ItemStack(ModItems.stake, 2),
            " L",
            "S ",
            'L', Blocks.log,
            'S', Items.stick
        );
        GameRegistry.addRecipe(new ItemStack(ModItems.stake, 2),
            "L ",
            " S",
            'L', Blocks.log,
            'S', Items.stick
        );

        // Wooden Crucifix (Tier 0)
        GameRegistry.addRecipe(new ItemStack(ModItems.crucifix, 1, 0),
            " P ",
            "PPP",
            " S ",
            'P', Blocks.planks,
            'S', Items.stick
        );

        // Iron Crucifix (Tier 1)
        GameRegistry.addRecipe(new ItemStack(ModItems.crucifix, 1, 1),
            " I ",
            "ICI",
            " I ",
            'I', Items.iron_ingot,
            'C', new ItemStack(ModItems.crucifix, 1, 0)
        );

        // Golden Crucifix (Tier 2)
        GameRegistry.addRecipe(new ItemStack(ModItems.crucifix, 1, 2),
            " G ",
            "GCG",
            " G ",
            'G', Items.gold_ingot,
            'C', new ItemStack(ModItems.crucifix, 1, 1)
        );

        // Holy Water Bottle (Normal, Tier 0) - water bottle (potion 0) + gold nugget + glowstone
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 0),
            new ItemStack(Items.potionitem, 1, 0),
            Items.gold_nugget,
            Items.glowstone_dust
        );

        // Enhanced Holy Water (Tier 1)
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 1),
            new ItemStack(ModItems.holyWaterBottle, 1, 0),
            Items.gold_ingot,
            Items.glowstone_dust
        );

        // Ultimate Holy Water (Tier 2)
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 2),
            new ItemStack(ModItems.holyWaterBottle, 1, 1),
            Items.diamond,
            Items.ghast_tear
        );

        // Splash Holy Water Bottles (+ Gunpowder)
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.holyWaterSplashBottle, 1, 0),
            new ItemStack(ModItems.holyWaterBottle, 1, 0),
            Items.gunpowder
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.holyWaterSplashBottle, 1, 1),
            new ItemStack(ModItems.holyWaterBottle, 1, 1),
            Items.gunpowder
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.holyWaterSplashBottle, 1, 2),
            new ItemStack(ModItems.holyWaterBottle, 1, 2),
            Items.gunpowder
        );

        // Garlic Diffuser Core (Tier 0)
        GameRegistry.addRecipe(new ItemStack(ModItems.garlicDiffuserCore, 1, 0),
            " W ",
            "WGW",
            " W ",
            'W', Blocks.wool,
            'G', ModItems.garlic
        );

        // Garlic Diffuser Core Long (Tier 1)
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.garlicDiffuserCore, 1, 1),
            new ItemStack(ModItems.garlicDiffuserCore, 1, 0),
            Items.redstone
        );

        // Garlic Diffuser Core Strong (Tier 2)
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.garlicDiffuserCore, 1, 2),
            new ItemStack(ModItems.garlicDiffuserCore, 1, 0),
            Items.glowstone_dust
        );

        // Hunter Intel 0 (Tier 1) Research Note
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.hunterIntel, 1, 0),
            Items.paper,
            ModItems.vampireFang,
            Items.iron_ingot
        );
    }
}
