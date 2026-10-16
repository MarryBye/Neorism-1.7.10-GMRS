package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.skills.SkillHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Sent from client to server to reset/forget a specific skill and refund its skill points.
 */
public class ForgetSkillPacket implements IMessage {

    private String skillId;

    public ForgetSkillPacket() {
    }

    public ForgetSkillPacket(String skillId) {
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

    public static class Handler implements IMessageHandler<ForgetSkillPacket, IMessage> {
        @Override
        public IMessage onMessage(ForgetSkillPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) return null;

            SkillHandler sh = SkillHandler.get(player);
            if (sh != null && message.skillId != null && !message.skillId.isEmpty()) {
                sh.forgetSkill(message.skillId);
            }
            return null;
        }
    }
}
