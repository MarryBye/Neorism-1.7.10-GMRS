package com.marrybye.neorism.common.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModCreativeTabs {

    public static final CreativeTabs tabMain = new CreativeTabs("neorism.main") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Items.glass_bottle;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getIconItemStack() {
            if (ModItems.vampireFang != null) {
                return new ItemStack(ModItems.vampireFang);
            }
            return new ItemStack(Items.glass_bottle);
        }
        @Override
        @SideOnly(Side.CLIENT)
        public String getTranslatedTabLabel() {
            String key = "itemGroup." + this.getTabLabel();
            String trans = net.minecraft.util.StatCollector.translateToLocal(key);
            if (trans != null && !trans.equals(key)) {
                return trans;
            }
            if (net.minecraft.util.StatCollector.canTranslate("itemGroup.neorism")) {
                return net.minecraft.util.StatCollector.translateToLocal("itemGroup.neorism");
            }
            return super.getTranslatedTabLabel();
        }
    };

    public static final CreativeTabs tabBooks = new CreativeTabs("neorism.books") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Items.book;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getTranslatedTabLabel() {
            String key = "itemGroup." + this.getTabLabel();
            String trans = net.minecraft.util.StatCollector.translateToLocal(key);
            if (trans != null && !trans.equals(key)) {
                return trans;
            }
            if (net.minecraft.util.StatCollector.canTranslate("itemGroup.vampirism.books")) {
                return net.minecraft.util.StatCollector.translateToLocal("itemGroup.vampirism.books");
            }
            return super.getTranslatedTabLabel();
        }
    };
}
