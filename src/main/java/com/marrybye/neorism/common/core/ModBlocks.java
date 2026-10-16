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
        altarInspiration = register("altar_inspiration", new BlockNeorism("altar_inspiration", Material.rock).setHardness(3.0F));
        altarInfusion = register("altar_infusion", new BlockNeorism("altar_infusion", Material.rock).setHardness(3.0F));
        altarPillar = register("altar_pillar", new BlockNeorism("altar_pillar", Material.rock).setHardness(2.0F));
        altarTip = register("altar_tip", new BlockNeorism("altar_tip", Material.rock).setHardness(2.0F));
        bloodPedestal = register("blood_pedestal", new BlockNeorism("blood_pedestal", Material.rock).setHardness(2.5F));
        bloodContainer = register("blood_container", new BlockNeorism("blood_container", Material.glass).setHardness(1.5F));

        ModLogger.debug("Initialized core Neorism blocks");
    }

    public static <T extends Block> T register(String name, T block) {
        GameRegistry.registerBlock(block, name);
        return block;
    }
}
