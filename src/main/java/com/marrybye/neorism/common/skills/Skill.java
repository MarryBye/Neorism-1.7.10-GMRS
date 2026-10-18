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
        this.icon = resolveIcon(skillId, iconName, action);
    }

    private static ResourceLocation resolveIcon(String skillId, String iconName, IAction action) {
        if (iconName != null && !iconName.isEmpty()) {
            if (iconName.indexOf(':') >= 0) {
                return new ResourceLocation(iconName);
            }
            if (iconName.startsWith("textures/")) {
                return new ResourceLocation("neorism", iconName.endsWith(".png") ? iconName : (iconName + ".png"));
            }
            if (iconName.startsWith("actions/")) {
                return new ResourceLocation("neorism", "textures/" + iconName + ".png");
            }
            return new ResourceLocation("neorism", "textures/skills/" + iconName + ".png");
        }
        if (action != null) {
            if (action.getIcon() != null) {
                return action.getIcon();
            }
            return new ResourceLocation("neorism", "textures/actions/" + action.getActionId() + ".png");
        }
        return new ResourceLocation("neorism", "textures/skills/" + skillId + ".png");
    }

    public Skill(String skillId, IPlayableFaction faction, int requiredLevel, int skillPointCost, boolean passive, String iconName) {
        this(skillId, faction, requiredLevel, skillPointCost, passive, iconName, null);
    }

    public Skill(String skillId, IPlayableFaction faction, int requiredLevel, int skillPointCost, boolean passive) {
        this(skillId, faction, requiredLevel, skillPointCost, passive, null, null);
    }

    @Override
    public String getSkillId() {
        return skillId;
    }

    @Override
    public String getTranslationKey() {
        if (!net.minecraft.util.StatCollector.canTranslate(translationKey) && action != null) {
            String actionKey = "action.neorism." + action.getActionId();
            if (net.minecraft.util.StatCollector.canTranslate(actionKey)) {
                return actionKey;
            }
        }
        return translationKey;
    }

    @Override
    public String getDescriptionTranslationKey() {
        if (!net.minecraft.util.StatCollector.canTranslate(descriptionTranslationKey) && action != null) {
            String actionDescKey = "action.neorism." + action.getActionId() + ".desc";
            if (net.minecraft.util.StatCollector.canTranslate(actionDescKey)) {
                return actionDescKey;
            }
        }
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
