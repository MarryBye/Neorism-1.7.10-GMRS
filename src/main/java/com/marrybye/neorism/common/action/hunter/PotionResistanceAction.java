package com.marrybye.neorism.common.action.hunter;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

/**
 * Hunter Potion Resistance active ability cleansing harmful debuffs and healing slightly.
 */
public class PotionResistanceAction extends ActionBase {

    public PotionResistanceAction() {
        super("potion_resistance_hunter", 400, 0, false, 0);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.removePotionEffect(Potion.poison.id);
        player.removePotionEffect(Potion.wither.id);
        player.removePotionEffect(Potion.weakness.id);
        player.removePotionEffect(Potion.moveSlowdown.id);
        player.removePotionEffect(Potion.blindness.id);
        player.removePotionEffect(Potion.confusion.id);
        if (ModPotions.bleeding != null) {
            player.removePotionEffect(ModPotions.bleeding.id);
        }

        player.heal(4.0F);
        ModSounds.playAtEntity(player, ModSounds.BEAM_ENTER, 0.8F, 1.2F);
        return true;
    }
}
