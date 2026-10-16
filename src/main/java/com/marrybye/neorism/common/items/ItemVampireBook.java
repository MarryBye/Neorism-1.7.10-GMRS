package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

/**
 * Vampire Book lore item (tiers 1-9 / metadata 0-8).
 * Ancient forbidden vampire tomes required for rituals, Hunter intelligence analysis, and leveling.
 */
public class ItemVampireBook extends ItemNeorism {

    public static final int TIERS = 9;

    public ItemVampireBook() {
        super("vampire_book");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(16);
        setCreativeTab(ModCreativeTabs.tabBooks != null ? ModCreativeTabs.tabBooks : ModCreativeTabs.tabMain);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = stack.getItemDamage() + 1;
        String specificKey = "item.neorism.vampire_book." + stack.getItemDamage() + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        return StatCollector.translateToLocal("item.neorism.vampire_book.name") + " (" +
            StatCollector.translateToLocal("text.neorism.tier") + " " + tier + ")";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < TIERS; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        int tier = stack.getItemDamage() + 1;
        tooltip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocalFormatted("tooltip.neorism.vampire_book.tier", tier));
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.vampire_book.lore"));
    }
}
