package com.marrybye.neorism.common.blocks;

import net.minecraft.block.material.Material;

public class BlockAltarInfusion extends BlockModeled {

    public BlockAltarInfusion() {
        super("altar_infusion", Material.rock);
        setHardness(3.0F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.8125F, 0.9375F);
    }
}
