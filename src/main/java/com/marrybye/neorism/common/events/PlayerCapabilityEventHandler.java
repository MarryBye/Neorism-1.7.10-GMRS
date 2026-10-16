package com.marrybye.neorism.common.events;

import com.marrybye.neorism.common.entity.ExtendedCreature;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Event handler managing IExtendedEntityProperties lifecycle, cloning across deaths, updates, and syncing.
 */
public class PlayerCapabilityEventHandler {

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer) {
            FactionPlayerHandler.register((EntityPlayer) event.entity);
            VampirePlayer.register((EntityPlayer) event.entity);
            com.marrybye.neorism.common.hunter.HunterPlayer.register((EntityPlayer) event.entity);
        } else if (event.entity instanceof EntityLivingBase) {
            ExtendedCreature.register((EntityLivingBase) event.entity);
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

        VampirePlayer oldVp = VampirePlayer.get(event.original);
        VampirePlayer newVp = VampirePlayer.get(event.entityPlayer);
        if (oldVp != null && newVp != null) {
            newVp.copyFrom(oldVp);
        }

        com.marrybye.neorism.common.hunter.HunterPlayer oldHp = com.marrybye.neorism.common.hunter.HunterPlayer.get(event.original);
        com.marrybye.neorism.common.hunter.HunterPlayer newHp = com.marrybye.neorism.common.hunter.HunterPlayer.get(event.entityPlayer);
        if (oldHp != null && newHp != null) {
            newHp.copyFrom(oldHp);
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
            VampirePlayer vp = VampirePlayer.get(player);
            if (vp != null) {
                vp.syncBlood();
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
        VampirePlayer vp = VampirePlayer.get(event.player);
        if (vp != null) {
            vp.syncBlood();
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(event.player);
        if (handler != null) {
            handler.sync();
        }
        VampirePlayer vp = VampirePlayer.get(event.player);
        if (vp != null) {
            vp.syncBlood();
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            VampirePlayer vp = VampirePlayer.get(player);
            if (vp != null) {
                vp.onUpdate();
            }
            com.marrybye.neorism.common.hunter.HunterPlayer hp = com.marrybye.neorism.common.hunter.HunterPlayer.get(player);
            if (hp != null) {
                hp.onUpdate();
            }
        } else {
            ExtendedCreature creature = ExtendedCreature.get(event.entityLiving);
            if (creature != null) {
                creature.updateLiving();
            }
        }

        if (!event.entityLiving.worldObj.isRemote && event.entityLiving.isPotionActive(com.marrybye.neorism.common.core.ModPotions.sanguinare)) {
            net.minecraft.potion.PotionEffect effect = event.entityLiving.getActivePotionEffect(com.marrybye.neorism.common.core.ModPotions.sanguinare);
            if (effect != null && effect.getDuration() <= 2) {
                if (event.entityLiving instanceof EntityPlayer) {
                    VampirePlayer vp = VampirePlayer.get((EntityPlayer) event.entityLiving);
                    if (vp != null) {
                        vp.onSanguinareFinished();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(net.minecraftforge.event.world.WorldEvent.Unload event) {
        if (!event.world.isRemote && event.world.provider.dimensionId == 0) {
            com.marrybye.neorism.common.world.garlic.GarlicHandler.instance().clearCache();
        }
    }
}
