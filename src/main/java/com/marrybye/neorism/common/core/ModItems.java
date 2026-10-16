package com.marrybye.neorism.common.core;

import com.marrybye.neorism.common.items.ItemBloodBottle;
import com.marrybye.neorism.common.items.ItemBloodFlask;
import com.marrybye.neorism.common.items.ItemCrossbow;
import com.marrybye.neorism.common.items.ItemCrucifix;
import com.marrybye.neorism.common.items.ItemGarlic;
import com.marrybye.neorism.common.items.ItemGarlicBread;
import com.marrybye.neorism.common.items.ItemGarlicDiffuserCore;
import com.marrybye.neorism.common.items.ItemHeartSeeker;
import com.marrybye.neorism.common.items.ItemHeartStriker;
import com.marrybye.neorism.common.items.ItemHolyWater;
import com.marrybye.neorism.common.items.ItemHolyWaterSplash;
import com.marrybye.neorism.common.items.ItemHunterArmor;
import com.marrybye.neorism.common.items.ItemHunterAxe;
import com.marrybye.neorism.common.items.ItemHunterHat;
import com.marrybye.neorism.common.items.ItemHunterIntel;
import com.marrybye.neorism.common.items.ItemNeorism;
import com.marrybye.neorism.common.items.ItemOilBottle;
import com.marrybye.neorism.common.items.ItemPitchfork;
import com.marrybye.neorism.common.items.ItemPureBlood;
import com.marrybye.neorism.common.items.ItemQuarrel;
import com.marrybye.neorism.common.items.ItemQuarrelClip;
import com.marrybye.neorism.common.items.ItemStake;
import com.marrybye.neorism.common.items.ItemSwiftnessArmor;
import com.marrybye.neorism.common.items.ItemUmbrella;
import com.marrybye.neorism.common.items.ItemVampireBook;
import com.marrybye.neorism.common.items.ItemVampireCloak;
import com.marrybye.neorism.common.items.ItemVampireClothing;
import com.marrybye.neorism.common.items.ItemVampireFang;
import com.marrybye.neorism.common.items.ItemInjection;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

/**
 * Items registry for Neorism.
 */
public class ModItems {

    // Armor Materials
    public static ArmorMaterial ARMOR_HUNTER;
    public static ArmorMaterial ARMOR_SWIFTNESS;
    public static ArmorMaterial ARMOR_VAMPIRE;

    // Materials & Food
    public static Item vampireFang;
    public static Item humanHeart;
    public static Item weakHumanHeart;
    public static Item garlic;
    public static Item purifiedGarlic;
    public static Item garlicBread;
    public static Item pureBlood;
    public static Item vampireBook;
    public static Item bloodBottle;
    public static Item bloodFlask;
    public static Item sovereignBlood;
    public static Item vampireBloodBottle;
    public static Item soulOrbVampire;
    public static Item injectionSanguinare;
    public static Item injectionGarlic;

    // Ingot infusions
    public static Item bloodInfusedIron;
    public static Item bloodInfusedGold;
    public static Item bloodInfusedDiamond;
    public static Item bloodInfusedNetherite;
    public static Item bloodInfusedRawIron;
    public static Item bloodInfusedRawGold;

    // Hunter Progression & Holy Equipment
    public static Item hunterIntel;
    public static Item holyWaterBottle;
    public static Item holyWaterSplashBottle;
    public static Item stake;
    public static Item crucifix;
    public static Item garlicDiffuserCore;

    // Weapons
    public static Item hunterAxe;
    public static Item heartSeeker;
    public static Item heartStriker;
    public static Item pitchfork;

    // Crossbows & Ammunition
    public static Item quarrel;
    public static Item quarrelClip;
    public static Item crossbow;
    public static Item oilBottle;

    // Wearables & Armor
    public static Item hunterHat;
    public static Item hunterCoatHead;
    public static Item hunterCoatChest;
    public static Item hunterCoatLegs;
    public static Item hunterCoatFeet;

    public static Item swiftnessHead;
    public static Item swiftnessChest;
    public static Item swiftnessLegs;
    public static Item swiftnessFeet;

    public static Item vampireCrown;
    public static Item vampireHat;
    public static Item vampireLegs;
    public static Item vampireBoots;
    public static Item vampireCloak;
    public static Item umbrella;

