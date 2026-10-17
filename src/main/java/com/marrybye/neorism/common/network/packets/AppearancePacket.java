package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class AppearancePacket implements IMessage {

    private int entityId;
    private int fangType;
    private int eyeType;
    private boolean glowingEyes;
    private boolean wings;
    private int wingsType;

    public AppearancePacket() {
    }

    public AppearancePacket(int fangType, int eyeType, boolean glowingEyes, boolean wings, int wingsType) {
        this(-1, fangType, eyeType, glowingEyes, wings, wingsType);
    }

    public AppearancePacket(int entityId, int fangType, int eyeType, boolean glowingEyes, boolean wings, int wingsType) {
        this.entityId = entityId;
        this.fangType = fangType;
        this.eyeType = eyeType;
        this.glowingEyes = glowingEyes;
        this.wings = wings;
        this.wingsType = wingsType;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.fangType = buf.readInt();
        this.eyeType = buf.readInt();
        this.glowingEyes = buf.readBoolean();
        this.wings = buf.readBoolean();
        this.wingsType = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(fangType);
        buf.writeInt(eyeType);
        buf.writeBoolean(glowingEyes);
        buf.writeBoolean(wings);
        buf.writeInt(wingsType);
    }

    public static class ServerHandler implements IMessageHandler<AppearancePacket, IMessage> {
        @Override
        public IMessage onMessage(AppearancePacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player != null) {
                FactionPlayerHandler fph = FactionPlayerHandler.get(player);
                if (fph != null) {
                    fph.setFangType(message.fangType);
                    fph.setEyeType(message.eyeType);
                    fph.setGlowingEyes(message.glowingEyes);
                    fph.setWings(message.wings);
                    fph.setWingsType(message.wingsType);
                    ModLogger.debug("Updated vampire appearance for %s (fangs: %d, eyes: %d, glowing: %s, wings: %s)",
                        player.getCommandSenderName(), message.fangType, message.eyeType, message.glowingEyes, message.wings);
                }
                AppearancePacket syncMsg = new AppearancePacket(
                    player.getEntityId(),
                    message.fangType,
                    message.eyeType,
                    message.glowingEyes,
                    message.wings,
                    message.wingsType
                );
                PacketHandler.sendTo(syncMsg, player);
                PacketHandler.sendToAllAround(syncMsg, new NetworkRegistry.TargetPoint(
                    player.dimension, player.posX, player.posY, player.posZ, 64.0D
                ));
            }
            return null;
        }
    }

    public static class ClientHandler implements IMessageHandler<AppearancePacket, IMessage> {
        @Override
        public IMessage onMessage(AppearancePacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null && mc.thePlayer.worldObj != null) {
                Entity entity = (message.entityId == -1 || message.entityId == mc.thePlayer.getEntityId())
                    ? mc.thePlayer
                    : mc.thePlayer.worldObj.getEntityByID(message.entityId);
                if (entity instanceof EntityPlayer) {
                    FactionPlayerHandler fph = FactionPlayerHandler.get((EntityPlayer) entity);
                    if (fph != null) {
                        fph.setFangType(message.fangType);
                        fph.setEyeType(message.eyeType);
                        fph.setGlowingEyes(message.glowingEyes);
                        fph.setWings(message.wings);
                        fph.setWingsType(message.wingsType);
                    }
                }
            }
            return null;
        }
    }
}
