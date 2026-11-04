package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Vampire Rage ability granting increased speed, strength, and attack rate.
 * Canonical port of TeamLapen's RageVampireAction.
 */
public class VampireRageAction extends ActionBase {

    public VampireRageAction() {
        super("vampire_rage", 400, 360, false, 6);
    }

    @Override
    public int getDuration(EntityPlayer player) {
        int level = Helper.getLevel(player);
        return 20 * (13 + 5 * level);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        int duration = getDuration(player);
        player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, duration, 0, false));
        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, duration, 2, false));
        player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, duration, 0, false));
        ModSounds.playAtEntity(player, ModSounds.RAGE, 1.0F, 1.0F);
        return true;
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
        if (player.isPotionActive(Potion.damageBoost)) {
            player.removePotionEffect(Potion.damageBoost.id);
        }
        if (player.isPotionActive(Potion.moveSpeed)) {
            player.removePotionEffect(Potion.moveSpeed.id);
        }
        if (player.isPotionActive(Potion.digSpeed)) {
            player.removePotionEffect(Potion.digSpeed.id);
        }
    }
}
