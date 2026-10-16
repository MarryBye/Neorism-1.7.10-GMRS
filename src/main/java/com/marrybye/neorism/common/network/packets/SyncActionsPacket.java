package com.marrybye.neorism.common.network.packets;

import java.util.HashMap;
import java.util.Map;

import com.marrybye.neorism.common.action.ActionHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

/**
 * Sent from server to client to synchronize action cooldowns and active states.
 */
public class SyncActionsPacket implements IMessage {

    private Map<String, Integer> cooldowns = new HashMap<String, Integer>();
    private Map<String, Integer> activeActions = new HashMap<String, Integer>();

    public SyncActionsPacket() {
    }

    public SyncActionsPacket(Map<String, Integer> cooldowns, Map<String, Integer> activeActions) {
        if (cooldowns != null) this.cooldowns.putAll(cooldowns);
        if (activeActions != null) this.activeActions.putAll(activeActions);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(cooldowns.size());
        for (Map.Entry<String, Integer> entry : cooldowns.entrySet()) {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            buf.writeInt(entry.getValue());
        }

        buf.writeInt(activeActions.size());
        for (Map.Entry<String, Integer> entry : activeActions.entrySet()) {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            buf.writeInt(entry.getValue());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int cdSize = buf.readInt();
        cooldowns = new HashMap<String, Integer>();
        for (int i = 0; i < cdSize; i++) {
            String key = ByteBufUtils.readUTF8String(buf);
            int val = buf.readInt();
            cooldowns.put(key, val);
        }

        int activeSize = buf.readInt();
        activeActions = new HashMap<String, Integer>();
        for (int i = 0; i < activeSize; i++) {
            String key = ByteBufUtils.readUTF8String(buf);
            int val = buf.readInt();
            activeActions.put(key, val);
        }
    }

    public static class Handler implements IMessageHandler<SyncActionsPacket, IMessage> {
        @Override
        public IMessage onMessage(SyncActionsPacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null) {
                ActionHandler ah = ActionHandler.get(mc.thePlayer);
                if (ah != null) {
                    ah.setClientData(message.cooldowns, message.activeActions);
                }
            }
            return null;
        }
    }
}
