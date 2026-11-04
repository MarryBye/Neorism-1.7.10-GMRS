package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Server-authoritative packet to toggle flight abilities for Vampire Lords.
 */
public class ToggleWingsPacket implements IMessage {

    public ToggleWingsPacket() {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<ToggleWingsPacket, IMessage> {
        @Override
        public IMessage onMessage(ToggleWingsPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) return null;

            if (Helper.isVampire(player)) {
                FactionPlayerHandler fph = FactionPlayerHandler.get(player);
                if (fph != null && (fph.getCurrentLevel() >= 14 || fph.getLordLevel() > 0 || player.capabilities.isCreativeMode)) {
                    player.capabilities.allowFlying = !player.capabilities.allowFlying;
                    if (!player.capabilities.allowFlying) {
                        player.capabilities.isFlying = false;
                    }
                    player.sendPlayerAbilities();
                }
            }
            return null;
        }
    }
}
