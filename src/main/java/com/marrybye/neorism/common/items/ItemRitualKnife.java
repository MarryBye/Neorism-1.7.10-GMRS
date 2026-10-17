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
 * Ritual Knife used in blood rituals and altar tier advancement.
 * Can be normal (meta 0) or charged/infused with blood (meta 1).
 * Ported 1:1 from TeamLapen's RitualKnifeItem.java.
 */
public class ItemRitualKnife extends ItemNeorism {

    @SideOnly(Side.CLIENT)
    private IIcon iconNormal;
    @SideOnly(Side.CLIENT)
    private IIcon iconCharged;

    public ItemRitualKnife() {
        super("ritual_knife");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(ModCreativeTabs.tabVampirism);
    }

    public static boolean isCharged(ItemStack stack) {
        return stack != null && (stack.getItemDamage() == 1 || (stack.hasTagCompound() && stack.getTagCompound().getBoolean("charged")));
    }

    public static ItemStack createChargedKnife() {
        return new ItemStack(com.marrybye.neorism.common.core.ModItems.ritualKnife, 1, 1);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return isCharged(stack) ? "item.neorism.ritual_knife_charged" : "item.neorism.ritual_knife";
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = "item.neorism.ritual_knife.name";
        String name = StatCollector.canTranslate(key) ? StatCollector.translateToLocal(key) : "Ritual Knife";
        if (isCharged(stack)) {
            return name + " (" + (StatCollector.canTranslate("tooltip.vampirism.ritual_knife.infused") ? StatCollector.translateToLocal("tooltip.vampirism.ritual_knife.infused") : "Infused") + ")";
        }
        return name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        iconNormal = register.registerIcon(Neorism.MODID + ":ritual_knife");
        iconCharged = register.registerIcon(Neorism.MODID + ":ritual_knife_heart");
        itemIcon = iconNormal;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return meta == 1 ? iconCharged : iconNormal;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        if (isCharged(stack)) {
            String key = "tooltip.vampirism.ritual_knife.infused";
            if (StatCollector.canTranslate(key)) {
                list.add(EnumChatFormatting.DARK_RED + StatCollector.translateToLocal(key));
            } else {
                list.add(EnumChatFormatting.DARK_RED + "Infused with Vampire Blood");
            }
        }
    }
}
