package com.marrybye.neorism.api.faction;

import com.marrybye.neorism.api.entity.IFactionEntity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Basic interface for all faction player handlers (VampirePlayer, HunterPlayer).
 */
public interface IFactionPlayer extends IFactionEntity {

    /**
     * @return The underlying EntityPlayer.
     */
    EntityPlayer getPlayer();

    /**
     * @return The playable faction.
     */
    @Override
    IPlayableFaction getFaction();

    /**
     * @return Current faction level (0 if neutral, 1-14 for vampires/hunters).
     */
    int getLevel();

    /**
     * @return Maximum level for this player's faction.
     */
    int getMaxLevel();

    /**
     * @return Current lord level (0-5).
     */
    int getLordLevel();

    /**
     * @return Whether the player is disguised.
     */
    boolean isDisguised();

    /**
     * @return Whether the player can leave the faction.
     */
    boolean canLeaveFaction();

    /**
     * Resets the player to neutral faction.
     */
    void leaveFaction();

    /**
     * @return Formatted level display name for GUI/chat.
     */
    String getLevelDisplay();
}
