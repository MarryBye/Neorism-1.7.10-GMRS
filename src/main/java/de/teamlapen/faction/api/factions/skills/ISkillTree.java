package de.teamlapen.faction.api.factions.skills;

import java.util.List;
import de.teamlapen.faction.api.factions.IPlayableFaction;

/**
 * Represents a complete skill tree for a playable faction.
 */
public interface ISkillTree {

    String getTreeId();

    String getTitleTranslationKey();

    IPlayableFaction getFaction();

    List<ISkillSegment> getRootSegments();

    List<ISkillSegment> getAllSegments();

    ISkillSegment getSegment(String id);
}
