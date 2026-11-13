package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Blood Vision ability.
 * Canonical port of TeamLapen's BloodVision.
 * Allows vampire players to sense the crimson aura of warm-blooded creatures through walls.
 */
public class BloodVisionAction extends ActionBase {

    public BloodVisionAction() {
        super("blood_vision", 40, 0, true, 0);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        return Helper.isVampire(player);
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
    }

    @Override
    public boolean isUnlocked(EntityPlayer player) {
        return Helper.isVampire(player) && Helper.getLevel(player) >= 3;
    }
}
