package com.marrybye.neorism.common.integration;

import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Integration with Hodgepodge mod.
 * Supports:
 * - Celestial time synchronization and day/night angle calculations for vampire sun exposure.
 */
public class HodgepodgeIntegration {

    public static void init() {
        ModLogger.debug("Hodgepodge integration active: Celestial time calculations synchronized");
    }

    /**
     * Checks if the sun is currently above the horizon illuminating the world surface.
     */
    public static boolean isSunPresent(World world) {
        if (world == null) return false;
        if (world.provider.hasNoSky) return false;

        float celestialAngle = world.getCelestialAngle(1.0F);
        // Daylight occurs when sun angle is between horizon transitions
        boolean dayTime = celestialAngle > 0.75F || celestialAngle < 0.25F;
        return dayTime && !world.isRaining() && !world.isThundering();
    }

    /**
     * Checks if a player is exposed to daylight taking celestial time, sky clearance, and weather into account.
     */
    public static boolean isSunExposed(EntityPlayer player) {
        if (player == null || player.worldObj == null) return false;
        World world = player.worldObj;
        if (!isSunPresent(world)) return false;

        int x = (int) Math.floor(player.posX);
        int y = (int) Math.floor(player.posY + player.getEyeHeight());
        int z = (int) Math.floor(player.posZ);

        return world.canBlockSeeTheSky(x, y, z);
    }
}
