package com.marrybye.neorism.common.core;

import com.marrybye.neorism.common.items.ItemHunterAxe;
import com.marrybye.neorism.common.items.ItemOilBottle;
import com.marrybye.neorism.common.items.ItemQuarrelClip;
import com.marrybye.neorism.common.items.ItemVampireBook;
import com.marrybye.neorism.common.items.ItemVampireCloak;
import com.marrybye.neorism.common.items.ItemVampireSword;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Canonical crafting, smelting, and OreDictionary recipes for Neorism.
 */
public class ModRecipes {

    private static final int WILD = OreDictionary.WILDCARD_VALUE;

    public static void init() {
        registerOreDictionary();

        registerCombatAndTools();
        registerArmorAndCloaks();
        registerAlchemyAndConsumables();
        registerBloodMetalsAndBlocks();
        registerWoodSets();
        registerStoneSets();
        registerFunctionalAndDecorBlocks();
        registerCustomRecipes();

        ModLogger.debug("Registered canonical Neorism crafting and smelting recipes");
    }

    private static void registerOreDictionary() {
        OreDictionary.registerOre("plankWood", ModBlocks.darkSprucePlanks);
        OreDictionary.registerOre("plankWood", ModBlocks.cursedSprucePlanks);
        OreDictionary.registerOre("logWood", ModBlocks.darkSpruceLog);
        OreDictionary.registerOre("logWood", ModBlocks.cursedSpruceLog);
        OreDictionary.registerOre("logWood", ModBlocks.darkSpruceWood);
        OreDictionary.registerOre("logWood", ModBlocks.cursedSpruceWood);
        OreDictionary.registerOre("logWood", ModBlocks.strippedDarkSpruceLog);
        OreDictionary.registerOre("logWood", ModBlocks.strippedCursedSpruceLog);
        OreDictionary.registerOre("treeLeaves", ModBlocks.darkSpruceLeaves);
        OreDictionary.registerOre("treeSapling", ModBlocks.darkSpruceSapling);
        OreDictionary.registerOre("treeSapling", ModBlocks.cursedSpruceSapling);
        OreDictionary.registerOre("slabWood", ModBlocks.darkSpruceSlab);
        OreDictionary.registerOre("slabWood", ModBlocks.cursedSpruceSlab);
        OreDictionary.registerOre("stairWood", ModBlocks.darkSpruceStairs);
        OreDictionary.registerOre("stairWood", ModBlocks.cursedSpruceStairs);
        OreDictionary.registerOre("cropGarlic", ModItems.garlic);
        OreDictionary.registerOre("cobblestone", ModBlocks.cobbledDarkStone);
        OreDictionary.registerOre("stone", ModBlocks.darkStone);
    }

