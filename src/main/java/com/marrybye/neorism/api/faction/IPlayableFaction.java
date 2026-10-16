package com.marrybye.neorism.api.faction;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Interface representing a faction that players can join, bridged to FactionAPI.
 */
public interface IPlayableFaction extends IFaction, de.teamlapen.faction.api.factions.IPlayableFaction {

    boolean isNeutral();

    @Override
    default int getMinLevel() {
        return 1;
    }

    @Override
    default boolean canPlayerJoin(EntityPlayer player) {
        return true;
    }

    @Override
    default boolean canPlayerLeave(EntityPlayer player) {
        return true;
    }
}

