package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Quarrel Clip magazine for the Hunter Tech Crossbow.
 * Holds up to 16 normal or heavy quarrels for rapid semi-automatic fire.
 */
public class ItemQuarrelClip extends Item {

    public static final int MAX_AMMO = 16;
    public static final int TYPES = 2; // 0 = Standard, 1 = Heavy

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemQuarrelClip() {
        super();
        setMaxStackSize(4);
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("neorism.quarrel_clip");
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    public static int getAmmoCount(ItemStack stack) {
        if (stack == null) return 0;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ammo")) {
            return stack.getTagCompound().getInteger("ammo");
        }
        return MAX_AMMO; // Default full
    }

    public static void setAmmoCount(ItemStack stack, int ammo) {
        if (stack == null) return;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger("ammo", Math.max(0, Math.min(ammo, MAX_AMMO)));
    }

    public static boolean isHeavy(ItemStack stack) {
        return stack != null && stack.getItemDamage() == 1;
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack stack) {
        return this.getUnlocalizedName(stack);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getItemDamage();
        return meta == 1 ? "item.neorism.heavy_quarrel_clip" : "item.neorism.quarrel_clip";
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

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        ItemStack normal = new ItemStack(item, 1, 0);
        setAmmoCount(normal, MAX_AMMO);
        list.add(normal);

        ItemStack heavy = new ItemStack(item, 1, 1);
        setAmmoCount(heavy, MAX_AMMO);
        list.add(heavy);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        int count = getAmmoCount(stack);
        boolean heavy = isHeavy(stack);
        String typeStr = heavy ? StatCollector.translateToLocal("item.neorism.quarrel.heavy.name")
                               : StatCollector.translateToLocal("item.neorism.quarrel.normal.name");

        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.quarrel_clip.ammo") + ": "
            + EnumChatFormatting.GOLD + count + "/" + MAX_AMMO + " (" + typeStr + ")");
        list.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("tooltip.neorism.quarrel_clip.desc"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[TYPES];
        icons[0] = register.registerIcon(Neorism.MODID + ":quarrel_clip");
        icons[1] = register.registerIcon(Neorism.MODID + ":heavy_quarrel_clip");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        int idx = Math.max(0, Math.min(meta, TYPES - 1));
        return icons != null && icons.length > idx ? icons[idx] : null;
    }
}
