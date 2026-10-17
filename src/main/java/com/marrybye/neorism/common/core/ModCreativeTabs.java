package com.marrybye.neorism.common.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

/**
 * Creative tabs for Neorism, matching canonical TeamLapen Vampirism structure:
 * 1. Vampirism (General, nature, materials, blocks, decorative) - Icon: Vampire Fang
 * 2. Vampirism: Hunter (Hunter gear, weapons, armor, alchemy, tables) - Icon: Hunter Minion Equipment
 * 3. Vampirism: Vampire (Vampire weapons, armor, altars, blood containers, coffins) - Icon: Vampire Minion Binding
 */
public class ModCreativeTabs {

    // Tab 1: Vampirism (General / Nature / Stone / Decor / Tech)
    public static final CreativeTabs tabVampirism = new CreativeTabs("neorism") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return ModItems.vampireFang != null ? ModItems.vampireFang : Items.glass_bottle;
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
            if (StatCollector.canTranslate("itemGroup.neorism")) {
                return StatCollector.translateToLocal("itemGroup.neorism");
            }
            if (StatCollector.canTranslate("itemGroup.vampirism")) {
                return StatCollector.translateToLocal("itemGroup.vampirism");
            }
            return "Vampirism";
        }
    };

    // Tab 2: Vampirism: Hunter (Weapons, Armor, Holy Water, Garlic, Alchemy, Diffusers)
    public static final CreativeTabs tabHunter = new CreativeTabs("neorism.hunter") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return ModItems.hunterMinionEquipment != null ? ModItems.hunterMinionEquipment : Items.bow;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getIconItemStack() {
            if (ModItems.hunterMinionEquipment != null) {
                return new ItemStack(ModItems.hunterMinionEquipment);
            }
            if (ModItems.hunterAxe != null) {
                return new ItemStack(ModItems.hunterAxe);
            }
            return new ItemStack(Items.bow);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getTranslatedTabLabel() {
            if (StatCollector.canTranslate("itemGroup.neorism.hunter")) {
                return StatCollector.translateToLocal("itemGroup.neorism.hunter");
            }
            if (StatCollector.canTranslate("itemGroup.vampirism.hunter")) {
                return StatCollector.translateToLocal("itemGroup.vampirism.hunter");
            }
            return "Vampirism: Hunter";
        }
    };

    // Tab 3: Vampirism: Vampire (Swords, Cloaks, Altars, Blood, Coffins)
    public static final CreativeTabs tabVampire = new CreativeTabs("neorism.vampire") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return ModItems.vampireMinionBinding != null ? ModItems.vampireMinionBinding : Items.nether_star;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getIconItemStack() {
            if (ModItems.vampireMinionBinding != null) {
                return new ItemStack(ModItems.vampireMinionBinding);
            }
            if (ModItems.bloodBottle != null) {
                return new ItemStack(ModItems.bloodBottle);
            }
            return new ItemStack(Items.nether_star);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getTranslatedTabLabel() {
            if (StatCollector.canTranslate("itemGroup.neorism.vampire")) {
                return StatCollector.translateToLocal("itemGroup.neorism.vampire");
            }
            if (StatCollector.canTranslate("itemGroup.vampirism.vampire")) {
                return StatCollector.translateToLocal("itemGroup.vampirism.vampire");
            }
            return "Vampirism: Vampire";
        }
    };

    // Backward-compatibility aliases
    public static final CreativeTabs tabMain = tabVampirism;
    public static final CreativeTabs tabBooks = tabVampirism;
}
