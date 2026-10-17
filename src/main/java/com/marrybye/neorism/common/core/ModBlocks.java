package com.marrybye.neorism.common.core;

import com.marrybye.neorism.common.blocks.BlockAltarInfusion;
import com.marrybye.neorism.common.blocks.BlockAltarInspiration;
import com.marrybye.neorism.common.blocks.BlockAltarPillar;
import com.marrybye.neorism.common.blocks.BlockAltarTip;
import com.marrybye.neorism.common.blocks.BlockBloodContainer;
import com.marrybye.neorism.common.blocks.BlockBloodPedestal;
import com.marrybye.neorism.common.blocks.BlockCoffin;
import com.marrybye.neorism.common.blocks.BlockCursedEarth;
import com.marrybye.neorism.common.blocks.BlockDarkSpruceLeaves;
import com.marrybye.neorism.common.blocks.BlockDarkSpruceLog;
import com.marrybye.neorism.common.blocks.BlockDarkSpruceSapling;
import com.marrybye.neorism.common.blocks.BlockFogDiffuser;
import com.marrybye.neorism.common.blocks.BlockGarlicDiffuser;
import com.marrybye.neorism.common.blocks.BlockNeorism;
import com.marrybye.neorism.common.blocks.BlockTotem;
import com.marrybye.neorism.common.tileentity.TileEntityAltarInfusion;
import com.marrybye.neorism.common.tileentity.TileEntityAltarInspiration;
import com.marrybye.neorism.common.tileentity.TileEntityBloodContainer;
import com.marrybye.neorism.common.tileentity.TileEntityBloodPedestal;
import com.marrybye.neorism.common.tileentity.TileEntityCoffin;
import com.marrybye.neorism.common.tileentity.TileEntityFogDiffuser;
import com.marrybye.neorism.common.tileentity.TileEntityGarlicDiffuser;
import com.marrybye.neorism.common.blocks.BlockNeorismStairs;
import com.marrybye.neorism.common.blocks.BlockNeorismSlab;
import com.marrybye.neorism.common.blocks.BlockNeorismFence;
import com.marrybye.neorism.common.blocks.BlockNeorismFenceGate;
import com.marrybye.neorism.common.blocks.BlockNeorismTrapDoor;
import com.marrybye.neorism.common.blocks.BlockNeorismWall;
import com.marrybye.neorism.common.blocks.BlockNeorismLog;
import com.marrybye.neorism.common.blocks.BlockNeorismPressurePlate;
import com.marrybye.neorism.common.blocks.BlockNeorismButton;
import com.marrybye.neorism.common.blocks.BlockNeorismDecor;
import com.marrybye.neorism.common.blocks.BlockModeled;
import com.marrybye.neorism.common.blocks.BlockModDoor;
import com.marrybye.neorism.common.blocks.BlockCursedEarthPath;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Blocks and TileEntities registry for Neorism.
 */
public class ModBlocks {

    public static Block cursedEarth;
    public static Block cursedGrass;
    public static Block darkStone;
    public static Block darkStoneBricks;
    public static Block cobbledDarkStone;
    public static Block polishedDarkStone;
    public static Block chiseledDarkStoneBricks;
    public static Block crackedDarkStoneBricks;
    public static Block vampireOrchid;
    public static Block cursedRoots;
    public static Block tent;

    public static Block darkSprucePlanks;
    public static Block darkSpruceLog;
    public static Block darkSpruceLeaves;
    public static Block darkSpruceSapling;

    // Category 1: Wood & Nature
    public static Block darkSpruceWood;
    public static Block strippedDarkSpruceLog;
    public static Block strippedDarkSpruceWood;
    public static Block darkSpruceStairs;
    public static Block darkSpruceSlab;
    public static Block darkSpruceFence;
    public static Block darkSpruceFenceGate;
    public static BlockModDoor darkSpruceDoor;
    public static Block darkSpruceTrapdoor;
    public static Block darkSprucePressurePlate;
    public static Block darkSpruceButton;

