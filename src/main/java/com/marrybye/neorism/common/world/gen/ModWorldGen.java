package com.marrybye.neorism.common.world.gen;

import java.util.Random;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.common.core.ModBiomes;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Master World Generator for Neorism structures and landmarks.
 * Dispatches procedural generation of Crypts, Hunter Camps, Altar Ruins, and Dracula Castle.
 */
public class ModWorldGen implements IWorldGenerator {

    private final WorldGenVampireCrypt cryptGen = new WorldGenVampireCrypt();
    private final WorldGenHunterCamp campGen = new WorldGenHunterCamp();
    private final WorldGenVampireAltarRuins altarGen = new WorldGenVampireAltarRuins();
    private final WorldGenDraculaCastle castleGen = new WorldGenDraculaCastle();

    public static void init() {
        GameRegistry.registerWorldGenerator(new ModWorldGen(), 10);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.dimensionId == 0) {
            int blockX = chunkX * 16 + random.nextInt(16);
            int blockZ = chunkZ * 16 + random.nextInt(16);
            BiomeGenBase biome = world.getBiomeGenForCoords(blockX, blockZ);
            boolean isVampireForest = biome == ModBiomes.vampireForest || (biome != null && biome.biomeName != null && biome.biomeName.toLowerCase().contains("vampire"));

            // 1. Dracula Castle in Vampire Forest (1 in 80 chunks)
            if (Config.generateDraculaCastle && isVampireForest) {
                if (random.nextInt(80) == 0) {
                    castleGen.generate(world, random, blockX, 0, blockZ);
                }
            }

            // 2. Underground Vampire Crypt (1 in 15 chunks in vampire forest, 1 in 50 in normal world)
            if (Config.generateVampireCrypt) {
                int cryptChance = isVampireForest ? 15 : 50;
                if (random.nextInt(cryptChance) == 0) {
                    int cryptY = 20 + random.nextInt(25);
                    cryptGen.generate(world, random, blockX, cryptY, blockZ);
                }
            }

            // 3. Hunter Camps on Overworld surface (1 in 45 chunks, avoided in deep vampire forest)
            if (Config.generateHunterCamp && !isVampireForest) {
                if (random.nextInt(45) == 0) {
                    campGen.generate(world, random, blockX, 0, blockZ);
                }
            }

            // 4. Ancient Vampire Altar Ruins (1 in 20 chunks in vampire forest, 1 in 60 in other forests)
            if (Config.generateAltarRuins) {
                int altarChance = isVampireForest ? 20 : 60;
                if (random.nextInt(altarChance) == 0) {
                    altarGen.generate(world, random, blockX, 0, blockZ);
                }
            }
        }
    }
}
