package de.teamlapen.faction.api.factions.villages;

import de.teamlapen.faction.api.factions.IPlayableFaction;

/**
 * Village territory controlled by a faction or neutral.
 */
public interface IVillageFaction {

    IPlayableFaction getControllingFaction();

    int getCaptureProgress();

    boolean isUnderAttack();

    void triggerRaid(IPlayableFaction attackingFaction);
}
