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
 * Quarrel crossbow ammunition with all 7 canonical variants:
 * 0: Normal
 * 1: Heavy
 * 2: Spitfire
 * 3: Garlic
 * 4: Vampire Killer
 * 5: Teleport
 * 6: Bleeding
 * Ported 1:1 from TeamLapen's ModItems.java and QuarrelItem.
 */
public class ItemQuarrel extends ItemNeorism {

    public static final int TYPES = 7;
    public static final String[] NAMES = {
        "quarrel_normal",
        "quarrel_heavy",
        "quarrel_spitfire",
        "quarrel_garlic",
        "quarrel_vampire_killer",
        "quarrel_teleport",
        "quarrel_bleeding"
    };

    @SideOnly(Side.CLIENT)
    private IIcon iconNormal;
    @SideOnly(Side.CLIENT)
    private IIcon iconHeavy;

    public ItemQuarrel() {
        super("quarrel");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(64);
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    public static String getQuarrelName(int meta) {
        int idx = Math.max(0, Math.min(meta, TYPES - 1));
        return NAMES[idx];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = Math.max(0, Math.min(stack != null ? stack.getItemDamage() : 0, TYPES - 1));
        return "item.neorism." + NAMES[meta];
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int meta = Math.max(0, Math.min(stack != null ? stack.getItemDamage() : 0, TYPES - 1));
        String key = "item.neorism." + NAMES[meta] + ".name";
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key).trim();
        }
        String vampKey = "item.vampirism." + NAMES[meta] + ".name";
        if (StatCollector.canTranslate(vampKey)) {
            return StatCollector.translateToLocal(vampKey).trim();
        }
        return meta == 1 ? "Heavy Quarrel" : "Quarrel";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        iconNormal = register.registerIcon(Neorism.MODID + ":quarrel");
        iconHeavy = register.registerIcon(Neorism.MODID + ":quarrel_heavy");
        itemIcon = iconNormal;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return meta == 1 ? iconHeavy : iconNormal;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < TYPES; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        int meta = Math.max(0, Math.min(stack.getItemDamage(), TYPES - 1));
        String descKey = "tooltip.neorism." + NAMES[meta] + ".desc";
        if (StatCollector.canTranslate(descKey)) {
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal(descKey));
        } else {
            String vDesc = "tooltip.vampirism." + NAMES[meta] + ".desc";
            if (StatCollector.canTranslate(vDesc)) {
                tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal(vDesc));
            }
        }
        if (meta == 1) {
            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocal("tooltip.neorism.quarrel_heavy.damage"));
        }
    }
}
