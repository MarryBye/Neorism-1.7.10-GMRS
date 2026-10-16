package de.teamlapen.faction.api.factions;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Represents player state within a faction.
 */
public interface IFactionPlayer {

    IPlayableFaction getFaction();

    int getLevel();

    int getLordLevel();

    EntityPlayer getPlayer();

    boolean isDisguised();
}
