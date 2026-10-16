package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

/**
 * Quarrel crossbow ammunition with standard (meta 0) and heavy (meta 1) variants.
 */
public class ItemQuarrel extends ItemNeorism {

    @SideOnly(Side.CLIENT)
    private IIcon iconNormal;
    @SideOnly(Side.CLIENT)
    private IIcon iconHeavy;

    public ItemQuarrel() {
        super("quarrel");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(64);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return stack.getItemDamage() == 1 ? "item.neorism.quarrel_heavy" : "item.neorism.quarrel";
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = getUnlocalizedName(stack) + ".name";
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key).trim();
        }
        return stack.getItemDamage() == 1 ? "Heavy Quarrel" : "Quarrel";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        iconNormal = register.registerIcon(Neorism.MODID + ":quarrel");
        iconHeavy = register.registerIcon(Neorism.MODID + ":quarrel_heavy");
        itemIcon = iconNormal;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return meta == 1 ? iconHeavy : iconNormal;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        if (stack.getItemDamage() == 1) {
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.quarrel_heavy.desc"));
            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocal("tooltip.neorism.quarrel_heavy.damage"));
        } else {
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.quarrel.desc"));
        }
    }
}
