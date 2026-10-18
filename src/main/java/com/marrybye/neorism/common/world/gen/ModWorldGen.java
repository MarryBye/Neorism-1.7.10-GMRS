package com.marrybye.neorism.common.world.gen;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.common.core.ModBiomes;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

/**
 * Master World Generator for Neorism structures and landmarks.
 * Canonical port of TeamLapen's ModStructures RandomSpreadStructurePlacement and SingleOccurrenceStructurePlacement.
 */
public class ModWorldGen implements IWorldGenerator {

    // Canonical salts and grid spacing/separation from ModStructures.createStructureSets
    private static final int SALT_HUNTER_CAMP = 1724616580;
    private static final int SALT_VAMPIRE_ALTAR = 573190874;
    private static final int SALT_CRYPT = 643510199;
    private static final int SALT_DRACULA_CASTLE = 1937195837;

    private final WorldGenVampireCrypt cryptGen = new WorldGenVampireCrypt();
    private final WorldGenHunterCamp campGen = new WorldGenHunterCamp();
    private final WorldGenVampireAltarRuins altarGen = new WorldGenVampireAltarRuins();
    private final WorldGenDraculaCastle castleGen = new WorldGenDraculaCastle();

    // Tracks generated Dracula Castle macro-regions per world seed so a Vampire Forest never gets multiple castles
    private static final Set<String> generatedCastleRegions = new HashSet<String>();

    public static void init() {
        GameRegistry.registerWorldGenerator(new ModWorldGen(), 10);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.dimensionId != 0) {
            return;
        }

        long seed = world.getSeed();
        int centerX = chunkX * 16 + 8;
        int centerZ = chunkZ * 16 + 8;
        BiomeGenBase biome = world.getBiomeGenForCoords(centerX, centerZ);
        if (biome == null) {
            return;
        }

        boolean isVampireForest = isVampireBiome(biome);

        // 1. Dracula Castle: Canonical SingleOccurrence / landmark placement in Vampire Forest (1 per 64x64 chunk macro-region, min 48 chunk separation)
        if (Config.generateDraculaCastle && isVampireForest) {
            if (isStructureChunk(seed, chunkX, chunkZ, 64, 48, SALT_DRACULA_CASTLE, false)) {
                int macroX = Math.floorDiv(chunkX, 64);
                int macroZ = Math.floorDiv(chunkZ, 64);
                String regionKey = seed + ":" + macroX + ":" + macroZ;
                if (!generatedCastleRegions.contains(regionKey)) {
                    if (castleGen.generate(world, random, centerX, 0, centerZ)) {
                        generatedCastleRegions.add(regionKey);
                        ModLogger.spawn("Generated Dracula Castle at chunk (%d, %d) [block %d, %d]", chunkX, chunkZ, centerX, centerZ);
                    }
                }
            }
        }

        // 2. Vampire Crypt: Canonical CRYPT_SET (spacing = 32, separation = 8, salt = 643510199), ONLY in Vampire Forest
        if (Config.generateVampireCrypt && isVampireForest) {
            if (isStructureChunk(seed, chunkX, chunkZ, 32, 8, SALT_CRYPT, false)) {
                int cryptY = 20 + random.nextInt(20);
                if (cryptGen.generate(world, random, centerX, cryptY, centerZ)) {
                    ModLogger.spawn("Generated Vampire Crypt at (%d, %d, %d)", centerX, cryptY, centerZ);
                }
            }
        }

        // 3. Vampire Altar Ruins: Canonical VAMPIRE_ALTAR_SET (spacing = 32, separation = 15, salt = 573190874)
        // Canonical ModBiomeTagsProvider.VAMPIRE_ALTAR: Vampire Forest, Spooky, Swamp, Wasteland, Mesa/Plateau, Rare
        if (Config.generateAltarRuins && isValidAltarBiome(biome, isVampireForest)) {
            if (isStructureChunk(seed, chunkX, chunkZ, 32, 15, SALT_VAMPIRE_ALTAR, false)) {
                if (altarGen.generate(world, random, centerX, 0, centerZ)) {
                    ModLogger.spawn("Generated Vampire Altar Ruins at (%d, %d)", centerX, centerZ);
                }
            }
        }

