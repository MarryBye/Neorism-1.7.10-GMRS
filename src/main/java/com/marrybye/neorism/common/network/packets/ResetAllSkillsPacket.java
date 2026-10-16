package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.skills.SkillHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Sent from client to server to reset all skills.
 */
public class ResetAllSkillsPacket implements IMessage {

    public ResetAllSkillsPacket() {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<ResetAllSkillsPacket, IMessage> {
        @Override
        public IMessage onMessage(ResetAllSkillsPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) return null;

            SkillHandler sh = SkillHandler.get(player);
            if (sh != null) {
                sh.resetSkills();
            }
            return null;
        }
    }
}
