package com.marrybye.neorism.common.vampire;

import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Registry and evaluation engine for Vampire Sun Damage.
 * Ported 1:1 from modern Vampirism SundamageRegistry.
 */
public class SundamageRegistry {

    private static final SundamageRegistry INSTANCE = new SundamageRegistry();

    public static SundamageRegistry instance() {
        return INSTANCE;
    }

    public static boolean isEntityInSunlight(EntityLivingBase entity) {
        return INSTANCE.isGettingSundamage(entity);
    }

    /**
     * Checks if a world coordinate is vulnerable to sun damage.
     */
    public boolean hasSunDamage(World world, int x, int y, int z) {
        if (world == null || world.provider == null || !world.provider.isSurfaceWorld()) {
            return false;
        }
        if (world.provider.dimensionId != 0) {
            // By default, only overworld surface dimensions have sun damage
            return false;
        }
        // Biome check: Vampire Forest biome canopy shields from sunlight
        net.minecraft.world.biome.BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        if (biome != null && biome.biomeName != null && biome.biomeName.toLowerCase().contains("vampire")) {
            return false;
        }
        return true;
    }

    /**
     * Checks if an entity is currently taking sun damage.
     */
    public boolean isGettingSundamage(EntityLivingBase entity) {
        if (entity == null || entity.worldObj == null || entity.isDead) return false;
        World world = entity.worldObj;

        if (!hasSunDamage(world, (int) entity.posX, (int) entity.posY, (int) entity.posZ)) {
            return false;
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.capabilities.isCreativeMode || player.capabilities.disableDamage) {
                return false;
            }
            if (isHoldingUmbrella(player)) {
                return false;
            }
        }

        // Potion protection (Sunscreen)
        if (entity.isPotionActive(ModPotions.sunscreen)) {
            return false;
        }

        // Water and Rain protection
        if (entity.isInWater()) {
            return false;
        }
        if (world.isRaining() && world.canLightningStrikeAt((int) entity.posX, (int) entity.posY, (int) entity.posZ)) {
            return false;
        }

        // Daylight and Sky exposure
        if (!Helper.isDay(world)) {
            return false;
        }
        return world.canBlockSeeTheSky((int) entity.posX, (int) Math.round(entity.posY + entity.getEyeHeight()), (int) entity.posZ);
    }

    /**
     * Checks if player is holding an umbrella in hand.
     */
    public boolean isHoldingUmbrella(EntityPlayer player) {
        if (player == null) return false;
        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() != null) {
            String unloc = held.getItem().getUnlocalizedName();
            return unloc != null && unloc.toLowerCase().contains("umbrella");
        }
        return false;
    }
}
