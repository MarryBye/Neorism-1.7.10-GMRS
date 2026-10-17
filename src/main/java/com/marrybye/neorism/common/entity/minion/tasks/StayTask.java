package com.marrybye.neorism.common.entity.minion.tasks;

import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class StayTask implements IMinionTask {

    public static final String ID = "stay";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public IChatComponent getName() {
        return new ChatComponentTranslation("minion_task.neorism.stay");
    }

    @Override
    public boolean isAvailable(ILordPlayer lord) {
        return true;
    }

    @Override
    public IMinionTaskDesc activateTask(EntityPlayer lord, IMinionEntity minion, Object minionData) {
        if (minion != null) {
            return new Desc((int) minion.asEntity().posX, (int) minion.asEntity().posY, (int) minion.asEntity().posZ);
        } else if (lord != null) {
            return new Desc((int) lord.posX, (int) lord.posY, (int) lord.posZ);
        }
        return new Desc(0, 64, 0);
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

        public int x, y, z;

        public Desc() {
        }

        public Desc(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public IMinionTask getTask() {
            return StayTask.this;
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            tag.setString("Task", ID);
            tag.setInteger("StayX", x);
            tag.setInteger("StayY", y);
            tag.setInteger("StayZ", z);
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            this.x = tag.getInteger("StayX");
            this.y = tag.getInteger("StayY");
            this.z = tag.getInteger("StayZ");
        }
    }
}
