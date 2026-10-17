package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.entity.minion.EntityMinionBase;
import com.marrybye.neorism.common.entity.minion.MinionData;
import com.marrybye.neorism.common.entity.minion.tasks.ModMinionTasks;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Serverbound packet when Lord selects a new task for a minion.
 */
public class SelectMinionTaskPacket implements IMessage {

    private int entityId;
    private String taskId;

    public SelectMinionTaskPacket() {
    }

    public SelectMinionTaskPacket(int entityId, String taskId) {
        this.entityId = entityId;
        this.taskId = taskId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        taskId = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeUTF8String(buf, taskId != null ? taskId : "");
    }

    public static class Handler implements IMessageHandler<SelectMinionTaskPacket, IMessage> {

        @Override
        public IMessage onMessage(SelectMinionTaskPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player != null && player.worldObj != null) {
                Entity entity = player.worldObj.getEntityByID(message.entityId);
                if (entity instanceof EntityMinionBase) {
                    EntityMinionBase minion = (EntityMinionBase) entity;
                    if (minion.isLord(player)) {
                        IMinionTask task = ModMinionTasks.getTask(message.taskId);
                        if (task != null) {
                            MinionData data = minion.getMinionData();
                            if (data != null) {
                                IMinionTask.IMinionTaskDesc desc = task.activateTask(player, minion, data);
                                data.setCurrentTaskDesc(desc);
                            }
                        }
                    }
                }
            }
            return null;
        }
    }
}
