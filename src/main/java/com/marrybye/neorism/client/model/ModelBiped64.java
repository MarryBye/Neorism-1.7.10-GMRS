package com.marrybye.neorism.client.model;

import com.marrybye.neorism.common.items.ItemCrossbow;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * 64x64 Modern Skin Biped Model for Minecraft 1.7.10.
 * Accurately maps modern 1.8+ dual-layer player skins, slim (3px) arms, and separate left/right limb textures.
 */
@SideOnly(Side.CLIENT)
public class ModelBiped64 extends ModelBiped {

    public ModelRenderer bipedLeftArmwear;
    public ModelRenderer bipedRightArmwear;
    public ModelRenderer bipedLeftLegwear;
    public ModelRenderer bipedRightLegwear;
    public ModelRenderer bipedBodyWear;
    public final boolean slim;

    public ModelBiped64(float modelSize) {
        this(modelSize, false);
    }

    public ModelBiped64(float modelSize, boolean slim) {
        this(modelSize, 0.0F, 64, 64, slim);
    }

    public ModelBiped64(float modelSize, float yOffset, int width, int height) {
        this(modelSize, yOffset, width, height, false);
    }

    public ModelBiped64(float modelSize, float yOffset, int width, int height, boolean slim) {
        super(modelSize, yOffset, width, height);
        this.slim = slim;
        this.textureWidth = width;
        this.textureHeight = height;

        // Head and outer head layer
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
        this.bipedHead.setRotationPoint(0.0F, 0.0F + yOffset, 0.0F);

        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize + 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + yOffset, 0.0F);

        // Torso and outer jacket layer
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
        this.bipedBody.setRotationPoint(0.0F, 0.0F + yOffset, 0.0F);

        this.bipedBodyWear = new ModelRenderer(this, 16, 32);
        this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F);
        this.bipedBodyWear.setRotationPoint(0.0F, 0.0F + yOffset, 0.0F);

        if (slim) {
            // Slim Right Arm (3px wide)
            this.bipedRightArm = new ModelRenderer(this, 40, 16);
            this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.5F + yOffset, 0.0F);

            this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
            this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
            this.bipedRightArmwear.setRotationPoint(-5.0F, 2.5F + yOffset, 0.0F);

            // Slim Left Arm (3px wide, at 32, 48)
            this.bipedLeftArm = new ModelRenderer(this, 32, 48);
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.5F + yOffset, 0.0F);

            this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
            this.bipedLeftArmwear.setRotationPoint(5.0F, 2.5F + yOffset, 0.0F);
        } else {
            // Classic Right Arm (4px wide)
            this.bipedRightArm = new ModelRenderer(this, 40, 16);
            this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + yOffset, 0.0F);

            this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
            this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
            this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F + yOffset, 0.0F);

            // Classic Left Arm (4px wide, at 32, 48)
            this.bipedLeftArm = new ModelRenderer(this, 32, 48);
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + yOffset, 0.0F);

            this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
            this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F + yOffset, 0.0F);
        }

        // Right Leg
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + yOffset, 0.0F);

        this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F + yOffset, 0.0F);

        // Left Leg (independent texture in 64x64 at 16, 48)
        this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + yOffset, 0.0F);

        this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F + yOffset, 0.0F);
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {
            ItemStack held = ((EntityLivingBase) entity).getHeldItem();
            this.aimedBow = (held != null && held.getItem() instanceof ItemCrossbow);
        }
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        // Synchronize outer layer angles with base limbs
        copyAngles(this.bipedHead, this.bipedHeadwear);
        copyAngles(this.bipedBody, this.bipedBodyWear);
        copyAngles(this.bipedRightArm, this.bipedRightArmwear);
        copyAngles(this.bipedLeftArm, this.bipedLeftArmwear);
        copyAngles(this.bipedRightLeg, this.bipedRightLegwear);
        copyAngles(this.bipedLeftLeg, this.bipedLeftLegwear);

        boolean blendWasEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean alphaWasEnabled = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
        boolean cullWasEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);

        // Canonical ClothedModel uses entityTranslucent: ensure strict alpha test and alpha blending
        // so transparent white pixels (RGBA 255,255,255,0) and translucent overlays on 64x64 skins
        // never render as solid artifacts on NPC heads or bodies.
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        if (this.isChild) {
            float scale = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / scale, 1.5F / scale, 1.5F / scale);
            GL11.glTranslatef(0.0F, 16.0F * f5, 0.0F);
            this.bipedHead.render(f5);
            GL11.glDisable(GL11.GL_CULL_FACE);
            this.bipedHeadwear.render(f5);
            if (cullWasEnabled) GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
            GL11.glTranslatef(0.0F, 24.0F * f5, 0.0F);
            this.bipedBody.render(f5);
            this.bipedRightArm.render(f5);
            this.bipedLeftArm.render(f5);
            this.bipedRightLeg.render(f5);
            this.bipedLeftLeg.render(f5);
            this.bipedBodyWear.render(f5);
            this.bipedRightArmwear.render(f5);
            this.bipedLeftArmwear.render(f5);
            this.bipedRightLegwear.render(f5);
            this.bipedLeftLegwear.render(f5);
            GL11.glPopMatrix();
        } else {
            this.bipedHead.render(f5);
            this.bipedBody.render(f5);
            this.bipedRightArm.render(f5);
            this.bipedLeftArm.render(f5);
            this.bipedRightLeg.render(f5);
            this.bipedLeftLeg.render(f5);

            GL11.glDisable(GL11.GL_CULL_FACE);
            this.bipedHeadwear.render(f5);
            if (cullWasEnabled) GL11.glEnable(GL11.GL_CULL_FACE);

            this.bipedBodyWear.render(f5);
            this.bipedRightArmwear.render(f5);
            this.bipedLeftArmwear.render(f5);
            this.bipedRightLegwear.render(f5);
            this.bipedLeftLegwear.render(f5);
        }

        if (!blendWasEnabled) {
            GL11.glDisable(GL11.GL_BLEND);
        }
        if (!alphaWasEnabled) {
            GL11.glDisable(GL11.GL_ALPHA_TEST);
        }
    }

    private static void copyAngles(ModelRenderer source, ModelRenderer target) {
        target.rotateAngleX = source.rotateAngleX;
        target.rotateAngleY = source.rotateAngleY;
        target.rotateAngleZ = source.rotateAngleZ;
        target.rotationPointX = source.rotationPointX;
        target.rotationPointY = source.rotationPointY;
        target.rotationPointZ = source.rotationPointZ;
    }
}
