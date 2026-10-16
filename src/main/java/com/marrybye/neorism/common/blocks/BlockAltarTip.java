package com.marrybye.neorism.common.blocks;

import net.minecraft.block.material.Material;

public class BlockAltarTip extends BlockModeled {

    public BlockAltarTip() {
        super("altar_tip", Material.rock);
        setHardness(2.0F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 0.5F, 0.875F);
    }
}
