package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.tileentity.TileEntityAltarInfusion;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockAltarInfusion extends BlockModeled implements ITileEntityProvider {

    public BlockAltarInfusion() {
        super("altar_infusion", Material.rock);
        setHardness(3.0F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.8125F, 0.9375F);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityAltarInfusion();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityAltarInfusion) {
            return ((TileEntityAltarInfusion) te).startRitual(player);
        }
        return false;
    }
}
