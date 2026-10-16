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

        // Garlic Bread
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.garlicBread, 1),
            Items.bread,
            ModItems.garlic
        );

        // Purified Garlic
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.purifiedGarlic, 2),
            ModItems.garlic,
            Items.sugar
        );

        // Blood Infused Metals
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodInfusedIron, 1),
            Items.iron_ingot,
            new ItemStack(ModItems.pureBlood, 1, 0)
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodInfusedGold, 1),
            Items.gold_ingot,
            new ItemStack(ModItems.pureBlood, 1, 1)
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodInfusedDiamond, 1),
            Items.diamond,
            new ItemStack(ModItems.pureBlood, 1, 2)
        );

        // Hunter Weapons
        GameRegistry.addRecipe(new ItemStack(ModItems.hunterAxe, 1, 0),
            "II",
            "SI",
            "S ",
            'I', Items.iron_ingot,
            'S', Items.stick
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.pitchfork, 1),
            "I I",
            " I ",
            " S ",
            'I', Items.iron_ingot,
            'S', Items.stick
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.heartSeeker, 1, 0),
            " G ",
            " S ",
            " H ",
            'G', Items.gold_ingot,
            'S', Items.iron_sword,
            'H', new ItemStack(ModItems.holyWaterBottle, 1, 0)
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.heartStriker, 1, 0),
            " F ",
            " S ",
            " B ",
            'F', ModItems.vampireFang,
            'S', Items.iron_sword,
            'B', new ItemStack(ModItems.pureBlood, 1, 0)
        );

        // Crossbow Ammunition (Quarrel x4)
        GameRegistry.addRecipe(new ItemStack(ModItems.quarrel, 4, 0),
            "I",
            "S",
            "F",
            'I', Items.iron_ingot,
            'S', Items.stick,
            'F', Items.feather
        );

        // Heavy Quarrel x4
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.quarrel, 4, 1),
            new ItemStack(ModItems.quarrel, 4, 0),
            Items.iron_ingot
        );

        // Basic Crossbow (Tier 0)
        GameRegistry.addRecipe(new ItemStack(ModItems.crossbow, 1, 0),
            "IWI",
            "SBS",
            " T ",
            'I', Items.iron_ingot,
            'W', Blocks.planks,
            'S', Items.string,
            'B', Items.bow,
            'T', Blocks.tripwire_hook
        );

        // Enhanced Crossbow (Tier 1)
        GameRegistry.addRecipe(new ItemStack(ModItems.crossbow, 1, 1),
            " G ",
            "GCG",
            " I ",
            'G', Items.gold_ingot,
            'C', new ItemStack(ModItems.crossbow, 1, 0),
            'I', Items.iron_ingot
        );

        // Double Crossbow (Tier 2)
        GameRegistry.addRecipe(new ItemStack(ModItems.crossbow, 1, 2),
            "C C",
            " I ",
            " T ",
            'C', new ItemStack(ModItems.crossbow, 1, 0),
            'I', Items.iron_ingot,
            'T', Blocks.tripwire_hook
        );

        // Umbrella
        GameRegistry.addRecipe(new ItemStack(ModItems.umbrella, 1),
            "WWW",
            " S ",
            " S ",
            'W', Blocks.wool,
            'S', Items.stick
        );

        // Hunter Hat
        GameRegistry.addRecipe(new ItemStack(ModItems.hunterHat, 1, 0),
            "LLL",
            "L L",
            'L', Items.leather
        );

        // Hunter Coat
        GameRegistry.addRecipe(new ItemStack(ModItems.hunterCoatHead, 1, 0),
            "LIL",
            "L L",
            'L', Items.leather,
            'I', Items.iron_ingot
        );
        GameRegistry.addRecipe(new ItemStack(ModItems.hunterCoatChest, 1, 0),
            "L L",
            "LIL",
            "LLL",
            'L', Items.leather,
            'I', Items.iron_ingot
        );
        GameRegistry.addRecipe(new ItemStack(ModItems.hunterCoatLegs, 1, 0),
            "LLL",
            "L L",
            "L L",
            'L', Items.leather
        );
        GameRegistry.addRecipe(new ItemStack(ModItems.hunterCoatFeet, 1, 0),
            "L L",
            "I I",
            'L', Items.leather,
            'I', Items.iron_ingot
        );

        // Vampire Cloak (Red / Black)
        GameRegistry.addRecipe(new ItemStack(ModItems.vampireCloak, 1, 1), // Red
            "WGW",
            "WWW",
            "WWW",
            'W', Blocks.wool,
            'G', Items.gold_ingot
        );
    }
}
