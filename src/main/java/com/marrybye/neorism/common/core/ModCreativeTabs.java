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

    // Backward-compatibility alias
    public static final CreativeTabs tabMain = tabVampirism;

    // Tab 4: Vampirism: Books & Leveling (Vampire Books, Hunter Intel, Case Files, Leveling Catalysts)
    public static final CreativeTabs tabBooks = new CreativeTabs("neorism.books") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return ModItems.vampireBook != null ? ModItems.vampireBook : Items.book;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getIconItemStack() {
            if (ModItems.vampireBook != null) {
                return new ItemStack(ModItems.vampireBook, 1, 0);
            }
            return new ItemStack(Items.book);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getTranslatedTabLabel() {
            if (StatCollector.canTranslate("itemGroup.neorism.books")) {
                return StatCollector.translateToLocal("itemGroup.neorism.books");
            }
            return "Vampirism: Books & Leveling";
        }

        @Override
        @SideOnly(Side.CLIENT)
        @SuppressWarnings({"unchecked", "rawtypes"})
        public void displayAllReleventItems(java.util.List list) {
            super.displayAllReleventItems(list);
            // Also include key leveling items & catalysts for convenience
            if (ModItems.pureBlood != null) {
                ModItems.pureBlood.getSubItems(ModItems.pureBlood, this, list);
            }
            if (ModItems.humanHeart != null) {
                list.add(new ItemStack(ModItems.humanHeart));
            }
            if (ModItems.oblivionPotion != null) {
                list.add(new ItemStack(ModItems.oblivionPotion));
            }
            if (ModItems.injectionSanguinare != null) {
                list.add(new ItemStack(ModItems.injectionSanguinare));
            }
            if (ModItems.injectionGarlic != null) {
                list.add(new ItemStack(ModItems.injectionGarlic));
            }
            if (ModItems.vampireMinionUpgradeSimple != null) list.add(new ItemStack(ModItems.vampireMinionUpgradeSimple));
            if (ModItems.vampireMinionUpgradeEnhanced != null) list.add(new ItemStack(ModItems.vampireMinionUpgradeEnhanced));
            if (ModItems.vampireMinionUpgradeSpecial != null) list.add(new ItemStack(ModItems.vampireMinionUpgradeSpecial));
            if (ModItems.hunterMinionUpgradeSimple != null) list.add(new ItemStack(ModItems.hunterMinionUpgradeSimple));
            if (ModItems.hunterMinionUpgradeEnhanced != null) list.add(new ItemStack(ModItems.hunterMinionUpgradeEnhanced));
            if (ModItems.hunterMinionUpgradeSpecial != null) list.add(new ItemStack(ModItems.hunterMinionUpgradeSpecial));
        }
    };
}
