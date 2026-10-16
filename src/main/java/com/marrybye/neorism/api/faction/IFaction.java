package com.marrybye.neorism.api.faction;

import net.minecraft.util.ResourceLocation;

/**
 * Fundamental faction interface for Neorism, bridged to FactionAPI.
 */
public interface IFaction extends de.teamlapen.faction.api.factions.IFaction {

    String getId();

    String getUnlocalizedName();

    int getColor();

    String getChatColor();

    int getMaxLevel();

    int getMaxLordLevel();

    boolean isHostileTowards(IFaction other);

    @Override
    default String getName() {
        return getId();
    }

    @Override
    default String getTranslationKey() {
        return getUnlocalizedName();
    }

    @Override
    default ResourceLocation getIcon() {
        return null;
    }

    @Override
    default int getHighestReachableLevel() {
        return getMaxLevel();
    }

    @Override
    default int getHighestLordLevel() {
        return getMaxLordLevel();
    }

    @Override
    default boolean isHostileTo(de.teamlapen.faction.api.factions.IFaction other) {
        if (other instanceof IFaction) {
            return isHostileTowards((IFaction) other);
        }
        return false;
    }
}

