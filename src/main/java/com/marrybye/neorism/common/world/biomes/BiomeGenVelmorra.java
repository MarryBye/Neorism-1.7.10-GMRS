package com.marrybye.neorism.common.world.biomes;

import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.entity.vampire.EntityAdvancedVampire;
import com.marrybye.neorism.common.entity.vampire.EntityBloodBat;
import com.marrybye.neorism.common.entity.vampire.EntityVampire;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

/**
 * Velmorra Dimension Biome.
 * Canonical port of TeamLapen's velmorra biome (vampirism:velmorra).
 * Features perpetual night, dark ash fog, zero rain, and exclusive high-tier vampire entities.
 */
public class BiomeGenVelmorra extends BiomeGenBase {

    public BiomeGenVelmorra(int id) {
        super(id);
        setBiomeName("Velmorra");
        topBlock = ModBlocks.cursedGrass != null ? ModBlocks.cursedGrass : ModBlocks.darkStone;
        fillerBlock = ModBlocks.cursedEarth != null ? ModBlocks.cursedEarth : ModBlocks.darkStone;
        setTemperatureRainfall(0.3F, 0.0F);
        setDisableRain();
        waterColorMultiplier = 0x670717;

        this.theBiomeDecorator.treesPerChunk = 0;
        this.theBiomeDecorator.flowersPerChunk = 0;
        this.theBiomeDecorator.grassPerChunk = 0;
        this.theBiomeDecorator.deadBushPerChunk = 0;
        this.theBiomeDecorator.generateLakes = false;

        this.flowers.clear();

        // Canonical Velmorra spawns: ONLY Vampires, Advanced Vampires, and Blood Bats
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();

        this.spawnableMonsterList.add(new SpawnListEntry(EntityVampire.class, 60, 1, 2));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityAdvancedVampire.class, 27, 1, 2));
        this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityBloodBat.class, 60, 2, 4));
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random rand) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBiomeGrassColor(int x, int y, int z) {
        return 0x242424;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBiomeFoliageColor(int x, int y, int z) {
        return 0x101010;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float temp) {
        return 0x131313;
    }
}
