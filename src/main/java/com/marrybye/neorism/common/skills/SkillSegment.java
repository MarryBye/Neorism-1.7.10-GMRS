package com.marrybye.neorism.common.skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;
import de.teamlapen.faction.api.factions.skills.ISkillTree;

/**
 * Node in the Skill Tree directed acyclic graph.
 */
public class SkillSegment implements ISkillSegment {

    private final String id;
    private ISkillTree tree;
    private final List<ISkill> skills = new ArrayList<ISkill>();
    private final List<String> parentKeys = new ArrayList<String>();
    private final List<String> lockingKeys = new ArrayList<String>();
    private final List<ISkillSegment> parents = new ArrayList<ISkillSegment>();
    private final List<ISkillSegment> children = new ArrayList<ISkillSegment>();
    private final List<ISkillSegment> exclusiveSegments = new ArrayList<ISkillSegment>();

    private int x;
    private int y;
    private int depth = 0;
    private String orderAfterKey = null;

    public SkillSegment(String id, ISkill... skills) {
        this.id = id;
        if (skills != null) {
            Collections.addAll(this.skills, skills);
        }
    }

    public void setTree(ISkillTree tree) {
        this.tree = tree;
    }

    public SkillSegment parents(String... parentIds) {
        if (parentIds != null) {
            Collections.addAll(this.parentKeys, parentIds);
        }
        return this;
    }

    public SkillSegment lockingSegments(String... lockingIds) {
        if (lockingIds != null) {
            Collections.addAll(this.lockingKeys, lockingIds);
        }
        return this;
    }

    public SkillSegment after(String segmentId) {
        this.orderAfterKey = segmentId;
        return this;
    }

    public String getOrderAfterKey() {
        return orderAfterKey;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ISkillTree getTree() {
        return tree;
    }

    @Override
    public List<ISkill> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public List<String> getParentKeys() {
        return Collections.unmodifiableList(parentKeys);
    }

    @Override
    public List<ISkillSegment> getParents() {
        return parents;
    }

    @Override
    public List<ISkillSegment> getChildren() {
        return children;
    }

    @Override
    public List<String> getLockingKeys() {
        return Collections.unmodifiableList(lockingKeys);
    }

    @Override
    public List<ISkillSegment> getExclusiveSegments() {
        return exclusiveSegments;
    }

    @Override
    public boolean isRoot() {
        return parentKeys.isEmpty();
    }
}
