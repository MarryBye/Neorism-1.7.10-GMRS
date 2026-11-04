package com.marrybye.neorism.common.core;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.world.biomes.BiomeGenVampireForest;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;

/**
 * Biomes registry for Neorism.
 * Registers Vampire Forest and adds it to the Overworld biome generation.
 */
public class ModBiomes {

    public static BiomeGenVampireForest vampireForest;
    public static com.marrybye.neorism.common.world.biomes.BiomeGenVelmorra velmorra;

    public static void init() {
        int biomeId = Config.biomeVampireForestId;
        if (BiomeGenBase.getBiome(biomeId) != null) {
            ModLogger.warn("Biome ID {} already taken! Finding next available slot...", biomeId);
            for (int i = 40; i < 256; i++) {
                if (BiomeGenBase.getBiome(i) == null) {
                    biomeId = i;
                    break;
                }
            }
        }

        vampireForest = new BiomeGenVampireForest(biomeId);

        // Register to BiomeDictionary (canonical: cold spooky dense coniferous forest)
        BiomeDictionary.registerBiomeType(vampireForest, Type.SPOOKY, Type.FOREST, Type.MAGICAL, Type.DENSE, Type.COLD, Type.CONIFEROUS);

        if (Config.generateVampireForest) {
            // Register to Overworld BiomeManager with canonical weight 4 (rare taiga replacement)
            BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(vampireForest, 4));
            ModLogger.info("Initialized Vampire Forest biome with ID {} and Overworld weight 4", biomeId);
        } else {
            ModLogger.info("Initialized Vampire Forest biome with ID {} (Overworld generation disabled)", biomeId);
        }

        // Initialize dedicated Velmorra Dimension Biome
        int velmorraBiomeId = 116;
        if (BiomeGenBase.getBiome(velmorraBiomeId) != null) {
            for (int i = 40; i < 256; i++) {
                if (BiomeGenBase.getBiome(i) == null && i != biomeId) {
                    velmorraBiomeId = i;
                    break;
                }
            }
        }
        velmorra = new com.marrybye.neorism.common.world.biomes.BiomeGenVelmorra(velmorraBiomeId);
        BiomeDictionary.registerBiomeType(velmorra, Type.SPOOKY, Type.MAGICAL, Type.WASTELAND, Type.END);
        ModLogger.info("Initialized Velmorra biome with ID {}", velmorraBiomeId);
    }
}
