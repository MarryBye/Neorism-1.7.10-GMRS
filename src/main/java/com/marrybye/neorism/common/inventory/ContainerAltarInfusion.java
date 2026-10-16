package com.marrybye.neorism.common.inventory;

import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.tileentity.TileEntityAltarInfusion;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Container menu for the Altar of Infusion.
 * Ported from TeamLapen's AltarInfusionMenu.java.
 */
public class ContainerAltarInfusion extends Container {

    private final TileEntityAltarInfusion altar;

    public ContainerAltarInfusion(InventoryPlayer playerInventory, TileEntityAltarInfusion altar) {
        this.altar = altar;

        // Slot 0: Pure Blood
        this.addSlotToContainer(new Slot(altar, 0, 44, 34) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack != null && stack.getItem() == ModItems.pureBlood;
            }
        });

        // Slot 1: Human Heart
        this.addSlotToContainer(new Slot(altar, 1, 80, 34) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack != null && stack.getItem() == ModItems.humanHeart;
            }
        });

        // Slot 2: Vampire Book
        this.addSlotToContainer(new Slot(altar, 2, 116, 34) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack != null && stack.getItem() == ModItems.vampireBook;
            }
        });

        // Player Inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public TileEntityAltarInfusion getAltar() {
        return altar;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return altar != null && altar.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            if (slotIndex < 3) {
                // From Altar to Player Inventory
                if (!this.mergeItemStack(stackInSlot, 3, 39, true)) {
                    return null;
                }
            } else {
                // From Player Inventory to Altar
                if (stackInSlot.getItem() == ModItems.pureBlood) {
                    if (!this.mergeItemStack(stackInSlot, 0, 1, false)) {
                        return null;
                    }
                } else if (stackInSlot.getItem() == ModItems.humanHeart) {
                    if (!this.mergeItemStack(stackInSlot, 1, 2, false)) {
                        return null;
                    }
                } else if (stackInSlot.getItem() == ModItems.vampireBook) {
                    if (!this.mergeItemStack(stackInSlot, 2, 3, false)) {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            if (stackInSlot.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (stackInSlot.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, stackInSlot);
        }

        return itemstack;
    }
}
