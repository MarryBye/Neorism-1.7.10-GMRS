package com.marrybye.neorism.common.blocks;

import net.minecraft.block.material.Material;

public class BlockAltarPillar extends BlockModeled {

    public BlockAltarPillar() {
        super("altar_pillar", Material.rock);
        setHardness(2.0F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 1.0F, 0.875F);
    }
}
