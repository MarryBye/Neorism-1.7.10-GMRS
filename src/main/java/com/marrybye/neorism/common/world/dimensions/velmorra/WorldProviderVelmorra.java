package com.marrybye.neorism.common.world.dimensions.velmorra;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.common.core.ModBiomes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;

/**
 * Dimension provider for Velmorra, Dracula's unholy pocket realm.
 * Canonical port of TeamLapen's velmorra dimension.
 * Features perpetual night, dark fog, no sun damage, and Dracula's gothic castle arena.
 */
public class WorldProviderVelmorra extends WorldProvider {

    public static void register() {
        DimensionManager.registerProviderType(Config.dimensionVelmorraId, WorldProviderVelmorra.class, false);
        DimensionManager.registerDimension(Config.dimensionVelmorraId, Config.dimensionVelmorraId);
    }

    @Override
    public void registerWorldChunkManager() {
        this.worldChunkMgr = new WorldChunkManagerHell(ModBiomes.vampireForest, 0.0F);
        this.dimensionId = Config.dimensionVelmorraId;
        this.hasNoSky = false;
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        return new ChunkProviderVelmorra(this.worldObj, this.worldObj.getSeed());
    }

    @Override
    public String getDimensionName() {
        return "Velmorra";
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        // Perpetual Gothic Midnight
        return 0.5F;
    }

    @Override
    public float getSunBrightness(float par1) {
        return 0.0F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3 getFogColor(float celestialAngle, float partialTicks) {
        // Dark ash color matching canonical #171717
        return Vec3.createVectorHelper(0.0902D, 0.0902D, 0.0902D);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
        // Deep obsidian-black sky #131313
        return Vec3.createVectorHelper(0.0745D, 0.0745D, 0.0745D);
    }

    @Override
    public ChunkCoordinates getEntrancePortalLocation() {
        return new ChunkCoordinates(0, 65, -30);
    }

    @Override
    public ChunkCoordinates getSpawnPoint() {
        return new ChunkCoordinates(0, 65, -30);
    }
}