        // 4. Hunter Camp: Canonical HUNTER_CAMP_SET (spacing = 12, separation = 5, salt = 1724616580)
        // Canonical ModBiomeTagsProvider.HUNTER_TENT & !IS_FACTION_BIOME
        if (Config.generateHunterCamp && !isVampireForest && isValidHunterCampBiome(biome)) {
            if (isStructureChunk(seed, chunkX, chunkZ, 12, 5, SALT_HUNTER_CAMP, false)) {
                // Exclude chunks near villages (canonical ExclusionZone(villageSet, 2))
                if (world.villageCollectionObj == null || world.villageCollectionObj.findNearestVillage(centerX, 64, centerZ, 48) == null) {
                    if (campGen.generate(world, random, centerX, 0, centerZ)) {
                        ModLogger.spawn("Generated Hunter Camp at (%d, %d)", centerX, centerZ);
                    }
                }
            }
        }
    }

    /**
     * Canonical port of Minecraft's RandomSpreadStructurePlacement.getPotentialStructureChunk.
     * Guarantees at most 1 structure per (spacing x spacing) chunk grid and at least (separation) chunks between structures.
     */
    private static boolean isStructureChunk(long worldSeed, int chunkX, int chunkZ, int spacing, int separation, int salt, boolean triangular) {
        int regionX = Math.floorDiv(chunkX, spacing);
        int regionZ = Math.floorDiv(chunkZ, spacing);
        long regionSeed = (long) regionX * 341873128712L + (long) regionZ * 132897987541L + worldSeed + (long) salt;
        Random rand = new Random(regionSeed);
        int bound = Math.max(1, spacing - separation);
        int offsetX;
        int offsetZ;
        if (triangular) {
            offsetX = (rand.nextInt(bound) + rand.nextInt(bound)) / 2;
            offsetZ = (rand.nextInt(bound) + rand.nextInt(bound)) / 2;
        } else {
            offsetX = rand.nextInt(bound);
            offsetZ = rand.nextInt(bound);
        }
        return chunkX == regionX * spacing + offsetX && chunkZ == regionZ * spacing + offsetZ;
    }

    public static boolean isVampireBiome(BiomeGenBase biome) {
        if (biome == null) return false;
        return biome == ModBiomes.vampireForest || (biome.biomeName != null && biome.biomeName.toLowerCase().contains("vampire"));
    }

    private static boolean isValidAltarBiome(BiomeGenBase biome, boolean isVampireForest) {
        if (isVampireForest) return true;
        if (BiomeDictionary.isBiomeOfType(biome, Type.OCEAN) || BiomeDictionary.isBiomeOfType(biome, Type.RIVER) || BiomeDictionary.isBiomeOfType(biome, Type.BEACH)) {
            return false;
        }
        return BiomeDictionary.isBiomeOfType(biome, Type.SPOOKY)
            || BiomeDictionary.isBiomeOfType(biome, Type.SWAMP)
            || BiomeDictionary.isBiomeOfType(biome, Type.WASTELAND)
            || BiomeDictionary.isBiomeOfType(biome, Type.MESA)
            || BiomeDictionary.isBiomeOfType(biome, Type.MAGICAL);
    }

    private static boolean isValidHunterCampBiome(BiomeGenBase biome) {
        if (BiomeDictionary.isBiomeOfType(biome, Type.OCEAN)
            || BiomeDictionary.isBiomeOfType(biome, Type.RIVER)
            || BiomeDictionary.isBiomeOfType(biome, Type.BEACH)
            || BiomeDictionary.isBiomeOfType(biome, Type.SWAMP)
            || BiomeDictionary.isBiomeOfType(biome, Type.MUSHROOM)
            || BiomeDictionary.isBiomeOfType(biome, Type.SPOOKY)) {
            return false;
        }
        return BiomeDictionary.isBiomeOfType(biome, Type.PLAINS)
            || BiomeDictionary.isBiomeOfType(biome, Type.FOREST)
            || BiomeDictionary.isBiomeOfType(biome, Type.CONIFEROUS)
            || BiomeDictionary.isBiomeOfType(biome, Type.SANDY)
            || BiomeDictionary.isBiomeOfType(biome, Type.MESA)
            || BiomeDictionary.isBiomeOfType(biome, Type.SAVANNA)
            || BiomeDictionary.isBiomeOfType(biome, Type.SNOWY);
    }
}
