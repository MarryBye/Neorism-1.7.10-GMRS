package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.tileentity.TileEntityTotem;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Lower pedestal block of the Faction Totem multiblock.
 * Canonical port of TeamLapen's TotemBaseBlock.
 */
public class BlockTotem extends BlockModeled {

    public BlockTotem() {
        super("totem_base", "dark_stone", Material.rock);
        setHardness(5.0F);
        setResistance(10.0F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        // If top block exists above, delegate right-click to top
        if (world.getBlock(x, y + 1, z) == ModBlocks.totemTop) {
            TileEntity te = world.getTileEntity(x, y + 1, z);
            if (te instanceof TileEntityTotem) {
                return ((TileEntityTotem) te).onRightClick(player);
            }
        }
        return false;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (world.getBlock(x, y + 1, z) == ModBlocks.totemTop) {
            TileEntity te = world.getTileEntity(x, y + 1, z);
            if (te instanceof TileEntityTotem && !((TileEntityTotem) te).canPlayerRemoveBlock(player)) {
                return false;
            }
        }
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block oldBlock, int oldMeta) {
        // Destroy top block above
        if (world.getBlock(x, y + 1, z) == ModBlocks.totemTop) {
            world.func_147480_a(x, y + 1, z, true);
        }
        super.breakBlock(world, x, y, z, oldBlock, oldMeta);
    }
}
