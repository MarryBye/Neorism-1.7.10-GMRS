package com.marrybye.neorism.common.action.hunter;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;

/**
 * Hunter Disguise action. Conceals the hunter as a villager.
 * Canonical port of TeamLapen's DisguiseHunterAction.
 */
public class DisguiseHunterAction extends ActionBase {

    public DisguiseHunterAction() {
        super("disguise_hunter", 1200, 1200, true, 0);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph != null) {
            fph.setDisguised(true);
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.disguise_activated"));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph != null) {
            fph.setDisguised(false);
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.disguise_deactivated"));
            }
        }
    }
}
