package de.teamlapen.faction.api.factions;

import net.minecraft.entity.EntityLivingBase;

/**
 * Interface implemented by entities belonging to a faction.
 */
public interface IFactionEntity {

    IFaction getFaction();

    EntityLivingBase asEntity();
}
