package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Garlic food and crop seed item. Edible by mortals and hunters; severely poisonous to vampires.
 */
public class ItemGarlic extends ItemFood implements IPlantable {

    public ItemGarlic() {
        super(2, 0.4F, false);
        setUnlocalizedName("neorism.garlic");
        setTextureName(Neorism.MODID + ":garlic");
        setCreativeTab(ModCreativeTabs.tabMain);
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
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (side != 1 || ModBlocks.garlic == null) {
            return false;
        }
        if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
            Block soil = world.getBlock(x, y, z);
            if (soil != null && soil.canSustainPlant(world, x, y, z, ForgeDirection.UP, this) && world.isAirBlock(x, y + 1, z)) {
                world.setBlock(x, y + 1, z, ModBlocks.garlic);
                if (!player.capabilities.isCreativeMode) {
                    stack.stackSize--;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
        return EnumPlantType.Crop;
    }

    @Override
    public Block getPlant(IBlockAccess world, int x, int y, int z) {
        return ModBlocks.garlic;
    }

    @Override
    public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
        return 0;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, net.minecraft.entity.Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote) {
            Helper.handleHeldNonVampireItem(stack, entity, isSelected);
        }
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        if (!world.isRemote) {
            if (Helper.isVampire(player)) {
                player.attackEntityFrom(ModDamageSource.GARLIC, 4.0F);
                player.addPotionEffect(new PotionEffect(Potion.poison.id, 160, 1));
                player.addPotionEffect(new PotionEffect(Potion.confusion.id, 160, 0));
                player.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 200, 1));
                ModSounds.playAtEntity(player, ModSounds.SCREECH, 0.8F, 1.0F);
                ModLogger.combat("Vampire %s ingested garlic and suffered severe burns", player.getCommandSenderName());
            } else {
                // Mortals receive temporary protection from vampire bites
                player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 60, 0));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.garlic.desc"));
        tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocal("tooltip.neorism.garlic.vampire_harm"));
    }
}
