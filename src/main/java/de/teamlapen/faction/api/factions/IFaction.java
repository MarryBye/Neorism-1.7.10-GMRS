package de.teamlapen.faction.api.factions;

import net.minecraft.util.ResourceLocation;

/**
 * Base faction interface for FactionAPI.
 */
public interface IFaction {

    String getName();

    String getTranslationKey();

    ResourceLocation getIcon();

    int getHighestReachableLevel();

    int getHighestLordLevel();

    boolean isHostileTo(IFaction other);
}
