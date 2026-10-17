package com.marrybye.neorism.common.items;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Blood bottle item capable of storing 0 to 5 blood units.
 * Vampires can drink from filled bottles to replenish blood.
 */
public class ItemBloodBottle extends Item {

    public static final int MAX_BLOOD = 5;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemBloodBottle() {
        super();
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(16);
        this.setCreativeTab(ModCreativeTabs.tabVampire);
        this.setUnlocalizedName("neorism.blood_bottle");
    }

    public static int getBlood(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBloodBottle) {
            return stack.getItemDamage();
        }
        return 0;
    }

    public static void setBlood(ItemStack stack, int blood) {
        if (stack != null && stack.getItem() instanceof ItemBloodBottle) {
            stack.setItemDamage(Math.max(0, Math.min(MAX_BLOOD, blood)));
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        if (stack == null) return 16;
        int blood = stack.getItemDamage();
        return (blood == 0 || blood == MAX_BLOOD) ? 16 : 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[MAX_BLOOD + 1];
        for (int i = 0; i <= MAX_BLOOD; i++) {
            icons[i] = register.registerIcon(Neorism.MODID + ":blood_bottle_" + i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        int index = Math.max(0, Math.min(damage, MAX_BLOOD));
        return icons != null && index < icons.length ? icons[index] : null;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getItemDamage();
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack stack) {
        return this.getUnlocalizedName(stack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = this.getUnlocalizedName(stack) + ".name";
        if (net.minecraft.util.StatCollector.canTranslate(key)) {
            return net.minecraft.util.StatCollector.translateToLocal(key).trim();
        }
        String fallback = this.getUnlocalizedName(stack);
        if (net.minecraft.util.StatCollector.canTranslate(fallback)) {
            return net.minecraft.util.StatCollector.translateToLocal(fallback).trim();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 24;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.drink;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack.getItemDamage() > 0 && Helper.isVampire(player)) {
            VampirePlayer vp = VampirePlayer.get(player);
            if (vp != null && vp.getBloodStats().needsBlood()) {
                player.setItemInUse(stack, getMaxItemUseDuration(stack));
            }
        }
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && Helper.isVampire(player)) {
            VampirePlayer vp = VampirePlayer.get(player);
            if (vp != null) {
                int bloodInBottle = stack.getItemDamage();
                int needed = vp.getBloodStats().getMaxBlood() - vp.getBloodStats().getBloodLevel();
                int drank = Math.min(bloodInBottle, Math.max(1, needed));

                vp.drinkBlood(drank, 0.5F);
                ModSounds.playAtEntity(player, ModSounds.BLOOD_DRIP, 0.6F, 1.0F);

                int remaining = bloodInBottle - drank;
                if (remaining > 0) {
                    stack.setItemDamage(remaining);
                    return stack;
                } else {
                    // Turn into empty blood bottle (damage 0)
                    if (stack.stackSize <= 1) {
                        stack.setItemDamage(0);
                        return stack;
                    } else {
                        stack.stackSize--;
                        ItemStack emptyBottle = new ItemStack(this, 1, 0);
                        if (!player.inventory.addItemStackToInventory(emptyBottle)) {
                            player.dropPlayerItemWithRandomChoice(emptyBottle, false);
                        }
                        return stack;
                    }
                }
            }
        }
        return stack;
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0)); // Empty
        list.add(new ItemStack(item, 1, MAX_BLOOD)); // Full
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        int blood = stack.getItemDamage();
        tooltip.add(StatCollector.translateToLocalFormatted("text.neorism.blood_bottle.content", blood, MAX_BLOOD));
    }
}
