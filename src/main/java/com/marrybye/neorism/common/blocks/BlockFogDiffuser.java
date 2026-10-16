package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.tileentity.TileEntityFogDiffuser;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFogDiffuser extends BlockModeled implements ITileEntityProvider {

    public BlockFogDiffuser() {
        super("fog_diffuser", Material.iron);
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
        return new TileEntityFogDiffuser();
    }
}
