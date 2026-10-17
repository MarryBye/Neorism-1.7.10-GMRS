package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.tileentity.TileEntityBloodPedestal;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBloodPedestal extends BlockModeled implements ITileEntityProvider {

    public BlockBloodPedestal() {
        super("blood_pedestal", Material.rock);
        setHardness(2.5F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.625F, 0.9375F);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityBloodPedestal();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityBloodPedestal)) return false;

        TileEntityBloodPedestal ped = (TileEntityBloodPedestal) te;
        ItemStack stored = ped.getStoredItem();
        ItemStack held = player.getHeldItem();

        if (stored != null) {
            // Take item from pedestal
            ped.takeStoredItem();
            if (stored.getItem() instanceof com.marrybye.neorism.common.items.ItemVampireSword) {
                com.marrybye.neorism.common.items.ItemVampireSword sword = (com.marrybye.neorism.common.items.ItemVampireSword) stored.getItem();
                if (sword.isFullyCharged(stored)) {
                    sword.tryName(stored, player);
                }
            }
            if (!player.inventory.addItemStackToInventory(stored)) {
                player.dropPlayerItemWithRandomChoice(stored, false);
            }
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.pop", 0.5F, 1.2F);
            return true;
        } else if (held != null) {
            // Canonical check: only IBloodChargeable items can be placed on Blood Pedestal
            if (!(held.getItem() instanceof com.marrybye.neorism.api.items.IBloodChargeable)) {
                return false;
            }

            // Place 1 item from hand onto pedestal
            ItemStack toPlace = held.copy();
            toPlace.stackSize = 1;
            ped.setStoredItem(toPlace);

            if (!player.capabilities.isCreativeMode) {
                held.stackSize--;
                if (held.stackSize <= 0) {
                    player.setCurrentItemOrArmor(0, null);
                }
            }
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.pop", 0.5F, 0.9F);
            return true;
        }

        return false;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityBloodPedestal) {
            return ((TileEntityBloodPedestal) te).getChargedProgress();
        }
        return 0;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityBloodPedestal) {
            ItemStack stored = ((TileEntityBloodPedestal) te).takeStoredItem();
            if (stored != null && !world.isRemote) {
                EntityItem entity = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, stored);
                world.spawnEntityInWorld(entity);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
