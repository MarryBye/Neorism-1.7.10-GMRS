package com.marrybye.neorism.common.blocks;

import net.minecraft.block.material.Material;

public class BlockAltarInspiration extends BlockModeled {

    public BlockAltarInspiration() {
        super("altar_inspiration", "cursed_spruce_log", Material.rock);
        setHardness(3.0F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }
}
