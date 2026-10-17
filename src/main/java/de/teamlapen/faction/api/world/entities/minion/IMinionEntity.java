package de.teamlapen.faction.api.world.entities.minion;

import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import net.minecraft.entity.EntityLivingBase;

import java.util.UUID;

/**
 * Entity that is able to follow tasks from a lord player.
 */
public interface IMinionEntity extends IFactionEntity {

    IMinionTask.IMinionTaskDesc getCurrentTask();

    IMinionInventory getInventory();

    ILordPlayer getLord();

    UUID getLordUUID();

    int getMinionId();

    void recallMinion();

    boolean isTaskLocked();

    EntityLivingBase asEntity();
}
