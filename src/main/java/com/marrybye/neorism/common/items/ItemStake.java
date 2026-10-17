package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.achievement.ModAchievements;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.faction.FactionRestriction;
import com.marrybye.neorism.common.faction.IFactionRestriction;
import com.marrybye.neorism.common.skills.SkillHandler;
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

/**
 * Wooden Stake weapon designed for hunters.
 * Features canonical instant-kill backstab calculation against weakened vampires.
 * Ported 1:1 from TeamLapen's StakeItem.java.
 */
public class ItemStake extends ItemSword implements IFactionRestriction {

    public ItemStake() {
        super(ToolMaterial.WOOD);
        setUnlocalizedName("neorism.stake");
        setTextureName(Neorism.MODID + ":stake");
        setMaxDamage(64);
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    @Override
    public IFaction getRequiredFaction(ItemStack stack) {
        return ModFactions.HUNTER;
    }

    @Override
    public int getMinLevel(ItemStack stack) {
        return 1;
    }

    @Override
    public String getRequiredSkill(ItemStack stack) {
        return null;
    }

    @Override
    public String getCustomMessageKey(ItemStack stack) {
        return null;
    }

    public static boolean canKillInstantly(EntityLivingBase target, EntityLivingBase attacker) {
        if (target == null || attacker == null || !attacker.isEntityAlive()) return false;

        boolean isHunter = false;
        if (attacker instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacker;
            SkillHandler sh = SkillHandler.get(player);
            boolean hasStakeSkill = sh != null && sh.isSkillUnlocked("stake1");
            isHunter = hasStakeSkill || Helper.isHunter(player);
        } else {
            isHunter = Helper.isHunter(attacker);
        }

        if (!isHunter) return false;

        // Target must be a vampire
        boolean isVampire = Helper.isVampire(target) ||
            (target instanceof IVampireEntity) ||
            target.getClass().getName().toLowerCase().contains("vampire");

        if (!isVampire) return false;

        // Canonical health threshold: 35% max HP
        float maxHealth = target.getMaxHealth();
        if (target.getHealth() > (0.35F * maxHealth)) {
            return false;
        }

        // Canonical backstab check: target cannot see attacker
        return !Helper.canReallySee(target, attacker, true);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (attacker instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacker;
            if (!FactionRestriction.canUse(player, stack, true)) {
                return false;
            }
        }

        if (canKillInstantly(target, attacker)) {
            if (!target.worldObj.isRemote) {
                target.attackEntityFrom(ModDamageSource.STAKE, 10000.0F);
                ModSounds.playAtEntity(target, ModSounds.STAKE, 1.5F, 0.7F);
                // Wood shatter visual FX
                target.worldObj.playAuxSFX(2001, (int) target.posX, (int) target.posY, (int) target.posZ,
                    net.minecraft.block.Block.getIdFromBlock(Blocks.log));

                if (attacker instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) attacker;
                    player.triggerAchievement(ModAchievements.stake);
                }

                // Drop vampire soul
                if (ModItems.soulOrbVampire != null) {
                    target.entityDropItem(new ItemStack(ModItems.soulOrbVampire, 1), 0.5F);
                }

                ModLogger.combat("STAKE INSTANT-KILL! %s staked vampire %s from behind",
                    attacker.getCommandSenderName(), target.getCommandSenderName());
            }
            stack.damageItem(3, attacker);
            return true;
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
        FactionRestriction.addTooltipIfExist(player, stack, tooltip);
    }
}
