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
import com.marrybye.neorism.common.items.ItemHumanHeart;
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
import com.marrybye.neorism.common.items.ItemSyringe;
import com.marrybye.neorism.common.items.ItemOblivionPotion;
import com.marrybye.neorism.common.items.ItemRitualKnife;
import com.marrybye.neorism.common.items.ItemModBoat;
import com.marrybye.neorism.common.items.ItemModDoor;
import com.marrybye.neorism.common.items.ItemTent;
import com.marrybye.neorism.common.items.ItemMinionUpgrade;
import com.marrybye.neorism.common.items.ItemQuarrelPouch;
import com.marrybye.neorism.common.items.ItemPureSaltWater;
import com.marrybye.neorism.common.items.ItemAlchemicalFire;
import com.marrybye.neorism.common.items.ItemFactionSpawnEgg;
import com.marrybye.neorism.common.items.ItemRefinement;
import com.marrybye.neorism.common.items.ItemFabricFilter;
import com.marrybye.neorism.common.items.ItemFeedingAdapter;
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

    // Category 1: General & Collectibles
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
    public static Item motherCore;
    public static Item caseFile144;
    public static Item garlicFinder;
    public static Item oblivionPotion;
    public static Item ritualKnife;
    public static Item syringeEmpty;
    public static Item syringeBlood;
    public static Item serumInjection;
    public static Item injectionSanguinare;
    public static Item injectionGarlic;
    public static Item darkSpruceBoat;
    public static Item cursedSpruceBoat;
    public static Item darkSpruceChestBoat;
    public static Item cursedSpruceChestBoat;
    public static Item itemTent;
    public static Item itemTentSpawner;
    public static Item darkSpruceDoorItem;
    public static Item cursedSpruceDoorItem;
    public static Item darkSpruceSignItem;
    public static Item cursedSpruceSignItem;
    public static Item darkSpruceHangingSignItem;
    public static Item cursedSpruceHangingSignItem;

    // Ingot infusions
    public static Item bloodInfusedIron;
    public static Item bloodInfusedGold;
    public static Item bloodInfusedDiamond;
    public static Item bloodInfusedNetherite;
    public static Item bloodInfusedRawIron;
    public static Item bloodInfusedRawGold;

    // Category 2: Hunter Progression, Holy Gear & Minions
    public static Item hunterIntel;
    public static Item holyWaterBottle;
    public static Item holyWaterSplashBottle;
    public static Item stake;
    public static Item crucifix;
    public static Item garlicDiffuserCore;
    public static Item pureSalt;
    public static Item pureSaltWater;
    public static Item alchemicalFire;
    public static Item hunterMinionEquipment;
    public static Item hunterMinionUpgradeSimple;
    public static Item hunterMinionUpgradeEnhanced;
    public static Item hunterMinionUpgradeSpecial;
    public static Item hunterSpawnEgg;

    // Category 2: Hunter Weapons & Ammunition
    public static Item hunterAxe;
    public static Item pitchfork;
    public static Item quarrel;
    public static Item quarrelClip;
    public static Item quarrelPouch;
    public static Item crossbow;
    public static Item hunterHat;
    public static Item hunterCoatHead;
    public static Item hunterCoatChest;
    public static Item hunterCoatLegs;
    public static Item hunterCoatFeet;
    public static Item swiftnessHead;
    public static Item swiftnessChest;
    public static Item swiftnessLegs;
    public static Item swiftnessFeet;

    // Category 3: Vampire Progression, Refinements & Gear
    public static Item heartSeeker;
    public static Item heartStriker;
    public static Item vampireCrown;
    public static Item vampireHat;
    public static Item vampireLegs;
    public static Item vampireBoots;
    public static Item vampireCloak;
    public static Item umbrella;
    public static Item amulet;
    public static Item ring;
    public static Item obiBelt;
    public static Item fabricFilter;
    public static Item feedingAdapter;
    public static Item oilBottle;
    public static Item vampireMinionBinding;
    public static Item vampireMinionUpgradeSimple;
    public static Item vampireMinionUpgradeEnhanced;
    public static Item vampireMinionUpgradeSpecial;
    public static Item vampireSpawnEgg;

    public static void init() {
        ARMOR_HUNTER = EnumHelper.addArmorMaterial("NEORISM_HUNTER", 18, new int[]{2, 5, 4, 2}, 15);
        ARMOR_SWIFTNESS = EnumHelper.addArmorMaterial("NEORISM_SWIFTNESS", 22, new int[]{3, 6, 5, 3}, 20);
        ARMOR_VAMPIRE = EnumHelper.addArmorMaterial("NEORISM_VAMPIRE", 25, new int[]{3, 7, 5, 3}, 25);

        // Category 1: Materials & Collectibles (tabVampirism)
        vampireFang = register("vampire_fang", new ItemVampireFang());
        humanHeart = register("human_heart", new ItemHumanHeart(false));
        weakHumanHeart = register("weak_human_heart", new ItemHumanHeart(true));
        garlic = register("garlic", new ItemGarlic());
        purifiedGarlic = register("purified_garlic", new ItemNeorism("purified_garlic", ModCreativeTabs.tabHunter));
        garlicBread = register("garlic_bread", new ItemGarlicBread());
        vampireBook = register("vampire_book", new ItemVampireBook());
        soulOrbVampire = register("soul_orb_vampire", new ItemNeorism("soul_orb_vampire", ModCreativeTabs.tabVampirism));
        motherCore = register("mother_core", new ItemNeorism("mother_core", ModCreativeTabs.tabVampirism));
        caseFile144 = register("case_file_144", new ItemNeorism("case_file_144", ModCreativeTabs.tabVampirism));
        garlicFinder = register("garlic_finder", new ItemNeorism("garlic_finder", ModCreativeTabs.tabVampirism));
        oblivionPotion = register("potion_of_oblivion", new ItemOblivionPotion());
        ritualKnife = register("ritual_knife", new ItemRitualKnife());

        syringeEmpty = register("syringe_empty", new ItemSyringe("syringe_empty", ItemSyringe.Type.EMPTY));
        syringeBlood = register("syringe_blood", new ItemSyringe("syringe_blood", ItemSyringe.Type.BLOOD));
        serumInjection = register("serum_injection", new ItemSyringe("serum_injection", ItemSyringe.Type.SERUM));
        injectionSanguinare = register("injection_sanguinare", new ItemInjection("injection_sanguinare", ItemInjection.Type.SANGUINARE));
        injectionGarlic = register("injection_garlic", new ItemInjection("injection_garlic", ItemInjection.Type.GARLIC));

        darkSpruceBoat = register("dark_spruce_boat", new ItemModBoat("dark_spruce_boat", false, false));
        cursedSpruceBoat = register("cursed_spruce_boat", new ItemModBoat("cursed_spruce_boat", true, false));
        darkSpruceChestBoat = register("dark_spruce_chest_boat", new ItemModBoat("dark_spruce_chest_boat", false, true));
        cursedSpruceChestBoat = register("cursed_spruce_chest_boat", new ItemModBoat("cursed_spruce_chest_boat", true, true));

        itemTent = register("tent_item", new ItemTent(false));
        itemTentSpawner = register("tent_spawner_item", new ItemTent(true));

        darkSpruceDoorItem = register("item_dark_spruce_door", new ItemModDoor("dark_spruce_door", ModBlocks.darkSpruceDoor));
        cursedSpruceDoorItem = register("item_cursed_spruce_door", new ItemModDoor("cursed_spruce_door", ModBlocks.cursedSpruceDoor));
        darkSpruceSignItem = register("item_dark_spruce_sign", new ItemNeorism("dark_spruce_sign", ModCreativeTabs.tabVampirism));
        cursedSpruceSignItem = register("item_cursed_spruce_sign", new ItemNeorism("cursed_spruce_sign", ModCreativeTabs.tabVampirism));
        darkSpruceHangingSignItem = register("item_dark_spruce_hanging_sign", new ItemNeorism("dark_spruce_hanging_sign", ModCreativeTabs.tabVampirism));
        cursedSpruceHangingSignItem = register("item_cursed_spruce_hanging_sign", new ItemNeorism("cursed_spruce_hanging_sign", ModCreativeTabs.tabVampirism));

        // Category 2: Hunter & Holy Equipment (tabHunter)
        hunterIntel = register("hunter_intel", new ItemHunterIntel());
        holyWaterBottle = register("holy_water_bottle", new ItemHolyWater());
        holyWaterSplashBottle = register("holy_water_splash_bottle", new ItemHolyWaterSplash());
        stake = register("stake", new ItemStake());
        crucifix = register("crucifix", new ItemCrucifix());
        garlicDiffuserCore = register("garlic_diffuser_core", new ItemGarlicDiffuserCore());
        pureSalt = register("pure_salt", new ItemNeorism("pure_salt", ModCreativeTabs.tabHunter));
        pureSaltWater = register("pure_salt_water", new ItemPureSaltWater());
        alchemicalFire = register("alchemical_fire", new ItemAlchemicalFire());

        hunterMinionEquipment = register("hunter_minion_equipment", new ItemNeorism("hunter_minion_equipment", ModCreativeTabs.tabHunter));
        hunterMinionUpgradeSimple = register("hunter_minion_upgrade_simple", new ItemMinionUpgrade("hunter_minion_upgrade_simple", 1, 2, ModFactions.HUNTER));
        hunterMinionUpgradeEnhanced = register("hunter_minion_upgrade_enhanced", new ItemMinionUpgrade("hunter_minion_upgrade_enhanced", 3, 4, ModFactions.HUNTER));
        hunterMinionUpgradeSpecial = register("hunter_minion_upgrade_special", new ItemMinionUpgrade("hunter_minion_upgrade_special", 5, 6, ModFactions.HUNTER));
        hunterSpawnEgg = register("hunter_spawn_egg", new ItemFactionSpawnEgg(false));

        hunterAxe = register("hunter_axe", new ItemHunterAxe());
        pitchfork = register("pitchfork", new ItemPitchfork());
        quarrel = register("quarrel", new ItemQuarrel());
        quarrelClip = register("quarrel_clip", new ItemQuarrelClip());
        quarrelPouch = register("quarrel_pouch", new ItemQuarrelPouch());
        crossbow = register("crossbow", new ItemCrossbow());

        hunterHat = register("hunter_hat", new ItemHunterHat(ARMOR_HUNTER));
        hunterCoatHead = register("hunter_coat_head", new ItemHunterArmor(ARMOR_HUNTER, 0, "head"));
        hunterCoatChest = register("hunter_coat_chest", new ItemHunterArmor(ARMOR_HUNTER, 1, "chest"));
        hunterCoatLegs = register("hunter_coat_legs", new ItemHunterArmor(ARMOR_HUNTER, 2, "legs"));
        hunterCoatFeet = register("hunter_coat_feet", new ItemHunterArmor(ARMOR_HUNTER, 3, "feet"));

        swiftnessHead = register("armor_of_swiftness_head", new ItemSwiftnessArmor(ARMOR_SWIFTNESS, 0, "head"));
        swiftnessChest = register("armor_of_swiftness_chest", new ItemSwiftnessArmor(ARMOR_SWIFTNESS, 1, "chest"));
        swiftnessLegs = register("armor_of_swiftness_legs", new ItemSwiftnessArmor(ARMOR_SWIFTNESS, 2, "legs"));
        swiftnessFeet = register("armor_of_swiftness_feet", new ItemSwiftnessArmor(ARMOR_SWIFTNESS, 3, "feet"));

        // Category 3: Vampire Weapons, Armor, Refinements & Fluids (tabVampire)
        heartSeeker = register("heart_seeker", new ItemHeartSeeker());
        heartStriker = register("heart_striker", new ItemHeartStriker());
        vampireCrown = register("vampire_clothing_crown", new ItemVampireClothing(ARMOR_VAMPIRE, 0, "crown"));
        vampireHat = register("vampire_clothing_hat", new ItemVampireClothing(ARMOR_VAMPIRE, 0, "hat"));
        vampireLegs = register("vampire_clothing_legs", new ItemVampireClothing(ARMOR_VAMPIRE, 2, "legs"));
        vampireBoots = register("vampire_clothing_boots", new ItemVampireClothing(ARMOR_VAMPIRE, 3, "boots"));
        vampireCloak = register("vampire_cloak", new ItemVampireCloak(ARMOR_VAMPIRE));
        umbrella = register("umbrella", new ItemUmbrella());

        amulet = register("vampire_amulet", new ItemRefinement(ItemRefinement.Type.AMULET));
        ring = register("vampire_ring", new ItemRefinement(ItemRefinement.Type.RING));
        obiBelt = register("vampire_obi_belt", new ItemRefinement(ItemRefinement.Type.OBI_BELT));

        pureBlood = register("pure_blood", new ItemPureBlood());
        bloodBottle = register("blood_bottle", new ItemBloodBottle());
        bloodFlask = register("blood_flask", new ItemBloodFlask());
        sovereignBlood = register("sovereign_blood", new ItemNeorism("sovereign_blood", ModCreativeTabs.tabVampire));
        vampireBloodBottle = register("vampire_blood_bottle", new ItemNeorism("vampire_blood_bottle", ModCreativeTabs.tabVampire));

        bloodInfusedIron = register("blood_infused_iron_ingot", new ItemNeorism("blood_infused_iron_ingot", ModCreativeTabs.tabVampire));
        bloodInfusedGold = register("blood_infused_gold_ingot", new ItemNeorism("blood_infused_gold_ingot", ModCreativeTabs.tabVampire));
        bloodInfusedDiamond = register("blood_infused_diamond", new ItemNeorism("blood_infused_diamond", ModCreativeTabs.tabVampire));
        bloodInfusedNetherite = register("blood_infused_netherite_ingot", new ItemNeorism("blood_infused_netherite_ingot", ModCreativeTabs.tabVampire));
        bloodInfusedRawIron = register("blood_infused_raw_iron", new ItemNeorism("blood_infused_raw_iron", ModCreativeTabs.tabVampire));
        bloodInfusedRawGold = register("blood_infused_raw_gold", new ItemNeorism("blood_infused_raw_gold", ModCreativeTabs.tabVampire));

        vampireMinionBinding = register("vampire_minion_binding", new ItemNeorism("vampire_minion_binding", ModCreativeTabs.tabVampire));
        vampireMinionUpgradeSimple = register("vampire_minion_upgrade_simple", new ItemMinionUpgrade("vampire_minion_upgrade_simple", 1, 2, ModFactions.VAMPIRE));
        vampireMinionUpgradeEnhanced = register("vampire_minion_upgrade_enhanced", new ItemMinionUpgrade("vampire_minion_upgrade_enhanced", 3, 4, ModFactions.VAMPIRE));
        vampireMinionUpgradeSpecial = register("vampire_minion_upgrade_special", new ItemMinionUpgrade("vampire_minion_upgrade_special", 5, 6, ModFactions.VAMPIRE));

        fabricFilter = register("fabric_filter", new ItemFabricFilter());
        feedingAdapter = register("feeding_adapter", new ItemFeedingAdapter());
        oilBottle = register("oil_bottle", new ItemOilBottle());
        vampireSpawnEgg = register("vampire_spawn_egg", new ItemFactionSpawnEgg(true));

        ModLogger.debug("Initialized full Phase 7 Neorism arsenal and equipment");
    }

    public static <T extends Item> T register(String name, T item) {
        GameRegistry.registerItem(item, name);
        return item;
    }
}
