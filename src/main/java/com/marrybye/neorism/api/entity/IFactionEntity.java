package com.marrybye.neorism.api.entity;

import com.marrybye.neorism.api.faction.IFaction;
import net.minecraft.entity.EntityLivingBase;

/**
 * Interface implemented by entities affiliated with a specific faction.
 */
public interface IFactionEntity {

    /**
     * @return The faction this entity belongs to.
     */
    IFaction getFaction();

    /**
     * @return The EntityLivingBase instance.
     */
    EntityLivingBase asEntity();
}
