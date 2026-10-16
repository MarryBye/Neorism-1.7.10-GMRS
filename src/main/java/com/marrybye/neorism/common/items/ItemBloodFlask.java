package com.marrybye.neorism.common.items;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Large blood flask item capable of holding up to 10 blood units (1000 mB).
 */
public class ItemBloodFlask extends Item {

    public static final int CAPACITY = 10;

    public ItemBloodFlask() {
        super();
        this.setMaxStackSize(1);
        this.setCreativeTab(ModCreativeTabs.tabMain);
        this.setTextureName(Neorism.MODID + ":vampire_blood_bottle");
        this.setUnlocalizedName("neorism.blood_flask");
    }

    public static int getBlood(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return 0;
        return stack.getTagCompound().getInteger("blood");
    }

    public static void setBlood(ItemStack stack, int blood) {
        if (stack == null) return;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger("blood", Math.max(0, Math.min(blood, CAPACITY)));
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 28;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.drink;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        int blood = getBlood(stack);
        if (blood > 0 && Helper.isVampire(player)) {
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
                int blood = getBlood(stack);
                int needed = vp.getBloodStats().getMaxBlood() - vp.getBloodStats().getBloodLevel();
                int drank = Math.min(blood, Math.max(1, needed));

                vp.drinkBlood(drank, 0.7F);
                ModSounds.playAtEntity(player, ModSounds.BLOOD_DRIP, 0.6F, 1.0F);

                setBlood(stack, blood - drank);
            }
        }
        return stack;
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        ItemStack empty = new ItemStack(item);
        setBlood(empty, 0);
        list.add(empty);

        ItemStack full = new ItemStack(item);
        setBlood(full, CAPACITY);
        list.add(full);
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        int blood = getBlood(stack);
        tooltip.add(StatCollector.translateToLocalFormatted("text.neorism.blood_flask.content", blood, CAPACITY));
    }
}
