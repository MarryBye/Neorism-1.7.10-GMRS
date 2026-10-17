package com.marrybye.neorism.common.items;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

/**
 * Umbrella portable sunshade item.
 * When held in hand, shields vampires from daylight burn and sun damage while slowing movement by 35%.
 * Ported 1:1 from TeamLapen's UmbrellaItem.java.
 */
public class ItemUmbrella extends ItemNeorism {

    public static final UUID UMBRELLA_SPEED_UUID = UUID.fromString("ce7b303b-d245-4293-9426-53843572adcf");

    public ItemUmbrella() {
        super("umbrella");
        setMaxDamage(0);
        setMaxStackSize(1);
        setFull3D();
        setCreativeTab(ModCreativeTabs.tabVampire);
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap multimap = HashMultimap.create();
        // Operation 1 = multiply base (-0.35 = -35% movement speed)
        multimap.put(SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(),
            new AttributeModifier(UMBRELLA_SPEED_UUID, "Umbrella speed modifier", -0.35D, 1));
        return multimap;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.umbrella.desc"));
        tooltip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.neorism.umbrella.sun_protection"));
        tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocal("tooltip.neorism.umbrella.slowdown"));
    }
}
