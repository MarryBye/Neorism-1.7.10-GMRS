package de.teamlapen.faction.api.factions.lord;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;

/**
 * Interface representing a faction lord player (Vampire Lord or Hunter General).
 */
public interface ILordPlayer {

    IPlayableFaction.TitleGender titleGender();

    int getLordLevel();

    int getMaxLordLevel();

    int getMaxMinions();

    IChatComponent getLordTitle();

    IChatComponent getLordTitleShort();

    void updateMinionAttributes(boolean increasedStats);

    /**
     * Reset all lord tasks that should be available again for the player's current lord level.
     */
    void resetLordTasks();

    boolean setTitleGender(IPlayableFaction.TitleGender gender);

    default boolean setTitleGender(boolean female) {
        return setTitleGender(female ? IPlayableFaction.TitleGender.FEMALE : IPlayableFaction.TitleGender.MALE);
    }

    EntityPlayer getPlayer();
}
