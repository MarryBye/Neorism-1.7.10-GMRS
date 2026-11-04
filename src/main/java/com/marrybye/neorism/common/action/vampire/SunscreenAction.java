package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.core.ModPotions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

/**
 * Vampire Sunscreen ability granting temporary sunlight immunity.
 * Canonical port of TeamLapen's SunscreenVampireAction.
 */
public class SunscreenAction extends ActionBase {

    public SunscreenAction() {
        super("sunscreen", 10000, 800, false, 5);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(ModPotions.sunscreen.id, 800, 0, false));
        return true;
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
        if (player.isPotionActive(ModPotions.sunscreen)) {
            player.removePotionEffect(ModPotions.sunscreen.id);
        }
    }
}
