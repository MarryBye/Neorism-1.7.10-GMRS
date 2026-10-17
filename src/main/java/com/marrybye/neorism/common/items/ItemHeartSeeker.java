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
 * Canonical Heart Seeker vampire sword with 3 tiers (Normal, Enhanced, Ultimate).
 * Agile vampire blade optimized for speed and refined blood consumption.
 * Ported 1:1 from TeamLapen's HeartSeekerItem.java.
 */
public class ItemHeartSeeker extends ItemVampireSword {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemHeartSeeker() {
        super(ToolMaterial.EMERALD);
        setUnlocalizedName("neorism.heart_seeker");
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = stack.getItemDamage();
        String suffix = tier == 1 ? "enhanced" : (tier == 2 ? "ultimate" : "normal");
        String specificKey = "item.neorism.heart_seeker_" + suffix + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        String numberedKey = "item.neorism.heart_seeker." + tier + ".name";
        if (StatCollector.canTranslate(numberedKey)) {
            return StatCollector.translateToLocal(numberedKey).trim();
        }
        String baseKey = "item.neorism.heart_seeker.name";
        if (StatCollector.canTranslate(baseKey)) {
            return StatCollector.translateToLocal(baseKey).trim() + " (" + (tier + 1) + ")";
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[TIERS];
        icons[0] = register.registerIcon(Neorism.MODID + ":heart_seeker_normal");
        icons[1] = register.registerIcon(Neorism.MODID + ":heart_seeker_enhanced");
        icons[2] = register.registerIcon(Neorism.MODID + ":heart_seeker_ultimate");
        itemIcon = icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        int idx = Math.max(0, Math.min(meta, TIERS - 1));
        return icons != null ? icons[idx] : itemIcon;
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
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.heart_seeker.desc"));
        super.addInformation(stack, player, tooltip, advanced);
    }
}
