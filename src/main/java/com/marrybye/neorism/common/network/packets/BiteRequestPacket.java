package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.vampire.VampirePlayer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Client to server request packet to start or cancel biting/feeding on a target entity.
 */
public class BiteRequestPacket implements IMessage {

    public static final int ACTION_START = 0;
    public static final int ACTION_STOP = 1;

    private int targetEntityId;
    private int action;

    public BiteRequestPacket() {
    }

    public BiteRequestPacket(int targetEntityId, int action) {
        this.targetEntityId = targetEntityId;
        this.action = action;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.targetEntityId = buf.readInt();
        this.action = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(targetEntityId);
        buf.writeInt(action);
    }

    public static class Handler implements IMessageHandler<BiteRequestPacket, IMessage> {

        @Override
        public IMessage onMessage(BiteRequestPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player != null) {
                VampirePlayer vp = VampirePlayer.get(player);
                if (vp != null) {
                    if (message.action == ACTION_START) {
                        Entity target = player.worldObj.getEntityByID(message.targetEntityId);
                        if (target instanceof EntityLivingBase) {
                            vp.startFeeding((EntityLivingBase) target);
                        }
                    } else if (message.action == ACTION_STOP) {
                        vp.endFeeding();
                    }
                }
            }
            return null;
        }
    }
}
