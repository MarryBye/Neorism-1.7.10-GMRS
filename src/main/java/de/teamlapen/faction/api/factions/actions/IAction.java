package de.teamlapen.faction.api.factions.actions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * An activatable faction ability accessible through the Action Ring (Skill Rose).
 */
public interface IAction {

    String getActionId();

    String getTranslationKey();

    ResourceLocation getIcon();

    int getCooldown();

    boolean onActivated(EntityPlayer player);

    boolean canBeUsed(EntityPlayer player);
}
