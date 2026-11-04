package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.action.ActionHandler;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Dark Stalker action.
 * Canonical port of TeamLapen's DarkStalker action:
 * Increases stealth and speed in shadows at the expense of continuous blood consumption.
 */
public class DarkStalkerAction extends ActionBase {

    public DarkStalkerAction() {
        super("dark_stalker", 1200, 600, false, 2);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 600, 0, true));
        ModSounds.playAtEntity(player, ModSounds.BEAM_ENTER, 0.7F, 1.5F);
        return true;
    }

    @Override
    public void onUpdate(EntityPlayer player, int activeTicksLeft) {
        if (!player.worldObj.isRemote) {
            // Blood upkeep: 1 blood every 100 ticks (5 seconds)
            if (player.ticksExisted % 100 == 0) {
                VampirePlayer vp = VampirePlayer.get(player);
                if (vp != null) {
                    if (vp.getBloodStats().getBloodLevel() <= 1) {
                        ActionHandler ah = ActionHandler.get(player);
                        if (ah != null) ah.deactivateAction(this);
                        return;
                    }
                    vp.getBloodStats().consumeBlood(1);
                }
            }
        }
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
        if (player.isPotionActive(Potion.moveSpeed)) {
            player.removePotionEffect(Potion.moveSpeed.id);
        }
    }
}
