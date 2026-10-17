package com.marrybye.neorism.common.inventory;

import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.hunter.HunterLeveling;
import com.marrybye.neorism.common.hunter.HunterLeveling.HunterTableRequirement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Container for the Hunter Table workstation.
 * Canonical port of TeamLapen's HunterTableMenu.
 * Handles the 4 input slots and output research result.
 */
public class ContainerHunterTable extends Container {

    private final IInventory craftMatrix = new InventoryBasic("HunterTable", false, 4) {
        @Override
        public void markDirty() {
            super.markDirty();
            ContainerHunterTable.this.onCraftMatrixChanged(this);
        }
    };

    private final IInventory craftResult = new InventoryCraftResult();
    private final EntityPlayer player;
    private final World worldObj;
    private final int posX;
    private final int posY;
    private final int posZ;

    public ContainerHunterTable(InventoryPlayer playerInv, World world, int x, int y, int z) {
        this.player = playerInv.player;
        this.worldObj = world;
        this.posX = x;
        this.posY = y;
        this.posZ = z;

        // Input slots
        // Slot 0: Book
        this.addSlotToContainer(new Slot(this.craftMatrix, 0, 15, 28) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack != null && stack.getItem() == Items.book;
            }
        });

        // Slot 1: Vampire Fang
        this.addSlotToContainer(new Slot(this.craftMatrix, 1, 42, 28) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack != null && stack.getItem() == ModItems.vampireFang;
            }
        });

        // Slot 2: Pure Blood
        this.addSlotToContainer(new Slot(this.craftMatrix, 2, 69, 28) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack != null && stack.getItem() == ModItems.pureBlood;
            }
        });

        // Slot 3: Vampire Book
        this.addSlotToContainer(new Slot(this.craftMatrix, 3, 96, 28) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack != null && stack.getItem() == ModItems.vampireBook;
            }
        });

        // Slot 4: Result slot
        this.addSlotToContainer(new Slot(this.craftResult, 0, 146, 28) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
                FactionPlayerHandler fph = FactionPlayerHandler.get(playerIn);
                int targetLevel = (fph != null ? fph.getCurrentLevel() : 0) + 1;
                HunterTableRequirement req = HunterLeveling.getTableRequirement(targetLevel);
                if (req != null) {
                    craftMatrix.decrStackSize(0, 1);
                    if (req.vampireFangQuantity > 0) {
                        craftMatrix.decrStackSize(1, req.vampireFangQuantity);
                    }
                    if (req.pureBloodQuantity > 0) {
                        craftMatrix.decrStackSize(2, req.pureBloodQuantity);
                    }
                    if (req.vampireBookQuantity > 0) {
                        craftMatrix.decrStackSize(3, req.vampireBookQuantity);
                    }
                }
                worldObj.playSoundAtEntity(playerIn, "random.levelup", 0.75F, 1.2F);
                super.onPickupFromSlot(playerIn, stack);
            }
        });

        // Player Inventory (Slots 5-31)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Player Hotbar (Slots 32-40)
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inv) {
        FactionPlayerHandler fph = FactionPlayerHandler.get(this.player);
        int targetLevel = (fph != null ? fph.getCurrentLevel() : 0) + 1;
        HunterTableRequirement req = HunterLeveling.getTableRequirement(targetLevel);

        if (req != null && isRequirementFulfilled(req)) {
            this.craftResult.setInventorySlotContents(0, new ItemStack(ModItems.hunterIntel, 1, req.resultIntelTier));
        } else {
            this.craftResult.setInventorySlotContents(0, null);
        }
    }

    private boolean isRequirementFulfilled(HunterTableRequirement req) {
        ItemStack bookStack = this.craftMatrix.getStackInSlot(0);
        if (bookStack == null || bookStack.getItem() != Items.book || bookStack.stackSize < 1) {
            return false;
        }

        ItemStack fangStack = this.craftMatrix.getStackInSlot(1);
        int fangs = fangStack != null && fangStack.getItem() == ModItems.vampireFang ? fangStack.stackSize : 0;
        if (fangs < req.vampireFangQuantity) {
            return false;
        }

        if (req.pureBloodQuantity > 0) {
            ItemStack bloodStack = this.craftMatrix.getStackInSlot(2);
            if (bloodStack == null || bloodStack.getItem() != ModItems.pureBlood || bloodStack.stackSize < req.pureBloodQuantity) {
                return false;
            }
            if (bloodStack.getItemDamage() < req.pureBloodLevel) {
                return false;
            }
        }

        if (req.vampireBookQuantity > 0) {
            ItemStack vBookStack = this.craftMatrix.getStackInSlot(3);
            if (vBookStack == null || vBookStack.getItem() != ModItems.vampireBook || vBookStack.stackSize < req.vampireBookQuantity) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.worldObj.getBlock(this.posX, this.posY, this.posZ) == com.marrybye.neorism.common.core.ModBlocks.hunterTable
            && player.getDistanceSq(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 4) { // Output slot
                if (!this.mergeItemStack(itemstack1, 5, 41, true)) {
                    return null;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= 5 && index < 41) { // Player inventory
                if (itemstack1.getItem() == Items.book) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) return null;
                } else if (itemstack1.getItem() == ModItems.vampireFang) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) return null;
                } else if (itemstack1.getItem() == ModItems.pureBlood) {
                    if (!this.mergeItemStack(itemstack1, 2, 3, false)) return null;
                } else if (itemstack1.getItem() == ModItems.vampireBook) {
                    if (!this.mergeItemStack(itemstack1, 3, 4, false)) return null;
                } else if (index >= 5 && index < 32) {
                    if (!this.mergeItemStack(itemstack1, 32, 41, false)) return null;
                } else if (index >= 32 && index < 41 && !this.mergeItemStack(itemstack1, 5, 32, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 5, 41, false)) {
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

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!this.worldObj.isRemote) {
            for (int i = 0; i < 4; ++i) {
                ItemStack stack = this.craftMatrix.getStackInSlotOnClosing(i);
                if (stack != null) {
                    playerIn.dropPlayerItemWithRandomChoice(stack, false);
                }
            }
        }
    }
}
