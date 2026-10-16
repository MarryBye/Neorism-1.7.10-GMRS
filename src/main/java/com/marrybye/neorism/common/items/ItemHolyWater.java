package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Drinkable Holy Water bottle (Normal, Enhanced, Ultimate).
 * Cures Sanguinare Vampiris infection on mortals, heavily burns and damages vampire players.
 */
public class ItemHolyWater extends ItemNeorism {

    public static final int TIERS = 3;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemHolyWater() {
        super("holy_water_bottle");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(16);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.drink;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            int tier = stack.getItemDamage();
            float dmg = tier == 2 ? 22.0F : (tier == 1 ? 14.0F : 8.0F);

            if (Helper.isVampire(player)) {
                // Vampire burns from holy water
                player.attackEntityFrom(ModDamageSource.causeIndirectHolyWaterDamage(player, player), dmg);
                player.addPotionEffect(new PotionEffect(Potion.confusion.id, 160, 0));
                player.addPotionEffect(new PotionEffect(Potion.weakness.id, 200, 1));
                ModSounds.playAtEntity(player, ModSounds.SIZZLE, 1.0F, 1.0F);
                ModLogger.combat("Vampire player %s drank holy water tier %d and suffered %.1f holy damage",
                    player.getCommandSenderName(), tier, dmg);
            } else {
                // Non-vampires: cure infection
                if (player.isPotionActive(ModPotions.sanguinare)) {
                    player.removePotionEffect(ModPotions.sanguinare.id);
                    ModSounds.playAtEntity(player, ModSounds.HEARTBEAT, 1.0F, 1.0F);
                    ModLogger.combat("Player %s was cured of Sanguinare Vampiris by drinking holy water",
                        player.getCommandSenderName());
                }
                // Gentle divine restoration
                player.heal(tier == 2 ? 6.0F : (tier == 1 ? 4.0F : 2.0F));
                player.addPotionEffect(new PotionEffect(Potion.regeneration.id, (tier + 1) * 60, 0));
            }

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
                if (stack.stackSize <= 0) {
                    return new ItemStack(Items.glass_bottle);
                }
                if (!player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle))) {
                    player.dropPlayerItemWithRandomChoice(new ItemStack(Items.glass_bottle), false);
                }
            }
        }
        return stack;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = stack.getItemDamage();
        String specificKey = "item.neorism.holy_water_bottle." + tier + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        String suffix = tier == 2 ? " (Ultimate)" : (tier == 1 ? " (Enhanced)" : "");
        return StatCollector.translateToLocal("item.neorism.holy_water_bottle.name").trim() + suffix;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[3];
        icons[0] = register.registerIcon(Neorism.MODID + ":holy_water_bottle_normal");
        icons[1] = register.registerIcon(Neorism.MODID + ":holy_water_bottle_enhanced");
        icons[2] = register.registerIcon(Neorism.MODID + ":holy_water_bottle_ultimate");
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
        int tier = stack.getItemDamage();
        if (tier == 2) {
            tooltip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.neorism.holy_water.ultimate"));
        } else if (tier == 1) {
            tooltip.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocal("tooltip.neorism.holy_water.enhanced"));
        } else {
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("tooltip.neorism.holy_water.normal"));
        }
    }
}
