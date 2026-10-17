package de.teamlapen.faction.api.world.entities.minion;

import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;

/**
 * Task for minion entity (e.g. Follow, Defend, Collect Blood).
 */
public interface IMinionTask {

    String getId();

    IChatComponent getName();

    boolean isAvailable(ILordPlayer lord);

    IMinionTaskDesc activateTask(EntityPlayer lord, IMinionEntity minion, Object minionData);

    void deactivateTask(IMinionTaskDesc desc);

    void tickActive(IMinionTaskDesc desc, IMinionEntity minion, Object minionData);

    void tickBackground(IMinionTaskDesc desc, Object minionData);

    interface IMinionTaskDesc {

        IMinionTask getTask();

        void writeToNBT(NBTTagCompound tag);

        void readFromNBT(NBTTagCompound tag);
    }
}
