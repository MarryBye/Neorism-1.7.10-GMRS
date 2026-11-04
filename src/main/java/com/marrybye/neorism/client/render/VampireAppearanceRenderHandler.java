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

    private final net.minecraft.client.model.ModelBat batModel = new net.minecraft.client.model.ModelBat();
    private static final ResourceLocation BAT_TEXTURE = new ResourceLocation("textures/entity/bat.png");
    private final ModelRenderer headModel;
    private net.minecraft.entity.passive.EntityBat dummyBat;

    public VampireAppearanceRenderHandler() {
        headModel = new ModelRenderer(new ModelBase() {}, 0, 0);
        headModel.setTextureSize(64, 64);
        headModel.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.05F);
        headModel.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        if (player == null || player.deathTime > 0) return;

        com.marrybye.neorism.common.action.ActionHandler handler = com.marrybye.neorism.common.action.ActionHandler.get(player);
        if (handler != null) {
            // Invisibility Action: completely conceal player entity rendering
            if (handler.isActive(com.marrybye.neorism.common.action.ActionRegistry.getAction("vampire_invisibility"))) {
                event.setCanceled(true);
                return;
            }

            // Bat Form: replace player render with vanilla bat model
            if (handler.isActive(com.marrybye.neorism.common.action.ActionRegistry.getAction("bat_form"))) {
                event.setCanceled(true);

                Minecraft mc = Minecraft.getMinecraft();
                if (mc.thePlayer == player && mc.gameSettings.thirdPersonView == 0) {
                    return;
                }

                if (player.isInvisible()) return;

                if (dummyBat == null || dummyBat.worldObj != player.worldObj) {
                    dummyBat = new net.minecraft.entity.passive.EntityBat(player.worldObj);
                }

                float partialTicks = event.partialRenderTick;
                double renderX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks - net.minecraft.client.renderer.entity.RenderManager.renderPosX;
                double renderY = (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks - (double) player.yOffset) - net.minecraft.client.renderer.entity.RenderManager.renderPosY;
                double renderZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks - net.minecraft.client.renderer.entity.RenderManager.renderPosZ;

                // Sync all rotation, animation, and hurt state to dummy bat.
                // Sync body yaw to rotationYaw so the bat always turns with the player crosshair.
                dummyBat.setLocationAndAngles(player.posX, player.posY - player.yOffset, player.posZ, player.rotationYaw, player.rotationPitch);
                dummyBat.prevRotationYaw = player.prevRotationYaw;
                dummyBat.rotationYaw = player.rotationYaw;
                dummyBat.prevRotationPitch = player.prevRotationPitch;
                dummyBat.rotationPitch = player.rotationPitch;
                dummyBat.rotationYawHead = player.rotationYaw;
                dummyBat.prevRotationYawHead = player.prevRotationYaw;
                dummyBat.renderYawOffset = player.rotationYaw;
                dummyBat.prevRenderYawOffset = player.prevRotationYaw;
                dummyBat.ticksExisted = player.ticksExisted;
                dummyBat.hurtTime = player.hurtTime;
                dummyBat.maxHurtTime = player.maxHurtTime;
                dummyBat.attackedAtYaw = player.attackedAtYaw;
                dummyBat.deathTime = player.deathTime;
                dummyBat.setIsBatHanging(false);
                dummyBat.setInvisible(false);

                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                net.minecraft.client.renderer.entity.RenderManager.instance.renderEntityWithPosYaw(
                    dummyBat, renderX, renderY + 0.1D, renderZ, player.rotationYaw, partialTicks
                );
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glPopMatrix();
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayerSpecialsPre(RenderPlayerEvent.Specials.Pre event) {
        EntityPlayer player = event.entityPlayer;
        if (player == null) return;

        com.marrybye.neorism.common.action.ActionHandler ah = com.marrybye.neorism.common.action.ActionHandler.get(player);
        if (ah != null) {
            // Cancel equipment and nametag rendering in bat form or invisibility
            if (ah.isActive(com.marrybye.neorism.common.action.ActionRegistry.getAction("bat_form")) ||
                ah.isActive(com.marrybye.neorism.common.action.ActionRegistry.getAction("vampire_invisibility"))) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(net.minecraftforge.client.event.RenderHandEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            com.marrybye.neorism.common.action.ActionHandler ah = com.marrybye.neorism.common.action.ActionHandler.get(mc.thePlayer);
            if (ah != null && ah.isActive(com.marrybye.neorism.common.action.ActionRegistry.getAction("bat_form"))) {
                event.setCanceled(true);
            }
        }
    }

    private float interpolateRotation(float prev, float current, float partialTicks) {
        float diff = current - prev;
        while (diff < -180.0F) diff += 360.0F;
        while (diff >= 180.0F) diff -= 360.0F;
        return prev + partialTicks * diff;
    }

    @SubscribeEvent
    public void onRenderPlayerSpecialsPost(RenderPlayerEvent.Specials.Post event) {
        EntityPlayer player = event.entityPlayer;
        if (player == null || player.deathTime > 0) return;

        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null) return;

        com.marrybye.neorism.common.action.ActionHandler ah = com.marrybye.neorism.common.action.ActionHandler.get(player);
        if (ah != null && ah.isActive(com.marrybye.neorism.common.action.ActionRegistry.getAction("bat_form"))) {
            return;
        }

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
