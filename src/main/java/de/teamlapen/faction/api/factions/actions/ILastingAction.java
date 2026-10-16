package de.teamlapen.faction.api.factions.actions;

/**
 * An action with an active duration before cooldown starts.
 */
public interface ILastingAction extends IAction {

    int getDuration();

    void onDeactivated(net.minecraft.entity.player.EntityPlayer player);
}
