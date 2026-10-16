package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Umbrella portable sunshade item.
 * When held in hand, shields vampires from daylight burn and sun damage.
 */
public class ItemUmbrella extends ItemNeorism {

    public ItemUmbrella() {
        super("umbrella");
        setMaxDamage(2400); // 2 minutes continuous sun protection durability
        setMaxStackSize(1);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote && isSelected && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (world.isDaytime() && world.canBlockSeeTheSky((int) player.posX, (int) player.posY + 1, (int) player.posZ)) {
                if (player.ticksExisted % 20 == 0) {
                    stack.damageItem(1, player);
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.umbrella.desc"));
        tooltip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.neorism.umbrella.sun_protection"));
    }
}
