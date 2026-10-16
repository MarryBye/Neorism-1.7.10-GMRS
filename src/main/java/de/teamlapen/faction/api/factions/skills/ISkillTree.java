package de.teamlapen.faction.api.factions.skills;

import java.util.List;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Represents a complete skill tree for a playable faction.
 */
public interface ISkillTree {

    String getTreeId();

    String getTitleTranslationKey();

    IPlayableFaction getFaction();

    ItemStack getDisplayStack();

    ResourceLocation getBackground();

    List<ISkillSegment> getRootSegments();

    List<ISkillSegment> getAllSegments();

    ISkillSegment getSegment(String id);
}
