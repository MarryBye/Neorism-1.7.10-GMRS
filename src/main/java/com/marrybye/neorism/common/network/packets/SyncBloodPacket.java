package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.vampire.VampirePlayer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Packet sent from server to client to synchronize vampire blood level, capacity, and saturation.
 */
public class SyncBloodPacket implements IMessage {

    private int entityId;
    private int bloodLevel;
    private int maxBlood;
    private float bloodSaturation;
    private float bloodExhaustion;

    public SyncBloodPacket() {
    }

    public SyncBloodPacket(int entityId, int bloodLevel, int maxBlood, float bloodSaturation, float bloodExhaustion) {
        this.entityId = entityId;
        this.bloodLevel = bloodLevel;
        this.maxBlood = maxBlood;
        this.bloodSaturation = bloodSaturation;
        this.bloodExhaustion = bloodExhaustion;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.bloodLevel = buf.readInt();
        this.maxBlood = buf.readInt();
        this.bloodSaturation = buf.readFloat();
        this.bloodExhaustion = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(bloodLevel);
        buf.writeInt(maxBlood);
        buf.writeFloat(bloodSaturation);
        buf.writeFloat(bloodExhaustion);
    }

    public static class Handler implements IMessageHandler<SyncBloodPacket, IMessage> {

        @Override
        public IMessage onMessage(SyncBloodPacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null) {
                Entity entity = mc.theWorld.getEntityByID(message.entityId);
                if (entity instanceof EntityPlayer) {
                    VampirePlayer vp = VampirePlayer.get((EntityPlayer) entity);
                    if (vp != null && vp.getBloodStats() != null) {
                        vp.getBloodStats().setClientData(
                            message.bloodLevel,
                            message.maxBlood,
                            message.bloodSaturation
                        );
                    }
                }
            }
            return null;
        }
    }
}
