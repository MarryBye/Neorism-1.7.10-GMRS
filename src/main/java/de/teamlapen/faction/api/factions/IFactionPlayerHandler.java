package de.teamlapen.faction.api.factions;

/**
 * Capability / property handler for entity player faction data.
 */
public interface IFactionPlayerHandler {

    IFactionPlayer getFactionPlayer();

    boolean canJoin(IPlayableFaction faction);

    boolean canLeaveFaction();

    void setFaction(IPlayableFaction faction, int level);

    void setLordLevel(int level);

    void setDisguised(boolean disguised);
}
