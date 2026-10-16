package com.marrybye.neorism.common.items;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import net.minecraft.item.Item;

/**
 * Base Item class for Neorism items in 1.7.10.
 */
public class ItemNeorism extends Item {

    public ItemNeorism(String name) {
        setUnlocalizedName("neorism." + name);
        setTextureName(Neorism.MODID + ":" + name);
        setCreativeTab(ModCreativeTabs.tabMain);
    }
}
