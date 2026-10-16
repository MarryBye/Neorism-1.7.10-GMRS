package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.skills.SkillHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Sent from client to server when binding an action to Skill 1 (slot 1 / R) or Skill 2 (slot 2 / Z).
 */
public class SelectActionPacket implements IMessage {

    private int slot; // 1 = primary (R), 2 = secondary (Z)
    private String actionId;

    public SelectActionPacket() {
    }

    public SelectActionPacket(int slot, String actionId) {
        this.slot = slot;
        this.actionId = actionId != null ? actionId : "";
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        ByteBufUtils.writeUTF8String(buf, actionId != null ? actionId : "");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.slot = buf.readInt();
        this.actionId = ByteBufUtils.readUTF8String(buf);
    }

    public static class Handler implements IMessageHandler<SelectActionPacket, IMessage> {
        @Override
        public IMessage onMessage(SelectActionPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) return null;

            SkillHandler sh = SkillHandler.get(player);
            if (sh != null && message.actionId != null) {
                if (message.slot == 1) {
                    sh.setPrimaryAction(message.actionId);
                } else if (message.slot == 2) {
                    sh.setSecondaryAction(message.actionId);
                }
            }
            return null;
        }
    }
}
