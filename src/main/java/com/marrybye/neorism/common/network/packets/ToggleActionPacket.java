package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.action.ActionHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Sent from client to server to trigger/toggle an action directly (e.g. from the Radial Menu).
 */
public class ToggleActionPacket implements IMessage {

    private String actionId;

    public ToggleActionPacket() {
    }

    public ToggleActionPacket(String actionId) {
        this.actionId = actionId != null ? actionId : "";
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, actionId != null ? actionId : "");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.actionId = ByteBufUtils.readUTF8String(buf);
    }

    public static class Handler implements IMessageHandler<ToggleActionPacket, IMessage> {
        @Override
        public IMessage onMessage(ToggleActionPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) return null;

            ActionHandler ah = ActionHandler.get(player);
            if (ah != null && message.actionId != null && !message.actionId.isEmpty()) {
                ah.executeAction(message.actionId);
            }
            return null;
        }
    }
}
