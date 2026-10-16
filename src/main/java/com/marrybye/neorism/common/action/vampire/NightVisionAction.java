package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Vampire Night Vision toggle ability.
 */
public class NightVisionAction extends ActionBase {

    public NightVisionAction() {
        super("night_vision", 20, -1, true, 0);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 400, 0, true));
        return true;
    }

    @Override
    public void onUpdate(EntityPlayer player, int activeTicksLeft) {
        if (!player.isPotionActive(Potion.nightVision) || player.getActivePotionEffect(Potion.nightVision).getDuration() < 200) {
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 400, 0, true));
        }
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
        if (player.isPotionActive(Potion.nightVision)) {
            player.removePotionEffect(Potion.nightVision.id);
        }
    }
}
