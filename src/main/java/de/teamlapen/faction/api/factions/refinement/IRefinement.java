package de.teamlapen.faction.api.factions.refinement;

import net.minecraft.util.ResourceLocation;

/**
 * Faction refinement / accessory perk.
 */
public interface IRefinement {

    String getId();

    String getTranslationKey();

    ResourceLocation getIcon();

    String getModifierType();

    double getValue();
}
