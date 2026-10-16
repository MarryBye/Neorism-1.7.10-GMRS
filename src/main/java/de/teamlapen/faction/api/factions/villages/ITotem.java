package de.teamlapen.faction.api.factions.villages;

import de.teamlapen.faction.api.factions.IPlayableFaction;

/**
 * Faction totem defending a village or territory.
 */
public interface ITotem {

    IVillageFaction getVillage();

    IPlayableFaction getFaction();

    boolean isFactionDefended();

    void onRaidProgress(int percent);
}
