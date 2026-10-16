package com.marrybye.neorism.common.core;

import com.marrybye.neorism.common.items.ItemNeorism;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

/**
 * Items registry for Neorism.
 */
public class ModItems {

    public static Item vampireFang;
    public static Item humanHeart;
    public static Item garlic;
    public static Item pureBlood;
    public static Item vampireBook;
    public static Item bloodBottle;
    public static Item bloodFlask;
    public static Item hunterIntel;
    public static Item holyWaterBottle;
    public static Item holyWaterSplashBottle;
    public static Item stake;
    public static Item crucifix;
    public static Item garlicDiffuserCore;

    public static void init() {
        vampireFang = register("vampire_fang", new ItemNeorism("vampire_fang"));
        humanHeart = register("human_heart", new ItemNeorism("human_heart"));
        garlic = register("garlic", new com.marrybye.neorism.common.items.ItemGarlic());
        pureBlood = register("pure_blood", new ItemNeorism("pure_blood_0"));
        vampireBook = register("vampire_book", new ItemNeorism("vampire_book"));
        bloodBottle = register("blood_bottle", new com.marrybye.neorism.common.items.ItemBloodBottle());
        bloodFlask = register("blood_flask", new com.marrybye.neorism.common.items.ItemBloodFlask());

        hunterIntel = register("hunter_intel", new com.marrybye.neorism.common.items.ItemHunterIntel());
        holyWaterBottle = register("holy_water_bottle", new com.marrybye.neorism.common.items.ItemHolyWater());
        holyWaterSplashBottle = register("holy_water_splash_bottle", new com.marrybye.neorism.common.items.ItemHolyWaterSplash());
        stake = register("stake", new com.marrybye.neorism.common.items.ItemStake());
        crucifix = register("crucifix", new com.marrybye.neorism.common.items.ItemCrucifix());
        garlicDiffuserCore = register("garlic_diffuser_core", new com.marrybye.neorism.common.items.ItemGarlicDiffuserCore());

        ModLogger.debug("Initialized core Neorism items");
    }

    public static <T extends Item> T register(String name, T item) {
        GameRegistry.registerItem(item, name);
        return item;
    }
}
