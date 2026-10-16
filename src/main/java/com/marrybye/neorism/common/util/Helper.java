package com.marrybye.neorism.common.util;

import com.marrybye.neorism.api.entity.IFactionEntity;
import com.marrybye.neorism.api.entity.IHunterEntity;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.api.faction.IPlayableFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * General utility and query helper for Neorism factions, entities, day-night cycles, and items.
 */
public class Helper {

    public static boolean isVampire(Entity entity) {
        if (entity instanceof EntityPlayer) {
            return isVampire((EntityPlayer) entity);
        }
        return entity instanceof IVampireEntity;
    }

    public static boolean isVampire(EntityPlayer player) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        return handler != null && handler.isVampire();
    }

    public static boolean isHunter(Entity entity) {
        if (entity instanceof EntityPlayer) {
            return isHunter((EntityPlayer) entity);
        }
        return entity instanceof IHunterEntity;
    }

    public static boolean isHunter(EntityPlayer player) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        return handler != null && handler.isHunter();
    }

    public static IFaction getFaction(Entity entity) {
        if (entity instanceof EntityPlayer) {
            return getFaction((EntityPlayer) entity);
        }
        if (entity instanceof IFactionEntity) {
            return ((IFactionEntity) entity).getFaction();
        }
        return ModFactions.NEUTRAL;
    }

    public static IPlayableFaction getFaction(EntityPlayer player) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        return handler != null ? handler.getFaction() : ModFactions.NEUTRAL;
    }

    public static int getLevel(EntityPlayer player) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        return handler != null ? handler.getCurrentLevel() : 0;
    }

    public static int getLordLevel(EntityPlayer player) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        return handler != null ? handler.getLordLevel() : 0;
    }

    public static boolean canBecomeVampire(EntityPlayer player) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        return handler != null && handler.canJoin(ModFactions.VAMPIRE);
    }

    public static boolean canBecomeHunter(EntityPlayer player) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        return handler != null && handler.canJoin(ModFactions.HUNTER);
    }

    public static boolean appearsAsVampire(Entity entity, Entity viewer) {
        if (entity instanceof EntityPlayer) {
            return appearsAsVampire((EntityPlayer) entity, viewer);
        }
        return isVampire(entity);
    }

    public static boolean appearsAsVampire(EntityPlayer player, Entity viewer) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        if (handler == null) return false;
        if (handler.isDisguised()) {
            return false;
        }
        return handler.isVampire();
    }

    /**
     * Checks if the time of day corresponds to daylight (sun angle).
     * Exact 1:1 ported formula from modern Vampirism Helper.isDay().
     */
    public static boolean isDay(World world) {
        if (world == null || world.provider == null || !world.provider.isSurfaceWorld()) {
            return false;
        }
        float angle = world.getCelestialAngle(1.0F);
        return angle > 0.78F || angle < 0.24F;
    }

    /**
     * Checks if entity is located within a vampire biome.
     */
    public static boolean isEntityInVampireBiome(Entity entity) {
        if (entity == null || entity.worldObj == null) return false;
        net.minecraft.world.biome.BiomeGenBase biome =
            entity.worldObj.getBiomeGenForCoords((int) entity.posX, (int) entity.posZ);
        return biome != null && biome.biomeName != null && biome.biomeName.toLowerCase().contains("vampire");
    }

    /**
     * Checks if position is exposed to direct sunlight.
     */
    public static boolean canSunBurn(World world, int x, int y, int z) {
        if (world == null || world.isRemote) return false;
        if (!isDay(world)) return false;
        if (world.isRaining()) return false;
        return world.canBlockSeeTheSky(x, y, z);
    }

    /**
     * Shrinks item stack by 1 and places container/remainder item in player inventory.
     */
    public static ItemStack shrinkItemStack(ItemStack stack, EntityPlayer player) {
        if (stack == null) return null;
        ItemStack containerItem = stack.getItem().hasContainerItem(stack) ? stack.getItem().getContainerItem(stack) : null;
        stack.stackSize--;
        if (containerItem != null) {
            if (stack.stackSize <= 0) {
                return containerItem;
            } else {
                if (!player.inventory.addItemStackToInventory(containerItem)) {
                    player.dropPlayerItemWithRandomChoice(containerItem, false);
                }
            }
        }
        return stack.stackSize > 0 ? stack : null;
    }
}
