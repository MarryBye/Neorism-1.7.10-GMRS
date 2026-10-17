package com.marrybye.neorism.client.render;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.client.gui.VampirePlayerAppearanceScreen;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;

/**
 * Renders vampire facial features (eyes, glowing eyes, fangs) on the player model.
 * Uses a dedicated 64x64 head model renderer to match 64x64 eye and fang decal sheets.
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

    private final ModelRenderer headModel;

    public VampireAppearanceRenderHandler() {
        headModel = new ModelRenderer(new ModelBase() {}, 0, 0);
        headModel.setTextureSize(64, 64);
        headModel.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.05F);
        headModel.setRotationPoint(0.0F, 0.0F, 0.0F);
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

        headModel.rotateAngleX = event.renderer.modelBipedMain.bipedHead.rotateAngleX;
        headModel.rotateAngleY = event.renderer.modelBipedMain.bipedHead.rotateAngleY;
        headModel.rotateAngleZ = event.renderer.modelBipedMain.bipedHead.rotateAngleZ;
        headModel.rotationPointX = event.renderer.modelBipedMain.bipedHead.rotationPointX;
        headModel.rotationPointY = event.renderer.modelBipedMain.bipedHead.rotationPointY;
        headModel.rotationPointZ = event.renderer.modelBipedMain.bipedHead.rotationPointZ;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-2.0F, -20.0F);
        GL11.glDepthMask(false);

        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        // Render eyes
        if (glowing) {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        } else {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(EYE_TEXTURES[eyeIdx]);
        headModel.render(0.0625F);

        // Restore lighting for fangs if eyes were glowing
        if (glowing) {
            GL11.glEnable(GL11.GL_LIGHTING);
            int light = player.getBrightnessForRender(event.partialRenderTick);
            int lightX = light % 65536;
            int lightY = light / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) lightX, (float) lightY);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        // Render fangs
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(FANG_TEXTURES[fangIdx]);
        headModel.render(0.0625F);

        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glPopMatrix();
    }
}
