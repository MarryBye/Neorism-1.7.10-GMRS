package com.marrybye.neorism.common.entity.minion.tasks;

import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class FollowLordTask implements IMinionTask {

    public static final String ID = "follow_lord";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public IChatComponent getName() {
        return new ChatComponentTranslation("minion_task.neorism.follow_lord");
    }

    @Override
    public boolean isAvailable(ILordPlayer lord) {
        return true;
    }

    @Override
    public IMinionTaskDesc activateTask(EntityPlayer lord, IMinionEntity minion, Object minionData) {
        return new Desc();
    }

    @Override
    public void deactivateTask(IMinionTaskDesc desc) {
    }

    @Override
    public void tickActive(IMinionTaskDesc desc, IMinionEntity minion, Object minionData) {
    }

    @Override
    public void tickBackground(IMinionTaskDesc desc, Object minionData) {
    }

    public class Desc implements IMinionTaskDesc {

        @Override
        public IMinionTask getTask() {
            return FollowLordTask.this;
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            tag.setString("Task", ID);
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
        }
    }
}
