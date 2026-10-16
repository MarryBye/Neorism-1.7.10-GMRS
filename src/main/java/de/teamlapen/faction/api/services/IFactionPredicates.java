package de.teamlapen.faction.api.services;

import net.minecraft.entity.Entity;

public interface IFactionPredicates {

    boolean isVampire(Entity entity);

    boolean isHunter(Entity entity);

    boolean isNeutral(Entity entity);
}
