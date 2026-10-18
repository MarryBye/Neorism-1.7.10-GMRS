package com.marrybye.neorism.common.util;

import com.marrybye.neorism.api.entity.IFactionEntity;
import com.marrybye.neorism.api.entity.IHunterEntity;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.api.faction.IPlayableFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
        if (com.marrybye.neorism.common.integration.IntegrationManager.isHodgepodgeLoaded()) {
            return com.marrybye.neorism.common.integration.HodgepodgeIntegration.isSunPresent(world);
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
     * Checks if entity can really see target within field of view (100 degrees cone).
     * Exact 1:1 ported formula from modern Vampirism UtilLib.canReallySee.
     */
    public static boolean canReallySee(EntityLivingBase entity, EntityLivingBase target, boolean alsoRaytrace) {
        if (entity == null || target == null) return false;
        if (alsoRaytrace && !entity.canEntityBeSeen(target)) {
            return false;
        }
        double lookX = -Math.sin(entity.rotationYawHead / 180.0F * (float) Math.PI);
        double lookZ = Math.cos(entity.rotationYawHead / 180.0F * (float) Math.PI);
        double lookLen = Math.sqrt(lookX * lookX + lookZ * lookZ);
        if (lookLen > 0) {
            lookX /= lookLen;
            lookZ /= lookLen;
        }
        double distX = target.posX - entity.posX;
        double distZ = target.posZ - entity.posZ;
        double distLen = Math.sqrt(distX * distX + distZ * distZ);
        if (distLen > 0) {
            distX /= distLen;
            distZ /= distLen;
        }
        double dot = lookX * distX + lookZ * distZ;
        dot = Math.max(-1.0D, Math.min(1.0D, dot));
        double alpha = Math.acos(dot);
        return alpha < Math.PI / 1.8D;
    }

    /**
     * If held by a vampire in hand, forces them to drop the item and inflicts toxicant effect.
     * Exact 1:1 ported from Helper.handleHeldNonVampireItem.
     */
    public static boolean handleHeldNonVampireItem(ItemStack stack, Entity entity, boolean isSelected) {
        if (entity instanceof net.minecraft.entity.EntityLivingBase && isSelected && entity.ticksExisted % 16 == 8) {
            if (Helper.isVampire(entity)) {
                ((net.minecraft.entity.EntityLivingBase) entity).addPotionEffect(new net.minecraft.potion.PotionEffect(com.marrybye.neorism.common.core.ModPotions.garlic.id, 20, 1));
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    player.dropPlayerItemWithRandomChoice(stack, true);
                    player.destroyCurrentEquippedItem();
                }
                return true;
            }
        }
        return false;
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

    /**
     * Applies garlic effect and garlic burn damage to a vampire entity.
     */
    public static void applyGarlicBurn(EntityLivingBase entity, float multiplier) {
        if (entity != null && isVampire(entity)) {
            entity.addPotionEffect(new net.minecraft.potion.PotionEffect(com.marrybye.neorism.common.core.ModPotions.garlic.id, (int) (multiplier * 20), 1));
            entity.attackEntityFrom(com.marrybye.neorism.common.core.ModDamageSource.GARLIC, multiplier);
        }
    }

    private static java.lang.reflect.Method setSizeMethod;
    static {
        try {
            setSizeMethod = Entity.class.getDeclaredMethod("setSize", float.class, float.class);
            setSizeMethod.setAccessible(true);
        } catch (Exception e1) {
            try {
                setSizeMethod = Entity.class.getDeclaredMethod("func_70105_a", float.class, float.class);
                setSizeMethod.setAccessible(true);
            } catch (Exception e2) {
                setSizeMethod = null;
            }
        }
    }

    public static void setEntitySize(Entity entity, float width, float height) {
        if (entity == null) return;
        if (setSizeMethod != null) {
            try {
                setSizeMethod.invoke(entity, width, height);
                return;
            } catch (Exception ignored) {}
        }
        entity.width = width;
        entity.height = height;
        if (entity.boundingBox != null) {
            entity.boundingBox.maxX = entity.boundingBox.minX + (double) width;
            entity.boundingBox.maxZ = entity.boundingBox.minZ + (double) width;
            entity.boundingBox.maxY = entity.boundingBox.minY + (double) height;
        }
    }
}

