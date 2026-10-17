package com.marrybye.neorism.common.faction;

import com.marrybye.neorism.api.faction.IFaction;
import net.minecraft.item.ItemStack;

/**
 * Interface implemented by items with faction, level, or skill restrictions.
 * Canonical backport of TeamLapen's IFactionRestriction.
 */
public interface IFactionRestriction {

    /**
     * Faction required to use this item (or null for any).
     */
    IFaction getRequiredFaction(ItemStack stack);

    /**
     * Minimum level in that faction required to use this item (or 0 if none).
     */
    int getMinLevel(ItemStack stack);

    /**
     * Required skill ID (or null if none).
     */
    String getRequiredSkill(ItemStack stack);

    /**
     * Custom message key for restriction fail (or null for default message).
     */
    String getCustomMessageKey(ItemStack stack);
}
