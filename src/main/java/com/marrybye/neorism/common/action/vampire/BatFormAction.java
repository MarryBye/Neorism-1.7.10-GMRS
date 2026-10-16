package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.action.ActionHandler;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.vampire.SundamageRegistry;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Bat Form transformation ability granting creative-like flight.
 */
public class BatFormAction extends ActionBase {

    public BatFormAction() {
        super("bat", 100, -1, true, 3);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.capabilities.allowFlying = true;
        player.sendPlayerAbilities();
        ModSounds.playAtEntity(player, ModSounds.BAT_SWARM, 0.8F, 1.2F);
        return true;
    }

    @Override
    public void onUpdate(EntityPlayer player, int activeTicksLeft) {
        // Continuous blood upkeep: 1 blood every 60 ticks (3 seconds)
        if (player.ticksExisted % 60 == 0) {
            VampirePlayer vp = VampirePlayer.get(player);
            if (vp != null) {
                if (vp.getBloodStats().getBloodLevel() <= 1) {
                    // Out of blood - force revert
                    ActionHandler ah = ActionHandler.get(player);
                    if (ah != null) ah.deactivateAction(this);
                    return;
                }
                vp.getBloodStats().consumeBlood(1);
            }
        }

        // Sunlight vulnerability in bat form
        if (SundamageRegistry.isEntityInSunlight(player)) {
            player.setFire(2);
        }
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
        }
        ModSounds.playAtEntity(player, ModSounds.BAT_SWARM, 0.8F, 0.9F);
    }
}
