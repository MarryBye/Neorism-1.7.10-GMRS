package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.tileentity.TileEntityCoffin;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Vampire Coffin block allowing nocturnal vampires to sleep safely through the daytime.
 */
public class BlockCoffin extends BlockModeled implements ITileEntityProvider {

    public BlockCoffin() {
        super("coffin_red", "coffin_base", Material.wood);
        setHardness(2.0F);
        setStepSound(soundTypeWood);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.6F, 1.0F);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityCoffin();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityCoffin) {
            return ((TileEntityCoffin) te).trySleep(player);
        }
        return false;
    }
}
