package com.marrybye.neorism.client.gui.overlay;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Sun Exposure HUD Vignette Overlay.
 * Canonical port of TeamLapen's SunOverlay.
 * Warns vampire players of burning sunlight exposure with a burning heat vignette.
 */
@SideOnly(Side.CLIENT)
public class SunOverlay {

    private static final ResourceLocation SUN_VIGNETTE = new ResourceLocation(Neorism.MODID, "textures/gui/overlay/sun.png");
    private final Minecraft mc;

    public SunOverlay(Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        EntityPlayer player = mc.thePlayer;
        if (player == null || !Helper.isVampire(player) || player.capabilities.isCreativeMode) {
            return;
        }

        VampirePlayer vp = VampirePlayer.get(player);
        if (vp == null) return;

        int ticksInSun = vp.getTicksInSun();
        if (ticksInSun <= 0) return;

        if (player.isPotionActive(ModPotions.sunscreen) && ticksInSun <= 50) {
            return;
        }

        ScaledResolution res = event.resolution;
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();

        float intensity = Math.min(1.0F, ticksInSun / 60.0F);
        // Pulse slightly when burning
        if (ticksInSun >= 50) {
            float pulse = (float) Math.sin((player.ticksExisted + event.partialTicks) * 0.2F) * 0.15F;
            intensity = Math.min(1.0F, intensity + pulse);
        }

        renderVignette(SUN_VIGNETTE, width, height, intensity, 1.0F, 0.7F, 0.4F);
    }

    public static void renderVignette(ResourceLocation texture, int width, int height, float alpha, float r, float g, float b) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GL11.glColor4f(r, g, b, alpha);

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV(0.0D, height, -90.0D, 0.0D, 1.0D);
        tess.addVertexWithUV(width, height, -90.0D, 1.0D, 1.0D);
        tess.addVertexWithUV(width, 0.0D, -90.0D, 1.0D, 0.0D);
        tess.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
        tess.draw();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
