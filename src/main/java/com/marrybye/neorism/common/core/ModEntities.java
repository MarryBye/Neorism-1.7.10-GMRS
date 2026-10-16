package com.marrybye.neorism.common.core;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.entity.EntityHolyWaterBottle;
import com.marrybye.neorism.common.entity.converted.EntityConvertedCow;
import com.marrybye.neorism.common.entity.converted.EntityConvertedHorse;
import com.marrybye.neorism.common.entity.converted.EntityConvertedSheep;
import com.marrybye.neorism.common.entity.converted.EntityConvertedVillager;
import com.marrybye.neorism.common.entity.hunter.EntityAdvancedHunter;
import com.marrybye.neorism.common.entity.hunter.EntityHunter;
import com.marrybye.neorism.common.entity.hunter.EntityHunterTrainer;
import com.marrybye.neorism.common.entity.hunter.EntityTaskmaster;
import com.marrybye.neorism.common.entity.projectile.EntityDarkBloodProjectile;
import com.marrybye.neorism.common.entity.projectile.EntityQuarrel;
import com.marrybye.neorism.common.entity.vampire.*;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

/**
 * Entity registry for Neorism entities, projectiles, vampires, hunters, and converted mobs.
 */
public class ModEntities {

    private static int nextEntityId = 0;

    public static void init() {
        // Projectiles
        EntityRegistry.registerModEntity(EntityHolyWaterBottle.class, "holy_water_bottle", ++nextEntityId, Neorism.instance, 64, 10, true);
        EntityRegistry.registerModEntity(EntityDarkBloodProjectile.class, "dark_blood_projectile", ++nextEntityId, Neorism.instance, 64, 10, true);
        EntityRegistry.registerModEntity(EntityQuarrel.class, "quarrel", ++nextEntityId, Neorism.instance, 64, 10, true);

        // Vampires
        registerMob(EntityVampire.class, "vampire", 0x3d0000, 0x8b0000);
        registerMob(EntityAdvancedVampire.class, "advanced_vampire", 0x220000, 0x990000);
        registerMob(EntityVampireBaron.class, "vampire_baron", 0x110000, 0xaa0000);
        registerMob(EntityVampireBaroness.class, "vampire_baroness", 0x110000, 0xbb0022);
        registerMob(EntityBloodBat.class, "blood_bat", 0x200000, 0xdd0000);
        registerMob(EntityDracula.class, "dracula", 0x050000, 0xff0000);
        registerMob(EntityVampireMother.class, "vampire_mother", 0x0a0005, 0xee0055);

        // Hunters
        registerMob(EntityHunter.class, "hunter", 0x5a452d, 0x2e6b9e);
        registerMob(EntityAdvancedHunter.class, "advanced_hunter", 0x3d2e1e, 0x1e88e5);
        registerMob(EntityHunterTrainer.class, "hunter_trainer", 0x8d6e63, 0xffb300);
        registerMob(EntityTaskmaster.class, "hunter_taskmaster", 0x546e7a, 0x00acc1);

        // Converted Mobs
        registerMob(EntityConvertedCow.class, "converted_cow", 0x443625, 0x8b0000);
        registerMob(EntityConvertedSheep.class, "converted_sheep", 0xdfdfdf, 0x8b0000);
        registerMob(EntityConvertedHorse.class, "converted_horse", 0x6e431f, 0x8b0000);
        registerMob(EntityConvertedVillager.class, "converted_villager", 0x563c24, 0x8b0000);

        ModLogger.debug("Successfully registered all Neorism entities and spawn eggs (total: %d entities)", nextEntityId);
    }

    private static void registerMob(Class<? extends Entity> entityClass, String name, int primaryEgg, int secondaryEgg) {
        EntityRegistry.registerModEntity(entityClass, name, ++nextEntityId, Neorism.instance, 80, 3, true);
        try {
            int globalId = EntityRegistry.findGlobalUniqueEntityId();
            if (globalId > 0) {
                EntityRegistry.registerGlobalEntityID(entityClass, name, globalId);
                EntityList.entityEggs.put(Integer.valueOf(globalId), new EntityList.EntityEggInfo(globalId, primaryEgg, secondaryEgg));
            }
        } catch (Throwable ignored) {
        }
    }
}
