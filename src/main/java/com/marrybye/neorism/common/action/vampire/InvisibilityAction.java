package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Vampire Invisibility stealth ability.
 * Canonical port of TeamLapen's InvisibilityVampireAction:
 * Grants complete visual invisibility (including armor and equipment hiding) for 25 seconds.
 */
public class InvisibilityAction extends ActionBase {

    public InvisibilityAction() {
        super("vampire_invisibility", 500, 500, false, 4);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.invisibility.id, 500, 0, false));
        return true;
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
        if (player.isPotionActive(Potion.invisibility)) {
            player.removePotionEffect(Potion.invisibility.id);
        }
    }
}
