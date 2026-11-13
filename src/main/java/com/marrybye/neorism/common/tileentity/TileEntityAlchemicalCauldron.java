package com.marrybye.neorism.common.tileentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.oredict.OreDictionary;

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
        if (!this.inventory[2].isItemEqual(result) || !ItemStack.areItemStackTagsEqual(this.inventory[2], result)) return false;
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

            ModLogger.ritual("Alchemical Cauldron brewed %s x%d at (%d, %d, %d)",
                result.getUnlocalizedName(), result.stackSize, xCoord, yCoord, zCoord);

            ItemStack fluidContainer = this.inventory[0].getItem().getContainerItem(this.inventory[0]);
            --this.inventory[0].stackSize;
            if (this.inventory[0].stackSize <= 0) {
                this.inventory[0] = fluidContainer;
            } else if (fluidContainer != null && this.worldObj != null && !this.worldObj.isRemote) {
                EntityItem drop = new EntityItem(this.worldObj, this.xCoord + 0.5D, this.yCoord + 1.0D, this.zCoord + 0.5D, fluidContainer.copy());
                this.worldObj.spawnEntityInWorld(drop);
            }

            --this.inventory[1].stackSize;
            if (this.inventory[1].stackSize <= 0) {
                this.inventory[1] = null;
            }
        }
    }

    /**
     * Canonical cauldron recipe entry shared between TileEntity brewing logic and NEI.
     */
    public static class CauldronRecipe {
        public final List<ItemStack> fluidInputs;
        public final List<ItemStack> ingredients;
        public final ItemStack result;
        public final int cookTime;

        public CauldronRecipe(ItemStack fluid, ItemStack ingredient, ItemStack result) {
            this(Collections.singletonList(fluid), Collections.singletonList(ingredient), result, TOTAL_COOK_TIME);
        }

        public CauldronRecipe(List<ItemStack> fluidInputs, List<ItemStack> ingredients, ItemStack result, int cookTime) {
            this.fluidInputs = fluidInputs;
            this.ingredients = ingredients;
            this.result = result;
            this.cookTime = cookTime;
        }

        public boolean matches(ItemStack fluid, ItemStack ingredient) {
            return matchesAny(this.fluidInputs, fluid) && matchesAny(this.ingredients, ingredient);
        }

        private static boolean matchesAny(List<ItemStack> candidates, ItemStack actual) {
            if (actual == null) return false;
            Item actualItem = actual.getItem();
            int actualDmg = actual.getItemDamage();
            for (ItemStack cand : candidates) {
                if (cand == null || cand.getItem() != actualItem) continue;
                int candDmg = cand.getItemDamage();
                if (candDmg == OreDictionary.WILDCARD_VALUE || candDmg == actualDmg) {
                    return true;
                }
            }
            return false;
        }
    }

    private static List<CauldronRecipe> recipes = null;

    public static List<CauldronRecipe> getRecipes() {
        if (recipes == null) {
            recipes = buildCanonicalRecipes();
        }
        return recipes;
    }

    private static List<ItemStack> stacksOf(Item item, int... metas) {
        List<ItemStack> list = new ArrayList<ItemStack>(metas.length);
        for (int meta : metas) {
            list.add(new ItemStack(item, 1, meta));
        }
        return list;
    }

    private static List<ItemStack> pureBloodRange(int minTier, int maxTier) {
        List<ItemStack> list = new ArrayList<ItemStack>(maxTier - minTier + 1);
        for (int i = minTier; i <= maxTier; i++) {
            list.add(new ItemStack(ModItems.pureBlood, 1, i));
        }
        return list;
    }

    private static List<CauldronRecipe> buildCanonicalRecipes() {
        List<CauldronRecipe> list = new ArrayList<CauldronRecipe>();

        ItemStack waterBottle = new ItemStack(Items.potionitem, 1, 0);
        ItemStack waterBucket = new ItemStack(Items.water_bucket, 1, 0);
        ItemStack glassBottle = new ItemStack(Items.glass_bottle, 1, 0);
        List<ItemStack> allHolyWater = stacksOf(ModItems.holyWaterBottle, 0, 1, 2);
        List<ItemStack> allPureBlood = pureBloodRange(0, 4);
        List<ItemStack> lowPureBlood = pureBloodRange(0, 3);
        List<ItemStack> allWool = Collections.singletonList(new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE));

        // Holy Water progression
        list.add(new CauldronRecipe(waterBottle, new ItemStack(ModItems.garlic), new ItemStack(ModItems.holyWaterBottle, 1, 0)));
        list.add(new CauldronRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 0), new ItemStack(ModItems.purifiedGarlic), new ItemStack(ModItems.holyWaterBottle, 1, 1)));
        list.add(new CauldronRecipe(Collections.singletonList(new ItemStack(ModItems.holyWaterBottle, 1, 1)), allPureBlood, new ItemStack(ModItems.holyWaterBottle, 1, 2), TOTAL_COOK_TIME));

        // Alchemical Fire (canonical alchemical_fire_4/5/6)
        list.add(new CauldronRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 0), new ItemStack(Items.gunpowder), new ItemStack(ModItems.alchemicalFire, 4)));
        list.add(new CauldronRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 1), new ItemStack(Items.gunpowder), new ItemStack(ModItems.alchemicalFire, 5)));
        list.add(new CauldronRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 2), new ItemStack(Items.gunpowder), new ItemStack(ModItems.alchemicalFire, 6)));

        // Pure Salt & Purified Garlic
        list.add(new CauldronRecipe(waterBucket, new ItemStack(ModItems.garlic), new ItemStack(ModItems.pureSalt, 4)));
        list.add(new CauldronRecipe(allHolyWater, Collections.singletonList(new ItemStack(ModItems.garlic)), new ItemStack(ModItems.purifiedGarlic, 2), TOTAL_COOK_TIME));

        // Garlic Diffuser Cores (Normal, Long, Strong)
        list.add(new CauldronRecipe(Collections.singletonList(new ItemStack(ModItems.garlic)), allWool, new ItemStack(ModItems.garlicDiffuserCore, 1, 0), TOTAL_COOK_TIME));
        list.add(new CauldronRecipe(Collections.singletonList(new ItemStack(ModItems.purifiedGarlic)), allWool, new ItemStack(ModItems.garlicDiffuserCore, 1, 1), TOTAL_COOK_TIME));
        list.add(new CauldronRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 2), new ItemStack(ModItems.garlicDiffuserCore, 1, 0), new ItemStack(ModItems.garlicDiffuserCore, 1, 2)));

        // Blood Infused Ingots / Gems / Blocks
        list.add(new CauldronRecipe(allPureBlood, Collections.singletonList(new ItemStack(Items.iron_ingot)), new ItemStack(ModItems.bloodInfusedIron, 1, 0), TOTAL_COOK_TIME));
        list.add(new CauldronRecipe(allPureBlood, Collections.singletonList(new ItemStack(Items.gold_ingot)), new ItemStack(ModItems.bloodInfusedGold, 1, 0), TOTAL_COOK_TIME));
        list.add(new CauldronRecipe(allPureBlood, Collections.singletonList(new ItemStack(Items.diamond)), new ItemStack(ModItems.bloodInfusedDiamond, 1, 0), TOTAL_COOK_TIME));
        list.add(new CauldronRecipe(lowPureBlood, Collections.singletonList(new ItemStack(Blocks.iron_block)), new ItemStack(ModBlocks.bloodInfusedIronBlock, 1, 0), TOTAL_COOK_TIME));
        list.add(new CauldronRecipe(new ItemStack(ModItems.pureBlood, 1, 4), new ItemStack(Blocks.iron_block), new ItemStack(ModBlocks.bloodInfusedEnhancedIronBlock, 1, 0)));
        list.add(new CauldronRecipe(new ItemStack(ModItems.sovereignBlood, 1, 0), new ItemStack(Blocks.iron_block), new ItemStack(ModBlocks.bloodInfusedRefinedIronBlock, 1, 0)));

        // Alchemical Weapon Oils (canonical AlchemyTable -> 1.7.10 Cauldron adaptation, metas 0..24)
        ItemStack plantOil = new ItemStack(ModItems.oilBottle, 1, 0);
        ItemStack vampireBloodOil = new ItemStack(ModItems.oilBottle, 1, 1);
        ItemStack garlicOil = new ItemStack(ModItems.oilBottle, 1, 21);

        list.add(new CauldronRecipe(glassBottle, new ItemStack(Items.wheat_seeds), plantOil)); // 0 plant
        list.add(new CauldronRecipe(plantOil, new ItemStack(ModItems.vampireBloodBottle), vampireBloodOil)); // 1 vampire_blood
        list.add(new CauldronRecipe(plantOil, new ItemStack(ModItems.sovereignBlood), new ItemStack(ModItems.oilBottle, 1, 2))); // 2 sovereign_blood
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), stacksOf(Items.potionitem, 8196, 8228, 8260), new ItemStack(ModItems.oilBottle, 1, 3), TOTAL_COOK_TIME)); // 3 poison
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), stacksOf(Items.potionitem, 8200, 8264), new ItemStack(ModItems.oilBottle, 1, 4), TOTAL_COOK_TIME)); // 4 weakness
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), stacksOf(Items.potionitem, 8202, 8266), new ItemStack(ModItems.oilBottle, 1, 5), TOTAL_COOK_TIME)); // 5 slowness
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), stacksOf(Items.potionitem, 8197, 8229), new ItemStack(ModItems.oilBottle, 1, 6), TOTAL_COOK_TIME)); // 6 healing
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), stacksOf(Items.potionitem, 8195, 8259), new ItemStack(ModItems.oilBottle, 1, 7), TOTAL_COOK_TIME)); // 7 fire_resistance
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), stacksOf(Items.potionitem, 8194, 8226, 8258), new ItemStack(ModItems.oilBottle, 1, 8), TOTAL_COOK_TIME)); // 8 swiftness
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), stacksOf(Items.potionitem, 8193, 8225, 8257), new ItemStack(ModItems.oilBottle, 1, 9), TOTAL_COOK_TIME)); // 9 regeneration
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), stacksOf(Items.potionitem, 8198, 8262), new ItemStack(ModItems.oilBottle, 1, 10), TOTAL_COOK_TIME)); // 10 night_vision
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), stacksOf(Items.potionitem, 8201, 8233, 8265), new ItemStack(ModItems.oilBottle, 1, 11), TOTAL_COOK_TIME)); // 11 strength
        List<ItemStack> jumpIngredients = stacksOf(Items.potionitem, 8203, 8235, 8267);
        jumpIngredients.add(new ItemStack(Items.slime_ball));
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), jumpIngredients, new ItemStack(ModItems.oilBottle, 1, 12), TOTAL_COOK_TIME)); // 12 jump
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), stacksOf(Items.potionitem, 8205, 8269), new ItemStack(ModItems.oilBottle, 1, 13), TOTAL_COOK_TIME)); // 13 water_breathing
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), stacksOf(Items.potionitem, 8206, 8270), new ItemStack(ModItems.oilBottle, 1, 14), TOTAL_COOK_TIME)); // 14 invisibility
        list.add(new CauldronRecipe(vampireBloodOil, new ItemStack(Items.feather), new ItemStack(ModItems.oilBottle, 1, 15))); // 15 slow_falling (1.7.10 adaptation)
        list.add(new CauldronRecipe(vampireBloodOil, new ItemStack(Items.emerald), new ItemStack(ModItems.oilBottle, 1, 16))); // 16 luck (1.7.10 adaptation)
        list.add(new CauldronRecipe(Collections.singletonList(vampireBloodOil), stacksOf(Items.potionitem, 8204, 8236), new ItemStack(ModItems.oilBottle, 1, 17), TOTAL_COOK_TIME)); // 17 harm
        list.add(new CauldronRecipe(vampireBloodOil, new ItemStack(ModItems.alchemicalFire), new ItemStack(ModItems.oilBottle, 1, 18))); // 18 smelt
        list.add(new CauldronRecipe(vampireBloodOil, new ItemStack(Items.ender_pearl), new ItemStack(ModItems.oilBottle, 1, 19))); // 19 teleport
        list.add(new CauldronRecipe(vampireBloodOil, new ItemStack(Items.sugar), new ItemStack(ModItems.oilBottle, 1, 20))); // 20 evasion (1.7.10 adaptation)
        list.add(new CauldronRecipe(plantOil, new ItemStack(ModItems.garlic), garlicOil)); // 21 garlic
        list.add(new CauldronRecipe(plantOil, new ItemStack(ModItems.alchemicalFire), new ItemStack(ModItems.oilBottle, 1, 22))); // 22 spitfire
        list.add(new CauldronRecipe(Collections.singletonList(garlicOil), allHolyWater, new ItemStack(ModItems.oilBottle, 1, 23), TOTAL_COOK_TIME)); // 23 vampire_killer
        list.add(new CauldronRecipe(plantOil, new ItemStack(Items.quartz), new ItemStack(ModItems.oilBottle, 1, 24))); // 24 bleeding (1.7.10 adaptation)

        return Collections.unmodifiableList(list);
    }

    public static ItemStack getBrewingResult(ItemStack fluid, ItemStack ingredient) {
        if (fluid == null || ingredient == null) return null;
        for (CauldronRecipe recipe : getRecipes()) {
            if (recipe.matches(fluid, ingredient)) {
                return recipe.result.copy();
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
