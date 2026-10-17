package com.marrybye.neorism.common.network;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.network.packets.SyncFactionPacket;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Network pipeline for Neorism client-server packet communication.
 */
public class PacketHandler {

    private static SimpleNetworkWrapper INSTANCE;
    private static int packetId = 0;

    public static void init() {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Neorism.MODID);
        ModLogger.debug("Initialized network channel '%s'", Neorism.MODID);

        // Packet registrations
        registerMessage(SyncFactionPacket.Handler.class, SyncFactionPacket.class, Side.CLIENT);
        registerMessage(com.marrybye.neorism.common.network.packets.SyncBloodPacket.Handler.class, com.marrybye.neorism.common.network.packets.SyncBloodPacket.class, Side.CLIENT);
        registerMessage(com.marrybye.neorism.common.network.packets.BiteRequestPacket.Handler.class, com.marrybye.neorism.common.network.packets.BiteRequestPacket.class, Side.SERVER);
        registerMessage(com.marrybye.neorism.common.network.packets.ActionKeyPacket.Handler.class, com.marrybye.neorism.common.network.packets.ActionKeyPacket.class, Side.SERVER);
        registerMessage(com.marrybye.neorism.common.network.packets.SyncActionsPacket.Handler.class, com.marrybye.neorism.common.network.packets.SyncActionsPacket.class, Side.CLIENT);
        registerMessage(com.marrybye.neorism.common.network.packets.UnlockSkillPacket.Handler.class, com.marrybye.neorism.common.network.packets.UnlockSkillPacket.class, Side.SERVER);
        registerMessage(com.marrybye.neorism.common.network.packets.SyncSkillsPacket.Handler.class, com.marrybye.neorism.common.network.packets.SyncSkillsPacket.class, Side.CLIENT);
        registerMessage(com.marrybye.neorism.common.network.packets.SelectActionPacket.Handler.class, com.marrybye.neorism.common.network.packets.SelectActionPacket.class, Side.SERVER);
        registerMessage(com.marrybye.neorism.common.network.packets.ForgetSkillPacket.Handler.class, com.marrybye.neorism.common.network.packets.ForgetSkillPacket.class, Side.SERVER);
        registerMessage(com.marrybye.neorism.common.network.packets.ResetAllSkillsPacket.Handler.class, com.marrybye.neorism.common.network.packets.ResetAllSkillsPacket.class, Side.SERVER);
        registerMessage(com.marrybye.neorism.common.network.packets.ToggleActionPacket.Handler.class, com.marrybye.neorism.common.network.packets.ToggleActionPacket.class, Side.SERVER);
        registerMessage(com.marrybye.neorism.common.network.packets.SelectMinionTaskPacket.Handler.class, com.marrybye.neorism.common.network.packets.SelectMinionTaskPacket.class, Side.SERVER);
        registerMessage(com.marrybye.neorism.common.network.packets.ToggleMinionTaskLockPacket.Handler.class, com.marrybye.neorism.common.network.packets.ToggleMinionTaskLockPacket.class, Side.SERVER);
    }

    public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
        Class<? extends IMessageHandler<REQ, REPLY>> handler,
        Class<REQ> message,
        Side side
    ) {
        INSTANCE.registerMessage(handler, message, packetId++, side);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        if (player != null && INSTANCE != null) {
            INSTANCE.sendTo(message, player);
        }
    }

    public static void sendToAll(IMessage message) {
        if (INSTANCE != null) {
            INSTANCE.sendToAll(message);
        }
    }

    public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        if (INSTANCE != null) {
            INSTANCE.sendToAllAround(message, point);
        }
    }

    public static void sendToDimension(IMessage message, int dimensionId) {
        if (INSTANCE != null) {
            INSTANCE.sendToDimension(message, dimensionId);
        }
    }

    public static void sendToServer(IMessage message) {
        if (INSTANCE != null) {
            INSTANCE.sendToServer(message);
        }
    }
}
