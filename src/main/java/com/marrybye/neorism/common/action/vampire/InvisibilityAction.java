package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Vampire Invisibility stealth ability.
 */
public class InvisibilityAction extends ActionBase {

    public InvisibilityAction() {
        super("vampire_invisibility", 600, 200, false, 4);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.invisibility.id, 200, 0));
        return true;
    }
}
