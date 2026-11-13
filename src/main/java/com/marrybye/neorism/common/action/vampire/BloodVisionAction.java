package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Blood Vision ability.
 * Canonical port of TeamLapen's BloodVision.
 * Allows vampire players to sense the crimson aura of warm-blooded creatures through walls.
 */
public class BloodVisionAction extends ActionBase {

    private static final ResourceLocation ICON = new ResourceLocation("neorism", "textures/skills/blood_vision.png");

    public BloodVisionAction() {
        super("blood_vision", 40, 0, true, 0);
    }

    @Override
    public ResourceLocation getIcon() {
        return ICON;
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
