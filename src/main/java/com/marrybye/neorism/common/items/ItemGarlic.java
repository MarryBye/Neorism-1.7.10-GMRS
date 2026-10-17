package com.marrybye.neorism.common.items;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Garlic food item. Edible by mortals and hunters; severely poisonous to vampires.
 */
public class ItemGarlic extends ItemFood {

    public ItemGarlic() {
        super(2, 0.4F, false);
        setUnlocalizedName("neorism.garlic");
        setTextureName(Neorism.MODID + ":garlic");
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack stack) {
        return this.getUnlocalizedName(stack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = this.getUnlocalizedName(stack) + ".name";
        if (net.minecraft.util.StatCollector.canTranslate(key)) {
            return net.minecraft.util.StatCollector.translateToLocal(key).trim();
        }
        String fallback = this.getUnlocalizedName(stack);
        if (net.minecraft.util.StatCollector.canTranslate(fallback)) {
            return net.minecraft.util.StatCollector.translateToLocal(fallback).trim();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, net.minecraft.entity.Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote) {
            Helper.handleHeldNonVampireItem(stack, entity, isSelected);
        }
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        if (!world.isRemote) {
            if (Helper.isVampire(player)) {
                player.attackEntityFrom(ModDamageSource.GARLIC, 4.0F);
                player.addPotionEffect(new PotionEffect(Potion.poison.id, 160, 1));
                player.addPotionEffect(new PotionEffect(Potion.confusion.id, 160, 0));
                player.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 200, 1));
                ModSounds.playAtEntity(player, ModSounds.SCREECH, 0.8F, 1.0F);
                ModLogger.combat("Vampire %s ingested garlic and suffered severe burns", player.getCommandSenderName());
            } else {
                // Mortals receive temporary protection from vampire bites
                player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 60, 0));
            }
        }
    }
}
