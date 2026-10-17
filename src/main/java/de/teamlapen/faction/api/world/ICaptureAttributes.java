package de.teamlapen.faction.api.world;

import de.teamlapen.faction.api.factions.IPlayableFaction;

import net.minecraft.util.AxisAlignedBB;

/**
 * Capture state and attributes provided to village capture entities during siege.
 * Canonical port of TeamLapen's ICaptureAttributes.
 */
public interface ICaptureAttributes {

    IPlayableFaction getAttackingFaction();

    IPlayableFaction getDefendingFaction();

    int getX();

    int getY();

    int getZ();

    AxisAlignedBB getVillageArea();

    default boolean shouldForceTargets() {
        return false;
    }
}
