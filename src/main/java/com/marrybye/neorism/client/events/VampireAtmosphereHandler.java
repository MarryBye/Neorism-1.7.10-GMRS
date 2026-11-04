package com.marrybye.neorism.client.events;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.core.ModBiomes;
import com.marrybye.neorism.common.util.Helper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;

/**
 * Client atmosphere and ambient renderer for Vampire Forest, fog diffusers, and gothic structures.
 * Canonical port of TeamLapen's atmospheric fog and environment attributes.
 * Seamlessly compatible with Angelica, Sodium, and GTNH rendering pipelines.
 */
@SideOnly(Side.CLIENT)
public class VampireAtmosphereHandler {

    public static final int VAMPIRE_BIOME_FADE_TICKS = 60; // 3 seconds fade
    private int vampireBiomeTicks = 0;
    private float vampireBiomeFogDistanceMultiplier = 1.0F;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) {
            vampireBiomeTicks = 0;
            return;
        }

        EntityPlayer player = mc.thePlayer;
        BiomeGenBase biome = mc.theWorld.getBiomeGenForCoords((int) player.posX, (int) player.posZ);
        boolean inVampireFog = (biome == ModBiomes.vampireForest)
            || (biome != null && biome.biomeName != null && biome.biomeName.toLowerCase().contains("vampire"))
            || com.marrybye.neorism.common.tileentity.TileEntityFogDiffuser.isEntityProtected(player)
            || com.marrybye.neorism.common.tileentity.TileEntityTotem.isEntityProtectedByVampireVillage(player);

        if (inVampireFog) {
            if (vampireBiomeTicks < VAMPIRE_BIOME_FADE_TICKS) {
                vampireBiomeTicks++;
            }
        } else {
            if (vampireBiomeTicks > 0) {
                vampireBiomeTicks--;
            }
        }

        // Canonical 2x distance multiplier for vampires (vision pierce)
        if (Helper.isVampire(player) && Helper.getLevel(player) > 0) {
            vampireBiomeFogDistanceMultiplier = 2.0F;
        } else {
            vampireBiomeFogDistanceMultiplier = 1.0F;
        }
    }

    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        if (vampireBiomeTicks <= 0) return;

        float fade = (float) vampireBiomeTicks / (float) VAMPIRE_BIOME_FADE_TICKS;

        // Canonical TeamLapen fog_color: #171717 (dark ash gothic grey)
        float targetR = 0.0902F;
        float targetG = 0.0902F;
        float targetB = 0.0902F;

        event.red = event.red * (1.0F - fade) + targetR * fade;
        event.green = event.green * (1.0F - fade) + targetG * fade;
        event.blue = event.blue * (1.0F - fade) + targetB * fade;
    }

    @SubscribeEvent
    public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (event.fogMode < 0) return; // Do not affect skybox/sun
        if (vampireBiomeTicks <= 0) return;

        float fade = (float) vampireBiomeTicks / (float) VAMPIRE_BIOME_FADE_TICKS;

        // Canonical distance formula from TeamLapen RenderHandler.onRenderFog:
        // Full fade: mortals near=4.0, far=33.3; vampires near=8.0, far=66.7
        float mult = vampireBiomeFogDistanceMultiplier;
        float targetNear = Math.min(event.farPlaneDistance * 0.75F, 4.0F * mult);
        float targetFar = Math.min(event.farPlaneDistance, 33.3F * mult);

        float currentNear = event.farPlaneDistance * 0.75F * (1.0F - fade) + targetNear * fade;
        float currentFar = event.farPlaneDistance * (1.0F - fade) + targetFar * fade;

        currentNear = Math.max(0.0F, currentNear);
        currentFar = Math.max(currentNear + 1.0F, currentFar);

        GL11.glFogf(GL11.GL_FOG_START, currentNear);
        GL11.glFogf(GL11.GL_FOG_END, currentFar);
    }

    @SubscribeEvent
    public void onFOVUpdate(net.minecraftforge.client.event.FOVUpdateEvent event) {
        if (event.entity == null) return;
        net.minecraft.entity.ai.attributes.IAttributeInstance speedAttr = event.entity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.movementSpeed);
        if (speedAttr != null) {
            // Prevent extreme 2x sniper zoom during Altar of Infusion ritual (-1.0 movement slowdown).
            // In 1.7.10, vanilla multiplies base fov by 0.5 when speed is 0. Multiplying by 2.0F cancels this slowdown zoom completely.
            if (speedAttr.getModifier(com.marrybye.neorism.common.tileentity.TileEntityAltarInfusion.ID_MOVEMENT_SLOWDOWN) != null) {
                event.newfov = event.fov * 2.0F;
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
