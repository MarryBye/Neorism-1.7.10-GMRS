package com.marrybye.neorism.common.blocks;

import net.minecraft.block.material.Material;

/**
 * Faction Totem block representing village capture and faction territorial control.
 */
public class BlockTotem extends BlockModeled {

    public BlockTotem() {
        super("totem_base", "dark_stone", Material.rock);
        setHardness(5.0F);
        setResistance(10.0F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
    }
}
