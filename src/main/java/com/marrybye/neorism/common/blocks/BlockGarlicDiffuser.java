package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.tileentity.TileEntityGarlicDiffuser;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockGarlicDiffuser extends BlockModeled implements ITileEntityProvider {

    public BlockGarlicDiffuser() {
        super("garlic_diffuser_normal", "garlic_diffuser", Material.iron);
        setHardness(3.5F);
        setStepSound(soundTypeMetal);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 0.75F, 0.875F);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityGarlicDiffuser();
    }
}
