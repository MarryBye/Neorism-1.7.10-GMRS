package com.marrybye.neorism.common.events;

import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.items.ItemOilBottle;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

/**
 * Handles weapon oil combat effects and tooltip descriptions on coated blades and crossbows.
 */
public class WeaponOilEventHandler {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote) return;
        if (!(event.source.getEntity() instanceof EntityPlayer)) return;

        EntityPlayer attacker = (EntityPlayer) event.source.getEntity();
        ItemStack weapon = attacker.getHeldItem();
        if (weapon == null) return;

        String oilType = ItemOilBottle.getAppliedOilType(weapon);
        if (oilType == null || oilType.isEmpty() || "empty".equals(oilType)) return;

        EntityLivingBase target = event.entityLiving;

        if ("vampire_killer".equals(oilType)) {
            if (Helper.isVampire(target)) {
                float oldAmt = event.ammount;
                event.ammount += 6.0F;
                target.setFire(4);
                ModSounds.playAtEntity(target, "random.fizz", 1.0F, 1.4F);
                ModLogger.combat("Vampire Killer Oil triggered on vampire %s (+6.0 damage: %.1f -> %.1f, ignited)",
                    target.getCommandSenderName(), oldAmt, event.ammount);
            }
        } else if ("garlic".equals(oilType)) {
            if (Helper.isVampire(target)) {
                target.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 140, 1));
                ModLogger.combat("Garlic Oil afflicted vampire %s with garlic sickness", target.getCommandSenderName());
            }
        } else if ("bleeding".equals(oilType)) {
            target.attackEntityFrom(DamageSource.generic, 3.0F);
            target.worldObj.playAuxSFX(2002, (int) target.posX, (int) target.posY, (int) target.posZ, 0x880000);
            ModLogger.combat("Bleeding Oil inflicted blood drain on %s", target.getCommandSenderName());
        } else if ("fire".equals(oilType)) {
            target.setFire(5);
        } else if ("poison".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.poison.id, 100, 1));
        } else if ("weakness".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.weakness.id, 140, 0));
        } else if ("slowness".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 140, 1));
        }

        // Consume 1 oil hit charge
        ItemOilBottle.consumeOilCharge(weapon);
        int remaining = ItemOilBottle.getAppliedOilCharges(weapon);
        if (remaining <= 0) {
            attacker.worldObj.playSoundAtEntity(attacker, "random.break", 0.6F, 1.2F);
            ModLogger.combat("Weapon oil on %s's blade fully depleted", attacker.getCommandSenderName());
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (stack == null) return;

        String oilType = ItemOilBottle.getAppliedOilType(stack);
        if (oilType != null && !oilType.isEmpty() && !"empty".equals(oilType)) {
            int charges = ItemOilBottle.getAppliedOilCharges(stack);
            String nameKey = "item.neorism.oil." + oilType + ".name";
            String name = StatCollector.canTranslate(nameKey) ? StatCollector.translateToLocal(nameKey) : oilType;

            event.toolTip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("tooltip.neorism.oil.coated") + ": "
                + EnumChatFormatting.LIGHT_PURPLE + name + EnumChatFormatting.GOLD + " (" + charges + "/" + ItemOilBottle.CHARGES_PER_BOTTLE + ")");
        }
    }
}
