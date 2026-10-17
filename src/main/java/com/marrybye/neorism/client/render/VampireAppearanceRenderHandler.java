package com.marrybye.neorism.client.render;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.client.gui.VampirePlayerAppearanceScreen;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;

/**
 * Renders vampire facial features (eyes, glowing eyes, fangs) on the player model.
 * Faithful 1.7.10 backport of TeamLapen's VampirePlayerHeadLayer.
 */
@SideOnly(Side.CLIENT)
public class VampireAppearanceRenderHandler {

    private static final ResourceLocation[] EYE_TEXTURES = new ResourceLocation[19];
    private static final ResourceLocation[] FANG_TEXTURES = new ResourceLocation[7];

    static {
        for (int i = 0; i < EYE_TEXTURES.length; i++) {
            EYE_TEXTURES[i] = new ResourceLocation("neorism", "textures/entity/eyes/eyes" + i + ".png");
        }
        for (int i = 0; i < FANG_TEXTURES.length; i++) {
            FANG_TEXTURES[i] = new ResourceLocation("neorism", "textures/entity/fangs/fangs" + i + ".png");
        }
    }

    @SubscribeEvent
    public void onRenderPlayerSpecialsPost(RenderPlayerEvent.Specials.Post event) {
        EntityPlayer player = event.entityPlayer;
        if (player == null || player.deathTime > 0) return;

        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null) return;

        boolean inAppearanceScreen = Minecraft.getMinecraft().currentScreen instanceof VampirePlayerAppearanceScreen;
        if (!inAppearanceScreen) {
            if (!fph.isVampire() || fph.isDisguised() || player.isInvisible()) {
                return;
            }
        }

        int eyeIdx = Math.max(0, Math.min(EYE_TEXTURES.length - 1, fph.getEyeType()));
        int fangIdx = Math.max(0, Math.min(FANG_TEXTURES.length - 1, fph.getFangType()));
        boolean glowing = fph.hasGlowingEyes();

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-1.0F, -10.0F);

        // Render eyes
        if (glowing) {
            GL11.glDisable(GL11.GL_LIGHTING);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(EYE_TEXTURES[eyeIdx]);
        event.renderer.modelBipedMain.bipedHead.render(0.0625F);

        if (glowing) {
            GL11.glEnable(GL11.GL_LIGHTING);
            int light = player.getBrightnessForRender(event.partialRenderTick);
            int lightX = light % 65536;
            int lightY = light / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) lightX, (float) lightY);
        }

        // Render fangs
        Minecraft.getMinecraft().getTextureManager().bindTexture(FANG_TEXTURES[fangIdx]);
        event.renderer.modelBipedMain.bipedHead.render(0.0625F);

        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
