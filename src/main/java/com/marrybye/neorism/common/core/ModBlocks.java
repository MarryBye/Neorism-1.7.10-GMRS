package com.marrybye.neorism.common.core;

import com.marrybye.neorism.common.blocks.BlockNeorism;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Blocks registry for Neorism.
 */
public class ModBlocks {

    public static Block cursedEarth;
    public static Block darkSprucePlanks;
    public static Block altarInspiration;
    public static Block altarInfusion;
    public static Block altarPillar;
    public static Block altarTip;
    public static Block bloodPedestal;
    public static Block bloodContainer;

    public static void init() {
        cursedEarth = register("cursed_earth", new BlockNeorism("cursed_earth", Material.ground).setHardness(0.6F));
        darkSprucePlanks = register("dark_spruce_planks", new BlockNeorism("dark_spruce_planks", Material.wood).setHardness(2.0F));
        altarInspiration = register("altar_inspiration", new com.marrybye.neorism.common.blocks.BlockAltarInspiration());
        altarInfusion = register("altar_infusion", new com.marrybye.neorism.common.blocks.BlockAltarInfusion());
        altarPillar = register("altar_pillar", new com.marrybye.neorism.common.blocks.BlockAltarPillar());
        altarTip = register("altar_tip", new com.marrybye.neorism.common.blocks.BlockAltarTip());
        bloodPedestal = register("blood_pedestal", new com.marrybye.neorism.common.blocks.BlockBloodPedestal());
        bloodContainer = register("blood_container", new com.marrybye.neorism.common.blocks.BlockBloodContainer());

        ModLogger.debug("Initialized core Neorism blocks");
    }

    public static <T extends Block> T register(String name, T block) {
        GameRegistry.registerBlock(block, name);
        return block;
    }
}
