package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Vampire Regeneration active ability consuming blood.
 * Canonical port of TeamLapen's RegenVampireAction.
 */
public class VampireRegenAction extends ActionBase {

    public VampireRegenAction() {
        super("regen", 1200, 400, false, 4);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 400, 1, false));
        return true;
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
        if (player.isPotionActive(Potion.regeneration)) {
            player.removePotionEffect(Potion.regeneration.id);
        }
    }
}
