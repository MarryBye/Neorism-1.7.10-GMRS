package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.core.ModFluids;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.items.ItemBloodBottle;
import com.marrybye.neorism.common.items.ItemBloodFlask;
import com.marrybye.neorism.common.tileentity.TileEntityBloodContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Blood Container multiblock storage block.
 * Supports full bidirectional interaction with buckets, blood bottles, and flasks.
 * Ported 1:1 from TeamLapen's BloodContainerBlock.java.
 */
public class BlockBloodContainer extends BlockModeled implements ITileEntityProvider {

    public BlockBloodContainer() {
        super("blood_container", "blood_container_side", Material.glass);
        setHardness(1.5F);
        setStepSound(soundTypeGlass);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 1.0F, 0.875F);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityBloodContainer();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityBloodContainer)) return false;

        TileEntityBloodContainer container = (TileEntityBloodContainer) te;
        ItemStack held = player.getHeldItem();

        if (held != null) {
            // 1. Bucket interactions
            if (held.getItem() == Items.bucket && container.getBloodAmount() >= 1000) {
                container.drainBlood(1000);
                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                    if (!player.inventory.addItemStackToInventory(new ItemStack(ModFluids.bloodBucket))) {
                        player.dropPlayerItemWithRandomChoice(new ItemStack(ModFluids.bloodBucket), false);
                    }
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.potion.smash", 0.8F, 1.0F);
                return true;
            } else if (held.getItem() == ModFluids.bloodBucket && container.getBloodAmount() <= container.getCapacity() - 1000) {
                container.addBlood(1000);
                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                    if (!player.inventory.addItemStackToInventory(new ItemStack(Items.bucket))) {
                        player.dropPlayerItemWithRandomChoice(new ItemStack(Items.bucket), false);
                    }
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "liquid.water", 0.8F, 1.0F);
                return true;
            }

            // 2. Empty vanilla glass bottle -> fill from container
            if (held.getItem() == Items.glass_bottle && container.getBloodAmount() >= 200) {
                container.drainBlood(200);
                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                    ItemStack bottle = new ItemStack(ModItems.bloodBottle, 1, 1);
                    if (!player.inventory.addItemStackToInventory(bottle)) {
                        player.dropPlayerItemWithRandomChoice(bottle, false);
                    }
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.potion.smash", 0.8F, 1.0F);
                return true;
            }

            // 3. Mod Blood Bottle interaction
            if (held.getItem() == ModItems.bloodBottle) {
                int curDamage = held.getItemDamage();
                if (curDamage > 0 && container.getBloodAmount() < container.getCapacity()) {
                    // Pour blood from bottle into container
                    int canAcceptUnits = (container.getCapacity() - container.getBloodAmount()) / 100;
                    int transfer = Math.min(curDamage, canAcceptUnits);
                    if (transfer > 0) {
                        container.addBlood(transfer * 100);
                        held.setItemDamage(curDamage - transfer);
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "liquid.water", 0.8F, 1.0F);
                        return true;
                    }
                } else if (curDamage < ItemBloodBottle.MAX_BLOOD && container.getBloodAmount() >= 100) {
                    // Fill bottle from container
                    int neededUnits = ItemBloodBottle.MAX_BLOOD - curDamage;
                    int availUnits = container.getBloodAmount() / 100;
                    int transfer = Math.min(neededUnits, availUnits);
                    if (transfer > 0) {
                        container.drainBlood(transfer * 100);
                        held.setItemDamage(curDamage + transfer);
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.potion.smash", 0.7F, 1.2F);
                        return true;
                    }
                }
            }

            // 4. Mod Blood Flask interaction
            if (held.getItem() == ModItems.bloodFlask) {
                int curFlaskBlood = ItemBloodFlask.getBlood(held);
                if (curFlaskBlood > 0 && container.getBloodAmount() < container.getCapacity()) {
                    // Pour blood from flask into container
                    int canAcceptUnits = (container.getCapacity() - container.getBloodAmount()) / 100;
                    int transfer = Math.min(curFlaskBlood, canAcceptUnits);
                    if (transfer > 0) {
                        container.addBlood(transfer * 100);
                        ItemBloodFlask.setBlood(held, curFlaskBlood - transfer);
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "liquid.water", 0.8F, 1.0F);
                        return true;
                    }
                } else if (curFlaskBlood < ItemBloodFlask.CAPACITY && container.getBloodAmount() >= 100) {
                    // Fill flask from container
                    int spaceUnits = ItemBloodFlask.CAPACITY - curFlaskBlood;
                    int availUnits = container.getBloodAmount() / 100;
                    int transfer = Math.min(spaceUnits, availUnits);
                    if (transfer > 0) {
                        container.drainBlood(transfer * 100);
                        ItemBloodFlask.setBlood(held, curFlaskBlood + transfer);
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "liquid.water", 0.8F, 1.2F);
                        return true;
                    }
                }
            }
        }

        // Status check
        player.addChatMessage(new ChatComponentTranslation("chat.neorism.blood_container_status",
            container.getBloodAmount(), container.getCapacity()));
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityBloodContainer) {
            TileEntityBloodContainer container = (TileEntityBloodContainer) te;
            if (container.getBloodAmount() == 0) return 0;
            return 1 + (int) ((float) container.getBloodAmount() / (float) container.getCapacity() * 14.0F);
        }
        return 0;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, net.minecraft.entity.EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, placer, stack);
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("FluidAmount")) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityBloodContainer) {
                ((TileEntityBloodContainer) te).addBlood(stack.getTagCompound().getInteger("FluidAmount"));
            }
        }
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        if (!world.isRemote && !player.capabilities.isCreativeMode) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityBloodContainer) {
                TileEntityBloodContainer container = (TileEntityBloodContainer) te;
                int blood = container.getBloodAmount();
                ItemStack drop = new ItemStack(this, 1);
                if (blood > 0) {
                    net.minecraft.nbt.NBTTagCompound tag = new net.minecraft.nbt.NBTTagCompound();
                    tag.setInteger("FluidAmount", blood);
                    drop.setTagCompound(tag);
                }
                dropBlockAsItem(world, x, y, z, drop);
            }
        }
        super.onBlockHarvested(world, x, y, z, meta, player);
    }

    @Override
    public java.util.ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new java.util.ArrayList<ItemStack>();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }
}
