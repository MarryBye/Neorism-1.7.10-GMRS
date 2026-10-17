package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.entity.EntityHolyWaterBottle;

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
import net.minecraft.world.World;

import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.api.items.IItemWithTier;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.faction.FactionRestriction;
import com.marrybye.neorism.common.faction.IFactionRestriction;

/**
 * Thrown splash holy water bottle.
 * Canonical port of TeamLapen's HolyWaterSplashBottleItem.
 */
public class ItemHolyWaterSplash extends ItemNeorism implements IItemWithTier, IFactionRestriction {

    public static final int TIERS = 3;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemHolyWaterSplash() {
        super("holy_water_splash_bottle");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(16);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!FactionRestriction.canUse(player, stack, true)) {
            return stack;
        }

        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }

        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote) {
            world.spawnEntityInWorld(new EntityHolyWaterBottle(world, player, stack.getItemDamage()));
        }

        return stack;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = stack.getItemDamage();
        String specificKey = "item.neorism.holy_water_splash_bottle." + tier + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        String suffix = tier == 2 ? " (Ultimate)" : (tier == 1 ? " (Enhanced)" : "");
        return StatCollector.translateToLocal("item.neorism.holy_water_splash_bottle.name").trim() + suffix;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[3];
        icons[0] = register.registerIcon(Neorism.MODID + ":holy_water_splash_bottle_normal");
        icons[1] = register.registerIcon(Neorism.MODID + ":holy_water_splash_bottle_enhanced");
        icons[2] = register.registerIcon(Neorism.MODID + ":holy_water_splash_bottle_ultimate");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        if (damage < 0 || damage >= icons.length) damage = 0;
        return icons[damage];
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
        addTierInformation(stack, tooltip);
        FactionRestriction.addTooltipIfExist(player, stack, tooltip);
    }

    @Override
    public Tier getVampirismTier() {
        return Tier.NORMAL;
    }

    @Override
    public Tier getVampirismTier(ItemStack stack) {
        if (stack == null) return Tier.NORMAL;
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        return Tier.values()[tier];
    }

    @Override
    public IFaction getRequiredFaction(ItemStack stack) {
        return ModFactions.HUNTER;
    }

    @Override
    public int getMinLevel(ItemStack stack) {
        return 0;
    }

    @Override
    public String getRequiredSkill(ItemStack stack) {
        return null;
    }

    @Override
    public String getCustomMessageKey(ItemStack stack) {
        return "message.vampirism.restriction.holy";
    }
}
