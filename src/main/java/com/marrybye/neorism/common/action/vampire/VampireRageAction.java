package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.core.ModSounds;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Vampire Rage ability granting increased speed and attack damage.
 */
public class VampireRageAction extends ActionBase {

    public VampireRageAction() {
        super("vampire_rage", 600, 200, false, 6);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 200, 1));
        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 200, 1));
        ModSounds.playAtEntity(player, ModSounds.RAGE, 1.0F, 1.0F);
        return true;
    }
}
