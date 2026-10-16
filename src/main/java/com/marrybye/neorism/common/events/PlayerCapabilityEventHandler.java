package com.marrybye.neorism.common.events;

import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

/**
 * Event handler managing IExtendedEntityProperties lifecycle, cloning across deaths, and syncing.
 */
public class PlayerCapabilityEventHandler {

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer) {
            FactionPlayerHandler.register((EntityPlayer) event.entity);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        FactionPlayerHandler oldHandler = FactionPlayerHandler.get(event.original);
        FactionPlayerHandler newHandler = FactionPlayerHandler.get(event.entityPlayer);
        if (oldHandler != null && newHandler != null) {
            newHandler.copyFrom(oldHandler);
            ModLogger.debug("Cloned faction properties across respawn/dimension for player %s",
                event.entityPlayer.getCommandSenderName());
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            if (handler != null) {
                handler.sync();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(event.player);
        if (handler != null) {
            handler.sync();
            ModLogger.debug("Player %s logged in with faction=%s, level=%d",
                event.player.getCommandSenderName(), handler.getFaction().getId(), handler.getCurrentLevel());
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(event.player);
        if (handler != null) {
            handler.sync();
        }
    }
}
