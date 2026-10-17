package com.marrybye.neorism.common.items;

import com.marrybye.neorism.common.core.ModCreativeTabs;

/**
 * Fabric Filter used in blood sieves and blood grinders.
 * Ported 1:1 from TeamLapen's fabric_filter item.
 */
public class ItemFabricFilter extends ItemNeorism {

    public ItemFabricFilter() {
        super("fabric_filter");
        setMaxStackSize(1);
        setMaxDamage(4800);
        setCreativeTab(ModCreativeTabs.tabVampire);
    }
}
