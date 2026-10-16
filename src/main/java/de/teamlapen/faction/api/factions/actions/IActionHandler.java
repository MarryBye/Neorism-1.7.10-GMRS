package de.teamlapen.faction.api.factions.actions;

import java.util.List;

/**
 * Manages player unlocked actions, cooldowns, and Action Ring activation.
 */
public interface IActionHandler {

    List<IAction> getAvailableActions();

    boolean isActionOnCooldown(IAction action);

    int getRemainingCooldown(IAction action);

    int getRemainingDuration(ILastingAction action);

    boolean triggerAction(IAction action);
}
