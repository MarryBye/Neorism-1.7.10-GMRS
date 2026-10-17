package com.marrybye.neorism.common.events;

import java.util.List;

import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.entity.converted.EntityConvertedVillager;
import com.marrybye.neorism.common.entity.hunter.EntityHunter;
import com.marrybye.neorism.common.entity.vampire.EntityVampire;
import com.marrybye.neorism.common.tileentity.TileEntityTotem;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

/**
 * Handles village faction territory mechanics:
 * - Peaceful blood feeding and golem pacification in Vampire-controlled villages.
 * - Hunter trade discounts and welcome in Hunter-controlled villages.
 * - Faction patrol spawning.
 */
public class VillageFactionHandler {

    public static TileEntityTotem getNearbyTotem(World world, double x, double y, double z, double radius) {
        if (world == null) return null;
        int minX = (int) Math.floor(x - radius) >> 4;
        int maxX = (int) Math.floor(x + radius) >> 4;
        int minZ = (int) Math.floor(z - radius) >> 4;
        int maxZ = (int) Math.floor(z + radius) >> 4;

        for (int cx = minX; cx <= maxX; cx++) {
            for (int cz = minZ; cz <= maxZ; cz++) {
                if (world.getChunkProvider().chunkExists(cx, cz)) {
                    for (Object obj : world.getChunkFromChunkCoords(cx, cz).chunkTileEntityMap.values()) {
                        if (obj instanceof TileEntityTotem) {
                            TileEntityTotem totem = (TileEntityTotem) obj;
                            if (totem.getDistanceFrom(x, y, z) <= radius * radius) {
                                return totem;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        if (event.entityPlayer.worldObj.isRemote) return;
        if (!(event.target instanceof EntityVillager)) return;

        EntityPlayer player = event.entityPlayer;
        EntityVillager villager = (EntityVillager) event.target;
        World world = player.worldObj;

        TileEntityTotem totem = getNearbyTotem(world, villager.posX, villager.posY, villager.posZ, 48.0D);
        if (totem == null) return;

        int controllingFaction = totem.getControllingFactionId();

        if (controllingFaction == TileEntityTotem.FACTION_VAMPIRE && Helper.isVampire(player)) {
            // Vampire territory: shift-right-click feeds on villager peacefully
            if (player.isSneaking() && player.getCurrentEquippedItem() == null) {
                VampirePlayer vp = VampirePlayer.get(player);
                if (vp != null && vp.getBloodStats().getBloodLevel() < vp.getBloodStats().getMaxBlood()) {
                    vp.getBloodStats().addBlood(4, 1.0F);
                    world.playSoundAtEntity(villager, "random.drink", 0.8F, 1.0F);
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.peaceful_feed"));
                    event.setCanceled(true);
                    ModLogger.debug("Vampire %s peacefully drank blood in Vampire village at [%d, %d, %d]",
                        player.getCommandSenderName(), (int) villager.posX, (int) villager.posY, (int) villager.posZ);
                }
            }
        } else if (controllingFaction == TileEntityTotem.FACTION_HUNTER && Helper.isHunter(player)) {
            // Hunter territory: friendly greeting
            if (world.rand.nextInt(10) == 0) {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.villager_hunter_greeting"));
            }
        }
    }

    @SubscribeEvent
    public void onGolemTarget(LivingSetAttackTargetEvent event) {
        if (!(event.entityLiving instanceof EntityIronGolem)) return;
        if (!(event.target instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.target;
        if (!Helper.isVampire(player)) return;

        World world = player.worldObj;
        TileEntityTotem totem = getNearbyTotem(world, player.posX, player.posY, player.posZ, 48.0D);
        if (totem != null && totem.getControllingFactionId() == TileEntityTotem.FACTION_VAMPIRE) {
            // Iron Golems do not attack vampires in a vampire-controlled village
            ((EntityIronGolem) event.entityLiving).setAttackTarget(null);
        }
    }

    @SubscribeEvent
    public void onSpawnCheck(LivingSpawnEvent.CheckSpawn event) {
        World world = event.world;
        if (world == null || world.isRemote) return;

        TileEntityTotem totem = getNearbyTotem(world, event.x, event.y, event.z, 48.0D);
        if (totem == null) return;

        int controlling = totem.getControllingFactionId();
        if (controlling == TileEntityTotem.FACTION_VAMPIRE) {
            // 20% chance to convert vanilla zombie spawns to Vampire patrols
            if (event.entityLiving instanceof net.minecraft.entity.monster.EntityZombie && world.rand.nextInt(5) == 0) {
                EntityVampire v = new EntityVampire(world);
                v.setPosition(event.x, event.y, event.z);
                world.spawnEntityInWorld(v);
                event.setResult(cpw.mods.fml.common.eventhandler.Event.Result.DENY);
            }
        } else if (controlling == TileEntityTotem.FACTION_HUNTER) {
            // 20% chance to spawn Hunter patrol
            if (event.entityLiving instanceof net.minecraft.entity.monster.EntityZombie && world.rand.nextInt(5) == 0) {
                EntityHunter h = new EntityHunter(world);
                h.setPosition(event.x, event.y, event.z);
                world.spawnEntityInWorld(h);
                event.setResult(cpw.mods.fml.common.eventhandler.Event.Result.DENY);
            }
        }
    }
}
