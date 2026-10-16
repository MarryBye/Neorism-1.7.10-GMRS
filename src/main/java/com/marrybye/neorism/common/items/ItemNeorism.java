package com.marrybye.neorism.common.items;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import net.minecraft.item.Item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

/**
 * Base Item class for Neorism items in 1.7.10.
 */
public class ItemNeorism extends Item {

    public ItemNeorism(String name) {
        setUnlocalizedName("neorism." + name);
        setTextureName(Neorism.MODID + ":" + name);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack stack) {
        return this.getUnlocalizedName(stack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = this.getUnlocalizedName(stack) + ".name";
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key).trim();
        }
        String fallback = this.getUnlocalizedName(stack);
        if (StatCollector.canTranslate(fallback)) {
            return StatCollector.translateToLocal(fallback).trim();
        }
        return super.getItemStackDisplayName(stack);
    }
}
