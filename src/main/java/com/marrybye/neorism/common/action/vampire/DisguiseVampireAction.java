package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;

/**
 * Vampire Disguise action. Conceals the vampire as human.
 * Ported 1:1 from TeamLapen's DisguiseVampireAction.
 */
public class DisguiseVampireAction extends ActionBase {

    public DisguiseVampireAction() {
        super("disguise_vampire", 200, -1, true, 0);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph != null) {
            boolean nextState = !fph.isDisguised();
            fph.setDisguised(nextState);
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentTranslation(
                    nextState ? "chat.neorism.disguise_activated" : "chat.neorism.disguise_deactivated"
                ));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph != null && fph.isDisguised()) {
            fph.setDisguised(false);
        }
    }
}
