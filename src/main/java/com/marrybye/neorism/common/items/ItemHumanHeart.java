package com.marrybye.neorism.common.items;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Human Heart food item (Normal or Weak).
 * Canonical port of TeamLapen's ModFoods.HUMAN_HEART / WEAK_HUMAN_HEART and ModConsumables.NASTY_NON_VAMPIRES.
 * - Mortals: consuming raw human heart inflicts severe Nausea (400 ticks) and modest nutrition (5 / 3).
 * - Vampires: rich source of pure sanguine essence (+20 blood / +10 blood).
 */
public class ItemHumanHeart extends ItemFood {

    private final boolean weak;

    public ItemHumanHeart(boolean weak) {
        super(weak ? 3 : 5, 1.0F, false);
        this.weak = weak;
        String name = weak ? "weak_human_heart" : "human_heart";
        setUnlocalizedName("neorism." + name);
        setTextureName(Neorism.MODID + ":" + name);
        setMaxStackSize(64);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (Helper.isVampire(player)) {
            VampirePlayer vp = VampirePlayer.get(player);
            if (vp != null && vp.getBloodStats() != null && vp.getBloodStats().needsBlood()) {
                player.setItemInUse(stack, getMaxItemUseDuration(stack));
                return stack;
            }
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        if (world.isRemote) return;

        if (Helper.isVampire(player)) {
            VampirePlayer vp = VampirePlayer.get(player);
            if (vp != null && vp.getBloodStats() != null) {
                int blood = weak ? 10 : 20;
                float sat = weak ? 0.9F : 1.5F;
                vp.drinkBlood(blood, sat);
                world.playSoundAtEntity(player, "random.burp", 0.5F, player.getRNG().nextFloat() * 0.1F + 0.9F);
                ModSounds.playAtEntity(player, ModSounds.HEARTBEAT, 0.8F, 1.0F);
                ModLogger.combat("Vampire %s consumed %s human heart (+%d blood, %.1f sat)",
                    player.getCommandSenderName(), weak ? "weak" : "pure", blood, sat);
            }
        } else {
            // Canonical ModConsumables.NASTY_NON_VAMPIRES: 400 ticks Nausea
            player.addPotionEffect(new PotionEffect(Potion.confusion.id, 400, 0));
            ModSounds.playAtEntity(player, ModSounds.SCREECH, 0.5F, 1.4F);
            ModLogger.combat("Mortal %s ate human heart and suffered revulsion/nausea", player.getCommandSenderName());
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = getUnlocalizedName(stack) + ".name";
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key).trim();
        }
        return super.getItemStackDisplayName(stack);
    }
}
