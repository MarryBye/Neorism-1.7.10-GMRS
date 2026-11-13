package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

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

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        com.marrybye.neorism.client.events.TooltipEventHandler.addWrappedTranslation(tooltip, "tooltip.vampirism.fabric_filter", EnumChatFormatting.GRAY);
    }
}
