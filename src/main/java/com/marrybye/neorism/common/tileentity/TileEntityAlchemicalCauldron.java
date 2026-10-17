package com.marrybye.neorism.common.tileentity;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

/**
 * TileEntity for the Alchemical Cauldron workstation.
 * Canonical port of TeamLapen's AlchemicalCauldronBlockEntity.
 * Slots:
 *   0: Fluid container
 *   1: Ingredient
 *   2: Result
 *   3: Fuel
 */
public class TileEntityAlchemicalCauldron extends TileEntity implements IInventory {

    private ItemStack[] inventory = new ItemStack[4];

    public int burnTime = 0;
    public int currentItemBurnTime = 0;
    public int cookTime = 0;
    public static final int TOTAL_COOK_TIME = 200; // 10 seconds

    @Override
    public int getSizeInventory() {
        return this.inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.inventory[slot] != null) {
            ItemStack itemstack;
            if (this.inventory[slot].stackSize <= amount) {
                itemstack = this.inventory[slot];
                this.inventory[slot] = null;
                this.markDirty();
                return itemstack;
            } else {
                itemstack = this.inventory[slot].splitStack(amount);
                if (this.inventory[slot].stackSize == 0) {
                    this.inventory[slot] = null;
                }
                this.markDirty();
                return itemstack;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.inventory[slot] != null) {
            ItemStack itemstack = this.inventory[slot];
            this.inventory[slot] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inventory[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return "container.neorism.alchemical_cauldron";
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
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
            && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == 2) return false;
        if (slot == 3) return TileEntityFurnace.isItemFuel(stack);
        return true;
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public void updateEntity() {
        boolean wasBurning = this.isBurning();
        boolean dirty = false;

        if (this.burnTime > 0) {
            --this.burnTime;
        }

        if (!this.worldObj.isRemote) {
            if (this.burnTime == 0 && this.canBrew()) {
                this.currentItemBurnTime = this.burnTime = TileEntityFurnace.getItemBurnTime(this.inventory[3]);
                if (this.burnTime > 0) {
                    dirty = true;
                    if (this.inventory[3] != null) {
                        --this.inventory[3].stackSize;
                        if (this.inventory[3].stackSize == 0) {
                            this.inventory[3] = this.inventory[3].getItem().getContainerItem(this.inventory[3]);
                        }
                    }
                }
            }

            if (this.isBurning() && this.canBrew()) {
                ++this.cookTime;
                if (this.cookTime >= TOTAL_COOK_TIME) {
                    this.cookTime = 0;
                    this.brewItem();
                    dirty = true;
                }
            } else {
                this.cookTime = 0;
            }

            if (wasBurning != this.isBurning()) {
                dirty = true;
            }
        }

        if (dirty) {
            this.markDirty();
        }
    }

    private boolean canBrew() {
        if (this.inventory[0] == null || this.inventory[1] == null) {
            return false;
        }

        ItemStack result = getBrewingResult(this.inventory[0], this.inventory[1]);
        if (result == null) return false;

        if (this.inventory[2] == null) return true;
        if (!this.inventory[2].isItemEqual(result)) return false;
        int newSize = this.inventory[2].stackSize + result.stackSize;
        return newSize <= this.getInventoryStackLimit() && newSize <= this.inventory[2].getMaxStackSize();
    }

    private void brewItem() {
        if (this.canBrew()) {
            ItemStack result = getBrewingResult(this.inventory[0], this.inventory[1]);
            if (this.inventory[2] == null) {
                this.inventory[2] = result.copy();
            } else if (this.inventory[2].isItemEqual(result)) {
                this.inventory[2].stackSize += result.stackSize;
            }

            --this.inventory[0].stackSize;
            if (this.inventory[0].stackSize <= 0) {
                this.inventory[0] = null;
            }

            --this.inventory[1].stackSize;
            if (this.inventory[1].stackSize <= 0) {
                this.inventory[1] = null;
            }
        }
    }

    public static ItemStack getBrewingResult(ItemStack fluid, ItemStack ingredient) {
        if (fluid == null || ingredient == null) return null;

        Item fItem = fluid.getItem();
        Item iItem = ingredient.getItem();
        int fMeta = fluid.getItemDamage();

        // Water Bottle checks (Items.potionitem with damage 0)
        boolean isWater = fItem == Items.potionitem && fMeta == 0;
        boolean isGlassBottle = fItem == Items.glass_bottle;

        // Holy water brewing
        if (isWater && iItem == ModItems.garlic) {
            return new ItemStack(ModItems.holyWaterBottle, 1, 0); // Normal
        }
        if (fItem == ModItems.holyWaterBottle && fMeta == 0 && iItem == ModItems.purifiedGarlic) {
            return new ItemStack(ModItems.holyWaterBottle, 1, 1); // Enhanced
        }
        if (fItem == ModItems.holyWaterBottle && fMeta == 1 && iItem == ModItems.pureBlood) {
            return new ItemStack(ModItems.holyWaterBottle, 1, 2); // Ultimate
        }

        // Splash holy water brewing
        if (fItem == ModItems.holyWaterBottle && iItem == Items.gunpowder) {
            return new ItemStack(ModItems.holyWaterSplashBottle, 1, fMeta);
        }
        if (isWater && iItem == Items.gunpowder) {
            return new ItemStack(ModItems.holyWaterSplashBottle, 1, 0);
        }

        // Purified Garlic
        if (isGlassBottle && iItem == ModItems.garlic) {
            return new ItemStack(ModItems.purifiedGarlic, 1, 0);
        }

        // Blood Infused metals
        if (fItem == ModItems.pureBlood && iItem == Items.iron_ingot) {
            return new ItemStack(ModItems.bloodInfusedIron, 1, 0);
        }
        if (fItem == ModItems.pureBlood && iItem == Items.gold_ingot) {
            return new ItemStack(ModItems.bloodInfusedGold, 1, 0);
        }
        if (fItem == ModItems.pureBlood && iItem == Items.diamond) {
            return new ItemStack(ModItems.bloodInfusedDiamond, 1, 0);
        }

        // Alchemical Weapon Oils
        if (isWater || isGlassBottle) {
            if (iItem == ModItems.vampireFang) {
                return new ItemStack(ModItems.oilBottle, 1, 0);
            }
            if (iItem == Item.getItemFromBlock(ModBlocks.vampireOrchid)) {
                return new ItemStack(ModItems.oilBottle, 1, 1);
            }
            if (iItem == Item.getItemFromBlock(ModBlocks.cursedRoots)) {
                return new ItemStack(ModItems.oilBottle, 1, 2);
            }
            if (iItem == Item.getItemFromBlock(ModBlocks.cursedEarth)) {
                return new ItemStack(ModItems.oilBottle, 1, 3);
            }
        }

        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagList nbttaglist = tag.getTagList("Items", 10);
        this.inventory = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound slotTag = nbttaglist.getCompoundTagAt(i);
            byte b0 = slotTag.getByte("Slot");
            if (b0 >= 0 && b0 < this.inventory.length) {
                this.inventory[b0] = ItemStack.loadItemStackFromNBT(slotTag);
            }
        }

        this.burnTime = tag.getShort("BurnTime");
        this.cookTime = tag.getShort("CookTime");
        this.currentItemBurnTime = tag.getShort("CurrentItemBurnTime");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setShort("BurnTime", (short) this.burnTime);
        tag.setShort("CookTime", (short) this.cookTime);
        tag.setShort("CurrentItemBurnTime", (short) this.currentItemBurnTime);

        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(slotTag);
                nbttaglist.appendTag(slotTag);
            }
        }
        tag.setTag("Items", nbttaglist);
    }
}