    public static Block cursedSpruceSapling;
    public static Block cursedSpruceLog;
    public static Block cursedSpruceWood;
    public static Block strippedCursedSpruceLog;
    public static Block strippedCursedSpruceWood;
    public static Block cursedSprucePlanks;
    public static Block cursedSpruceStairs;
    public static Block cursedSpruceSlab;
    public static Block cursedSpruceFence;
    public static Block cursedSpruceFenceGate;
    public static BlockModDoor cursedSpruceDoor;
    public static Block cursedSpruceTrapdoor;
    public static Block cursedSprucePressurePlate;
    public static Block cursedSpruceButton;
    public static Block cursedEarthPath;
    public static Block hangingRoots;
    public static Block directCursedBark;

    // Category 1: Stone Sets
    public static Block darkStoneTiles;
    public static Block crackedDarkStoneTiles;
    public static Block darkStoneStairs;
    public static Block darkStoneSlab;
    public static Block darkStoneWall;
    public static Block darkStoneBricksStairs;
    public static Block darkStoneBricksSlab;
    public static Block darkStoneBricksWall;
    public static Block cobbledDarkStoneStairs;
    public static Block cobbledDarkStoneSlab;
    public static Block cobbledDarkStoneWall;
    public static Block polishedDarkStoneStairs;
    public static Block polishedDarkStoneSlab;
    public static Block polishedDarkStoneWall;

    public static Block purpleStoneBricks;
    public static Block purpleStoneTiles;
    public static Block purpleStoneBricksStairs;
    public static Block purpleStoneBricksSlab;
    public static Block purpleStoneBricksWall;
    public static Block purpleStoneTilesStairs;
    public static Block purpleStoneTilesSlab;
    public static Block purpleStoneTilesWall;

    // Category 1: Blood Infused Blocks & Decor
    public static Block bloodInfusedIronBlock;
    public static Block bloodInfusedEnhancedIronBlock;
    public static Block bloodInfusedRefinedIronBlock;
    public static Block candlestick;
    public static Block candelabra;
    public static Block chandelier;
    public static Block vampireSoulLantern;
    public static Block cross;
    public static Block tombstoneShort;
    public static Block tombstoneMedium;
    public static Block tombstoneCross;
    public static Block graveCage;
    public static Block vampireRack;
    public static Block throne;
    public static Block batCage;
    public static Block ancientTrophy;
    public static Block velmorraAltar;

    // Category 2: Hunter Functional Blocks & Coffins
    public static Block hunterTable;
    public static Block hunterWeaponTable;
    public static Block alchemicalCauldron;
    public static Block vaporStill;
    public static Block alchemyTable;
    public static Block injectionChair;
    public static Block altarCleansing;
    public static Block garlicDiffuser;
    public static Block garlicDiffuserLong;
    public static Block garlicDiffuserStrong;
    public static Block sunscreenBeacon;
    public static Block coffin;
    public static Block[] coffins;

    // Category 3: Vampire Blocks
    public static Block altarInspiration;
    public static Block altarInfusion;
    public static Block altarPillar;
    public static Block altarTip;
    public static Block bloodPedestal;
    public static Block bloodContainer;
    public static Block fogDiffuser;
    public static Block totem;
    public static Block totemTop;
    public static Block chalice;
    public static Block bloodGrinder;
    public static Block bloodSieve;
    public static Block infuser;
    public static Block vampireBeacon;

