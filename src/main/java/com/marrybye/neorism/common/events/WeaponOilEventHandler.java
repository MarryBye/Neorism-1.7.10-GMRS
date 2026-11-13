package com.marrybye.neorism.common.events;

import java.util.List;

import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.items.ItemOilBottle;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;

/**
 * Handles weapon, tool, and armor oil combat/mining/evasion effects and tooltip descriptions.
 */
public class WeaponOilEventHandler {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.entityLiving == null || event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote) return;
        if (!(event.source.getEntity() instanceof EntityPlayer)) return;

        EntityPlayer attacker = (EntityPlayer) event.source.getEntity();
        ItemStack weapon = attacker.getHeldItem();
        if (weapon == null) return;

        String oilType = ItemOilBottle.getAppliedOilType(weapon);
        int oilMeta = ItemOilBottle.getMetaForOil(oilType);
        if (oilType == null || oilMeta < 0 || !ItemOilBottle.isWeaponOil(oilMeta)) return;

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
        } else if ("spitfire".equals(oilType)) {
            target.setFire(5);
        } else {
            applyPotionOilEffect(target, oilType);
        }

        // Consume 1 oil hit charge
        ItemOilBottle.consumeOilCharge(weapon);
        int remaining = ItemOilBottle.getAppliedOilCharges(weapon);
        if (remaining <= 0) {
            attacker.worldObj.playSoundAtEntity(attacker, "random.break", 0.6F, 1.2F);
            ModLogger.combat("Weapon oil on %s's blade fully depleted", attacker.getCommandSenderName());
        }
    }

    private static void applyPotionOilEffect(EntityLivingBase target, String oilType) {
        if ("poison".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.poison.id, 50, 0));
        } else if ("weakness".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.weakness.id, 50, 0));
        } else if ("slowness".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 0));
        } else if ("healing".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.heal.id, 1, 0));
        } else if ("fire_resistance".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 200, 0));
        } else if ("swiftness".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 200, 0));
        } else if ("regeneration".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.regeneration.id, 100, 0));
        } else if ("night_vision".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.nightVision.id, 100, 0));
        } else if ("strength".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 100, 0));
        } else if ("jump".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.jump.id, 100, 0));
        } else if ("water_breathing".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 200, 0));
        } else if ("invisibility".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.invisibility.id, 100, 0));
        } else if ("harm".equals(oilType)) {
            target.addPotionEffect(new PotionEffect(Potion.harm.id, 1, 0));
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.entityLiving == null || event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote) return;
        if (!(event.entityLiving instanceof EntityPlayer) || event.source == DamageSource.outOfWorld) return;

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        float evadeChance = 0.0F;
        for (int i = 0; i < 4; i++) {
            ItemStack armor = player.getCurrentArmor(i);
            if ("evasion".equals(ItemOilBottle.getAppliedOilType(armor))) {
                evadeChance += 0.01F;
            }
        }
        if (evadeChance > 0.0F && player.getRNG().nextFloat() < evadeChance) {
            event.setCanceled(true);
            for (int i = 0; i < 4; i++) {
                ItemStack armor = player.getCurrentArmor(i);
                if ("evasion".equals(ItemOilBottle.getAppliedOilType(armor))) {
                    ItemOilBottle.consumeOilCharge(armor);
                }
            }
        }
    }

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        if (event.world == null || event.world.isRemote || event.harvester == null) return;
        ItemStack tool = event.harvester.getHeldItem();
        if (!"smelt".equals(ItemOilBottle.getAppliedOilType(tool))) return;

        List<ItemStack> drops = event.drops;
        boolean smeltedAny = false;
        for (int i = 0; i < drops.size(); i++) {
            ItemStack drop = drops.get(i);
            if (drop == null) continue;
            ItemStack smelted = FurnaceRecipes.smelting().getSmeltingResult(drop);
            if (smelted != null) {
                ItemStack out = smelted.copy();
                out.stackSize = Math.max(1, smelted.stackSize * drop.stackSize);
                drops.set(i, out);
                smeltedAny = true;
            }
        }
        if (smeltedAny) {
            ItemOilBottle.consumeOilCharge(tool);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (stack == null) return;

        String oilType = ItemOilBottle.getAppliedOilType(stack);
        if (oilType != null && !oilType.isEmpty() && !"empty".equals(oilType)) {
            int charges = ItemOilBottle.getAppliedOilCharges(stack);
            String name = ItemOilBottle.getLocalizedOilName(oilType);

            event.toolTip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("tooltip.neorism.oil.coated") + ": "
                + EnumChatFormatting.LIGHT_PURPLE + name + EnumChatFormatting.GOLD + " (" + charges + "/" + ItemOilBottle.CHARGES_PER_BOTTLE + ")");
        }
    }
}
