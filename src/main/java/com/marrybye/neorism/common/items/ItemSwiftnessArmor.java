package com.marrybye.neorism.common.items;

import java.util.List;

import com.google.common.collect.Multimap;
import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

/**
 * Armor of Swiftness set (head, chest, legs, feet) with 3 tiers (Normal, Enhanced, Ultimate).
 * High-agility armor designed for rapid combat maneuvers.
 */
public class ItemSwiftnessArmor extends ItemArmor {

    public static final int TIERS = 3;
    private static final String[] SUFFIXES = {"normal", "enhanced", "ultimate"};

    private final String partName;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemSwiftnessArmor(ArmorMaterial material, int armorType, String partName) {
        super(material, 0, armorType);
        this.partName = partName;
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("neorism.armor_of_swiftness_" + partName);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        return 250 + tier * 300;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        String suffix = tier == 0 ? "" : "_" + SUFFIXES[tier];
        if (slot == 2) {
            return "neorism:textures/entity/equipment/humanoid_leggings/armor_of_swiftness" + suffix + ".png";
        }
        return "neorism:textures/entity/equipment/humanoid/armor_of_swiftness" + suffix + ".png";
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap multimap = super.getAttributeModifiers(stack);
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        double speedBonus = (tier + 1) * 0.05D; // +5% / +10% / +15% speed per piece!
        multimap.put(SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(),
            new AttributeModifier(field_111210_e, "Swiftness Armor Speed", speedBonus, 1));
        return multimap;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        String specificKey = "item.neorism.armor_of_swiftness_" + partName + "_" + SUFFIXES[tier] + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        String numberedKey = "item.neorism.armor_of_swiftness_" + partName + "." + tier + ".name";
        if (StatCollector.canTranslate(numberedKey)) {
            return StatCollector.translateToLocal(numberedKey).trim();
        }
        String baseKey = "item.neorism.armor_of_swiftness_" + partName + ".name";
        if (StatCollector.canTranslate(baseKey)) {
            return StatCollector.translateToLocal(baseKey).trim() + " (" + (tier + 1) + ")";
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[TIERS];
        for (int i = 0; i < TIERS; i++) {
            icons[i] = register.registerIcon(Neorism.MODID + ":armor_of_swiftness_" + partName + "_" + SUFFIXES[i]);
        }
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
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.armor_swiftness.desc"));
        tooltip.add(EnumChatFormatting.AQUA + "+" + ((tier + 1) * 5) + "% " +
            StatCollector.translateToLocal("tooltip.neorism.movement_speed"));
    }
}
