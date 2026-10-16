package de.teamlapen.faction.api.factions.minions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Minion entity commanded by a faction player or Lord.
 */
public interface IMinionEntity {

    EntityLivingBase getEntity();

    EntityPlayer getLord();

    IMinionTask getCurrentTask();

    void setTask(IMinionTask task);

    int getMinionLevel();
}
