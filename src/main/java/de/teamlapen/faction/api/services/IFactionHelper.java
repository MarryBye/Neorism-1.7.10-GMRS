package de.teamlapen.faction.api.services;

import de.teamlapen.faction.api.factions.IFaction;
import net.minecraft.entity.Entity;

public interface IFactionHelper {

    IFaction getFaction(Entity entity);
}
