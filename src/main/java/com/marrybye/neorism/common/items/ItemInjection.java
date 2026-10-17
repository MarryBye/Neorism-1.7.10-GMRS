package com.marrybye.neorism.common.items;

import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.entity.ExtendedCreature;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Injection syringes used for direct biological faction induction and vaccination.
 * Ported 1:1 from TeamLapen's GarlicInjectionItem.java and SanguinareInjectionItem.java.
 */
public class ItemInjection extends ItemNeorism {

    public enum Type {
        SANGUINARE,
        GARLIC
    }

    private final Type type;

    public ItemInjection(String name, Type type) {
        super(name);
        this.type = type;
        setMaxStackSize(16);
    }

    public Type getType() {
        return type;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            FactionPlayerHandler fph = FactionPlayerHandler.get(player);
            if (fph == null) return stack;

            if (type == Type.SANGUINARE) {
                if (Helper.isVampire(player)) {
                    player.addChatMessage(new ChatComponentTranslation("message.vampirism.already_vampire"));
                    return stack;
                }

                if (Helper.isHunter(player)) {
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.injection_hunter_reject"));
                    return stack;
                }

                // Inoculate with Sanguinare Vampiris disease (incubation period) + toxicant reaction
                player.addPotionEffect(new PotionEffect(ModPotions.sanguinare.id, 2400, 0));
                player.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 60, 0));
                ModSounds.playAtEntity(player, ModSounds.VAMPIRE_BITE, 1.0F, 1.0F);
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.injection_sanguinare_success"));
                ModLogger.leveling("Player %s inoculated with Sanguinare Vampiris via Injection", player.getCommandSenderName());

            } else if (type == Type.GARLIC) {
                if (Helper.isVampire(player)) {
                    // Cure vampire, revert to neutral
                    fph.setFaction(ModFactions.NEUTRAL, 0);
                    player.clearActivePotions();
                    world.playSoundAtEntity(player, "random.fizz", 1.0F, 1.2F);
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.injection_cured_vampire"));
                    ModLogger.leveling("Player %s cured of Vampirism via Garlic Injection", player.getCommandSenderName());
                } else if (fph.isNeutral()) {
                    // Induct into Hunter Level 1 + toxicant reaction
                    fph.setFaction(ModFactions.HUNTER, 1);
                    player.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 200, 1));
                    world.playSoundAtEntity(player, "random.levelup", 1.0F, 1.0F);
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.hunter_joined"));
                    ModLogger.leveling("Player %s inducted into Hunter Faction (Level 1) via Garlic Injection", player.getCommandSenderName());
                } else {
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.injection_already_hunter"));
                    return stack;
                }
            }

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return stack;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        if (type == Type.GARLIC && target != null) {
            ExtendedCreature ec = ExtendedCreature.get(target);
            if (ec != null) {
                if (!player.worldObj.isRemote) {
                    ec.setPoisonousBlood(ExtendedCreature.POISONOUS_BLOOD_DOSE_DURATION);
                    ModSounds.playAtEntity(player, ModSounds.VAMPIRE_BITE, 1.0F, 1.0F);
                    if (!player.capabilities.isCreativeMode) {
                        stack.stackSize--;
                    }
                    ModLogger.combat("Creature %s vaccinated with garlic (poisonous blood active for 10 minutes)",
                        target.getCommandSenderName());
                }
                return true;
            }
        }
        return super.itemInteractionForEntity(stack, player, target);
    }
}
