package com.marrybye.neorism.api.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Interface for items that can be charged with blood (e.g. Heart Striker, Heart Seeker).
 * Canonical backport of TeamLapen's IBloodChargeable.
 */
public interface IBloodChargeable {

    /**
     * @param stack Stack of this item
     * @return If the itemstack can be charged
     */
    boolean canBeCharged(ItemStack stack);

    /**
     * Try to charge the given itemstack.
     *
     * @param stack  Stack of this item
     * @param amount The max amount of blood in mB to be charged
     * @return The actual amount of blood in mB charged
     */
    int charge(ItemStack stack, int amount);

    /**
     * Gets the charge percentage of the item (between 0.0F and 1.0F).
     *
     * @param stack Stack of this item
     * @return the charge percentage of the item
     */
    default float getChargePercentage(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return 0.0F;
        return stack.getTagCompound().getFloat("blood_charge");
    }

    /**
     * Sets the charge percentage of the item (clamped between 0.0F and 1.0F).
     */
    default void setChargePercentage(ItemStack stack, float value) {
        if (stack == null) return;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        float clamped = Math.max(0.0F, Math.min(1.0F, value));
        stack.getTagCompound().setFloat("blood_charge", clamped);
    }
}
