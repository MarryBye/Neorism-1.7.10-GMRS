package com.marrybye.neorism.common.skills;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.skills.ISkill;
import net.minecraft.util.ResourceLocation;

/**
 * Concrete implementation of ISkill for Neorism.
 */
public class Skill implements ISkill {

    private final String skillId;
    private final String translationKey;
    private final String descriptionTranslationKey;
    private final ResourceLocation icon;
    private final IPlayableFaction faction;
    private final int requiredLevel;
    private final int skillPointCost;
    private final boolean passive;
    private final IAction action;

    public Skill(String skillId, IPlayableFaction faction, int requiredLevel, int skillPointCost, boolean passive, String iconName, IAction action) {
        this.skillId = skillId;
        this.faction = faction;
        this.requiredLevel = requiredLevel;
        this.skillPointCost = skillPointCost;
        this.passive = passive;
        this.action = action;
        this.translationKey = "skill.neorism." + skillId;
        this.descriptionTranslationKey = "skill.neorism." + skillId + ".desc";
        this.icon = new ResourceLocation("neorism", "textures/skills/" + (iconName != null ? iconName : skillId) + ".png");
    }

    public Skill(String skillId, IPlayableFaction faction, int requiredLevel, int skillPointCost, boolean passive, String iconName) {
        this(skillId, faction, requiredLevel, skillPointCost, passive, iconName, null);
    }

    public Skill(String skillId, IPlayableFaction faction, int requiredLevel, int skillPointCost, boolean passive) {
        this(skillId, faction, requiredLevel, skillPointCost, passive, skillId, null);
    }

    @Override
    public String getSkillId() {
        return skillId;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public String getDescriptionTranslationKey() {
        return descriptionTranslationKey;
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public IPlayableFaction getFaction() {
        return faction;
    }

    @Override
    public int getRequiredLevel() {
        return requiredLevel;
    }

    @Override
    public int getSkillPointCost() {
        return skillPointCost;
    }

    @Override
    public boolean isPassive() {
        return passive;
    }

    @Override
    public IAction getAction() {
        return action;
    }
}
