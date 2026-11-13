package com.marrybye.neorism.common.world.biomes;

import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.entity.converted.EntityConvertedCow;
import com.marrybye.neorism.common.entity.converted.EntityConvertedHorse;
import com.marrybye.neorism.common.entity.converted.EntityConvertedSheep;
import com.marrybye.neorism.common.entity.vampire.EntityBloodBat;
import com.marrybye.neorism.common.entity.vampire.EntityVampire;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaron;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaroness;
import com.marrybye.neorism.common.world.gen.WorldGenDarkSpruceTree;
import com.marrybye.neorism.common.world.gen.WorldGenGiantRoot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;

/**
 * Vampire Forest Biome.
 * Canonical port of TeamLapen's VampirismBiomes.createVampireForest.
 * Atmospheric cursed woods shielding vampires from sun damage.
 */
public class BiomeGenVampireForest extends BiomeGenBase {

    private final WorldGenDarkSpruceTree darkSpruceGen = new WorldGenDarkSpruceTree(false);
    private final WorldGenDarkSpruceTree cursedSpruceGen = new WorldGenDarkSpruceTree(false, ModBlocks.cursedSpruceLog);
    private final WorldGenMinable cursedEarthGen = new WorldGenMinable(ModBlocks.cursedEarth, 33);
    private final WorldGenMinable darkStoneGen = new WorldGenMinable(ModBlocks.darkStone, 16);
    private final WorldGenGiantRoot giantRootGen = new WorldGenGiantRoot();

    public BiomeGenVampireForest(int id) {
        super(id);
        setBiomeName("Vampire Forest");
        topBlock = ModBlocks.cursedGrass;
        fillerBlock = ModBlocks.cursedEarth;
        setTemperatureRainfall(0.3F, 0.6F);
        setHeight(new Height(0.2F, 0.2F));
        setDisableRain();
        waterColorMultiplier = 0x670717;

        this.theBiomeDecorator.treesPerChunk = 15;
        this.theBiomeDecorator.flowersPerChunk = 0;
        this.theBiomeDecorator.grassPerChunk = 30;
        this.theBiomeDecorator.deadBushPerChunk = 2;
        this.theBiomeDecorator.generateLakes = true;

        this.flowers.clear();

        // Canonical VampirismBiomes.createVampireForest + NO_DEFAULT_MONSTERS:
        // Clear default vanilla monsters/creatures so only Vampire Forest entities spawn here
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();

        this.spawnableMonsterList.add(new SpawnListEntry(EntityVampire.class, 80, 1, 3));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityVampireBaron.class, 14, 1, 1));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityVampireBaroness.class, 13, 1, 1));
        this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityBloodBat.class, 60, 2, 4));

        // Canonical converted fauna spawns (total creature weight 80 matching dummy_creature)
        this.spawnableCreatureList.add(new SpawnListEntry(EntityConvertedCow.class, 35, 3, 6));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityConvertedSheep.class, 35, 3, 6));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityConvertedHorse.class, 10, 1, 3));
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random rand) {
        return rand.nextFloat() < 0.3F ? cursedSpruceGen : darkSpruceGen;
    }

    @Override
    public void decorate(World world, Random rand, int chunkX, int chunkZ) {
        super.decorate(world, rand, chunkX, chunkZ);

        // Explicit canonical Vampire Orchid generation (5 per chunk)
        for (int i = 0; i < 5; ++i) {
            int rx = chunkX + rand.nextInt(16) + 8;
            int rz = chunkZ + rand.nextInt(16) + 8;
            int ry = world.getHeightValue(rx, rz);
            if (world.isAirBlock(rx, ry, rz) && ModBlocks.vampireOrchid.canBlockStay(world, rx, ry, rz)) {
                world.setBlock(rx, ry, rz, ModBlocks.vampireOrchid, 0, 2);
            }
        }

        // Decorate ground with Cursed Roots
        for (int i = 0; i < 6; ++i) {
            int rx = chunkX + rand.nextInt(16) + 8;
            int rz = chunkZ + rand.nextInt(16) + 8;
            int ry = world.getHeightValue(rx, rz);
            if (world.isAirBlock(rx, ry, rz) && ModBlocks.cursedRoots.canBlockStay(world, rx, ry, rz)) {
                world.setBlock(rx, ry, rz, ModBlocks.cursedRoots, 0, 2);
            }
        }

        // Underground dark stone and cursed earth veins
        for (int i = 0; i < 7; ++i) {
            int ox = chunkX + rand.nextInt(16);
            int oy = rand.nextInt(64);
            int oz = chunkZ + rand.nextInt(16);
            cursedEarthGen.generate(world, rand, ox, oy, oz);
            darkStoneGen.generate(world, rand, ox, oy, oz);
        }

        // Canonical Giant Dark Stone Root (rarity_filter chance = 3); keep origin inside [chunkX+12..chunkX+19] so +-10 drift/radius stays in [chunkX..chunkX+31]
        if (rand.nextInt(3) == 0) {
            int gx = chunkX + 12 + rand.nextInt(8);
            int gz = chunkZ + 12 + rand.nextInt(8);
            int gy = world.getTopSolidOrLiquidBlock(gx, gz);
            giantRootGen.generate(world, rand, gx, gy, gz);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBiomeGrassColor(int x, int y, int z) {
        return 0x242424;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBiomeFoliageColor(int x, int y, int z) {
        return 0x1f1f1f;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float temp) {
        return 0x131313;
    }
}
