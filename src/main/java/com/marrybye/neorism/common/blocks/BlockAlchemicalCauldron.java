package com.marrybye.neorism.common.blocks;

import java.util.Random;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.network.ModGuiHandler;
import com.marrybye.neorism.common.tileentity.TileEntityAlchemicalCauldron;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Alchemical Cauldron workstation block.
 * Canonical port of TeamLapen's AlchemicalCauldronBlock.
 * Used by hunters for brewing holy water, weapon oils, and infusing materials.
 */
public class BlockAlchemicalCauldron extends BlockModeled implements ITileEntityProvider {

    private final Random random = new Random();

    public BlockAlchemicalCauldron() {
        super("alchemical_cauldron", "cauldron_side", Material.iron);
        setHardness(3.5F);
        setResistance(8.0F);
        setStepSound(soundTypeMetal);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAlchemicalCauldron();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        player.openGui(Neorism.instance, ModGuiHandler.GUI_ALCHEMICAL_CAULDRON, world, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block oldBlock, int oldMeta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityAlchemicalCauldron) {
            TileEntityAlchemicalCauldron cauldron = (TileEntityAlchemicalCauldron) te;
            for (int i = 0; i < cauldron.getSizeInventory(); ++i) {
                ItemStack itemstack = cauldron.getStackInSlot(i);
                if (itemstack != null) {
                    float rx = this.random.nextFloat() * 0.8F + 0.1F;
                    float ry = this.random.nextFloat() * 0.8F + 0.1F;
                    float rz = this.random.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.stackSize > 0) {
                        int j = this.random.nextInt(21) + 10;
                        if (j > itemstack.stackSize) {
                            j = itemstack.stackSize;
                        }
                        itemstack.stackSize -= j;

                        EntityItem entityitem = new EntityItem(world, x + rx, y + ry, z + rz,
                            new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));
                        if (itemstack.hasTagCompound()) {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                        }

                        float f3 = 0.05F;
                        entityitem.motionX = (float) this.random.nextGaussian() * f3;
                        entityitem.motionY = (float) this.random.nextGaussian() * f3 + 0.2F;
                        entityitem.motionZ = (float) this.random.nextGaussian() * f3;
                        world.spawnEntityInWorld(entityitem);
                    }
                }
            }
            world.func_147453_f(x, y, z, oldBlock);
        }
        super.breakBlock(world, x, y, z, oldBlock, oldMeta);
    }
}
