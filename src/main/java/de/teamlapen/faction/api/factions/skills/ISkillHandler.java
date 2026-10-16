package de.teamlapen.faction.api.factions.skills;

import java.util.Set;

/**
 * Manages player unlocked skills and available skill points.
 */
public interface ISkillHandler {

    boolean isSkillUnlocked(ISkill skill);

    boolean canUnlock(ISkill skill);

    boolean unlock(ISkill skill);

    boolean resetSkills();

    int getAvailablePoints();

    Set<ISkill> getUnlockedSkills();
}
