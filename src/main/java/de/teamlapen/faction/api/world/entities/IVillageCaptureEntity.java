package de.teamlapen.faction.api.world.entities;

import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.world.ICaptureAttributes;

import net.minecraft.util.AxisAlignedBB;

/**
 * Required for entities that participate in attacking or defending villages during capture raids.
 * Canonical port of TeamLapen's IVillageCaptureEntity.
 */
public interface IVillageCaptureEntity extends IFactionEntity {

    void attackVillage(ICaptureAttributes totem);

    void defendVillage(ICaptureAttributes totem);

    ICaptureAttributes getCaptureInfo();

    AxisAlignedBB getTargetVillageArea();

    boolean isAttackingVillage();

    boolean isDefendingVillage();

    void stopVillageAttackDefense();
}
