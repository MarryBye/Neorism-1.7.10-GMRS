package com.marrybye.neorism.common.items;

import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Pure Salt Water item which can be blessed into Holy Water.
 * Ported 1:1 from TeamLapen's BlessableItem.java.
 */
public class ItemPureSaltWater extends ItemNeorism {

    public ItemPureSaltWater() {
        super("pure_salt_water");
        setMaxStackSize(1);
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }
}
