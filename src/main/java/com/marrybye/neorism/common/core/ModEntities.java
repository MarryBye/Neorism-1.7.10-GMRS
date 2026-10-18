package com.marrybye.neorism.common.core;

import java.util.ArrayList;
import java.util.List;

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
import com.marrybye.neorism.common.entity.vampire.EntityAdvancedVampire;
import com.marrybye.neorism.common.entity.vampire.EntityBloodBat;
import com.marrybye.neorism.common.entity.vampire.EntityDracula;
import com.marrybye.neorism.common.entity.vampire.EntityVampire;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaron;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaroness;
import com.marrybye.neorism.common.entity.vampire.EntityVampireMother;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * Entity registry for Neorism entities, projectiles, vampires, hunters, and converted mobs.
 * Registers both Forge mod entities and global vanilla mappings for spawn eggs, /summon, and world generation.
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
        registerMob(com.marrybye.neorism.common.entity.minion.EntityVampireMinion.class, "vampire_minion", 0x330000, 0x660000);

        // Hunters
        registerMob(EntityHunter.class, "hunter", 0x5a452d, 0x2e6b9e);
        registerMob(EntityAdvancedHunter.class, "advanced_hunter", 0x3d2e1e, 0x1e88e5);
        registerMob(EntityHunterTrainer.class, "hunter_trainer", 0x8d6e63, 0xffb300);
        registerMob(EntityTaskmaster.class, "hunter_taskmaster", 0x546e7a, 0x00acc1);
        registerMob(com.marrybye.neorism.common.entity.minion.EntityHunterMinion.class, "hunter_minion", 0x4a3c28, 0x1976d2);

        // Converted Mobs
        registerMob(EntityConvertedCow.class, "converted_cow", 0x443625, 0x8b0000);
        registerMob(EntityConvertedSheep.class, "converted_sheep", 0xdfdfdf, 0x8b0000);
        registerMob(EntityConvertedHorse.class, "converted_horse", 0x6e431f, 0x8b0000);
        registerMob(EntityConvertedVillager.class, "converted_villager", 0x563c24, 0x8b0000);

        // Natural Spawns in Overworld (Canonical ModBiomeTags.NoSpawn: exclude Vampire Forest, Mushroom Island, Nether, End)
        List<BiomeGenBase> overworldBiomes = new ArrayList<BiomeGenBase>();
        for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
            if (biome != null
                && biome != ModBiomes.vampireForest
                && biome.biomeID != BiomeGenBase.hell.biomeID
                && biome.biomeID != BiomeGenBase.sky.biomeID
                && biome.biomeID != BiomeGenBase.mushroomIsland.biomeID
                && biome.biomeID != BiomeGenBase.mushroomIslandShore.biomeID) {
                overworldBiomes.add(biome);
            }
        }
        BiomeGenBase[] biomes = overworldBiomes.toArray(new BiomeGenBase[0]);

        EntityRegistry.addSpawn(EntityVampire.class, 25, 1, 3, EnumCreatureType.monster, biomes);
        EntityRegistry.addSpawn(EntityAdvancedVampire.class, 8, 1, 2, EnumCreatureType.monster, biomes);
        EntityRegistry.addSpawn(EntityBloodBat.class, 15, 1, 3, EnumCreatureType.ambient, biomes);
        EntityRegistry.addSpawn(EntityHunter.class, 10, 1, 3, EnumCreatureType.creature, biomes);
        EntityRegistry.addSpawn(EntityAdvancedHunter.class, 3, 1, 2, EnumCreatureType.creature, biomes);

        ModLogger.debug("Successfully registered all Neorism entities, spawn eggs, and natural world spawns (total: %d entities)", nextEntityId);
    }

    @SuppressWarnings("unchecked")
    private static void registerMob(Class<? extends Entity> entityClass, String name, int primaryEgg, int secondaryEgg) {
        int modId = ++nextEntityId;
        EntityRegistry.registerModEntity(entityClass, name, modId, Neorism.instance, 80, 3, true);

        try {
            int globalId = EntityRegistry.findGlobalUniqueEntityId();
            EntityRegistry.registerGlobalEntityID(entityClass, "neorism." + name, globalId, primaryEgg, secondaryEgg);
        } catch (Throwable t) {
            ModLogger.warn("Failed to register global entity ID for %s: %s", name, t.getMessage());
        }

        // Map all vanilla /summon strings and aliases
        EntityList.stringToClassMapping.put(name, entityClass);
        EntityList.stringToClassMapping.put(name.toLowerCase(), entityClass);
        EntityList.stringToClassMapping.put("neorism." + name, entityClass);
        EntityList.stringToClassMapping.put("neorism:" + name, entityClass);

        String capitalized = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        EntityList.stringToClassMapping.put(capitalized, entityClass);
        EntityList.classToStringMapping.put(entityClass, "neorism." + name);
    }
}
