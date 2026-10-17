package com.marrybye.neorism.common.entity.minion;

import com.marrybye.neorism.common.entity.minion.tasks.ModMinionTasks;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Controller managing all minion slots for a specific Lord player.
 */
public class PlayerMinionController {

    private final UUID lordUUID;
    private final List<MinionData> minions = new ArrayList<MinionData>();
    private int maxMinions = 2;

    public PlayerMinionController(UUID lordUUID) {
        this.lordUUID = lordUUID;
    }

    public UUID getLordUUID() {
        return lordUUID;
    }

    public int getMaxMinions() {
        return maxMinions;
    }

    public void setMaxMinions(int max) {
        this.maxMinions = Math.max(0, max);
    }

    public int getMinionCount() {
        return minions.size();
    }

    public MinionData getMinionData(int index) {
        if (index >= 0 && index < minions.size()) {
            return minions.get(index);
        }
        return null;
    }

    public MinionData createMinionSlot(String name) {
        if (minions.size() >= maxMinions) {
            return null;
        }
        MinionData data = new MinionData(name != null ? name : ("Minion #" + (minions.size() + 1)));
        minions.add(data);
        return data;
    }

    public boolean removeMinion(int index) {
        if (index >= 0 && index < minions.size()) {
            minions.remove(index);
            return true;
        }
        return false;
    }

    public void tick(EntityPlayer lord) {
        for (MinionData data : minions) {
            IMinionTask.IMinionTaskDesc taskDesc = data.getCurrentTaskDesc();
            if (taskDesc != null && taskDesc.getTask() != null) {
                taskDesc.getTask().tickBackground(taskDesc, data);
            }
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setString("LordUUID", lordUUID.toString());
        tag.setInteger("MaxMinions", maxMinions);

        NBTTagList list = new NBTTagList();
        for (int i = 0; i < minions.size(); i++) {
            NBTTagCompound minionTag = new NBTTagCompound();
            minionTag.setInteger("Slot", i);
            minions.get(i).writeToNBT(minionTag);
            list.appendTag(minionTag);
        }
        tag.setTag("Minions", list);
    }

    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("MaxMinions")) {
            this.maxMinions = tag.getInteger("MaxMinions");
        }
        minions.clear();
        NBTTagList list = tag.getTagList("Minions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound minionTag = list.getCompoundTagAt(i);
            MinionData data = new MinionData();
            data.readFromNBT(minionTag);
            minions.add(data);
        }
    }
}
