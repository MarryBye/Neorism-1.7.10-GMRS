package com.marrybye.neorism.common.blocks;

import net.minecraft.block.material.Material;

public class BlockBloodPedestal extends BlockModeled {

    public BlockBloodPedestal() {
        super("blood_pedestal", Material.rock);
        setHardness(2.5F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.625F, 0.9375F);
    }
}
