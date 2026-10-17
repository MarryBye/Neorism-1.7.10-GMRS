package com.marrybye.neorism.common.inventory;

import com.marrybye.neorism.common.tileentity.TileEntityAlchemicalCauldron;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

/**
 * Container for the Alchemical Cauldron workstation.
 * Canonical port of TeamLapen's AlchemicalCauldronMenu.
 */
public class ContainerAlchemicalCauldron extends Container {

    private final TileEntityAlchemicalCauldron cauldron;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;

    public ContainerAlchemicalCauldron(InventoryPlayer playerInv, TileEntityAlchemicalCauldron tile) {
        this.cauldron = tile;

        // Slot 0: Fluid container
        this.addSlotToContainer(new Slot(tile, 0, 56, 17));

        // Slot 1: Ingredient
        this.addSlotToContainer(new Slot(tile, 1, 79, 17));

        // Slot 2: Result (output only)
        this.addSlotToContainer(new Slot(tile, 2, 116, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });

        // Slot 3: Fuel
        this.addSlotToContainer(new Slot(tile, 3, 56, 53) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return TileEntityFurnace.isItemFuel(stack);
            }
        });

        // Player Inventory (Slots 4-30)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Player Hotbar (Slots 31-39)
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter) {
        super.addCraftingToCrafters(crafter);
        crafter.sendProgressBarUpdate(this, 0, this.cauldron.cookTime);
        crafter.sendProgressBarUpdate(this, 1, this.cauldron.burnTime);
        crafter.sendProgressBarUpdate(this, 2, this.cauldron.currentItemBurnTime);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i) {
            ICrafting icrafting = (ICrafting) this.crafters.get(i);

            if (this.lastCookTime != this.cauldron.cookTime) {
                icrafting.sendProgressBarUpdate(this, 0, this.cauldron.cookTime);
            }
            if (this.lastBurnTime != this.cauldron.burnTime) {
                icrafting.sendProgressBarUpdate(this, 1, this.cauldron.burnTime);
            }
            if (this.lastItemBurnTime != this.cauldron.currentItemBurnTime) {
                icrafting.sendProgressBarUpdate(this, 2, this.cauldron.currentItemBurnTime);
            }
        }

        this.lastCookTime = this.cauldron.cookTime;
        this.lastBurnTime = this.cauldron.burnTime;
        this.lastItemBurnTime = this.cauldron.currentItemBurnTime;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            this.cauldron.cookTime = data;
        } else if (id == 1) {
            this.cauldron.burnTime = data;
        } else if (id == 2) {
            this.cauldron.currentItemBurnTime = data;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.cauldron.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 2) { // Result slot
                if (!this.mergeItemStack(itemstack1, 4, 40, true)) {
                    return null;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 1 && index != 0 && index != 3) { // From inventory
                if (TileEntityFurnace.isItemFuel(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 3, 4, false)) {
                        return null;
                    }
                } else if (!this.mergeItemStack(itemstack1, 0, 2, false)) {
                    return null;
                } else if (index >= 4 && index < 31) {
                    if (!this.mergeItemStack(itemstack1, 31, 40, false)) return null;
                } else if (index >= 31 && index < 40 && !this.mergeItemStack(itemstack1, 4, 31, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 4, 40, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }
}
