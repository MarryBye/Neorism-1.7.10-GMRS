package com.marrybye.neorism.common.items;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Garlic Bread food item crafted with bread and garlic.
 * Hearty food for mortals and hunters giving regeneration; lethal to vampires.
 */
public class ItemGarlicBread extends ItemFood {

    public ItemGarlicBread() {
        super(6, 0.7F, false);
        setUnlocalizedName("neorism.garlic_bread");
        setTextureName(Neorism.MODID + ":garlic_bread");
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = this.getUnlocalizedName(stack) + ".name";
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key).trim();
        }
        return super.getItemStackDisplayName(stack);
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
        if (world.isRemote) return;

        if (Helper.isVampire(player)) {
            Helper.applyGarlicBurn(player, 6.0F);
            player.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 200, 1));
            ModSounds.playAtEntity(player, ModSounds.SCREECH, 1.0F, 0.9F);
            ModLogger.combat("Vampire %s ate Garlic Bread and suffered severe garlic burning!", player.getCommandSenderName());
        } else {
            // Canonical ModConsumables.GARLIC: Cures Sanguinare Vampiris
            if (player.isPotionActive(ModPotions.sanguinare)) {
                player.removePotionEffect(ModPotions.sanguinare.id);
                ModSounds.playAtEntity(player, ModSounds.HEARTBEAT, 1.0F, 1.0F);
                ModLogger.combat("Player %s cured Sanguinare Vampiris by eating Garlic Bread", player.getCommandSenderName());
            }
            // Hunter bonus: nutrition 8, saturation 0.8F (+2 hunger, +0.1F saturation over base 6/0.7F)
            if (Helper.isHunter(player) && player.getFoodStats() != null) {
                player.getFoodStats().addStats(2, 0.1F);
            }
        }
    }
}
