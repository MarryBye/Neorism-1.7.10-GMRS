package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Network packet to synchronize player faction, level, lord rank, and disguise state to clients.
 */
public class SyncFactionPacket implements IMessage {

    private int entityId;
    private String factionId;
    private int level;
    private int lordLevel;
    private boolean disguised;
    private byte titleGender;

    public SyncFactionPacket() {
    }

    public SyncFactionPacket(int entityId, String factionId, int level, int lordLevel, boolean disguised) {
        this(entityId, factionId, level, lordLevel, disguised, (byte) 0);
    }

    public SyncFactionPacket(int entityId, String factionId, int level, int lordLevel, boolean disguised, byte titleGender) {
        this.entityId = entityId;
        this.factionId = factionId;
        this.level = level;
        this.lordLevel = lordLevel;
        this.disguised = disguised;
        this.titleGender = titleGender;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        factionId = ByteBufUtils.readUTF8String(buf);
        level = buf.readInt();
        lordLevel = buf.readInt();
        disguised = buf.readBoolean();
        if (buf.isReadable()) {
            titleGender = buf.readByte();
        } else {
            titleGender = 0;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeUTF8String(buf, factionId != null ? factionId : "neutral");
        buf.writeInt(level);
        buf.writeInt(lordLevel);
        buf.writeBoolean(disguised);
        buf.writeByte(titleGender);
    }

    public static class Handler implements IMessageHandler<SyncFactionPacket, IMessage> {

        @Override
        public IMessage onMessage(SyncFactionPacket message, MessageContext ctx) {
            EntityPlayer clientPlayer = Minecraft.getMinecraft().thePlayer;
            if (clientPlayer != null && clientPlayer.worldObj != null) {
                Entity target = clientPlayer.worldObj.getEntityByID(message.entityId);
                if (target instanceof EntityPlayer) {
                    EntityPlayer targetPlayer = (EntityPlayer) target;
                    FactionPlayerHandler handler = FactionPlayerHandler.get(targetPlayer);
                    if (handler != null) {
                        handler.setClientData(
                            ModFactions.getFaction(message.factionId),
                            message.level,
                            message.lordLevel,
                            message.disguised,
                            message.titleGender
                        );
                        targetPlayer.refreshDisplayName();
                        ModLogger.leveling(
                            "Client synced faction data for %s: faction=%s, level=%d, lordLevel=%d, disguised=%b, gender=%d",
                            targetPlayer.getCommandSenderName(), message.factionId, message.level, message.lordLevel, message.disguised, message.titleGender
                        );
                    }
                }
            }
            return null;
        }
    }
}
