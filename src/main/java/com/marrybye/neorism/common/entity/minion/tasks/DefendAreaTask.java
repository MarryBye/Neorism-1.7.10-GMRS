package com.marrybye.neorism.common.entity.minion.tasks;

import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class DefendAreaTask implements IMinionTask {

    public static final String ID = "defend_area";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public IChatComponent getName() {
        return new ChatComponentTranslation("minion_task.neorism.defend_area");
    }

    @Override
    public boolean isAvailable(ILordPlayer lord) {
        return true;
    }

    @Override
    public IMinionTaskDesc activateTask(EntityPlayer lord, IMinionEntity minion, Object minionData) {
        if (minion != null) {
            return new Desc((int) minion.asEntity().posX, (int) minion.asEntity().posY, (int) minion.asEntity().posZ, 12);
        } else if (lord != null) {
            return new Desc((int) lord.posX, (int) lord.posY, (int) lord.posZ, 12);
        }
        return new Desc(0, 64, 0, 12);
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

        public int centerX, centerY, centerZ;
        public int radius = 12;

        public Desc() {
        }

        public Desc(int x, int y, int z, int radius) {
            this.centerX = x;
            this.centerY = y;
            this.centerZ = z;
            this.radius = radius;
        }

        @Override
        public IMinionTask getTask() {
            return DefendAreaTask.this;
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            tag.setString("Task", ID);
            tag.setInteger("CenterX", centerX);
            tag.setInteger("CenterY", centerY);
            tag.setInteger("CenterZ", centerZ);
            tag.setInteger("Radius", radius);
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            this.centerX = tag.getInteger("CenterX");
            this.centerY = tag.getInteger("CenterY");
            this.centerZ = tag.getInteger("CenterZ");
            this.radius = tag.getInteger("Radius");
            if (this.radius <= 0) this.radius = 12;
        }
    }
}
