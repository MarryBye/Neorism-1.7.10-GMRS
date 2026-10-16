package com.marrybye.neorism.common.faction;

import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.api.faction.IPlayableFaction;

/**
 * Concrete implementation of IPlayableFaction.
 */
public class PlayableFaction implements IPlayableFaction {

    private final String id;
    private final String unlocalizedName;
    private final int color;
    private final String chatColor;
    private final int maxLevel;
    private final int maxLordLevel;
    private final boolean isNeutral;

    public PlayableFaction(
        String id,
        String unlocalizedName,
        int color,
        String chatColor,
        int maxLevel,
        int maxLordLevel,
        boolean isNeutral
    ) {
        this.id = id;
        this.unlocalizedName = unlocalizedName;
        this.color = color;
        this.chatColor = chatColor;
        this.maxLevel = maxLevel;
        this.maxLordLevel = maxLordLevel;
        this.isNeutral = isNeutral;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public String getChatColor() {
        return chatColor;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getMaxLordLevel() {
        return maxLordLevel;
    }

    @Override
    public boolean isNeutral() {
        return isNeutral;
    }

    @Override
    public boolean isHostileTowards(IFaction other) {
        if (other == null || this.isNeutral() || other.getMaxLevel() == 0) {
            return false;
        }
        return !this.id.equals(other.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IFaction)) return false;
        return id.equals(((IFaction) obj).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Faction[" + id + "]";
    }
}
