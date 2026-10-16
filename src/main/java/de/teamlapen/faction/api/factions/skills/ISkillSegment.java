package de.teamlapen.faction.api.factions.skills;

import java.util.List;

/**
 * A node/segment in the Skill Tree graph.
 * Represents a position, parent connections, exclusive branches, and associated skills.
 */
public interface ISkillSegment {

    String getId();

    ISkillTree getTree();

    List<ISkill> getSkills();

    default ISkill getSkill() {
        List<ISkill> list = getSkills();
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    int getX();

    int getY();

    void setPosition(int x, int y);

    List<String> getParentKeys();

    List<ISkillSegment> getParents();

    List<ISkillSegment> getChildren();

    List<String> getLockingKeys();

    List<ISkillSegment> getExclusiveSegments();

    boolean isRoot();
}
