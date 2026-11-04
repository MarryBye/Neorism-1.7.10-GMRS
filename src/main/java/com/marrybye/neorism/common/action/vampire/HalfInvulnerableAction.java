package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.core.ModSounds;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Half Invulnerable active ability.
 * Canonical port of TeamLapen's HalfInvulnerableAction:
 * When active, caps lethal hits exceeding 40% max HP at the cost of blood,
 * while imparting slight movement slowness.
 */
public class HalfInvulnerableAction extends ActionBase {

    public HalfInvulnerableAction() {
        super("half_invulnerable", 1200, 600, false, 4);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 25, 0, false));
        ModSounds.playAtEntity(player, ModSounds.BEAM_ENTER, 0.9F, 0.8F);
        return true;
    }

    @Override
    public void onUpdate(EntityPlayer player, int activeTicksLeft) {
        if (!player.worldObj.isRemote && player.ticksExisted % 20 == 0) {
            player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 25, 0, false));
        }
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
        if (player.isPotionActive(Potion.moveSlowdown)) {
            player.removePotionEffect(Potion.moveSlowdown.id);
        }
    }
}
