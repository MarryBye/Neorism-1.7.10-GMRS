package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.entity.minion.EntityMinionBase;
import com.marrybye.neorism.common.entity.minion.MinionData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Serverbound packet when Lord toggles task lock for a minion.
 */
public class ToggleMinionTaskLockPacket implements IMessage {

    private int entityId;

    public ToggleMinionTaskLockPacket() {
    }

    public ToggleMinionTaskLockPacket(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
    }

    public static class Handler implements IMessageHandler<ToggleMinionTaskLockPacket, IMessage> {

        @Override
        public IMessage onMessage(ToggleMinionTaskLockPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player != null && player.worldObj != null) {
                Entity entity = player.worldObj.getEntityByID(message.entityId);
                if (entity instanceof EntityMinionBase) {
                    EntityMinionBase minion = (EntityMinionBase) entity;
                    if (minion.isLord(player)) {
                        MinionData data = minion.getMinionData();
                        if (data != null) {
                            boolean newState = !data.isTaskLocked();
                            data.setTaskLocked(newState);
                            minion.getDataWatcher().updateObject(EntityMinionBase.DW_TASK_LOCKED, (byte) (newState ? 1 : 0));
                        }
                    }
                }
            }
            return null;
        }
    }
}
