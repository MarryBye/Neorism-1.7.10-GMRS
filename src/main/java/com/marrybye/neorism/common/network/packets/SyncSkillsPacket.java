package com.marrybye.neorism.common.network.packets;

import java.util.HashSet;
import java.util.Set;

import com.marrybye.neorism.common.skills.SkillHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

/**
 * Sent from server to client to synchronize unlocked skills and skill points.
 */
public class SyncSkillsPacket implements IMessage {

    private int skillPoints;
    private String primaryAction;
    private String secondaryAction;
    private Set<String> unlockedSkills = new HashSet<String>();

    public SyncSkillsPacket() {
    }

    public SyncSkillsPacket(int skillPoints, String primaryAction, String secondaryAction, Set<String> unlockedSkills) {
        this.skillPoints = skillPoints;
        this.primaryAction = primaryAction != null ? primaryAction : "";
        this.secondaryAction = secondaryAction != null ? secondaryAction : "";
        if (unlockedSkills != null) this.unlockedSkills.addAll(unlockedSkills);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(skillPoints);
        ByteBufUtils.writeUTF8String(buf, primaryAction != null ? primaryAction : "");
        ByteBufUtils.writeUTF8String(buf, secondaryAction != null ? secondaryAction : "");

        buf.writeInt(unlockedSkills.size());
        for (String skill : unlockedSkills) {
            ByteBufUtils.writeUTF8String(buf, skill);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.skillPoints = buf.readInt();
        this.primaryAction = ByteBufUtils.readUTF8String(buf);
        this.secondaryAction = ByteBufUtils.readUTF8String(buf);

        int count = buf.readInt();
        this.unlockedSkills = new HashSet<String>();
        for (int i = 0; i < count; i++) {
            this.unlockedSkills.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    public static class Handler implements IMessageHandler<SyncSkillsPacket, IMessage> {
        @Override
        public IMessage onMessage(SyncSkillsPacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null) {
                SkillHandler sh = SkillHandler.get(mc.thePlayer);
                if (sh != null) {
                    sh.setClientData(message.skillPoints, message.primaryAction, message.secondaryAction, message.unlockedSkills);
                }
            }
            return null;
        }
    }
}
