package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModFactions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

/**
 * Minion Upgrade Items for Vampire and Hunter minions.
 * Ported 1:1 from TeamLapen's MinionUpgradeItem.java.
 */
public class ItemMinionUpgrade extends ItemNeorism {

    private final int minLevel;
    private final int maxLevel;
    private final IFaction faction;

    public ItemMinionUpgrade(String name, int minLevel, int maxLevel, IFaction faction) {
        super(name);
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.faction = faction;
        setMaxStackSize(16);
        setCreativeTab(faction == ModFactions.VAMPIRE ? ModCreativeTabs.tabVampire : ModCreativeTabs.tabHunter);
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public IFaction getFaction() {
        return faction;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.vampirism.minion_upgrade"));
        String levels = StatCollector.translateToLocalFormatted("tooltip.vampirism.for_to_levels", minLevel, maxLevel);
        list.add(EnumChatFormatting.GRAY + levels);
    }
}
