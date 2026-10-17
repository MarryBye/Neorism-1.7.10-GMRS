package com.marrybye.neorism.client.events;

import com.marrybye.neorism.common.core.ModBiomes;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;

/**
 * Client atmosphere and ambient renderer for Vampire Forest and gothic structures.
 * Canonical port of TeamLapen's atmospheric fog and environment attributes.
 * Seamlessly compatible with Angelica, Sodium, and GTNH rendering pipelines.
 */
@SideOnly(Side.CLIENT)
public class VampireAtmosphereHandler {

    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        EntityLivingBase entity = event.entity;
        if (entity == null || entity.worldObj == null) {
            return;
        }

        BiomeGenBase biome = entity.worldObj.getBiomeGenForCoords((int) entity.posX, (int) entity.posZ);
        if (biome == ModBiomes.vampireForest || (biome != null && biome.biomeName != null && biome.biomeName.toLowerCase().contains("vampire"))) {
            // Dark purple-black vampire fog (0x1b002c)
            float targetR = 0.106F;
            float targetG = 0.000F;
            float targetB = 0.172F;

            event.red = event.red * 0.3F + targetR * 0.7F;
            event.green = event.green * 0.2F + targetG * 0.8F;
            event.blue = event.blue * 0.3F + targetB * 0.7F;
        }
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        EntityLivingBase entity = event.entity;
        if (entity == null || entity.worldObj == null) {
            return;
        }

        if (entity.isInWater()) {
            return;
        }

        BiomeGenBase biome = entity.worldObj.getBiomeGenForCoords((int) entity.posX, (int) entity.posZ);
        if (biome == ModBiomes.vampireForest || (biome != null && biome.biomeName != null && biome.biomeName.toLowerCase().contains("vampire"))) {
            event.density = 0.035F;
            event.setCanceled(true);
        }
    }
}
