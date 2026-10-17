package com.marrybye.neorism.common.tileentity;

import com.marrybye.neorism.api.items.IBloodChargeable;
import com.marrybye.neorism.common.core.ModFluids;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * TileEntity for the Blood Pedestal holding ritual catalysts and charging vampire equipment.
 * Ported 1:1 from TeamLapen's PedestalBlockEntity.java.
 */
public class TileEntityBloodPedestal extends TileEntityNeorism implements IInventory {

    private final int chargeRate = 30;
    private int chargingTicks = 0;
    private int bloodStored = 0;
    private ItemStack itemStack;

    public ItemStack getStoredItem() {
        return itemStack;
    }

    public void setStoredItem(ItemStack stack) {
        this.itemStack = stack;
        this.chargingTicks = 0;
        markForUpdate();
        markDirty();
    }

    public ItemStack takeStoredItem() {
        ItemStack item = this.itemStack;
        this.itemStack = null;
        this.chargingTicks = 0;
        markForUpdate();
        markDirty();
        return item;
    }

    public boolean hasStack() {
        return this.itemStack != null;
    }

    public int getChargingTicks() {
        return this.chargingTicks;
    }

    public int getBloodStored() {
        return this.bloodStored;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote) {
            // Client particles
            if (chargingTicks > 0 && worldObj.rand.nextInt(4) == 0) {
                worldObj.spawnParticle("reddust", xCoord + 0.5D + (worldObj.rand.nextDouble() - 0.5D) * 0.4D,
                    yCoord + 0.8D, zCoord + 0.5D + (worldObj.rand.nextDouble() - 0.5D) * 0.4D, 0.0D, 0.2D, 0.0D);
            }
            return;
        }

        // Server tick
        if (chargingTicks > 0) {
            chargingTicks--;
            if (chargingTicks == 0) {
                IBloodChargeable chargeable = getChargeItem(this.itemStack);
                if (chargeable != null && bloodStored > 0) {
                    int charged = chargeable.charge(this.itemStack, bloodStored);
                    bloodStored -= Math.max(0, charged);
                }
                markForUpdate();
                markDirty();
            }
        } else if (chargingTicks == 0) {
            IBloodChargeable chargeable = getChargeItem(this.itemStack);
            if (chargeable != null && chargeable.canBeCharged(this.itemStack)) {
                if (bloodStored < chargeRate) {
                    drainBloodFromBelow();
                }
                if (bloodStored > 0) {
                    chargingTicks = 20;
                    markForUpdate();
                    markDirty();
                } else {
                    chargingTicks = -40;
                }
            } else {
                chargingTicks = -40;
            }
        } else {
            chargingTicks++;
        }
    }

    private void drainBloodFromBelow() {
        if (worldObj == null) return;
        TileEntity teBelow = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
        if (teBelow instanceof IFluidHandler) {
            IFluidHandler handler = (IFluidHandler) teBelow;
            FluidStack drained = handler.drain(ForgeDirection.UP, 100, true);
            if (drained != null && drained.getFluid() == ModFluids.fluidBlood) {
                this.bloodStored += drained.amount;
            }
        }
    }

    public int getChargedProgress() {
        IBloodChargeable chargeItem = getChargeItem(this.itemStack);
        if (chargeItem != null) {
            return (int) (chargeItem.getChargePercentage(this.itemStack) * 10);
        } else {
            return 0;
        }
    }

    private static IBloodChargeable getChargeItem(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof IBloodChargeable) {
            return (IBloodChargeable) stack.getItem();
        }
        return null;
    }

    // --- IInventory Implementation ---

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
                markDirty();
                return res;
            } else {
                ItemStack res = itemStack.splitStack(amount);
                if (itemStack.stackSize == 0) {
                    itemStack = null;
                }
                markForUpdate();
                markDirty();
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
            setStoredItem(stack);
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
    public void openInventory() {}

    @Override
    public void closeInventory() {}

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
        tag.setInteger("bloodStored", bloodStored);
        tag.setInteger("chargingTicks", chargingTicks);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("StoredItem")) {
            this.itemStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("StoredItem"));
        } else {
            this.itemStack = null;
        }
        this.bloodStored = tag.getInteger("bloodStored");
        this.chargingTicks = tag.getInteger("chargingTicks");
    }
}
