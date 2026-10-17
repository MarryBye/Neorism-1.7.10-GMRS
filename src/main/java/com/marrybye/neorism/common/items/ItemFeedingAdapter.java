package com.marrybye.neorism.common.items;

import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.vampire.BloodStats;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Feeding Adapter allowing vampires to drink directly from blood containers in their inventory.
 * Ported 1:1 from TeamLapen's FeedingAdapterItem.java.
 */
public class ItemFeedingAdapter extends ItemNeorism {

    public ItemFeedingAdapter() {
        super("feeding_adapter");
        setMaxStackSize(1);
        setCreativeTab(ModCreativeTabs.tabVampire);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.drink;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 20;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (Helper.isVampire(player)) {
            VampirePlayer vp = VampirePlayer.get(player);
            if (vp != null && vp.getBloodStats().needsBlood() && findBloodContainer(player) != null) {
                player.setItemInUse(stack, getMaxItemUseDuration(stack));
            }
        }
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && Helper.isVampire(player)) {
            ItemStack container = findBloodContainer(player);
            if (container != null) {
                VampirePlayer vp = VampirePlayer.get(player);
                if (vp != null && vp.getBloodStats().needsBlood()) {
                    int blood = ItemBloodBottle.getBlood(container);
                    if (blood > 0) {
                        int drink = Math.min(blood, 4);
                        ItemBloodBottle.setBlood(container, blood - drink);
                        vp.getBloodStats().addBlood(drink);
                        world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
                    }
                }
            }
        }
        return stack;
    }

    private ItemStack findBloodContainer(EntityPlayer player) {
        for (ItemStack item : player.inventory.mainInventory) {
            if (item != null && item.getItem() == ModItems.bloodBottle && ItemBloodBottle.getBlood(item) > 0) {
                return item;
            }
        }
        return null;
    }
}