    private static void registerCombatAndTools() {
        // Wooden Stake (canonical: stick / plank / stick)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.stake, 1),
            "S",
            "P",
            "S",
            'S', "stickWood",
            'P', "plankWood"
        ));

        // Crucifixes (0=Normal, 1=Enhanced, 2=Ultimate)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.crucifix, 1, 0),
            "HP ",
            "SPS",
            " P ",
            'H', new ItemStack(ModItems.holyWaterBottle, 1, 0),
            'P', "plankWood",
            'S', "stickWood"
        ));
        GameRegistry.addRecipe(new ItemStack(ModItems.crucifix, 1, 1),
            " I ",
            "ICI",
            " H ",
            'I', Items.iron_ingot,
            'C', new ItemStack(ModItems.crucifix, 1, 0),
            'H', new ItemStack(ModItems.holyWaterBottle, 1, 0)
        );
        GameRegistry.addRecipe(new ItemStack(ModItems.crucifix, 1, 2),
            " G ",
            "GCG",
            " F ",
            'G', Blocks.gold_block,
            'C', new ItemStack(ModItems.crucifix, 1, 1),
            'F', ModItems.alchemicalFire
        );

        // Hunter Axe (0=Normal, 1=Enhanced, 2=Ultimate; 1.7.10 3x3 weapon_table adaptation)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.hunterAxe, 1, 0),
            "II ",
            "ISG",
            " S ",
            'I', Items.iron_ingot,
            'S', "stickWood",
            'G', "cropGarlic"
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.hunterAxe, 1, 1),
            "DD ",
            "DAG",
            " S ",
            'D', Items.diamond,
            'A', new ItemStack(ModItems.hunterAxe, 1, 0),
            'G', "cropGarlic",
            'S', "stickWood"
        ));
        GameRegistry.addRecipe(new ItemStack(ModItems.hunterAxe, 1, 2),
            "OO ",
            "DAG",
            " D ",
            'O', Blocks.obsidian,
            'D', Items.diamond,
            'A', new ItemStack(ModItems.hunterAxe, 1, 1),
            'G', ModItems.purifiedGarlic
        );

        // Pitchfork
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.pitchfork, 1),
            "I I",
            "SSS",
            " S ",
            'I', Items.iron_ingot,
            'S', "stickWood"
        ));

        // Heart Seeker (0=Normal, 1=Enhanced, 2=Ultimate)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.heartSeeker, 1, 0),
            "X",
            "X",
            "S",
            'X', ModItems.bloodInfusedIron,
            'S', "stickWood"
        ));
        GameRegistry.addRecipe(new ItemStack(ModItems.heartSeeker, 1, 1),
            " D ",
            "DWD",
            " S ",
            'D', ModItems.bloodInfusedDiamond,
            'W', new ItemStack(ModItems.heartSeeker, 1, 0),
            'S', Items.stick
        );
        GameRegistry.addRecipe(new ItemStack(ModItems.heartSeeker, 1, 2),
            " N ",
            "NWN",
            " S ",
            'N', ModItems.bloodInfusedNetherite,
            'W', new ItemStack(ModItems.heartSeeker, 1, 1),
            'S', Items.stick
        );

        // Heart Striker (0=Normal, 1=Enhanced, 2=Ultimate)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.heartStriker, 1, 0),
            "XX",
            "XX",
            "SS",
            'X', ModItems.bloodInfusedIron,
            'S', "stickWood"
        ));
        GameRegistry.addRecipe(new ItemStack(ModItems.heartStriker, 1, 1),
            "DD",
            "DW",
            "SS",
            'D', ModItems.bloodInfusedDiamond,
            'W', new ItemStack(ModItems.heartStriker, 1, 0),
            'S', Items.stick
        );
        GameRegistry.addRecipe(new ItemStack(ModItems.heartStriker, 1, 2),
            "NN",
            "NW",
            "SS",
            'N', ModItems.bloodInfusedNetherite,
            'W', new ItemStack(ModItems.heartStriker, 1, 1),
            'S', Items.stick
        );

        // Crossbows: 0=basic, 1=basic_double, 2=enhanced, 3=enhanced_double, 4=basic_tech, 5=enhanced_tech
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.crossbow, 1, 0),
            "SIS",
            "IPI",
            " P ",
            'S', Items.string,
            'I', Items.iron_ingot,
            'P', "plankWood"
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.crossbow, 1, 1),
            "SIS",
            "SCS",
            " P ",
            'S', Items.string,
            'I', Items.iron_ingot,
            'C', new ItemStack(ModItems.crossbow, 1, 0),
            'P', "plankWood"
        ));
        GameRegistry.addRecipe(new ItemStack(ModItems.crossbow, 1, 2),
            " G ",
            "ICI",
            " D ",
            'G', Items.gold_ingot,
            'I', Items.iron_ingot,
            'C', new ItemStack(ModItems.crossbow, 1, 0),
            'D', Items.diamond
        );
        GameRegistry.addRecipe(new ItemStack(ModItems.crossbow, 1, 3),
            " S ",
            "GCG",
            " D ",
            'S', Items.string,
            'G', Items.gold_ingot,
            'C', new ItemStack(ModItems.crossbow, 1, 1),
            'D', Items.diamond
        );
        GameRegistry.addRecipe(new ItemStack(ModItems.crossbow, 1, 4),
            "RDR",
            "ICI",
            " T ",
            'R', Items.redstone,
            'D', Items.diamond,
            'I', Items.iron_ingot,
            'C', new ItemStack(ModItems.crossbow, 1, 0),
            'T', Blocks.tripwire_hook
        );
        GameRegistry.addRecipe(new ItemStack(ModItems.crossbow, 1, 5),
            "RDR",
            "ECE",
            " D ",
            'R', Items.redstone,
            'D', Items.diamond,
            'E', Items.repeater,
            'C', new ItemStack(ModItems.crossbow, 1, 4)
        );

        // Quarrels (0=normal, 1=heavy, 2=spitfire, 3=garlic, 4=vampire_killer, 5=teleport, 6=bleeding)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.quarrel, 6, 0),
            "I",
            "S",
            'I', Items.iron_ingot,
            'S', "stickWood"
        ));
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.quarrel, 1, 0), Items.arrow);

        // Heavy quarrel: 6x normal quarrel + iron ingot -> 6x heavy (individual args in 1.7.10!)
        ItemStack qNormal = new ItemStack(ModItems.quarrel, 1, 0);
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.quarrel, 6, 1),
            qNormal, qNormal, qNormal, qNormal, qNormal, qNormal, Items.iron_ingot
        );

        // Oiled quarrels (1..3 per oil bottle; teleport is 1:1)
        registerOiledQuarrelRecipes(2, 22, 3); // spitfire
        registerOiledQuarrelRecipes(3, 21, 3); // garlic
        registerOiledQuarrelRecipes(4, 23, 3); // vampire_killer
        registerOiledQuarrelRecipes(5, 19, 1); // teleport (1 only)
        registerOiledQuarrelRecipes(6, 24, 3); // bleeding

        // Quarrel Clips (0=normal, 1=heavy) & Quarrel Pouch
        ItemStack emptyStandardClip = new ItemStack(ModItems.quarrelClip, 1, 0);
        ItemQuarrelClip.setAmmoCount(emptyStandardClip, 0);
        GameRegistry.addRecipe(new ShapedOreRecipe(emptyStandardClip,
            "NIN",
            "NSN",
            " N ",
            'N', Items.iron_ingot,
            'I', Items.iron_ingot,
            'S', "stickWood"
        ));
        // Quarrel-loaded clip variants (8x quarrel -> 8 ammo, exact conservation; refill via RecipeFillQuarrelClip)
        ItemStack loadedStandardClip = new ItemStack(ModItems.quarrelClip, 1, 0);
        ItemQuarrelClip.setAmmoCount(loadedStandardClip, 8);
        GameRegistry.addRecipe(new ShapedOreRecipe(loadedStandardClip,
            "QQQ",
            "QIQ",
            "QQQ",
            'Q', new ItemStack(ModItems.quarrel, 1, 0),
            'I', Items.iron_ingot
        ));
        ItemStack loadedHeavyClip = new ItemStack(ModItems.quarrelClip, 1, 1);
        ItemQuarrelClip.setAmmoCount(loadedHeavyClip, 8);
        GameRegistry.addRecipe(new ShapedOreRecipe(loadedHeavyClip,
            "QQQ",
            "QIQ",
            "QQQ",
            'Q', new ItemStack(ModItems.quarrel, 1, 1),
            'I', Items.iron_ingot
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.quarrelPouch, 1),
            "ILI",
            "PLP",
            "ILI",
            'I', Items.iron_ingot,
            'L', Items.leather,
            'P', "plankWood"
        ));

        // Ritual Knife (0=uncharged, 1=charged with Mother Core)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.ritualKnife, 1, 0),
            "I",
            "S",
            'I', Items.iron_ingot,
            'S', "stickWood"
        ));
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.ritualKnife, 1, 1),
            new ItemStack(ModItems.ritualKnife, 1, 0),
            ModItems.motherCore
        );

        // Umbrella
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.umbrella, 1),
            "WWW",
            "OSO",
            " S ",
            'W', new ItemStack(Blocks.wool, 1, WILD),
            'O', ModBlocks.vampireOrchid,
            'S', "stickWood"
        ));

        // Finders
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.garlicFinder),
            "III",
            "IGI",
            "PRP",
            'I', ModItems.bloodInfusedIron,
            'G', "cropGarlic",
            'P', "plankWood",
            'R', Items.redstone
        ));
        GameRegistry.addRecipe(new ItemStack(ModItems.bloodFinder),
            "III",
            "IBI",
            "ICI",
            'I', ModItems.bloodInfusedIron,
            'B', new ItemStack(ModItems.bloodBottle, 1, WILD),
            'C', Items.compass
        );
    }

    private static void registerOiledQuarrelRecipes(int quarrelMeta, int oilMeta, int maxCount) {
        ItemStack q = new ItemStack(ModItems.quarrel, 1, 0);
        ItemStack oil = new ItemStack(ModItems.oilBottle, 1, oilMeta);
        if (maxCount >= 1) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.quarrel, 1, quarrelMeta), q, oil);
        }
        if (maxCount >= 2) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.quarrel, 2, quarrelMeta), q, q, oil);
        }
        if (maxCount >= 3) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.quarrel, 3, quarrelMeta), q, q, q, oil);
        }
    }

    private static void registerArmorAndCloaks() {
        // Hunter Hats (0=broad, 1=tall)
        ItemStack blackWool = new ItemStack(Blocks.wool, 1, 15);
        GameRegistry.addRecipe(new ItemStack(ModItems.hunterHat, 1, 0),
            " WW",
            "III",
            'W', blackWool,
            'I', Items.iron_ingot
        );
        GameRegistry.addRecipe(new ItemStack(ModItems.hunterHat, 1, 1),
            "WW ",
            "WW ",
            "III",
            'W', blackWool,
            'I', Items.iron_ingot
        );

        // Hunter Coat (0=normal, 1=enhanced, 2=ultimate)
        // Tier 0
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.hunterCoatHead, 1, 0),
            "LIL",
            "IGI",
            'L', Items.leather,
            'I', Items.iron_ingot,
            'G', "cropGarlic"
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.hunterCoatChest, 1, 0),
            "F F",
            "IGI",
            "ILI",
            'F', ModItems.vampireFang,
            'I', Items.iron_ingot,
            'G', "cropGarlic",
            'L', Items.leather
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.hunterCoatLegs, 1, 0),
            "LIL",
            "IGI",
            "I I",
            'L', Items.leather,
            'I', Items.iron_ingot,
            'G', "cropGarlic"
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.hunterCoatFeet, 1, 0),
            "I I",
            "IGI",
            "L L",
            'I', Items.iron_ingot,
            'G', "cropGarlic",
            'L', Items.leather
        ));
        // Tier 1 (Enhanced)
        registerTieredArmorUpgrade(ModItems.hunterCoatHead, 0, 1, Items.diamond, ModItems.garlic);
        registerTieredArmorUpgrade(ModItems.hunterCoatChest, 0, 1, Items.diamond, ModItems.garlic);
        registerTieredArmorUpgrade(ModItems.hunterCoatLegs, 0, 1, Items.diamond, ModItems.garlic);
        registerTieredArmorUpgrade(ModItems.hunterCoatFeet, 0, 1, Items.diamond, ModItems.garlic);
        // Tier 2 (Ultimate - 1.7.10 obsidian + diamond + purified garlic)
        registerTieredArmorUpgrade(ModItems.hunterCoatHead, 1, 2, Blocks.obsidian, ModItems.purifiedGarlic);
        registerTieredArmorUpgrade(ModItems.hunterCoatChest, 1, 2, Blocks.obsidian, ModItems.purifiedGarlic);
        registerTieredArmorUpgrade(ModItems.hunterCoatLegs, 1, 2, Blocks.obsidian, ModItems.purifiedGarlic);
        registerTieredArmorUpgrade(ModItems.hunterCoatFeet, 1, 2, Blocks.obsidian, ModItems.purifiedGarlic);

        // Armor of Swiftness (0=normal, 1=enhanced, 2=ultimate)
        ItemStack swiftPotion = new ItemStack(Items.potionitem, 1, 8194);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.swiftnessHead, 1, 0),
            "LLL",
            "LGL",
            " P ",
            'L', Items.leather,
            'G', "cropGarlic",
            'P', swiftPotion
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.swiftnessChest, 1, 0),
            "LPL",
            "LLL",
            "LGL",
            'L', Items.leather,
            'G', "cropGarlic",
            'P', swiftPotion
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.swiftnessLegs, 1, 0),
            "LLL",
            "LGL",
            "P P",
            'L', Items.leather,
            'G', "cropGarlic",
            'P', swiftPotion
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.swiftnessFeet, 1, 0),
            "LPL",
            "LGL",
            "L L",
            'L', Items.leather,
            'G', "cropGarlic",
            'P', swiftPotion
        ));
        registerTieredArmorUpgrade(ModItems.swiftnessHead, 0, 1, Items.gold_ingot, ModItems.garlic);
        registerTieredArmorUpgrade(ModItems.swiftnessChest, 0, 1, Items.gold_ingot, ModItems.garlic);
        registerTieredArmorUpgrade(ModItems.swiftnessLegs, 0, 1, Items.gold_ingot, ModItems.garlic);
        registerTieredArmorUpgrade(ModItems.swiftnessFeet, 0, 1, Items.gold_ingot, ModItems.garlic);
        registerTieredArmorUpgrade(ModItems.swiftnessHead, 1, 2, Items.diamond, ModItems.purifiedGarlic);
        registerTieredArmorUpgrade(ModItems.swiftnessChest, 1, 2, Items.diamond, ModItems.purifiedGarlic);
        registerTieredArmorUpgrade(ModItems.swiftnessLegs, 1, 2, Items.diamond, ModItems.purifiedGarlic);
        registerTieredArmorUpgrade(ModItems.swiftnessFeet, 1, 2, Items.diamond, ModItems.purifiedGarlic);

        // Vampire Noble Clothing (crown, hat, legs, boots) - accepts humanHeart or weakHumanHeart
        ItemStack redWool = new ItemStack(Blocks.wool, 1, 14);
        ItemStack grayWool = new ItemStack(Blocks.wool, 1, 7);
        ItemStack brownWool = new ItemStack(Blocks.wool, 1, 12);
        Item[] hearts = { ModItems.humanHeart, ModItems.weakHumanHeart };
        for (Item heart : hearts) {
            GameRegistry.addRecipe(new ItemStack(ModItems.vampireCrown, 1),
                "GHG",
                "GGG",
                'G', Items.gold_ingot,
                'H', heart
            );
            GameRegistry.addRecipe(new ItemStack(ModItems.vampireHat, 1),
                "RBB",
                " H ",
                "BBB",
                'R', redWool,
                'B', blackWool,
                'H', heart
            );
            GameRegistry.addRecipe(new ItemStack(ModItems.vampireLegs, 1),
                "WWW",
                "W W",
                "WHW",
                'W', grayWool,
                'H', heart
            );
            GameRegistry.addRecipe(new ItemStack(ModItems.vampireBoots, 1),
                "WHW",
                "W W",
                'W', brownWool,
                'H', heart
            );
        }

        // Vampire Cloaks (all 16 dye colors + re-dyeing)
        for (int cloakMeta = 0; cloakMeta < ItemVampireCloak.COLORS.length; cloakMeta++) {
            int woolMeta = 15 - cloakMeta;
            ItemStack wool = new ItemStack(Blocks.wool, 1, woolMeta);
            ItemStack cloak = new ItemStack(ModItems.vampireCloak, 1, cloakMeta);

            GameRegistry.addRecipe(cloak,
                "WDW",
                "WPW",
                "W W",
                'W', wool,
                'D', Items.diamond,
                'P', new ItemStack(ModItems.pureBlood, 1, WILD)
            );

            // Re-dye any cloak to this color
            GameRegistry.addShapelessRecipe(cloak,
                new ItemStack(ModItems.vampireCloak, 1, WILD),
                new ItemStack(Items.dye, 1, cloakMeta)
            );
        }

        // Recycling accessories -> gold nugget / leather
        GameRegistry.addSmelting(ModItems.amulet, new ItemStack(Items.gold_nugget, 1), 0.1F);
        GameRegistry.addSmelting(ModItems.ring, new ItemStack(Items.gold_nugget, 1), 0.1F);
        GameRegistry.addShapelessRecipe(new ItemStack(Items.leather, 1), ModItems.obiBelt);
    }

    private static void registerTieredArmorUpgrade(Item armor, int fromTier, int toTier, Object material, Item extra) {
        GameRegistry.addRecipe(new ItemStack(armor, 1, toTier),
            " M ",
            "MAM",
            " E ",
            'M', material,
            'A', new ItemStack(armor, 1, fromTier),
            'E', extra
        );
    }

    private static void registerAlchemyAndConsumables() {
        // Holy Water crafting & dilution
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 0),
            new ItemStack(Items.potionitem, 1, 0),
            Items.gold_nugget,
            Items.glowstone_dust
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 1),
            new ItemStack(ModItems.holyWaterBottle, 1, 0),
            Items.gold_ingot,
            Items.glowstone_dust
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 2),
            new ItemStack(ModItems.holyWaterBottle, 1, 1),
            Items.diamond,
            Items.ghast_tear
        );

        // Pure Salt Water + canonical Holy Water dilution
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.pureSaltWater, 1),
            new ItemStack(Items.potionitem, 1, 0),
            ModItems.pureSalt
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.holyWaterBottle, 2, 0),
            new ItemStack(ModItems.holyWaterBottle, 1, 1),
            ModItems.pureSaltWater
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.holyWaterBottle, 2, 1),
            new ItemStack(ModItems.holyWaterBottle, 1, 2),
            ModItems.pureSaltWater
        );

        // Splash Holy Water Bottles (+ Gunpowder)
        for (int tier = 0; tier <= 2; tier++) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.holyWaterSplashBottle, 1, tier),
                new ItemStack(ModItems.holyWaterBottle, 1, tier),
                Items.gunpowder
            );
        }

        // Garlic Diffuser Cores (crafting fallback alongside cauldron)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.garlicDiffuserCore, 1, 0),
            " W ",
            "WGW",
            " W ",
            'W', new ItemStack(Blocks.wool, 1, WILD),
            'G', "cropGarlic"
        ));
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.garlicDiffuserCore, 1, 1),
            new ItemStack(ModItems.garlicDiffuserCore, 1, 0),
            ModItems.purifiedGarlic
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.garlicDiffuserCore, 1, 2),
            new ItemStack(ModItems.garlicDiffuserCore, 1, 0),
            new ItemStack(ModItems.holyWaterBottle, 1, 2)
        );

        // Garlic Bread
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.garlicBread, 1),
            Items.bread,
            "cropGarlic"
        ));

        // Pure Blood downgrade (pure_blood_(n+1) + vampire_blood_bottle -> pure_blood_n)
        for (int tier = 0; tier < 4; tier++) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.pureBlood, 1, tier),
                new ItemStack(ModItems.pureBlood, 1, tier + 1),
                ModItems.vampireBloodBottle
            );
        }

        // Blood Bottle <-> Glass Bottle
        GameRegistry.addRecipe(new ItemStack(ModItems.bloodBottle, 1, 0),
            "GRG",
            " G ",
            'G', Blocks.glass,
            'R', Items.rotten_flesh
        );
        GameRegistry.addShapelessRecipe(new ItemStack(Items.glass_bottle, 1),
            new ItemStack(ModItems.bloodBottle, 1, WILD)
        );

        // Syringes, Serum & Injections
        GameRegistry.addRecipe(new ItemStack(ModItems.syringeEmpty, 1),
            "I",
            "G",
            "I",
            'I', Items.iron_ingot,
            'G', Blocks.glass
        );
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.injectionGarlic, 1),
            ModItems.syringeEmpty,
            "cropGarlic"
        ));
        ItemStack fang = new ItemStack(ModItems.vampireFang);
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.injectionSanguinare, 1),
            ModItems.syringeEmpty, fang, fang, fang, fang, fang, fang, fang, fang
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.serumInjection, 4),
            new ItemStack(Items.potionitem, 1, WILD),
            ModItems.syringeEmpty, ModItems.syringeEmpty, ModItems.syringeEmpty, ModItems.syringeEmpty
        );

        // Fill Blood Bottle from Blood Syringes (glass_bottle + 1..5 syringes, or bloodBottle(n) + syringe -> bloodBottle(n+1))
        for (int count = 1; count <= 5; count++) {
            Object[] inputs = new Object[count + 1];
            inputs[0] = Items.glass_bottle;
            for (int k = 1; k <= count; k++) {
                inputs[k] = ModItems.syringeBlood;
            }
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodBottle, 1, count), inputs);
        }
        for (int blood = 0; blood < 5; blood++) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodBottle, 1, blood + 1),
                new ItemStack(ModItems.bloodBottle, 1, blood),
                ModItems.syringeBlood
            );
        }

        // Fabric Filter & Hunter Tent
        GameRegistry.addRecipe(new ItemStack(ModItems.fabricFilter, 1),
            "SWS",
            'S', Items.string,
            'W', new ItemStack(Blocks.wool, 1, WILD)
        );
        GameRegistry.addRecipe(new ItemStack(ModItems.itemTent, 1),
            " W ",
            "WBW",
            'W', new ItemStack(Blocks.wool, 1, WILD),
            'B', Items.bed
        );
    }

    private static void registerBloodMetalsAndBlocks() {
        // Blood Infused Iron / Gold / Diamond (canonical 3 ingots + pure blood -> 3 infused ingots)
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodInfusedIron, 3),
            Items.iron_ingot, Items.iron_ingot, Items.iron_ingot,
            new ItemStack(ModItems.pureBlood, 1, WILD)
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodInfusedGold, 3),
            Items.gold_ingot, Items.gold_ingot, Items.gold_ingot,
            new ItemStack(ModItems.pureBlood, 1, WILD)
        );
        ItemStack anyPureBlood = new ItemStack(ModItems.pureBlood, 1, WILD);
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodInfusedDiamond, 1),
            Items.diamond, anyPureBlood, anyPureBlood, anyPureBlood, anyPureBlood
        );

        // Raw blood-infused ores (1.7.10 ore adaptation) + smelting
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodInfusedRawIron, 1),
            Blocks.iron_ore, new ItemStack(ModItems.pureBlood, 1, WILD)
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodInfusedRawGold, 1),
            Blocks.gold_ore, new ItemStack(ModItems.pureBlood, 1, WILD)
        );
        GameRegistry.addSmelting(ModItems.bloodInfusedRawIron, new ItemStack(ModItems.bloodInfusedIron, 1), 0.7F);
        GameRegistry.addSmelting(ModItems.bloodInfusedRawGold, new ItemStack(ModItems.bloodInfusedGold, 1), 1.0F);

        // Blood Infused Netherite (1.7.10 obsidian + blood-infused gold + pure_blood_4/sovereign_blood)
        ItemStack bGold = new ItemStack(ModItems.bloodInfusedGold);
        ItemStack obs = new ItemStack(Blocks.obsidian);
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodInfusedNetherite, 1),
            obs, obs, obs, obs, bGold, bGold, bGold, bGold, new ItemStack(ModItems.pureBlood, 1, 4)
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodInfusedNetherite, 1),
            obs, obs, obs, obs, bGold, bGold, bGold, bGold, ModItems.sovereignBlood
        );

        // Blood Infused Iron Blocks (compact & unpack + enhanced/refined crafting fallback)
        GameRegistry.addRecipe(new ItemStack(ModBlocks.bloodInfusedIronBlock, 1),
            "###",
            "###",
            "###",
            '#', ModItems.bloodInfusedIron
        );
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.bloodInfusedIron, 9), ModBlocks.bloodInfusedIronBlock);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.bloodInfusedEnhancedIronBlock, 1),
            "###",
            "#P#",
            "###",
            '#', ModItems.bloodInfusedIron,
            'P', new ItemStack(ModItems.pureBlood, 1, 4)
        );
        GameRegistry.addRecipe(new ItemStack(ModBlocks.bloodInfusedRefinedIronBlock, 1),
            "###",
            "#S#",
            "###",
            '#', ModItems.bloodInfusedIron,
            'S', ModItems.sovereignBlood
        );
    }

    private static void registerWoodSets() {
        registerSingleWoodSet(
            ModBlocks.darkSpruceLog, ModBlocks.darkSpruceWood,
            ModBlocks.strippedDarkSpruceLog, ModBlocks.strippedDarkSpruceWood,
            ModBlocks.darkSprucePlanks, ModBlocks.darkSpruceStairs, ModBlocks.darkSpruceSlab,
            ModBlocks.darkSpruceFence, ModBlocks.darkSpruceFenceGate,
            ModItems.darkSpruceDoorItem, ModBlocks.darkSpruceTrapdoor,
            ModBlocks.darkSprucePressurePlate, ModBlocks.darkSpruceButton,
            ModItems.darkSpruceBoat, ModItems.darkSpruceChestBoat,
            ModItems.darkSpruceSignItem, ModItems.darkSpruceHangingSignItem
        );
        registerSingleWoodSet(
            ModBlocks.cursedSpruceLog, ModBlocks.cursedSpruceWood,
            ModBlocks.strippedCursedSpruceLog, ModBlocks.strippedCursedSpruceWood,
            ModBlocks.cursedSprucePlanks, ModBlocks.cursedSpruceStairs, ModBlocks.cursedSpruceSlab,
            ModBlocks.cursedSpruceFence, ModBlocks.cursedSpruceFenceGate,
            ModItems.cursedSpruceDoorItem, ModBlocks.cursedSpruceTrapdoor,
            ModBlocks.cursedSprucePressurePlate, ModBlocks.cursedSpruceButton,
            ModItems.cursedSpruceBoat, ModItems.cursedSpruceChestBoat,
            ModItems.cursedSpruceSignItem, ModItems.cursedSpruceHangingSignItem
        );
    }

    private static void registerSingleWoodSet(
            Block log, Block wood, Block strippedLog, Block strippedWood, Block planks,
            Block stairs, Block slab, Block fence, Block fenceGate,
            Item doorItem, Block trapdoor, Block pressurePlate, Block button,
            Item boat, Item chestBoat, Item sign, Item hangingSign) {

        // Planks from log / wood / stripped variants
        GameRegistry.addShapelessRecipe(new ItemStack(planks, 4), log);
        GameRegistry.addShapelessRecipe(new ItemStack(planks, 4), wood);
        GameRegistry.addShapelessRecipe(new ItemStack(planks, 4), strippedLog);
        GameRegistry.addShapelessRecipe(new ItemStack(planks, 4), strippedWood);

        // Wood & Stripped log/wood
        GameRegistry.addRecipe(new ItemStack(wood, 3), "##", "##", '#', log);
        GameRegistry.addShapelessRecipe(new ItemStack(strippedLog, 1), log, new ItemStack(Items.iron_axe, 1, WILD));
        GameRegistry.addShapelessRecipe(new ItemStack(strippedLog, 1), log, new ItemStack(Items.stone_axe, 1, WILD));
        GameRegistry.addRecipe(new ItemStack(strippedWood, 3), "##", "##", '#', strippedLog);

        // Building components
        GameRegistry.addRecipe(new ItemStack(stairs, 4), "#  ", "## ", "###", '#', planks);
        GameRegistry.addRecipe(new ItemStack(slab, 6), "###", '#', planks);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(fence, 3), "PSP", "PSP", 'P', planks, 'S', "stickWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(fenceGate, 1), "SPS", "SPS", 'P', planks, 'S', "stickWood"));
        GameRegistry.addRecipe(new ItemStack(doorItem, 3), "##", "##", "##", '#', planks);
        GameRegistry.addRecipe(new ItemStack(trapdoor, 2), "###", "###", '#', planks);
        GameRegistry.addRecipe(new ItemStack(pressurePlate, 1), "##", '#', planks);
        GameRegistry.addShapelessRecipe(new ItemStack(button, 1), planks);

        // Boats & Signs
        GameRegistry.addRecipe(new ItemStack(boat, 1), "# #", "###", '#', planks);
        GameRegistry.addShapelessRecipe(new ItemStack(chestBoat, 1), Blocks.chest, boat);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(sign, 3), "###", "###", " S ", '#', planks, 'S', "stickWood"));
        GameRegistry.addRecipe(new ItemStack(hangingSign, 6), "I I", "###", "###", 'I', Blocks.iron_bars, '#', strippedLog);
    }

    private static void registerStoneSets() {
        // Smelting
        GameRegistry.addSmelting(ModBlocks.cobbledDarkStone, new ItemStack(ModBlocks.darkStone, 1), 0.1F);
        GameRegistry.addSmelting(ModBlocks.darkStoneBricks, new ItemStack(ModBlocks.crackedDarkStoneBricks, 1), 0.1F);
        GameRegistry.addSmelting(ModBlocks.darkStoneTiles, new ItemStack(ModBlocks.crackedDarkStoneTiles, 1), 0.1F);

        // Stone variants
        GameRegistry.addRecipe(new ItemStack(ModBlocks.darkStoneBricks, 4), "##", "##", '#', ModBlocks.darkStone);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.polishedDarkStone, 4), "##", "##", '#', ModBlocks.cobbledDarkStone);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.darkStoneTiles, 4), "##", "##", '#', ModBlocks.polishedDarkStone);
        GameRegistry.addRecipe(new ItemStack(ModBlocks.chiseledDarkStoneBricks, 1), "#", "#", '#', ModBlocks.darkStoneBricksSlab);

        // Purple stone (8x dark bricks/tiles + vampire orchid)
        ItemStack dBrick = new ItemStack(ModBlocks.darkStoneBricks);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.purpleStoneBricks, 8),
            dBrick, dBrick, dBrick, dBrick, dBrick, dBrick, dBrick, dBrick, ModBlocks.vampireOrchid
        );
        ItemStack dTile = new ItemStack(ModBlocks.darkStoneTiles);
        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.purpleStoneTiles, 8),
            dTile, dTile, dTile, dTile, dTile, dTile, dTile, dTile, ModBlocks.vampireOrchid
        );
        GameRegistry.addRecipe(new ItemStack(ModBlocks.purpleStoneTiles, 4), "##", "##", '#', ModBlocks.purpleStoneBricks);

        // Stairs / Slabs / Walls
        registerStoneDecor(ModBlocks.darkStone, ModBlocks.darkStoneStairs, ModBlocks.darkStoneSlab, ModBlocks.darkStoneWall);
        registerStoneDecor(ModBlocks.darkStoneBricks, ModBlocks.darkStoneBricksStairs, ModBlocks.darkStoneBricksSlab, ModBlocks.darkStoneBricksWall);
        registerStoneDecor(ModBlocks.cobbledDarkStone, ModBlocks.cobbledDarkStoneStairs, ModBlocks.cobbledDarkStoneSlab, ModBlocks.cobbledDarkStoneWall);
        registerStoneDecor(ModBlocks.polishedDarkStone, ModBlocks.polishedDarkStoneStairs, ModBlocks.polishedDarkStoneSlab, ModBlocks.polishedDarkStoneWall);
        registerStoneDecor(ModBlocks.purpleStoneBricks, ModBlocks.purpleStoneBricksStairs, ModBlocks.purpleStoneBricksSlab, ModBlocks.purpleStoneBricksWall);
        registerStoneDecor(ModBlocks.purpleStoneTiles, ModBlocks.purpleStoneTilesStairs, ModBlocks.purpleStoneTilesSlab, ModBlocks.purpleStoneTilesWall);
    }

    private static void registerStoneDecor(Block base, Block stairs, Block slab, Block wall) {
        GameRegistry.addRecipe(new ItemStack(stairs, 4), "#  ", "## ", "###", '#', base);
        GameRegistry.addRecipe(new ItemStack(slab, 6), "###", '#', base);
        GameRegistry.addRecipe(new ItemStack(wall, 6), "###", "###", '#', base);
    }

    private static void registerFunctionalAndDecorBlocks() {
        // Workstations
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hunterTable),
            "FBG",
            "PPP",
            "P P",
            'F', ModItems.vampireFang,
            'B', Items.book,
            'G', "cropGarlic",
            'P', "plankWood"
        ));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.hunterWeaponTable),
            "B  ",
            "III",
            " M ",
            'B', Items.bucket,
            'I', Items.iron_ingot,
            'M', Blocks.iron_block
        );
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.alchemicalCauldron),
            "IGI",
            "III",
            "B B",
            'I', Items.iron_ingot,
            'G', "cropGarlic",
            'B', Blocks.stonebrick
        ));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.vaporStill),
            "MBG",
            "CIG",
            'M', Blocks.gold_block,
            'C', Items.gold_ingot,
            'B', Items.glass_bottle,
            'I', Items.iron_ingot,
            'G', Items.glass_bottle
        );
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.alchemyTable),
            "S  ",
            "SSS",
            "P P",
            'S', ModBlocks.darkStone,
            'P', "plankWood"
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.injectionChair),
            "PL ",
            "PWP",
            "P P",
            'P', "plankWood",
            'L', Items.leather,
            'W', new ItemStack(Blocks.wool, 1, WILD)
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.altarCleansing),
            " B ",
            "PPP",
            " P ",
            'B', new ItemStack(ModItems.vampireBook, 1, WILD),
            'P', "plankWood"
        ));

        // Garlic Diffusers
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.garlicDiffuser),
            "PDP",
            "DCD",
            "OOO",
            'P', "plankWood",
            'D', Items.diamond,
            'C', new ItemStack(ModItems.garlicDiffuserCore, 1, 0),
            'O', Blocks.obsidian
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.garlicDiffuserLong),
            "PDP",
            "DCD",
            "OOO",
            'P', "plankWood",
            'D', Items.diamond,
            'C', new ItemStack(ModItems.garlicDiffuserCore, 1, 1),
            'O', Blocks.obsidian
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.garlicDiffuserStrong),
            "PDP",
            "DCD",
            "OOO",
            'P', "plankWood",
            'D', Items.diamond,
            'C', new ItemStack(ModItems.garlicDiffuserCore, 1, 2),
            'O', Blocks.obsidian
        ));

        // Vampire Progression & Functional Blocks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.bloodContainer),
            "PGP",
            "GIG",
            "PGP",
            'P', "plankWood",
            'G', Blocks.glass,
            'I', Items.iron_ingot
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.altarInspiration),
            "P P",
            "PCP",
            "PPP",
            'P', "plankWood",
            'C', ModBlocks.bloodContainer
        ));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.altarInfusion),
            "GOG",
            "OOO",
            'G', Items.gold_ingot,
            'O', Blocks.obsidian
        );
        GameRegistry.addRecipe(new ItemStack(ModBlocks.altarPillar),
            "B B",
            "   ",
            "BBB",
            'B', Blocks.stonebrick
        );
        GameRegistry.addRecipe(new ItemStack(ModBlocks.altarTip),
            " I ",
            "IMI",
            'I', Items.iron_ingot,
            'M', Blocks.iron_block
        );
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.bloodPedestal),
            "GPG",
            "PBP",
            "OOO",
            'G', Items.gold_ingot,
            'P', "plankWood",
            'B', new ItemStack(ModItems.bloodBottle, 1, WILD),
            'O', Blocks.obsidian
        ));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.fogDiffuser),
            "PDP",
            "DMD",
            "OOO",
            'P', ModBlocks.cursedSprucePlanks,
            'D', Items.diamond,
            'M', ModItems.motherCore,
            'O', Blocks.obsidian
        );
        GameRegistry.addRecipe(new ItemStack(ModBlocks.chalice),
            "G G",
            "GGG",
            " G ",
            'G', Items.gold_ingot
        );
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.bloodGrinder),
            "PIP",
            "PSP",
            "PIP",
            'P', "plankWood",
            'I', Items.iron_ingot,
            'S', new ItemStack(Blocks.stone_slab, 1, 0)
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.bloodSieve),
            " I ",
            "PCP",
            " I ",
            'I', Items.iron_ingot,
            'P', "plankWood",
            'C', ModBlocks.bloodContainer
        ));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.infuser),
            "G G",
            "GGG",
            "OOO",
            'G', Items.gold_ingot,
            'O', Blocks.obsidian
        );
        GameRegistry.addRecipe(new ItemStack(ModBlocks.vampireBeacon),
            "GGG",
            "GCG",
            "OOO",
            'G', Blocks.glass,
            'C', ModItems.motherCore,
            'O', Blocks.obsidian
        );

        // Lighting & Gothic Decor
        GameRegistry.addRecipe(new ItemStack(ModBlocks.candlestick),
            " I ",
            "NNN",
            'I', Items.iron_ingot,
            'N', Items.gold_nugget
        );
        GameRegistry.addRecipe(new ItemStack(ModBlocks.candelabra),
            "III",
            "NIN",
            'I', Items.iron_ingot,
            'N', Items.gold_nugget
        );
        GameRegistry.addRecipe(new ItemStack(ModBlocks.chandelier),
            " B ",
            "ICI",
            'B', Blocks.iron_bars,
            'C', ModBlocks.candelabra,
            'I', Items.iron_ingot
        );
        GameRegistry.addRecipe(new ItemStack(ModBlocks.vampireSoulLantern),
            "GGG",
            "GSG",
            "GGG",
            'G', Items.gold_nugget,
            'S', ModItems.soulOrbVampire
        );
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.cross),
            " P ",
            "PHP",
            " P ",
            'P', "plankWood",
            'H', new ItemStack(ModItems.holyWaterBottle, 1, WILD)
        ));
        GameRegistry.addRecipe(new ItemStack(ModBlocks.tombstoneShort),
            "SS",
            "SS",
            'S', new ItemStack(Blocks.stone_slab, 1, 0)
        );
        GameRegistry.addRecipe(new ItemStack(ModBlocks.tombstoneMedium),
            "SS",
            "SS",
            "SS",
            'S', new ItemStack(Blocks.stone_slab, 1, 0)
        );
        GameRegistry.addRecipe(new ItemStack(ModBlocks.tombstoneCross),
            " S ",
            "SSS",
            " S ",
            'S', new ItemStack(Blocks.stone_slab, 1, 0)
        );
        GameRegistry.addRecipe(new ItemStack(ModBlocks.graveCage),
            " I ",
            "ICI",
            "ICI",
            'I', Items.iron_ingot,
            'C', new ItemStack(Items.coal, 1, WILD)
        );
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.vampireRack),
            "PBP",
            "FGS",
            "PBP",
            'P', "plankWood",
            'B', Items.book,
            'F', ModItems.vampireFang,
            'G', Items.glass_bottle,
            'S', Items.sugar
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.throne),
            " WP",
            "PWP",
            "S S",
            'W', new ItemStack(Blocks.wool, 1, WILD),
            'P', "plankWood",
            'S', "stickWood"
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.batCage),
            "GGG",
            "GPG",
            "PPP",
            'G', Items.gold_ingot,
            'P', "plankWood"
        ));

        // Coffins (all 16 colors + recoloring from white coffin)
        registerSingleCoffin(ModBlocks.coffin, 14); // red
        for (int i = 0; i < ModBlocks.coffins.length; i++) {
            int woolMeta = i < 14 ? i : 15; // coffins skips 14 (red)
            registerSingleCoffin(ModBlocks.coffins[i], woolMeta);
        }
    }

    private static void registerSingleCoffin(Block coffinBlock, int woolMeta) {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(coffinBlock, 1),
            "PPP",
            "WWW",
            "PPP",
            'P', "plankWood",
            'W', new ItemStack(Blocks.wool, 1, woolMeta)
        ));
        if (ModBlocks.coffins != null && ModBlocks.coffins.length > 0 && coffinBlock != ModBlocks.coffins[0]) {
            int dyeMeta = 15 - woolMeta;
            GameRegistry.addShapelessRecipe(new ItemStack(coffinBlock, 1),
                ModBlocks.coffins[0],
                new ItemStack(Items.dye, 1, dyeMeta)
            );
        }
    }

    private static void registerCustomRecipes() {
        RecipeSorter.register("neorism:applicable_oil", RecipeApplicableOil.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.register("neorism:clean_oil", RecipeCleanOil.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.register("neorism:reroll_vampire_book", RecipeRerollVampireBook.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.register("neorism:fill_quarrel_clip", RecipeFillQuarrelClip.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

        GameRegistry.addRecipe(new RecipeApplicableOil());
        GameRegistry.addRecipe(new RecipeCleanOil());
        GameRegistry.addRecipe(new RecipeRerollVampireBook());
        GameRegistry.addRecipe(new RecipeFillQuarrelClip());
    }

    /**
     * Canonical ApplicableOil recipe: valid hunter tool/weapon/armor + Oil Bottle -> oiled gear.
     */
    public static class RecipeApplicableOil implements IRecipe {
        @Override
        public boolean matches(InventoryCrafting inv, World world) {
            return getCraftingResult(inv) != null;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            ItemStack oil = null;
            ItemStack target = null;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack s = inv.getStackInSlot(i);
                if (s == null) continue;
                if (s.getItem() instanceof ItemOilBottle) {
                    if (oil != null) return null;
                    oil = s;
                } else {
                    if (target != null) return null;
                    target = s;
                }
            }
            if (oil == null || target == null) return null;
            if (!ItemOilBottle.canApplyOil(oil.getItemDamage(), target)) return null;
            ItemStack out = target.copy();
            out.stackSize = 1;
            ItemOilBottle.applyOil(out, oil.getItemDamage());
            return out;
        }

        @Override
        public int getRecipeSize() {
            return 2;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return null;
        }
    }

    /**
     * Canonical CleanOil recipe: oiled weapon + paper -> cleaned weapon.
     */
    public static class RecipeCleanOil implements IRecipe {
        @Override
        public boolean matches(InventoryCrafting inv, World world) {
            return getCraftingResult(inv) != null;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            ItemStack paper = null;
            ItemStack weapon = null;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack s = inv.getStackInSlot(i);
                if (s == null) continue;
                if (s.getItem() == Items.paper) {
                    if (paper != null) return null;
                    paper = s;
                } else {
                    String oil = ItemOilBottle.getAppliedOilType(s);
                    if (oil != null && !oil.isEmpty() && !"empty".equals(oil)) {
                        if (weapon != null) return null;
                        weapon = s;
                    } else {
                        return null;
                    }
                }
            }
            if (paper == null || weapon == null) return null;
            ItemStack out = weapon.copy();
            out.stackSize = 1;
            if (out.hasTagCompound()) {
                out.getTagCompound().removeTag("appliedOil");
                out.getTagCompound().removeTag("oilCharges");
                if (out.getTagCompound().hasNoTags()) {
                    out.setTagCompound(null);
                }
            }
            return out;
        }

        @Override
        public int getRecipeSize() {
            return 2;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return null;
        }
    }

    /**
     * Canonical RerollVampireBook recipe: 1x Vampire Book -> Vampire Book of another tier.
     * Deterministic output based on current tier and crafting grid slot so NEI/crafting results match cleanly.
     */
    public static class RecipeRerollVampireBook implements IRecipe {

        @Override
        public boolean matches(InventoryCrafting inv, World world) {
            ItemStack book = null;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack s = inv.getStackInSlot(i);
                if (s == null) continue;
                if (s.getItem() == ModItems.vampireBook) {
                    if (book != null) return false;
                    book = s;
                } else {
                    return false;
                }
            }
            return book != null;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            int currentTier = 0;
            int slotIdx = 0;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack s = inv.getStackInSlot(i);
                if (s != null && s.getItem() == ModItems.vampireBook) {
                    currentTier = s.getItemDamage();
                    slotIdx = i;
                    break;
                }
            }
            int nextTier = (currentTier + 1 + (slotIdx % (ItemVampireBook.TIERS - 1))) % ItemVampireBook.TIERS;
            return new ItemStack(ModItems.vampireBook, 1, nextTier);
        }

        @Override
        public int getRecipeSize() {
            return 1;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return new ItemStack(ModItems.vampireBook, 1, 0);
        }
    }

    /**
     * Loads 1..8 matching quarrels into a Quarrel Clip (standard or heavy), up to MAX_AMMO (16).
     */
    public static class RecipeFillQuarrelClip implements IRecipe {
        @Override
        public boolean matches(InventoryCrafting inv, World world) {
            return getCraftingResult(inv) != null;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            ItemStack clip = null;
            int quarrels = 0;
            int expectedQuarrelMeta = -1;

            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack s = inv.getStackInSlot(i);
                if (s == null) continue;
                if (s.getItem() == ModItems.quarrelClip) {
                    if (clip != null) return null;
                    int clipMeta = s.getItemDamage();
                    if (clipMeta != 0 && clipMeta != 1) return null;
                    if (expectedQuarrelMeta != -1 && expectedQuarrelMeta != clipMeta) return null;
                    clip = s;
                    expectedQuarrelMeta = clipMeta;
                } else if (s.getItem() == ModItems.quarrel) {
                    int qMeta = s.getItemDamage();
                    if (qMeta != 0 && qMeta != 1) return null;
                    if (expectedQuarrelMeta != -1 && expectedQuarrelMeta != qMeta) return null;
                    expectedQuarrelMeta = qMeta;
                    quarrels++;
                } else {
                    return null;
                }
            }

            if (clip == null || quarrels <= 0) return null;
            int currentAmmo = ItemQuarrelClip.getAmmoCount(clip);
            if (currentAmmo + quarrels > ItemQuarrelClip.MAX_AMMO) return null;

            ItemStack out = clip.copy();
            out.stackSize = 1;
            ItemQuarrelClip.setAmmoCount(out, currentAmmo + quarrels);
            return out;
        }

        @Override
        public int getRecipeSize() {
            return 9;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return null;
        }
    }
}
