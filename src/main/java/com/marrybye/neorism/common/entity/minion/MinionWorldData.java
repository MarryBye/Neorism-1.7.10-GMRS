package com.marrybye.neorism.common.entity.minion;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * WorldSavedData storage for all Lord minion controllers.
 */
public class MinionWorldData extends WorldSavedData {

    public static final String IDENTIFIER = "neorism_minions";
    private final Map<UUID, PlayerMinionController> controllers = new HashMap<UUID, PlayerMinionController>();

    public MinionWorldData() {
        super(IDENTIFIER);
    }

    public MinionWorldData(String id) {
        super(id);
    }

    public static MinionWorldData get(World world) {
        if (world == null || world.mapStorage == null) return null;
        MinionWorldData data = (MinionWorldData) world.mapStorage.loadData(MinionWorldData.class, IDENTIFIER);
        if (data == null) {
            data = new MinionWorldData();
            world.mapStorage.setData(IDENTIFIER, data);
        }
        return data;
    }

    public PlayerMinionController getController(UUID lordUUID) {
        if (lordUUID == null) return null;
        return controllers.get(lordUUID);
    }

    public PlayerMinionController getOrCreateController(UUID lordUUID) {
        if (lordUUID == null) return null;
        PlayerMinionController c = controllers.get(lordUUID);
        if (c == null) {
            c = new PlayerMinionController(lordUUID);
            controllers.put(lordUUID, c);
            markDirty();
        }
        return c;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        controllers.clear();
        NBTTagList list = tag.getTagList("Controllers", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound cTag = list.getCompoundTagAt(i);
            try {
                UUID uuid = UUID.fromString(cTag.getString("LordUUID"));
                PlayerMinionController c = new PlayerMinionController(uuid);
                c.readFromNBT(cTag);
                controllers.put(uuid, c);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID, PlayerMinionController> entry : controllers.entrySet()) {
            NBTTagCompound cTag = new NBTTagCompound();
            entry.getValue().writeToNBT(cTag);
            list.appendTag(cTag);
        }
        tag.setTag("Controllers", list);
    }
}
