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

    public static Block altarInspiration;
    public static Block altarInfusion;
    public static Block altarPillar;
    public static Block altarTip;
    public static Block bloodPedestal;
    public static Block bloodContainer;
    public static Block coffin;
    public static Block fogDiffuser;
    public static Block garlicDiffuser;
    public static Block totem;
    public static Block totemTop;
    public static Block hunterTable;
    public static Block alchemicalCauldron;

    public static void init() {
        cursedEarth = register("cursed_earth", new BlockCursedEarth());
        cursedGrass = register("cursed_grass", new com.marrybye.neorism.common.blocks.BlockCursedGrass());
        darkStone = register("dark_stone", new com.marrybye.neorism.common.blocks.BlockDarkStone("dark_stone"));
        darkStoneBricks = register("dark_stone_bricks", new com.marrybye.neorism.common.blocks.BlockDarkStone("dark_stone_bricks"));
        cobbledDarkStone = register("cobbled_dark_stone", new com.marrybye.neorism.common.blocks.BlockDarkStone("cobbled_dark_stone"));
        polishedDarkStone = register("polished_dark_stone", new com.marrybye.neorism.common.blocks.BlockDarkStone("polished_dark_stone"));
        chiseledDarkStoneBricks = register("chiseled_dark_stone_bricks", new com.marrybye.neorism.common.blocks.BlockDarkStone("chiseled_dark_stone_bricks"));
        crackedDarkStoneBricks = register("cracked_dark_stone_bricks", new com.marrybye.neorism.common.blocks.BlockDarkStone("cracked_dark_stone_bricks"));
        vampireOrchid = register("vampire_orchid", new com.marrybye.neorism.common.blocks.BlockVampireOrchid());
        cursedRoots = register("cursed_roots", new com.marrybye.neorism.common.blocks.BlockCursedRoots());
        tent = register("tent", new com.marrybye.neorism.common.blocks.BlockTent());

        darkSprucePlanks = register("dark_spruce_planks", new BlockNeorism("dark_spruce_planks", Material.wood).setHardness(2.0F));
        darkSpruceLog = register("dark_spruce_log", new BlockDarkSpruceLog());
        darkSpruceLeaves = register("dark_spruce_leaves", new BlockDarkSpruceLeaves());
        darkSpruceSapling = register("dark_spruce_sapling", new BlockDarkSpruceSapling());

        altarInspiration = register("altar_inspiration", new BlockAltarInspiration());
        altarInfusion = register("altar_infusion", new BlockAltarInfusion());
        altarPillar = register("altar_pillar", new BlockAltarPillar());
        altarTip = register("altar_tip", new BlockAltarTip());
        bloodPedestal = register("blood_pedestal", new BlockBloodPedestal());
        bloodContainer = register("blood_container", new BlockBloodContainer());
        coffin = register("coffin_red", new BlockCoffin());
        fogDiffuser = register("fog_diffuser", new BlockFogDiffuser());
        garlicDiffuser = register("garlic_diffuser_normal", new BlockGarlicDiffuser());
        totem = register("totem_base", new BlockTotem());
        totemTop = register("totem_top", new com.marrybye.neorism.common.blocks.BlockTotemTop());
        hunterTable = register("hunter_table", new com.marrybye.neorism.common.blocks.BlockHunterTable());
        alchemicalCauldron = register("alchemical_cauldron", new com.marrybye.neorism.common.blocks.BlockAlchemicalCauldron());

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
