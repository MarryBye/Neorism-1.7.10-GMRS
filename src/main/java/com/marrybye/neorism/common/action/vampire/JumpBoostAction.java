package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Vampire Jump Boost ability.
 * Canonical port of TeamLapen's JumpBoostAction:
 * Grants enhanced leaping capability while toggled active.
 */
public class JumpBoostAction extends ActionBase {

    public JumpBoostAction() {
        super("jump_boost", 20, -1, true, 0);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.jump.id, 999999, 1, true));
        return true;
    }

    @Override
    public void onUpdate(EntityPlayer player, int activeTicksLeft) {
        if (!player.isPotionActive(Potion.jump)) {
            player.addPotionEffect(new PotionEffect(Potion.jump.id, 999999, 1, true));
        }
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
        if (player.isPotionActive(Potion.jump)) {
            player.removePotionEffect(Potion.jump.id);
        }
    }
}
