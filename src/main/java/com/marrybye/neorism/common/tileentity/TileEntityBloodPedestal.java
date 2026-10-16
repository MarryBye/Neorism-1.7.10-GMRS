package com.marrybye.neorism.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * TileEntity for the Blood Pedestal holding ritual catalysts and pure blood.
 */
public class TileEntityBloodPedestal extends TileEntityNeorism implements IInventory {

    private ItemStack itemStack;

    public ItemStack getStoredItem() {
        return itemStack;
    }

    public void setStoredItem(ItemStack stack) {
        this.itemStack = stack;
        markForUpdate();
    }

    public ItemStack takeStoredItem() {
        ItemStack item = this.itemStack;
        this.itemStack = null;
        markForUpdate();
        return item;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? itemStack : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot == 0 && itemStack != null) {
            if (itemStack.stackSize <= amount) {
                ItemStack res = itemStack;
                itemStack = null;
                markForUpdate();
                return res;
            } else {
                ItemStack res = itemStack.splitStack(amount);
                if (itemStack.stackSize == 0) {
                    itemStack = null;
                }
                markForUpdate();
                return res;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return takeStoredItem();
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot == 0) {
            this.itemStack = stack;
            markForUpdate();
        }
    }

    @Override
    public String getInventoryName() {
        return "container.neorism.blood_pedestal";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this &&
            player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot == 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (itemStack != null) {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemStack.writeToNBT(itemTag);
            tag.setTag("StoredItem", itemTag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("StoredItem")) {
            this.itemStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("StoredItem"));
        } else {
            this.itemStack = null;
        }
    }
}
