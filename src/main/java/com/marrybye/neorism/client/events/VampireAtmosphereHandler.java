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
        boolean inVampireFog = biome == ModBiomes.vampireForest
            || (biome != null && biome.biomeName != null && biome.biomeName.toLowerCase().contains("vampire"))
            || com.marrybye.neorism.common.tileentity.TileEntityTotem.isEntityProtectedByVampireVillage(entity);
        if (inVampireFog) {
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
        boolean inVampireFog = biome == ModBiomes.vampireForest
            || (biome != null && biome.biomeName != null && biome.biomeName.toLowerCase().contains("vampire"))
            || com.marrybye.neorism.common.tileentity.TileEntityTotem.isEntityProtectedByVampireVillage(entity);
        if (inVampireFog) {
            event.density = 0.035F;
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onFOVUpdate(net.minecraftforge.client.event.FOVUpdateEvent event) {
        if (event.entity == null) return;
        net.minecraft.entity.ai.attributes.IAttributeInstance speedAttr = event.entity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.movementSpeed);
        if (speedAttr != null) {
            // Prevent extreme 2x sniper zoom during Altar of Infusion ritual (-1.0 movement slowdown)
            if (speedAttr.getModifier(com.marrybye.neorism.common.tileentity.TileEntityAltarInfusion.ID_MOVEMENT_SLOWDOWN) != null) {
                event.newfov = 1.0F;
                return;
            }
            // Canonical FOV normalization for Vampire movement speed boost
            net.minecraft.entity.ai.attributes.AttributeModifier vampSpeed = speedAttr.getModifier(com.marrybye.neorism.common.vampire.LevelAttributeModifier.SPEED_MOD_UUID);
            if (vampSpeed != null && vampSpeed.getAmount() > 0) {
                double mult = 1.0D + vampSpeed.getAmount() * 0.65D;
                event.newfov = (float) (event.fov / ((mult + 1.0D) / 2.0D));
            }
        }
    }
}
