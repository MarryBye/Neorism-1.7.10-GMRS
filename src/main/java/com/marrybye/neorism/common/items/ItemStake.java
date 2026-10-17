package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.achievement.ModAchievements;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;

/**
 * Wooden Stake weapon designed for hunters.
 * Features an instant-kill backstab calculation against weakened vampires.
 */
public class ItemStake extends ItemSword {

    public ItemStake() {
        super(ToolMaterial.WOOD);
        setUnlocalizedName("neorism.stake");
        setTextureName(Neorism.MODID + ":stake");
        setMaxDamage(64);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (target == null || attacker == null) {
            return super.hitEntity(stack, target, attacker);
        }

        boolean isVampire = Helper.isVampire(target) ||
            (target instanceof IVampireEntity) ||
            target.getClass().getName().toLowerCase().contains("vampire");

        if (isVampire) {
            Vec3 attackerLook = attacker.getLookVec();
            Vec3 targetLook = target.getLookVec();
            double dot = attackerLook != null && targetLook != null ? attackerLook.dotProduct(targetLook) : 0.0D;
            boolean fromBehind = dot > 0.4D;

            float maxHp = target.getMaxHealth();
            float curHp = target.getHealth();
            float hpPercent = maxHp > 0.0F ? curHp / maxHp : 1.0F;

            float thresholdPercent = 0.40F;
            float thresholdHp = 8.0F;
            float frontalBonus = 3.0F;

            if (attacker instanceof EntityPlayer) {
                com.marrybye.neorism.common.skills.SkillHandler sh = com.marrybye.neorism.common.skills.SkillHandler.get((EntityPlayer) attacker);
                if (sh != null && sh.isSkillUnlocked("stake_mastery")) {
                    thresholdPercent = 0.60F;
                    thresholdHp = 14.0F;
                    frontalBonus = 6.0F;
                }
            }

            // Instant kill condition: from behind and (<= threshold health)
            if (fromBehind && (hpPercent <= thresholdPercent || curHp <= thresholdHp)) {
                if (!target.worldObj.isRemote) {
                    target.attackEntityFrom(ModDamageSource.STAKE, curHp + 100.0F);
                    ModSounds.playAtEntity(target, ModSounds.STAKE, 1.2F, 0.9F);
                    // Wood shatter particle & sound effect
                    target.worldObj.playAuxSFX(2001, (int) target.posX, (int) target.posY, (int) target.posZ,
                        net.minecraft.block.Block.getIdFromBlock(Blocks.log));

                    if (attacker instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) attacker;
                        player.triggerAchievement(ModAchievements.stake);
                        // Chance to drop vampire blood bottle
                        if (target.worldObj.rand.nextInt(2) == 0 && ModItems.bloodBottle != null) {
                            target.entityDropItem(new ItemStack(ModItems.bloodBottle, 1), 0.5F);
                        }
                    }

                    ModLogger.combat("STAKE INSTANT-KILL! %s staked vampire %s from behind (dot=%.2f, hp=%.1f/%.1f)",
                        attacker.getCommandSenderName(), target.getCommandSenderName(), dot, curHp, maxHp);
                }
                stack.damageItem(3, attacker);
                return true;
            } else {
                // Frontal or non-lethal strike: bonus damage against vampires
                if (!target.worldObj.isRemote) {
                    target.attackEntityFrom(ModDamageSource.STAKE, frontalBonus);
                    ModSounds.playAtEntity(target, ModSounds.STAKE, 0.7F, 1.2F);
                }
            }
        }

        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = this.getUnlocalizedName(stack) + ".name";
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key).trim();
        }
        String fallback = this.getUnlocalizedName(stack);
        if (StatCollector.canTranslate(fallback)) {
            return StatCollector.translateToLocal(fallback).trim();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.stake"));
        tooltip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.neorism.stake.backstab_hint"));
        tooltip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("tooltip.neorism.faction_restriction.hunter"));
    }
}
