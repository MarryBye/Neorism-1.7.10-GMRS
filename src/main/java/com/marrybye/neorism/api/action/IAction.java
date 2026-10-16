package com.marrybye.neorism.api.action;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Interface representing active abilities and skills for Vampires and Hunters, bridged to FactionAPI.
 */
public interface IAction extends de.teamlapen.faction.api.factions.actions.IAction {

    /**
     * Unique identifier for the action (e.g. "bat_form", "vampire_rage", "freeze").
     */
    String getId();

    /**
     * Unlocalized translation key (e.g. "action.neorism.bat").
     */
    String getUnlocalizedName();

    /**
     * Base cooldown in ticks (20 ticks = 1 second).
     */
    int getCooldown(EntityPlayer player);

    /**
     * Duration in ticks while active, or -1 for instant/toggle actions.
     */
    int getDuration(EntityPlayer player);

    /**
     * True if the action can be toggled on and off (like bat form or night vision).
     */
    boolean isToggle();

    /**
     * Blood cost consumed upon activation (for vampire actions).
     */
    int getBloodCost(EntityPlayer player);

    /**
     * Checks if the player meets all requirements (unlocked, not on cooldown, enough blood, no garlic).
     */
    boolean canUse(EntityPlayer player);

    /**
     * Called when the player activates the action. Returns true if activation succeeded.
     */
    boolean onActivated(EntityPlayer player);

    /**
     * Called when the active duration expires or when toggled off.
     */
    void onDeactivated(EntityPlayer player);

    /**
     * Called each tick while the action is active.
     */
    void onUpdate(EntityPlayer player, int activeTicksLeft);

    /**
     * Checks if the player has unlocked this action via their skill tree.
     */
    boolean isUnlocked(EntityPlayer player);

    @Override
    default String getActionId() {
        return getId();
    }

    @Override
    default String getTranslationKey() {
        return getUnlocalizedName();
    }

    @Override
    default net.minecraft.util.ResourceLocation getIcon() {
        return null;
    }

    @Override
    default int getCooldown() {
        return getCooldown(null);
    }

    @Override
    default boolean canBeUsed(EntityPlayer player) {
        return canUse(player);
    }
}

