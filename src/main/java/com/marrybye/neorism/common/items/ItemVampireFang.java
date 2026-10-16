package com.marrybye.neorism.common.items;

import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Vampire Fang dropped by vampires.
 * Can be used in alchemy/crafting, or pricked against mortal flesh to contract Sanguinare Vampiris.
 */
public class ItemVampireFang extends ItemNeorism {

    public ItemVampireFang() {
        super("vampire_fang");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (Helper.isVampire(player)) {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.fang_already_vampire"));
                return stack;
            }

            if (player.isPotionActive(ModPotions.sanguinare)) {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.fang_already_infected"));
                return stack;
            }

            // Prick flesh: inflict Sanguinare Vampiris (2 minutes incubation in ticks)
            player.addPotionEffect(new PotionEffect(ModPotions.sanguinare.id, 2400, 0));
            ModSounds.playAtEntity(player, ModSounds.VAMPIRE_BITE, 1.0F, 1.0F);
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.fang_pricked"));

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return stack;
    }
}
