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

    public SyncFactionPacket() {
    }

    public SyncFactionPacket(int entityId, String factionId, int level, int lordLevel, boolean disguised) {
        this.entityId = entityId;
        this.factionId = factionId;
        this.level = level;
        this.lordLevel = lordLevel;
        this.disguised = disguised;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        factionId = ByteBufUtils.readUTF8String(buf);
        level = buf.readInt();
        lordLevel = buf.readInt();
        disguised = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeUTF8String(buf, factionId != null ? factionId : "neutral");
        buf.writeInt(level);
        buf.writeInt(lordLevel);
        buf.writeBoolean(disguised);
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
                            message.disguised
                        );
                        ModLogger.leveling(
                            "Client synced faction data for %s: faction=%s, level=%d, lordLevel=%d, disguised=%b",
                            targetPlayer.getCommandSenderName(), message.factionId, message.level, message.lordLevel, message.disguised
                        );
                    }
                }
            }
            return null;
        }
    }
}
