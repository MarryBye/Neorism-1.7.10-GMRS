package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.tileentity.TileEntityTotem;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Upper crown block of the Faction Totem multiblock.
 * Canonical port of TeamLapen's TotemTopBlock.
 * Displays the glowing faction core and controls village territory state.
 */
public class BlockTotemTop extends BlockModeled implements ITileEntityProvider {

    public BlockTotemTop() {
        super("totem_top", "dark_stone", Material.rock);
        setHardness(5.0F);
        setResistance(2000.0F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        TileEntityTotem te = new TileEntityTotem();
        te.setControllingFactionId(meta);
        return te;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityTotem) {
            return ((TileEntityTotem) te).onRightClick(player);
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, net.minecraft.block.Block neighbor) {
        // If the base below is broken, destroy top
        if (world.getBlock(x, y - 1, z) != com.marrybye.neorism.common.core.ModBlocks.totem) {
            world.func_147480_a(x, y, z, true);
        }
    }
}
