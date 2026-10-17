package com.marrybye.neorism.common.faction;

import java.util.List;

import com.marrybye.neorism.api.entity.IFactionEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.skills.SkillHandler;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

/**
 * Universal evaluator and tooltip generator for item faction restrictions.
 * Canonical backport of TeamLapen's FactionRestriction.
 */
public class FactionRestriction {

    public static boolean canUse(EntityLivingBase entity, ItemStack stack, boolean sendMessage) {
        if (stack == null || stack.getItem() == null) return true;

        if (stack.getItem() instanceof IFactionRestriction) {
            IFactionRestriction res = (IFactionRestriction) stack.getItem();
            IFaction reqFaction = res.getRequiredFaction(stack);
            int minLevel = res.getMinLevel(stack);
            String reqSkill = res.getRequiredSkill(stack);
            String customKey = res.getCustomMessageKey(stack);

            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                FactionPlayerHandler fph = FactionPlayerHandler.get(player);

                if (reqFaction != null) {
                    boolean factionMatches = fph != null && fph.isInFaction(reqFaction);
                    if (!factionMatches) {
                        if (sendMessage && !player.worldObj.isRemote) {
                            if (customKey != null && StatCollector.canTranslate(customKey)) {
                                player.addChatComponentMessage(new ChatComponentTranslation(customKey));
                            } else {
                                String key = "message.neorism.restriction.faction." + reqFaction.getId();
                                if (StatCollector.canTranslate(key)) {
                                    player.addChatComponentMessage(new ChatComponentTranslation(key));
                                } else {
                                    player.addChatComponentMessage(new ChatComponentTranslation("message.neorism.restriction.faction", reqFaction.getName()));
                                }
                            }
                        }
                        return false;
                    }
                }

                if (minLevel > 0) {
                    int curLevel = fph != null ? fph.getCurrentLevel() : 0;
                    if (curLevel < minLevel) {
                        if (sendMessage && !player.worldObj.isRemote) {
                            if (customKey != null && StatCollector.canTranslate(customKey)) {
                                player.addChatComponentMessage(new ChatComponentTranslation(customKey));
                            } else {
                                player.addChatComponentMessage(new ChatComponentTranslation("message.neorism.restriction.level", minLevel));
                            }
                        }
                        return false;
                    }
                }

                if (reqSkill != null && !reqSkill.isEmpty()) {
                    SkillHandler sh = SkillHandler.get(player);
                    boolean hasSkill = sh != null && sh.isSkillUnlocked(reqSkill);
                    if (!hasSkill) {
                        if (sendMessage && !player.worldObj.isRemote) {
                            player.addChatComponentMessage(new ChatComponentTranslation("message.neorism.restriction.skill"));
                        }
                        return false;
                    }
                }
            } else if (entity != null) {
                if (reqFaction != null) {
                    if (reqFaction == ModFactions.VAMPIRE && !Helper.isVampire(entity)) return false;
                    if (reqFaction == ModFactions.HUNTER && !Helper.isHunter(entity)) return false;
                }
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public static void addTooltipIfExist(EntityPlayer player, ItemStack stack, List tooltips) {
        if (stack == null || !(stack.getItem() instanceof IFactionRestriction)) return;

        IFactionRestriction res = (IFactionRestriction) stack.getItem();
        IFaction reqFaction = res.getRequiredFaction(stack);
        int minLevel = res.getMinLevel(stack);
        String reqSkill = res.getRequiredSkill(stack);

        if (reqFaction == null && minLevel <= 0 && (reqSkill == null || reqSkill.isEmpty())) return;

        FactionPlayerHandler fph = player != null ? FactionPlayerHandler.get(player) : null;

        tooltips.add("");
        tooltips.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.faction_specifics"));

        if (reqFaction != null) {
            boolean matches = fph != null && fph.isInFaction(reqFaction);
            EnumChatFormatting color = matches ? EnumChatFormatting.DARK_GREEN : EnumChatFormatting.DARK_RED;
            tooltips.add(" " + color + reqFaction.getName());
        }

        if (minLevel > 0) {
            boolean matches = fph != null && fph.getCurrentLevel() >= minLevel;
            EnumChatFormatting color = matches ? EnumChatFormatting.DARK_GREEN : EnumChatFormatting.DARK_RED;
            tooltips.add(" " + color + StatCollector.translateToLocalFormatted("tooltip.neorism.required_level", minLevel));
        }

        if (reqSkill != null && !reqSkill.isEmpty()) {
            SkillHandler sh = player != null ? SkillHandler.get(player) : null;
            boolean matches = sh != null && sh.isSkillUnlocked(reqSkill);
            EnumChatFormatting color = matches ? EnumChatFormatting.DARK_GREEN : EnumChatFormatting.DARK_RED;
            String skillName = StatCollector.canTranslate("skill.neorism." + reqSkill + ".name") ?
                StatCollector.translateToLocal("skill.neorism." + reqSkill + ".name") : reqSkill;
            tooltips.add(" " + color + skillName);
        }
    }

    public static IChatComponent getFactionRestrictionMessage(IFaction faction) {
        if (faction != null) {
            String vampirismKey = "message.vampirism.restriction.faction." + faction.getId();
            if (StatCollector.canTranslate(vampirismKey)) {
                return new ChatComponentTranslation(vampirismKey);
            }
            String neorismKey = "message.neorism.restriction.faction." + faction.getId();
            if (StatCollector.canTranslate(neorismKey)) {
                return new ChatComponentTranslation(neorismKey);
            }
            return new ChatComponentTranslation("message.neorism.restriction.faction", faction.getName());
        }
        return new ChatComponentTranslation("message.neorism.restriction.wrong_faction");
    }
}
