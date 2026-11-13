package com.marrybye.neorism.common.items;

import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Stores item wear durability in NBT ("neorismWear") so {@code ItemStack.getItemDamage()}
 * remains an immutable subtype/tier/color discriminator in Forge 1.7.10.
 */
public final class DurabilityHelper {

    public static final String NBT_WEAR = "neorismWear";
    private static final Random RAND = new Random();

    private DurabilityHelper() {
    }

    public static int getWearDamage(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return 0;
        }
        return Math.max(0, stack.getTagCompound().getInteger(NBT_WEAR));
    }

    public static void setWearDamage(ItemStack stack, int wear) {
        if (stack == null) {
            return;
        }
        if (wear <= 0) {
            if (stack.hasTagCompound()) {
                stack.getTagCompound().removeTag(NBT_WEAR);
                if (stack.getTagCompound().hasNoTags()) {
                    stack.setTagCompound(null);
                }
            }
            return;
        }
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger(NBT_WEAR, wear);
    }

    public static double getDurabilityForDisplay(ItemStack stack, int maxDurability) {
        if (maxDurability <= 0) {
            return 0.0D;
        }
        return Math.min(1.0D, (double) getWearDamage(stack) / (double) maxDurability);
    }

    /**
     * Applies wear damage to an NBT-durability ItemStack, honoring Creative mode and Unbreaking.
     *
     * @return {@code true} if the stack broke from this damage.
     */
    public static boolean damageStack(ItemStack stack, int amount, EntityLivingBase entity, int maxDurability) {
        if (stack == null || amount <= 0 || maxDurability <= 0) {
            return false;
        }
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode) {
            return false;
        }

        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
        int effective = amount;
        if (unbreaking > 0) {
            int negated = 0;
            for (int i = 0; i < amount; i++) {
                if (EnchantmentDurability.negateDamage(stack, unbreaking, RAND)) {
                    negated++;
                }
            }
            effective -= negated;
            if (effective <= 0) {
                return false;
            }
        }

        int newWear = getWearDamage(stack) + effective;
        if (newWear >= maxDurability) {
            if (entity != null) {
                entity.renderBrokenItemStack(stack);
            }
            stack.stackSize--;
            if (stack.stackSize < 0) {
                stack.stackSize = 0;
            }
            setWearDamage(stack, 0);
            return true;
        }

        setWearDamage(stack, newWear);
        return false;
    }
}
