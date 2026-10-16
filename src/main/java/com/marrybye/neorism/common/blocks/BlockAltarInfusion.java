package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.network.ModGuiHandler;
import com.marrybye.neorism.common.tileentity.TileEntityAltarInfusion;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Altar of Infusion block.
 * Ported from TeamLapen's AltarInfusionBlock.java.
 */
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
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityAltarInfusion)) return false;
        TileEntityAltarInfusion altar = (TileEntityAltarInfusion) te;

        if (!Helper.isVampire(player)) {
            if (!world.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.not_a_vampire"));
            }
            return true;
        }

        if (altar.isRunning()) {
            if (!world.isRemote) {
                player.addChatMessage(TileEntityAltarInfusion.Result.STILL_RUNNING.getMessage());
            }
            return true;
        }

        TileEntityAltarInfusion.Result activationResult = altar.tryActivate(player);

        if (!player.isSneaking()) {
            if (activationResult == TileEntityAltarInfusion.Result.SUCCESS) {
                if (!world.isRemote) {
                    altar.startRitual(player);
                }
                return true;
            } else if (activationResult != TileEntityAltarInfusion.Result.MISSING_ITEMS) {
                if (!world.isRemote) {
                    player.addChatMessage(activationResult.getMessage());
                }
                return true;
            }
        }

        if (!world.isRemote) {
            player.openGui(Neorism.instance, ModGuiHandler.GUI_ALTAR_INFUSION, world, x, y, z);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityAltarInfusion) {
            TileEntityAltarInfusion altar = (TileEntityAltarInfusion) te;
            for (int i = 0; i < altar.getSizeInventory(); i++) {
                ItemStack stack = altar.getStackInSlot(i);
                if (stack != null && !world.isRemote) {
                    EntityItem entity = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, stack);
                    world.spawnEntityInWorld(entity);
                }
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
