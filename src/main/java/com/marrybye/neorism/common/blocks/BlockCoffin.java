package com.marrybye.neorism.common.blocks;

import java.util.Random;

import com.marrybye.neorism.common.tileentity.TileEntityCoffin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Vampire Coffin block (2-block multiblock: Foot & Head).
 * Allows nocturnal vampires to sleep safely through daytime and sets spawn chunk.
 */
public class BlockCoffin extends BlockModeled implements ITileEntityProvider {

    public BlockCoffin() {
        super("coffin_red", "coffin_base", Material.wood);
        setHardness(2.0F);
        setStepSound(soundTypeWood);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.65F, 1.0F);
    }

    @Override
    public void setBlockBoundsBasedOnState(net.minecraft.world.IBlockAccess world, int x, int y, int z) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.65F, 1.0F);
    }


    @Override
    public boolean hasTileEntity(int metadata) {
        return (metadata & 8) == 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return (metadata & 8) == 0 ? new TileEntityCoffin() : null;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        int dir = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int dx = 0;
        int dz = 0;
        if (dir == 0) dz = 1;       // South
        else if (dir == 1) dx = -1; // West
        else if (dir == 2) dz = -1; // North
        else if (dir == 3) dx = 1;  // East

        if (world.isAirBlock(x + dx, y, z + dz) || world.getBlock(x + dx, y, z + dz).isReplaceable(world, x + dx, y, z + dz)) {
            world.setBlockMetadataWithNotify(x, y, z, dir, 3);
            world.setBlock(x + dx, y, z + dz, this, dir | 8, 3);
        } else {
            world.setBlockToAir(x, y, z);
            if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode) {
                dropBlockAsItem(world, x, y, z, stack);
            }
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        int meta = world.getBlockMetadata(x, y, z);
        int dir = meta & 3;
        boolean isHead = (meta & 8) != 0;

        int dx = 0;
        int dz = 0;
        if (dir == 0) dz = 1;
        else if (dir == 1) dx = -1;
        else if (dir == 2) dz = -1;
        else if (dir == 3) dx = 1;

        int otherX = isHead ? x - dx : x + dx;
        int otherZ = isHead ? z - dz : z + dz;

        if (world.getBlock(otherX, y, otherZ) != this) {
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return (meta & 8) != 0 ? null : Item.getItemFromBlock(this);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        int meta = world.getBlockMetadata(x, y, z);
        int dir = meta & 3;
        boolean isHead = (meta & 8) != 0;

        int footX = x;
        int footZ = z;
        if (isHead) {
            int dx = 0;
            int dz = 0;
            if (dir == 0) dz = 1;
            else if (dir == 1) dx = -1;
            else if (dir == 2) dz = -1;
            else if (dir == 3) dx = 1;
            footX = x - dx;
            footZ = z - dz;
        }

        TileEntity te = world.getTileEntity(footX, y, footZ);
        if (te instanceof TileEntityCoffin) {
            TileEntityCoffin coffin = (TileEntityCoffin) te;
            if (player.isSneaking()) {
                if (!world.isRemote) {
                    coffin.toggleClosed();
                    world.playSoundEffect(footX + 0.5D, y + 0.5D, footZ + 0.5D,
                        "neorism:cc/block/coffin_lid_slide", 0.8F, 1.0F);
                }
                return true;
            }
            return coffin.trySleep(player);
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return -1;
    }
}
