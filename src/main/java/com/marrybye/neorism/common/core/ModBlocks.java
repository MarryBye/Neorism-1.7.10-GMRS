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

    public static void init() {
        cursedEarth = register("cursed_earth", new BlockCursedEarth());
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

        // TileEntity Registration
        GameRegistry.registerTileEntity(TileEntityBloodContainer.class, "neorism:blood_container");
        GameRegistry.registerTileEntity(TileEntityBloodPedestal.class, "neorism:blood_pedestal");
        GameRegistry.registerTileEntity(TileEntityAltarInspiration.class, "neorism:altar_inspiration");
        GameRegistry.registerTileEntity(TileEntityAltarInfusion.class, "neorism:altar_infusion");
        GameRegistry.registerTileEntity(TileEntityCoffin.class, "neorism:coffin");
        GameRegistry.registerTileEntity(TileEntityFogDiffuser.class, "neorism:fog_diffuser");
        GameRegistry.registerTileEntity(TileEntityGarlicDiffuser.class, "neorism:garlic_diffuser");

        ModLogger.debug("Initialized core Neorism blocks and TileEntities");
    }

    public static <T extends Block> T register(String name, T block) {
        GameRegistry.registerBlock(block, name);
        return block;
    }
}
