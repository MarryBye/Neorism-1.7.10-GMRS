package com.marrybye.neorism.common.entity.minion;

import de.teamlapen.faction.api.world.entities.minion.IMinionInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

/**
 * Minion inventory storing equipment (hands & armor) and up to 15 inventory items.
 * Slots:
 * 0: Main Hand
 * 1: Off Hand
 * 2: Feet (Boots)
 * 3: Legs (Leggings)
 * 4: Chest (Chestplate)
 * 5: Head (Helmet)
 * 6..20: Storage (9, 12, or 15 slots)
 */
public class MinionInventory implements IMinionInventory {

    public static final int SLOT_MAINHAND = 0;
    public static final int SLOT_OFFHAND = 1;
    public static final int SLOT_FEET = 2;
    public static final int SLOT_LEGS = 3;
    public static final int SLOT_CHEST = 4;
    public static final int SLOT_HEAD = 5;
    public static final int STORAGE_OFFSET = 6;
    public static final int MAX_STORAGE = 15;
    public static final int TOTAL_SLOTS = STORAGE_OFFSET + MAX_STORAGE; // 21

    private final ItemStack[] inventory = new ItemStack[TOTAL_SLOTS];
    private int availableStorage = 9;

    public MinionInventory() {
        this(9);
    }

    public MinionInventory(int availableStorage) {
        this.availableStorage = Math.max(9, Math.min(availableStorage, MAX_STORAGE));
    }

    @Override
    public int getSizeInventory() {
        return STORAGE_OFFSET + availableStorage;
    }

    @Override
    public int getAvailableSize() {
        return availableStorage;
    }

    public void setAvailableSize(int size) {
        this.availableStorage = Math.max(9, Math.min(size, MAX_STORAGE));
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= 0 && slot < inventory.length) {
            return inventory[slot];
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (slot >= 0 && slot < inventory.length && inventory[slot] != null) {
            ItemStack stack;
            if (inventory[slot].stackSize <= count) {
                stack = inventory[slot];
                inventory[slot] = null;
                markDirty();
                return stack;
            } else {
                stack = inventory[slot].splitStack(count);
                if (inventory[slot].stackSize == 0) {
                    inventory[slot] = null;
                }
                markDirty();
                return stack;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (slot >= 0 && slot < inventory.length && inventory[slot] != null) {
            ItemStack stack = inventory[slot];
            inventory[slot] = null;
            return stack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot >= 0 && slot < inventory.length) {
            inventory[slot] = stack;
            if (stack != null && stack.stackSize > getInventoryStackLimit()) {
                stack.stackSize = getInventoryStackLimit();
            }
            markDirty();
        }
    }

    @Override
    public String getInventoryName() {
        return "container.neorism.minion";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public void addItemStack(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return;

        // Try merge into existing storage slots
        for (int i = STORAGE_OFFSET; i < STORAGE_OFFSET + availableStorage; i++) {
            ItemStack existing = inventory[i];
            if (existing != null && existing.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(existing, stack)) {
                int max = Math.min(getInventoryStackLimit(), existing.getMaxStackSize());
                int space = max - existing.stackSize;
                if (space > 0) {
                    int toAdd = Math.min(space, stack.stackSize);
                    existing.stackSize += toAdd;
                    stack.stackSize -= toAdd;
                    markDirty();
                    if (stack.stackSize <= 0) return;
                }
            }
        }

        // Try insert into empty storage slots
        for (int i = STORAGE_OFFSET; i < STORAGE_OFFSET + availableStorage; i++) {
            if (inventory[i] == null) {
                inventory[i] = stack.copy();
                stack.stackSize = 0;
                markDirty();
                return;
            }
        }
    }

    @Override
    public ItemStack[] getInventoryArmor() {
        return new ItemStack[]{
            inventory[SLOT_FEET],
            inventory[SLOT_LEGS],
            inventory[SLOT_CHEST],
            inventory[SLOT_HEAD]
        };
    }

    @Override
    public ItemStack[] getInventoryHands() {
        return new ItemStack[]{
            inventory[SLOT_MAINHAND],
            inventory[SLOT_OFFHAND]
        };
    }

    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(itemTag);
                list.appendTag(itemTag);
            }
        }
        compound.setTag("Items", list);
        compound.setInteger("StorageSize", availableStorage);
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("StorageSize")) {
            this.availableStorage = compound.getInteger("StorageSize");
        }
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = null;
        }
        NBTTagList list = compound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound itemTag = list.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < inventory.length) {
                inventory[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }
    }
}
