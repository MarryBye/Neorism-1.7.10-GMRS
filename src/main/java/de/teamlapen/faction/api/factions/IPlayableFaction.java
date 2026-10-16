package de.teamlapen.faction.api.factions;

/**
 * Playable faction that can be joined by players (e.g. Vampires, Hunters).
 */
public interface IPlayableFaction extends IFaction {

    int getMinLevel();

    boolean canPlayerJoin(net.minecraft.entity.player.EntityPlayer player);

    boolean canPlayerLeave(net.minecraft.entity.player.EntityPlayer player);
}
