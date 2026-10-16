package com.marrybye.neorism.api.faction;

/**
 * Fundamental faction interface for Neorism.
 */
public interface IFaction {

    String getId();

    String getUnlocalizedName();

    int getColor();

    String getChatColor();

    int getMaxLevel();

    int getMaxLordLevel();

    boolean isHostileTowards(IFaction other);
}