    public static void init() {
        ARMOR_HUNTER = EnumHelper.addArmorMaterial("NEORISM_HUNTER", 18, new int[]{2, 5, 4, 2}, 15);
        ARMOR_SWIFTNESS = EnumHelper.addArmorMaterial("NEORISM_SWIFTNESS", 22, new int[]{3, 6, 5, 3}, 20);
        ARMOR_VAMPIRE = EnumHelper.addArmorMaterial("NEORISM_VAMPIRE", 25, new int[]{3, 7, 5, 3}, 25);

        // Materials & Collectibles
        vampireFang = register("vampire_fang", new ItemVampireFang());
        humanHeart = register("human_heart", new ItemNeorism("human_heart"));
        weakHumanHeart = register("weak_human_heart", new ItemNeorism("weak_human_heart"));
        garlic = register("garlic", new ItemGarlic());
        purifiedGarlic = register("purified_garlic", new ItemNeorism("purified_garlic"));
        garlicBread = register("garlic_bread", new ItemGarlicBread());
        pureBlood = register("pure_blood", new ItemPureBlood());
        vampireBook = register("vampire_book", new ItemVampireBook());
        bloodBottle = register("blood_bottle", new ItemBloodBottle());
        bloodFlask = register("blood_flask", new ItemBloodFlask());
        sovereignBlood = register("sovereign_blood", new ItemNeorism("sovereign_blood"));
        vampireBloodBottle = register("vampire_blood_bottle", new ItemNeorism("vampire_blood_bottle"));
        soulOrbVampire = register("soul_orb_vampire", new ItemNeorism("soul_orb_vampire"));
        injectionSanguinare = register("injection_sanguinare", new ItemInjection("injection_sanguinare", ItemInjection.Type.SANGUINARE));
        injectionGarlic = register("injection_garlic", new ItemInjection("injection_garlic", ItemInjection.Type.GARLIC));

        // Infusions
        bloodInfusedIron = register("blood_infused_iron_ingot", new ItemNeorism("blood_infused_iron_ingot"));
        bloodInfusedGold = register("blood_infused_gold_ingot", new ItemNeorism("blood_infused_gold_ingot"));
        bloodInfusedDiamond = register("blood_infused_diamond", new ItemNeorism("blood_infused_diamond"));
        bloodInfusedNetherite = register("blood_infused_netherite_ingot", new ItemNeorism("blood_infused_netherite_ingot"));
        bloodInfusedRawIron = register("blood_infused_raw_iron", new ItemNeorism("blood_infused_raw_iron"));
        bloodInfusedRawGold = register("blood_infused_raw_gold", new ItemNeorism("blood_infused_raw_gold"));

        // Hunter & Holy Gear
        hunterIntel = register("hunter_intel", new ItemHunterIntel());
        holyWaterBottle = register("holy_water_bottle", new ItemHolyWater());
        holyWaterSplashBottle = register("holy_water_splash_bottle", new ItemHolyWaterSplash());
        stake = register("stake", new ItemStake());
        crucifix = register("crucifix", new ItemCrucifix());
        garlicDiffuserCore = register("garlic_diffuser_core", new ItemGarlicDiffuserCore());

        // Weapons
        hunterAxe = register("hunter_axe", new ItemHunterAxe());
        heartSeeker = register("heart_seeker", new ItemHeartSeeker());
        heartStriker = register("heart_striker", new ItemHeartStriker());
        pitchfork = register("pitchfork", new ItemPitchfork());

        // Crossbow & Ammunition
        quarrel = register("quarrel", new ItemQuarrel());
        quarrelClip = register("quarrel_clip", new ItemQuarrelClip());
        crossbow = register("crossbow", new ItemCrossbow());
        oilBottle = register("oil_bottle", new ItemOilBottle());

        // Wearables & Armor
        hunterHat = register("hunter_hat", new ItemHunterHat(ARMOR_HUNTER));
        hunterCoatHead = register("hunter_coat_head", new ItemHunterArmor(ARMOR_HUNTER, 0, "head"));
        hunterCoatChest = register("hunter_coat_chest", new ItemHunterArmor(ARMOR_HUNTER, 1, "chest"));
        hunterCoatLegs = register("hunter_coat_legs", new ItemHunterArmor(ARMOR_HUNTER, 2, "legs"));
        hunterCoatFeet = register("hunter_coat_feet", new ItemHunterArmor(ARMOR_HUNTER, 3, "feet"));

        swiftnessHead = register("armor_of_swiftness_head", new ItemSwiftnessArmor(ARMOR_SWIFTNESS, 0, "head"));
        swiftnessChest = register("armor_of_swiftness_chest", new ItemSwiftnessArmor(ARMOR_SWIFTNESS, 1, "chest"));
        swiftnessLegs = register("armor_of_swiftness_legs", new ItemSwiftnessArmor(ARMOR_SWIFTNESS, 2, "legs"));
        swiftnessFeet = register("armor_of_swiftness_feet", new ItemSwiftnessArmor(ARMOR_SWIFTNESS, 3, "feet"));

        vampireCrown = register("vampire_clothing_crown", new ItemVampireClothing(ARMOR_VAMPIRE, 0, "crown"));
        vampireHat = register("vampire_clothing_hat", new ItemVampireClothing(ARMOR_VAMPIRE, 0, "hat"));
        vampireLegs = register("vampire_clothing_legs", new ItemVampireClothing(ARMOR_VAMPIRE, 2, "legs"));
        vampireBoots = register("vampire_clothing_boots", new ItemVampireClothing(ARMOR_VAMPIRE, 3, "boots"));
        vampireCloak = register("vampire_cloak", new ItemVampireCloak(ARMOR_VAMPIRE));
        umbrella = register("umbrella", new ItemUmbrella());

        ModLogger.debug("Initialized full Phase 7 Neorism arsenal and equipment");
    }

    public static <T extends Item> T register(String name, T item) {
        GameRegistry.registerItem(item, name);
        return item;
    }
}
