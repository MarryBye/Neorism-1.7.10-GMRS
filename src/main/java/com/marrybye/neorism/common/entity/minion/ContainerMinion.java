package com.marrybye.neorism.common.entity.minion;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Minion Container matching TeamLapen's MinionContainer layout.
 */
public class ContainerMinion extends Container {

    private final EntityMinionBase minion;
    private final MinionInventory minionInventory;
    private final int extraSlots;

    public ContainerMinion(InventoryPlayer playerInventory, EntityMinionBase minion) {
        this.minion = minion;
        this.minionInventory = (MinionInventory) minion.getInventory();
        this.extraSlots = minionInventory != null ? minionInventory.getAvailableSize() : 9;

        if (minionInventory != null) {
            // Main hand (slot 0)
            this.addSlotToContainer(new Slot(minionInventory, MinionInventory.SLOT_MAINHAND, 7, 60));
            // Off hand (slot 1)
            this.addSlotToContainer(new Slot(minionInventory, MinionInventory.SLOT_OFFHAND, 7, 78));

            // Armor slots (boots, legs, chest, helm)
            this.addSlotToContainer(new ArmorSlot(minionInventory, MinionInventory.SLOT_FEET, 81, 22, 3));
            this.addSlotToContainer(new ArmorSlot(minionInventory, MinionInventory.SLOT_LEGS, 63, 22, 2));
            this.addSlotToContainer(new ArmorSlot(minionInventory, MinionInventory.SLOT_CHEST, 45, 22, 1));
            this.addSlotToContainer(new ArmorSlot(minionInventory, MinionInventory.SLOT_HEAD, 27, 22, 0));

            // Extra inventory slots (up to 15 slots: 3 columns x up to 5 rows)
            for (int i = 0; i < extraSlots; i++) {
                int col = i / 3;
                int row = i % 3;
                this.addSlotToContainer(new Slot(minionInventory, MinionInventory.STORAGE_OFFSET + i, 27 + 18 * col, 42 + 18 * row));
            }
        }

        // Player Inventory
        int xOffset = 27;
        int yOffset = 103;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, xOffset + col * 18, yOffset + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(playerInventory, col, xOffset + col * 18, yOffset + 58));
        }
    }

    public EntityMinionBase getMinion() {
        return minion;
    }

    public int getExtraSlots() {
        return extraSlots;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return minion != null && minion.isEntityAlive() && player.getDistanceSqToEntity(minion) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack current = slot.getStack();
            itemstack = current.copy();

            int minionSlots = MinionInventory.STORAGE_OFFSET + extraSlots;

            if (slotIndex < minionSlots) {
                // From minion to player
                if (!this.mergeItemStack(current, minionSlots, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else {
                // From player to minion
                if (!this.mergeItemStack(current, MinionInventory.STORAGE_OFFSET, minionSlots, false)) {
                    return null;
                }
            }

            if (current.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    private static class ArmorSlot extends Slot {
        private final int armorType;

        public ArmorSlot(net.minecraft.inventory.IInventory inv, int index, int x, int y, int armorType) {
            super(inv, index, x, y);
            this.armorType = armorType;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (stack == null) return false;
            if (stack.getItem() instanceof ItemArmor) {
                return ((ItemArmor) stack.getItem()).armorType == armorType;
            }
            return false;
        }
    }
}
