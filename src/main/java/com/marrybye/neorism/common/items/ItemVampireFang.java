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
            if (Helper.canBecomeVampire(player)) {
                // Inoculate with Sanguinare Vampiris (2 minutes incubation in ticks) + poison reaction
                player.addPotionEffect(new PotionEffect(ModPotions.sanguinare.id, 2400, 0));
                player.addPotionEffect(new PotionEffect(net.minecraft.potion.Potion.poison.id, 60, 0));
                ModSounds.playAtEntity(player, ModSounds.VAMPIRE_BITE, 1.0F, 1.0F);
            } else {
                if (Helper.isVampire(player)) {
                    player.addChatMessage(new ChatComponentTranslation("message.vampirism.already_vampire"));
                } else {
                    player.addChatMessage(new ChatComponentTranslation("message.vampirism.immune_to_sanguinare"));
                }
            }

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return stack;
    }
}
