package com.marrybye.neorism.common.items;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Pure Salt Water item which can be blessed into Holy Water at an Altar of Cleansing.
 * Canonical port of TeamLapen's BlessableItem.java.
 */
public class ItemPureSaltWater extends ItemNeorism {

    public static final int BLESS_USE_DURATION = 316;

    public ItemPureSaltWater() {
        super("pure_salt_water");
        setMaxStackSize(1);
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return BLESS_USE_DURATION;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (ModBlocks.altarCleansing != null && world.getBlock(x, y, z) == ModBlocks.altarCleansing && Helper.isHunter(player)) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
            if (!world.isRemote) {
                player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40, 2));
                player.addPotionEffect(new PotionEffect(Potion.blindness.id, 40, 2));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (count == 300 && player.worldObj.isRemote) {
            player.playSound(ModSounds.BLESSING_MUSIC, 1.0F, 1.0F);
        }
        if (count % 20 == 1) {
            if (player.worldObj.isRemote) {
                for (int j = 0; j < 6; ++j) {
                    double ox = (player.worldObj.rand.nextFloat() - 0.5F) * 0.4F;
                    double oy = (player.worldObj.rand.nextFloat() - 0.3F) * 0.9F;
                    double oz = (player.worldObj.rand.nextFloat() - 0.5F) * 0.4F;
                    player.worldObj.spawnParticle("enchantmenttable",
                        player.posX + ox, player.posY + 1.0D + oy, player.posZ + oz,
                        0.0D, 0.2D, 0.0D);
                }
            }
            if (!player.worldObj.isRemote && count > 21) {
                player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40, 2));
                player.addPotionEffect(new PotionEffect(Potion.blindness.id, 40, 2));
            }
        }
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            ModLogger.ritual("Hunter %s blessed Pure Salt Water into Holy Water at Altar of Cleansing",
                player.getCommandSenderName());
        }
        return new ItemStack(ModItems.holyWaterBottle, stack.stackSize, 0);
    }
}