    public static void init() {
        // Category 1: Nature & Wood
        cursedEarth = register("cursed_earth", new BlockCursedEarth());
        cursedGrass = register("cursed_grass", new com.marrybye.neorism.common.blocks.BlockCursedGrass());
        cursedEarthPath = register("cursed_earth_path", new BlockCursedEarthPath());
        vampireOrchid = register("vampire_orchid", new com.marrybye.neorism.common.blocks.BlockVampireOrchid());
        cursedRoots = register("cursed_roots", new com.marrybye.neorism.common.blocks.BlockCursedRoots());
        hangingRoots = register("cursed_hanging_roots", new BlockNeorismDecor("cursed_hanging_roots", "cursed_hanging_roots", Material.plants, ModCreativeTabs.tabVampirism, 0.2F, 0.0F, 0.2F, 0.8F, 1.0F, 0.8F, false));
        directCursedBark = register("direct_cursed_bark", new BlockNeorism("direct_cursed_bark", "cursed_spruce_log", Material.wood).setHardness(2.0F));

        darkSprucePlanks = register("dark_spruce_planks", new BlockNeorism("dark_spruce_planks", Material.wood).setHardness(2.0F));
        darkSpruceLog = register("dark_spruce_log", new BlockDarkSpruceLog());
        darkSpruceWood = register("dark_spruce_wood", new BlockNeorismLog("dark_spruce_wood", "dark_spruce_log", "dark_spruce_log", ModCreativeTabs.tabVampirism));
        strippedDarkSpruceLog = register("stripped_dark_spruce_log", new BlockNeorismLog("stripped_dark_spruce_log", "stripped_dark_spruce_log", "stripped_dark_spruce_log_top", ModCreativeTabs.tabVampirism));
        strippedDarkSpruceWood = register("stripped_dark_spruce_wood", new BlockNeorismLog("stripped_dark_spruce_wood", "stripped_dark_spruce_log", "stripped_dark_spruce_log", ModCreativeTabs.tabVampirism));
        darkSpruceLeaves = register("dark_spruce_leaves", new BlockDarkSpruceLeaves());
        darkSpruceSapling = register("dark_spruce_sapling", new BlockDarkSpruceSapling());
        darkSpruceStairs = register("dark_spruce_stairs", new BlockNeorismStairs("dark_spruce_stairs", darkSprucePlanks, 0, ModCreativeTabs.tabVampirism));
        darkSpruceSlab = register("dark_spruce_slab", new BlockNeorismSlab("dark_spruce_slab", darkSprucePlanks, Material.wood, ModCreativeTabs.tabVampirism));
        darkSpruceFence = register("dark_spruce_fence", new BlockNeorismFence("dark_spruce_fence", "dark_spruce_planks", Material.wood, ModCreativeTabs.tabVampirism));
        darkSpruceFenceGate = register("dark_spruce_fence_gate", new BlockNeorismFenceGate("dark_spruce_fence_gate", darkSprucePlanks, ModCreativeTabs.tabVampirism));
        darkSpruceDoor = register("dark_spruce_door", new BlockModDoor("dark_spruce_door", "dark_spruce_door"));
        darkSpruceTrapdoor = register("dark_spruce_trapdoor", new BlockNeorismTrapDoor("dark_spruce_trapdoor", "dark_spruce_trapdoor", Material.wood, ModCreativeTabs.tabVampirism));
        darkSprucePressurePlate = register("dark_spruce_pressure_plate", new BlockNeorismPressurePlate("dark_spruce_pressure_plate", "dark_spruce_planks", ModCreativeTabs.tabVampirism));
        darkSpruceButton = register("dark_spruce_button", new BlockNeorismButton("dark_spruce_button", darkSprucePlanks, ModCreativeTabs.tabVampirism));

        cursedSpruceSapling = register("cursed_spruce_sapling", new BlockDarkSpruceSapling());
        cursedSprucePlanks = register("cursed_spruce_planks", new BlockNeorism("cursed_spruce_planks", Material.wood).setHardness(2.0F));
        cursedSpruceLog = register("cursed_spruce_log", new BlockNeorismLog("cursed_spruce_log", "cursed_spruce_log", "cursed_spruce_log_top", ModCreativeTabs.tabVampirism));
        cursedSpruceWood = register("cursed_spruce_wood", new BlockNeorismLog("cursed_spruce_wood", "cursed_spruce_log", "cursed_spruce_log", ModCreativeTabs.tabVampirism));
        strippedCursedSpruceLog = register("stripped_cursed_spruce_log", new BlockNeorismLog("stripped_cursed_spruce_log", "stripped_cursed_spruce_log", "stripped_cursed_spruce_log_top", ModCreativeTabs.tabVampirism));
        strippedCursedSpruceWood = register("stripped_cursed_spruce_wood", new BlockNeorismLog("stripped_cursed_spruce_wood", "stripped_cursed_spruce_log", "stripped_cursed_spruce_log", ModCreativeTabs.tabVampirism));
        cursedSpruceStairs = register("cursed_spruce_stairs", new BlockNeorismStairs("cursed_spruce_stairs", cursedSprucePlanks, 0, ModCreativeTabs.tabVampirism));
        cursedSpruceSlab = register("cursed_spruce_slab", new BlockNeorismSlab("cursed_spruce_slab", cursedSprucePlanks, Material.wood, ModCreativeTabs.tabVampirism));
        cursedSpruceFence = register("cursed_spruce_fence", new BlockNeorismFence("cursed_spruce_fence", "cursed_spruce_planks", Material.wood, ModCreativeTabs.tabVampirism));
        cursedSpruceFenceGate = register("cursed_spruce_fence_gate", new BlockNeorismFenceGate("cursed_spruce_fence_gate", cursedSprucePlanks, ModCreativeTabs.tabVampirism));
        cursedSpruceDoor = register("cursed_spruce_door", new BlockModDoor("cursed_spruce_door", "cursed_spruce_door"));
        cursedSpruceTrapdoor = register("cursed_spruce_trapdoor", new BlockNeorismTrapDoor("cursed_spruce_trapdoor", "cursed_spruce_trapdoor", Material.wood, ModCreativeTabs.tabVampirism));
        cursedSprucePressurePlate = register("cursed_spruce_pressure_plate", new BlockNeorismPressurePlate("cursed_spruce_pressure_plate", "cursed_spruce_planks", ModCreativeTabs.tabVampirism));
        cursedSpruceButton = register("cursed_spruce_button", new BlockNeorismButton("cursed_spruce_button", cursedSprucePlanks, ModCreativeTabs.tabVampirism));

        // Category 1: Dark Stone & Purple Stone
        darkStone = register("dark_stone", new com.marrybye.neorism.common.blocks.BlockDarkStone("dark_stone"));
        darkStoneBricks = register("dark_stone_bricks", new com.marrybye.neorism.common.blocks.BlockDarkStone("dark_stone_bricks"));
        cobbledDarkStone = register("cobbled_dark_stone", new com.marrybye.neorism.common.blocks.BlockDarkStone("cobbled_dark_stone"));
        polishedDarkStone = register("polished_dark_stone", new com.marrybye.neorism.common.blocks.BlockDarkStone("polished_dark_stone"));
        chiseledDarkStoneBricks = register("chiseled_dark_stone_bricks", new com.marrybye.neorism.common.blocks.BlockDarkStone("chiseled_dark_stone_bricks"));
        crackedDarkStoneBricks = register("cracked_dark_stone_bricks", new com.marrybye.neorism.common.blocks.BlockDarkStone("cracked_dark_stone_bricks"));
        darkStoneTiles = register("dark_stone_tiles", new com.marrybye.neorism.common.blocks.BlockDarkStone("dark_stone_tiles"));
        crackedDarkStoneTiles = register("cracked_dark_stone_tiles", new com.marrybye.neorism.common.blocks.BlockDarkStone("cracked_dark_stone_tiles"));

        darkStoneStairs = register("dark_stone_stairs", new BlockNeorismStairs("dark_stone_stairs", darkStone, 0, ModCreativeTabs.tabVampirism));
        darkStoneSlab = register("dark_stone_slab", new BlockNeorismSlab("dark_stone_slab", darkStone, Material.rock, ModCreativeTabs.tabVampirism));
        darkStoneWall = register("dark_stone_wall", new BlockNeorismWall("dark_stone_wall", darkStone, ModCreativeTabs.tabVampirism));

        darkStoneBricksStairs = register("dark_stone_brick_stairs", new BlockNeorismStairs("dark_stone_brick_stairs", darkStoneBricks, 0, ModCreativeTabs.tabVampirism));
        darkStoneBricksSlab = register("dark_stone_brick_slab", new BlockNeorismSlab("dark_stone_brick_slab", darkStoneBricks, Material.rock, ModCreativeTabs.tabVampirism));
        darkStoneBricksWall = register("dark_stone_brick_wall", new BlockNeorismWall("dark_stone_brick_wall", darkStoneBricks, ModCreativeTabs.tabVampirism));

        cobbledDarkStoneStairs = register("cobbled_dark_stone_stairs", new BlockNeorismStairs("cobbled_dark_stone_stairs", cobbledDarkStone, 0, ModCreativeTabs.tabVampirism));
        cobbledDarkStoneSlab = register("cobbled_dark_stone_slab", new BlockNeorismSlab("cobbled_dark_stone_slab", cobbledDarkStone, Material.rock, ModCreativeTabs.tabVampirism));
        cobbledDarkStoneWall = register("cobbled_dark_stone_wall", new BlockNeorismWall("cobbled_dark_stone_wall", cobbledDarkStone, ModCreativeTabs.tabVampirism));

        polishedDarkStoneStairs = register("polished_dark_stone_stairs", new BlockNeorismStairs("polished_dark_stone_stairs", polishedDarkStone, 0, ModCreativeTabs.tabVampirism));
        polishedDarkStoneSlab = register("polished_dark_stone_slab", new BlockNeorismSlab("polished_dark_stone_slab", polishedDarkStone, Material.rock, ModCreativeTabs.tabVampirism));
        polishedDarkStoneWall = register("polished_dark_stone_wall", new BlockNeorismWall("polished_dark_stone_wall", polishedDarkStone, ModCreativeTabs.tabVampirism));

        purpleStoneBricks = register("purple_stone_bricks", new com.marrybye.neorism.common.blocks.BlockDarkStone("purple_stone_bricks"));
        purpleStoneTiles = register("purple_stone_tiles", new com.marrybye.neorism.common.blocks.BlockDarkStone("purple_stone_tiles"));
        purpleStoneBricksStairs = register("purple_stone_brick_stairs", new BlockNeorismStairs("purple_stone_brick_stairs", purpleStoneBricks, 0, ModCreativeTabs.tabVampirism));
        purpleStoneBricksSlab = register("purple_stone_brick_slab", new BlockNeorismSlab("purple_stone_brick_slab", purpleStoneBricks, Material.rock, ModCreativeTabs.tabVampirism));
        purpleStoneBricksWall = register("purple_stone_brick_wall", new BlockNeorismWall("purple_stone_brick_wall", purpleStoneBricks, ModCreativeTabs.tabVampirism));
        purpleStoneTilesStairs = register("purple_stone_tiles_stairs", new BlockNeorismStairs("purple_stone_tiles_stairs", purpleStoneTiles, 0, ModCreativeTabs.tabVampirism));
        purpleStoneTilesSlab = register("purple_stone_tiles_slab", new BlockNeorismSlab("purple_stone_tiles_slab", purpleStoneTiles, Material.rock, ModCreativeTabs.tabVampirism));
        purpleStoneTilesWall = register("purple_stone_tiles_wall", new BlockNeorismWall("purple_stone_tiles_wall", purpleStoneTiles, ModCreativeTabs.tabVampirism));

        // Category 1: Blood Infused Iron Blocks & Decor
        bloodInfusedIronBlock = register("blood_infused_iron_block", new BlockNeorism("blood_infused_iron_block", Material.iron).setHardness(5.0F));
        bloodInfusedEnhancedIronBlock = register("blood_infused_enhanced_iron_block", new BlockNeorism("blood_infused_enhanced_iron_block", Material.iron).setHardness(6.0F));
        bloodInfusedRefinedIronBlock = register("blood_infused_refined_iron_block", new BlockNeorism("blood_infused_refined_iron_block", Material.iron).setHardness(7.0F));

        candlestick = register("candle_stick", new BlockNeorismDecor("candle_stick", "candle_stick_base", Material.circuits, ModCreativeTabs.tabVampirism, 0.4F, 0.0F, 0.4F, 0.6F, 0.6F, 0.6F, false).setLightLevel(0.9F));
        candelabra = register("candelabra", new BlockNeorismDecor("candelabra", "candelabra_base", Material.circuits, ModCreativeTabs.tabVampirism, 0.25F, 0.0F, 0.25F, 0.75F, 0.8F, 0.75F, false).setLightLevel(0.95F));
        chandelier = register("chandelier", new BlockNeorismDecor("chandelier", "chandelier_base", Material.circuits, ModCreativeTabs.tabVampirism, 0.1F, 0.2F, 0.1F, 0.9F, 1.0F, 0.9F, false).setLightLevel(1.0F));
        vampireSoulLantern = register("vampire_soul_lantern", new BlockNeorismDecor("vampire_soul_lantern", "vampire_soul_lantern", Material.glass, ModCreativeTabs.tabVampirism, 0.25F, 0.0F, 0.25F, 0.75F, 0.6F, 0.75F, false).setLightLevel(0.8F));

        cross = register("cross", new BlockNeorismDecor("cross", "tombstone_cross", Material.wood, ModCreativeTabs.tabVampirism, 0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F, true));
        tombstoneShort = register("tombstone_short", new BlockNeorismDecor("tombstone_short", "tombstone", Material.rock, ModCreativeTabs.tabVampirism, 0.1F, 0.0F, 0.3F, 0.9F, 0.8F, 0.7F, true));
        tombstoneMedium = register("tombstone_medium", new BlockNeorismDecor("tombstone_medium", "tombstone", Material.rock, ModCreativeTabs.tabVampirism, 0.1F, 0.0F, 0.3F, 0.9F, 1.0F, 0.7F, true));
        tombstoneCross = register("tombstone_cross", new BlockNeorismDecor("tombstone_cross", "tombstone_cross", Material.rock, ModCreativeTabs.tabVampirism, 0.1F, 0.0F, 0.3F, 0.9F, 1.0F, 0.7F, true));
        graveCage = register("grave_cage", new BlockNeorismDecor("grave_cage", "bat_cage", Material.iron, ModCreativeTabs.tabVampirism, 0.05F, 0.0F, 0.05F, 0.95F, 0.95F, 0.95F, true));
        vampireRack = register("vampire_rack", new BlockNeorismDecor("vampire_rack", "vampire_rack", Material.wood, ModCreativeTabs.tabVampirism, 0.0F, 0.0F, 0.0F, 1.0F, 0.8F, 1.0F, true));
        throne = register("throne", new BlockNeorismDecor("throne", "throne", Material.wood, ModCreativeTabs.tabVampirism, 0.1F, 0.0F, 0.1F, 0.9F, 1.0F, 0.9F, true));
        batCage = register("bat_cage", new BlockNeorismDecor("bat_cage", "bat_cage", Material.iron, ModCreativeTabs.tabVampirism, 0.15F, 0.0F, 0.15F, 0.85F, 0.9F, 0.85F, true));
        ancientTrophy = register("mother_trophy", new BlockNeorismDecor("mother_trophy", "vulnerable_remains", Material.rock, ModCreativeTabs.tabVampirism, 0.2F, 0.0F, 0.2F, 0.8F, 0.8F, 0.8F, false).setLightLevel(0.1F));
        velmorraAltar = register("velmorra_altar", new BlockNeorismDecor("velmorra_altar", "velmorra_altar_bloody", Material.rock, ModCreativeTabs.tabVampirism, 0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F, true));
        tent = register("tent", new com.marrybye.neorism.common.blocks.BlockTent());

        // Category 2: Hunter Functional Blocks & 16 Coffins
        hunterTable = register("hunter_table", new com.marrybye.neorism.common.blocks.BlockHunterTable());
        hunterWeaponTable = register("weapon_table", new BlockModeled("weapon_table", "weapon_table_hammer", Material.iron, ModCreativeTabs.tabHunter).setHardness(3.0F));
        alchemicalCauldron = register("alchemical_cauldron", new com.marrybye.neorism.common.blocks.BlockAlchemicalCauldron());
        vaporStill = register("vapor_still", new BlockNeorismDecor("vapor_still", "vapor_still", Material.iron, ModCreativeTabs.tabHunter, 0.1F, 0.0F, 0.1F, 0.9F, 0.9F, 0.9F, true));
        alchemyTable = register("alchemy_table", new BlockNeorismDecor("alchemy_table", "alchemical_cauldron_side", Material.iron, ModCreativeTabs.tabHunter, 0.0F, 0.0F, 0.0F, 1.0F, 0.9F, 1.0F, true).setLightLevel(0.2F));
        injectionChair = register("injection_chair", new BlockNeorismDecor("injection_chair", "coffin_base", Material.wood, ModCreativeTabs.tabHunter, 0.1F, 0.0F, 0.1F, 0.9F, 0.9F, 0.9F, true));
        altarCleansing = register("altar_cleansing", new BlockNeorismDecor("altar_cleansing", "altar_pillar_stone", Material.wood, ModCreativeTabs.tabHunter, 0.1F, 0.0F, 0.1F, 0.9F, 0.8F, 0.9F, true));
        garlicDiffuser = register("garlic_diffuser_normal", new BlockGarlicDiffuser());
        garlicDiffuserLong = register("garlic_diffuser_long", new BlockGarlicDiffuser());
        garlicDiffuserStrong = register("garlic_diffuser_strong", new BlockGarlicDiffuser());
        sunscreenBeacon = register("sunscreen_beacon", new BlockNeorismDecor("sunscreen_beacon", "vampire_beacon", Material.iron, ModCreativeTabs.tabHunter).setLightLevel(1.0F));

        coffin = register("coffin_red", new BlockCoffin("red"));
        String[] coffinColors = {"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "black"};
        coffins = new Block[coffinColors.length];
        for (int i = 0; i < coffinColors.length; i++) {
            coffins[i] = register("coffin_" + coffinColors[i], new BlockCoffin(coffinColors[i]));
        }

        // Category 3: Vampire Blocks
        altarInspiration = register("altar_inspiration", new BlockAltarInspiration());
        altarInfusion = register("altar_infusion", new BlockAltarInfusion());
        altarPillar = register("altar_pillar", new BlockAltarPillar());
        altarTip = register("altar_tip", new BlockAltarTip());
        bloodPedestal = register("blood_pedestal", new BlockBloodPedestal());
        bloodContainer = register("blood_container", new BlockBloodContainer());
        fogDiffuser = register("fog_diffuser", new BlockFogDiffuser());
        totem = register("totem_base", new BlockTotem());
        totemTop = register("totem_top", new com.marrybye.neorism.common.blocks.BlockTotemTop());
        chalice = register("chalice", new BlockNeorismDecor("chalice", "chalice", Material.iron, ModCreativeTabs.tabVampire, 0.25F, 0.0F, 0.25F, 0.75F, 0.7F, 0.75F, false));
        bloodGrinder = register("blood_grinder", new BlockModeled("blood_grinder", "blood_grinder_top", Material.wood, ModCreativeTabs.tabVampire).setHardness(3.0F));
        bloodSieve = register("blood_sieve", new BlockModeled("blood_sieve", "blood_sieve_top", Material.wood, ModCreativeTabs.tabVampire).setHardness(3.0F));
        infuser = register("blood_infuser", new BlockModeled("blood_infuser", "infuser", Material.iron, ModCreativeTabs.tabVampire).setHardness(3.0F));
        vampireBeacon = register("vampire_beacon", new BlockNeorismDecor("vampire_beacon", "vampire_beacon", Material.iron, ModCreativeTabs.tabVampire).setLightLevel(1.0F));

        // TileEntity Registration
        GameRegistry.registerTileEntity(TileEntityBloodContainer.class, "neorism:blood_container");
        GameRegistry.registerTileEntity(TileEntityBloodPedestal.class, "neorism:blood_pedestal");
        GameRegistry.registerTileEntity(TileEntityAltarInspiration.class, "neorism:altar_inspiration");
        GameRegistry.registerTileEntity(TileEntityAltarInfusion.class, "neorism:altar_infusion");
        GameRegistry.registerTileEntity(TileEntityCoffin.class, "neorism:coffin");
        GameRegistry.registerTileEntity(TileEntityFogDiffuser.class, "neorism:fog_diffuser");
        GameRegistry.registerTileEntity(TileEntityGarlicDiffuser.class, "neorism:garlic_diffuser");
        GameRegistry.registerTileEntity(com.marrybye.neorism.common.tileentity.TileEntityTent.class, "neorism:tent");
        GameRegistry.registerTileEntity(com.marrybye.neorism.common.tileentity.TileEntityAlchemicalCauldron.class, "neorism:alchemical_cauldron");
        GameRegistry.registerTileEntity(com.marrybye.neorism.common.tileentity.TileEntityTotem.class, "neorism:totem");

        ModLogger.debug("Initialized core Neorism blocks and TileEntities");
    }

    public static <T extends Block> T register(String name, T block) {
        GameRegistry.registerBlock(block, name);
        return block;
    }
}
