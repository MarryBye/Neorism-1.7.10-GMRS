package com.marrybye.neorism.client.events;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.client.gui.overlay.SunOverlay;
import com.marrybye.neorism.common.action.ActionHandler;
import com.marrybye.neorism.common.action.ActionRegistry;
import com.marrybye.neorism.common.entity.hunter.EntityHunter;
import com.marrybye.neorism.common.util.Helper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;

/**
 * Client rendering handler for Blood Vision.
 * Canonical port of TeamLapen's BloodVision shader and creature highlighting.
 * Fully compatible with Angelica and Sodium pipelines without breaking shaders.
 */
@SideOnly(Side.CLIENT)
public class BloodVisionHandler {

    private static final ResourceLocation AWARENESS_TEXTURE = new ResourceLocation("neorism", "textures/gui/overlay/awareness.png");
    private boolean visionActive = false;

    private boolean isBloodVisionActive() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || !Helper.isVampire(mc.thePlayer)) {
            return false;
        }
        ActionHandler handler = ActionHandler.get(mc.thePlayer);
        return handler != null && handler.isActive(ActionRegistry.getAction("blood_vision"));
    }

    private boolean isWarmBlooded(EntityLivingBase entity) {
        if (entity == null || entity.isDead) return false;
        if (Helper.isVampire(entity)) return false;
        return entity instanceof EntityPlayer || entity instanceof EntityVillager || entity instanceof EntityAnimal || entity instanceof EntityHunter;
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre event) {
        if (!isBloodVisionActive()) return;

        EntityLivingBase target = event.entity;
        if (target == Minecraft.getMinecraft().thePlayer) return;

        if (isWarmBlooded(target)) {
            visionActive = true;
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GL11.glColor4f(1.0F, 0.25F, 0.25F, 0.85F);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post event) {
        if (visionActive) {
            visionActive = false;
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!isBloodVisionActive()) return;

        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();

        // Subtle ambient crimson pulse vignette
        float pulse = 0.35F + (float) Math.sin((Minecraft.getMinecraft().thePlayer.ticksExisted + event.partialTicks) * 0.15F) * 0.1F;
        SunOverlay.renderVignette(AWARENESS_TEXTURE, width, height, pulse, 0.9F, 0.15F, 0.15F);
    }
}
