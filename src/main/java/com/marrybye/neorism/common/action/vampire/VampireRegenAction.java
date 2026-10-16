package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Vampire Regeneration active burst ability consuming blood.
 */
public class VampireRegenAction extends ActionBase {

    public VampireRegenAction() {
        super("regen", 400, 100, false, 4);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 100, 1));
        return true;
    }
}
