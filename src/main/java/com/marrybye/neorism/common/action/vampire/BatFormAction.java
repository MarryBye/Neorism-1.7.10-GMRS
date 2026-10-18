package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.action.ActionHandler;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
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
        if (SundamageRegistry.instance().isHoldingUmbrella(player)) {
            player.addChatMessage(new net.minecraft.util.ChatComponentTranslation("message.vampirism.action.bat.has_umbrella"));
            return false;
        }
        player.capabilities.allowFlying = true;
        player.capabilities.isFlying = true;
        player.capabilities.setFlySpeed(0.10F);
        player.sendPlayerAbilities();
        Helper.setEntitySize(player, 0.6F, 0.6F);
        player.eyeHeight = 0.45F;
        ModSounds.playAtEntity(player, ModSounds.BAT_SWARM, 0.8F, 1.2F);
        return true;
    }

    @Override
    public void onUpdate(EntityPlayer player, int activeTicksLeft) {
        // Maintain flight abilities in bat form
        if (!player.capabilities.allowFlying) {
            player.capabilities.allowFlying = true;
            player.sendPlayerAbilities();
        }

        // Drop out of bat form if submerged in water
        if (player.isInWater()) {
            ActionHandler ah = ActionHandler.get(player);
            if (ah != null) ah.deactivateAction(this);
            return;
        }

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
        }
        player.capabilities.setFlySpeed(0.05F);
        player.sendPlayerAbilities();
        Helper.setEntitySize(player, 0.6F, 1.8F);
        player.eyeHeight = player.getDefaultEyeHeight();

        // Safe landing resistance if reverting mid-air
        if (!player.onGround) {
            player.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.potion.Potion.resistance.id, 60, 4));
        }

        ModSounds.playAtEntity(player, ModSounds.BAT_SWARM, 0.8F, 0.9F);
    }
}
