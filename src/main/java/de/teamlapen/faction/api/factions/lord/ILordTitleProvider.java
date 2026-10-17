package de.teamlapen.faction.api.factions.lord;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import net.minecraft.util.IChatComponent;

public interface ILordTitleProvider {

    /**
     * Get a lord title.
     *
     * @param level       the lord level (1 to 5)
     * @param titleGender the gender of the title
     * @return the lord title component
     */
    IChatComponent getLordTitle(int level, IPlayableFaction.TitleGender titleGender);

    /**
     * Get a short lord title.
     *
     * @param level       the lord level (1 to 5)
     * @param titleGender the gender of the title
     * @return the short lord title component
     */
    IChatComponent getShort(int level, IPlayableFaction.TitleGender titleGender);
}
