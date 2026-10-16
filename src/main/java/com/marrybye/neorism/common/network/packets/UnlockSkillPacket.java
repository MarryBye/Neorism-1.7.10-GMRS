package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.skills.SkillHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Sent from client to server when a player clicks to unlock a skill node.
 */
public class UnlockSkillPacket implements IMessage {

    private String skillId;

    public UnlockSkillPacket() {
    }

    public UnlockSkillPacket(String skillId) {
        this.skillId = skillId != null ? skillId : "";
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, skillId != null ? skillId : "");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.skillId = ByteBufUtils.readUTF8String(buf);
    }

    public static class Handler implements IMessageHandler<UnlockSkillPacket, IMessage> {
        @Override
        public IMessage onMessage(UnlockSkillPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) return null;

            SkillHandler sh = SkillHandler.get(player);
            if (sh != null && message.skillId != null && !message.skillId.isEmpty()) {
                sh.unlockSkill(message.skillId);
            }
            return null;
        }
    }
}
