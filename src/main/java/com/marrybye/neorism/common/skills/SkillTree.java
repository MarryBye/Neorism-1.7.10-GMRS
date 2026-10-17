package com.marrybye.neorism.common.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Concrete implementation of ISkillTree for Neorism.
 */
public class SkillTree implements ISkillTree {

    private final String treeId;
    private final String titleTranslationKey;
    private final IPlayableFaction faction;
    private final ItemStack displayStack;
    private final ResourceLocation background;

    private final List<ISkillSegment> allSegments = new ArrayList<ISkillSegment>();
    private final List<ISkillSegment> rootSegments = new ArrayList<ISkillSegment>();
    private final Map<String, ISkillSegment> segmentMap = new HashMap<String, ISkillSegment>();

    public SkillTree(String treeId, IPlayableFaction faction, ItemStack displayStack, ResourceLocation background) {
        this.treeId = treeId;
        this.faction = faction;
        this.displayStack = displayStack;
        this.background = background != null ? background : new ResourceLocation("factionapi", "textures/gui/skills/backgrounds/level.png");
        this.titleTranslationKey = "skill_tree.neorism." + treeId;
    }

    public void addSegment(SkillSegment segment) {
        segment.setTree(this);
        this.allSegments.add(segment);
        this.segmentMap.put(segment.getId(), segment);
        if (segment.isRoot()) {
            this.rootSegments.add(segment);
        }
    }

    @Override
    public String getTreeId() {
        return treeId;
    }

    @Override
    public String getTitleTranslationKey() {
        return titleTranslationKey;
    }

    @Override
    public IPlayableFaction getFaction() {
        return faction;
    }

    @Override
    public ItemStack getDisplayStack() {
        return displayStack;
    }

    @Override
    public ResourceLocation getBackground() {
        return background;
    }

    @Override
    public List<ISkillSegment> getRootSegments() {
        return Collections.unmodifiableList(rootSegments);
    }

    @Override
    public List<ISkillSegment> getAllSegments() {
        return Collections.unmodifiableList(allSegments);
    }

    @Override
    public ISkillSegment getSegment(String id) {
        return segmentMap.get(id);
    }
}
