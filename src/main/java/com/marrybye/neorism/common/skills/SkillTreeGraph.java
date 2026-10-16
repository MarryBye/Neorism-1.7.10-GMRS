package com.marrybye.neorism.common.skills;

import java.util.*;

import com.marrybye.neorism.common.util.ModLogger;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;

/**
 * Organizes skill segments into directed acyclic graphs and computes topological depth and layouts.
 */
public class SkillTreeGraph {

    private final Map<String, SkillTree> trees = new LinkedHashMap<String, SkillTree>();
    private final Map<String, SkillTreeLayout> layouts = new HashMap<String, SkillTreeLayout>();

    public void registerTree(SkillTree tree) {
        trees.put(tree.getTreeId(), tree);
    }

    public SkillTree getTree(String treeId) {
        return trees.get(treeId);
    }

    public Collection<SkillTree> getAllTrees() {
        return Collections.unmodifiableCollection(trees.values());
    }

    public SkillTreeLayout getLayout(String treeId) {
        return layouts.get(treeId);
    }

    public void buildGraphs() {
        for (SkillTree tree : trees.values()) {
            buildTree(tree);
            SkillTreeLayout layout = SkillTreeLayout.layout(tree);
            layouts.put(tree.getTreeId(), layout);
            ModLogger.debug("Built skill tree graph '%s' with %d segments (width: %d, height: %d)",
                tree.getTreeId(), tree.getAllSegments().size(), layout.getWidth(), layout.getHeight());
        }
    }

    private void buildTree(SkillTree tree) {
        Map<String, SkillSegment> segmentMap = new HashMap<String, SkillSegment>();
        for (ISkillSegment seg : tree.getAllSegments()) {
            segmentMap.put(seg.getId(), (SkillSegment) seg);
        }

        // Link parents and children
        for (SkillSegment seg : segmentMap.values()) {
            seg.getParents().clear();
            seg.getChildren().clear();
            for (String parentKey : seg.getParentKeys()) {
                SkillSegment parent = segmentMap.get(parentKey);
                if (parent != null) {
                    seg.getParents().add(parent);
                    parent.getChildren().add(seg);
                } else {
                    ModLogger.warn("Skill segment '%s' references unknown parent '%s'", seg.getId(), parentKey);
                }
            }

            // Link exclusive/locking segments
            seg.getExclusiveSegments().clear();
            for (String lockKey : seg.getLockingKeys()) {
                SkillSegment other = segmentMap.get(lockKey);
                if (other != null) {
                    seg.getExclusiveSegments().add(other);
                }
            }
        }

        // Calculate depth (longest path from any root)
        for (SkillSegment seg : segmentMap.values()) {
            calculateDepth(seg, new HashSet<SkillSegment>());
        }
    }

    private int calculateDepth(SkillSegment seg, Set<SkillSegment> visiting) {
        if (seg.isRoot()) {
            seg.setDepth(0);
            return 0;
        }
        if (visiting.contains(seg)) {
            ModLogger.error("Cycle detected in skill segment graph at '%s'", seg.getId());
            return seg.getDepth();
        }
        visiting.add(seg);
        int maxParentDepth = 0;
        for (ISkillSegment p : seg.getParents()) {
            int d = calculateDepth((SkillSegment) p, visiting);
            if (d > maxParentDepth) {
                maxParentDepth = d;
            }
        }
        visiting.remove(seg);
        int depth = maxParentDepth + 1;
        seg.setDepth(depth);
        return depth;
    }
}
