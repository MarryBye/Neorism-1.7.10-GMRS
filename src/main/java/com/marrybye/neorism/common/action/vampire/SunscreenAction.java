package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.core.ModPotions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

/**
 * Vampire Sunscreen ability granting temporary sunlight immunity.
 */
public class SunscreenAction extends ActionBase {

    public SunscreenAction() {
        super("sunscreen", 1200, 400, false, 5);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(ModPotions.sunscreen.id, 400, 0));
        return true;
    }
}
