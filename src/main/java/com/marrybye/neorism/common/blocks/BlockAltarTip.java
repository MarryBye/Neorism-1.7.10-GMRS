package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockAltarTip extends BlockModeled {

    public BlockAltarTip() {
        super("altar_tip", Material.rock);
        setHardness(2.0F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 0.5F, 0.875F);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return canBlockStay(world, x, y, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        Block below = world.getBlock(x, y - 1, z);
        return below == ModBlocks.altarPillar || World.doesBlockHaveSolidTopSurface(world, x, y - 1, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (!canBlockStay(world, x, y, z)) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }
}
