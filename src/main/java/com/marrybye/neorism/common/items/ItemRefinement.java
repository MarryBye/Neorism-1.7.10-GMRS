package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.faction.FactionRestriction;
import com.marrybye.neorism.common.faction.IFactionRestriction;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

/**
 * Accessory refinement items (Amulet, Ring, Obi Belt).
 * Ported 1:1 from TeamLapen's RefinementItem.java.
 */
public class ItemRefinement extends ItemNeorism implements IFactionRestriction {

    public enum Type {
        AMULET("vampire_amulet", "amulet"),
        RING("vampire_ring", "ring"),
        OBI_BELT("vampire_obi_belt", "obi_belt");

        public final String texturePrefix;
        public final String name;

        Type(String texturePrefix, String name) {
            this.texturePrefix = texturePrefix;
            this.name = name;
        }
    }

    private final Type accessoryType;

    @SideOnly(Side.CLIENT)
    private IIcon iconLayer0;
    @SideOnly(Side.CLIENT)
    private IIcon iconLayer1;

    public ItemRefinement(Type type) {
        super(type.name);
        this.accessoryType = type;
        setMaxStackSize(1);
        setMaxDamage(500);
        setCreativeTab(ModCreativeTabs.tabVampire);
    }

    public Type getAccessoryType() {
        return accessoryType;
    }

    @Override
    public IFaction getRequiredFaction(ItemStack stack) {
        return ModFactions.VAMPIRE;
    }

    @Override
    public int getMinLevel(ItemStack stack) {
        return 1;
    }

    @Override
    public String getRequiredSkill(ItemStack stack) {
        return null;
    }

    @Override
    public String getCustomMessageKey(ItemStack stack) {
        return "text.neorism.restriction.vampire";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        iconLayer0 = register.registerIcon(Neorism.MODID + ":" + accessoryType.texturePrefix + "_layer0");
        iconLayer1 = register.registerIcon(Neorism.MODID + ":" + accessoryType.texturePrefix + "_layer1");
        itemIcon = iconLayer0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderPasses(int metadata) {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return pass == 1 ? iconLayer1 : iconLayer0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        FactionRestriction.addInformation(stack, player, list);
        list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("tooltip.vampirism.refinement.when_equipped"));
        String descKey = "tooltip.neorism." + accessoryType.name + ".desc";
        if (StatCollector.canTranslate(descKey)) {
            list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal(descKey));
        }
    }
}
