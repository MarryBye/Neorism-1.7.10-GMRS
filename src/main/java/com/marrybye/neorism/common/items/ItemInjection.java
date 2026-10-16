package com.marrybye.neorism.common.items;

import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Injection syringes used for direct biological faction induction and purification.
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

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            FactionPlayerHandler fph = FactionPlayerHandler.get(player);
            if (fph == null) return stack;

            if (type == Type.SANGUINARE) {
                if (Helper.isVampire(player)) {
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.injection_already_vampire"));
                    return stack;
                }

                if (Helper.isHunter(player)) {
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.injection_hunter_reject"));
                    return stack;
                }

                // Directly transform into Vampire Level 1
                fph.setFaction(ModFactions.VAMPIRE, 1);
                world.playSoundAtEntity(player, "random.levelup", 1.0F, 0.8F);
                ModSounds.playAtEntity(player, ModSounds.VAMPIRE_BITE, 1.0F, 1.0F);
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.injection_sanguinare_success"));
                ModLogger.leveling("Player %s inducted into Vampire Faction (Level 1) via Sanguinare Injection", player.getCommandSenderName());

            } else if (type == Type.GARLIC) {
                if (Helper.isVampire(player)) {
                    // Cure vampire, revert to neutral
                    fph.setFaction(ModFactions.NEUTRAL, 0);
                    player.clearActivePotions();
                    world.playSoundAtEntity(player, "random.fizz", 1.0F, 1.2F);
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.injection_cured_vampire"));
                    ModLogger.leveling("Player %s cured of Vampirism via Garlic Injection", player.getCommandSenderName());
                } else if (fph.isNeutral()) {
                    // Induct into Hunter Level 1
                    fph.setFaction(ModFactions.HUNTER, 1);
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
}
