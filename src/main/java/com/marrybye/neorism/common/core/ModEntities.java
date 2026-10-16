package com.marrybye.neorism.common.core;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.entity.EntityHolyWaterBottle;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.registry.EntityRegistry;

/**
 * Entity registry for Neorism entities.
 */
public class ModEntities {

    private static int nextEntityId = 0;

    public static void init() {
        EntityRegistry.registerModEntity(EntityHolyWaterBottle.class, "holy_water_bottle", ++nextEntityId, Neorism.instance, 64, 10, true);
        EntityRegistry.registerModEntity(com.marrybye.neorism.common.entity.projectile.EntityDarkBloodProjectile.class, "dark_blood_projectile", ++nextEntityId, Neorism.instance, 64, 10, true);
        EntityRegistry.registerModEntity(com.marrybye.neorism.common.entity.projectile.EntityQuarrel.class, "quarrel", ++nextEntityId, Neorism.instance, 64, 10, true);
        ModLogger.debug("Registered Neorism core entities (holy_water_bottle, dark_blood_projectile, quarrel)");
    }
}
