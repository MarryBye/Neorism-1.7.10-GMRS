package com.marrybye.neorism.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;

public class BlockBloodContainer extends BlockModeled {

    public BlockBloodContainer() {
        super("blood_container", "blood_container_side", Material.glass);
        setHardness(1.5F);
        setStepSound(soundTypeGlass);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 1.0F, 0.875F);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }
}
