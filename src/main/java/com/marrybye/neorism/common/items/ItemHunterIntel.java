package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
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
 * Hunter Intel item (tiers 1-10 / meta 0-9) used for advanced Hunter leveling (levels 5-14).
 */
public class ItemHunterIntel extends ItemNeorism {

    public static final int TIERS = 10;

    public ItemHunterIntel() {
        super("hunter_intel");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(64);
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = stack.getItemDamage() + 1;
        String specificKey = "item.neorism.hunter_intel." + stack.getItemDamage() + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        String base = super.getItemStackDisplayName(stack);
        return base + " (" + StatCollector.translateToLocal("text.neorism.tier") + " " + tier + ")";
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
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("tooltip.neorism.hunter_intel.tier", tier));
        tooltip.add(EnumChatFormatting.DARK_AQUA + StatCollector.translateToLocal("tooltip.neorism.hunter_intel.usage"));
    }
}
