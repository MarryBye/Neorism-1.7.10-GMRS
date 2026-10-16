package de.teamlapen.faction.api.factions.skills;

import java.util.List;

/**
 * A node/segment in the Skill Tree graph.
 * Represents a position, parent connections, exclusive branches, and associated skill.
 */
public interface ISkillSegment {

    String getId();

    ISkill getSkill();

    int getX();

    int getY();

    List<ISkillSegment> getParents();

    List<ISkillSegment> getExclusiveSegments();

    boolean isRoot();
}
