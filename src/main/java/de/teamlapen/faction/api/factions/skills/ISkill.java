package de.teamlapen.faction.api.factions.skills;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import net.minecraft.util.ResourceLocation;

/**
 * Individual skill definition within FactionAPI.
 */
public interface ISkill {

    String getSkillId();

    String getTranslationKey();

    String getDescriptionTranslationKey();

    ResourceLocation getIcon();

    IPlayableFaction getFaction();

    int getRequiredLevel();

    int getSkillPointCost();

    boolean isPassive();
}
