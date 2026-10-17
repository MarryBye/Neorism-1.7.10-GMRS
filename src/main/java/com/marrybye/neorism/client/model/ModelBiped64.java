package com.marrybye.neorism.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * 64x64 Modern Skin Biped Model for Minecraft 1.7.10.
 * Accurately maps modern 1.8+ dual-layer player skins and separate left/right limb textures.
 */
@SideOnly(Side.CLIENT)
public class ModelBiped64 extends ModelBiped {

    public ModelRenderer bipedLeftArmwear;
    public ModelRenderer bipedRightArmwear;
    public ModelRenderer bipedLeftLegwear;
    public ModelRenderer bipedRightLegwear;
    public ModelRenderer bipedBodyWear;

    public ModelBiped64(float modelSize) {
        this(modelSize, 0.0F, 64, 64);
    }

    public ModelBiped64(float modelSize, float p_i1149_2_, int width, int height) {
        super(modelSize, p_i1149_2_, width, height);
        this.textureWidth = width;
        this.textureHeight = height;

        // Head and outer head layer
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
        this.bipedHead.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);

        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize + 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);

        // Torso and outer jacket layer
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
        this.bipedBody.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);

        this.bipedBodyWear = new ModelRenderer(this, 16, 32);
        this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F);
        this.bipedBodyWear.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);

        // Right Arm
        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + p_i1149_2_, 0.0F);

        this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
        this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F + p_i1149_2_, 0.0F);

        // Left Arm (independent texture in 64x64 at 32, 48)
        this.bipedLeftArm = new ModelRenderer(this, 32, 48);
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + p_i1149_2_, 0.0F);

        this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
        this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F + p_i1149_2_, 0.0F);

        // Right Leg
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + p_i1149_2_, 0.0F);

        this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F + p_i1149_2_, 0.0F);

        // Left Leg (independent texture in 64x64 at 16, 48)
        this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + p_i1149_2_, 0.0F);

        this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F + p_i1149_2_, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        // Synchronize outer layer angles with base limbs
        copyAngles(this.bipedBody, this.bipedBodyWear);
        copyAngles(this.bipedRightArm, this.bipedRightArmwear);
        copyAngles(this.bipedLeftArm, this.bipedLeftArmwear);
        copyAngles(this.bipedRightLeg, this.bipedRightLegwear);
        copyAngles(this.bipedLeftLeg, this.bipedLeftLegwear);

        if (this.isChild) {
            float scale = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / scale, 1.5F / scale, 1.5F / scale);
            GL11.glTranslatef(0.0F, 16.0F * f5, 0.0F);
            this.bipedHead.render(f5);
            this.bipedHeadwear.render(f5);
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
            GL11.glTranslatef(0.0F, 24.0F * f5, 0.0F);
            this.bipedBody.render(f5);
            this.bipedBodyWear.render(f5);
            this.bipedRightArm.render(f5);
            this.bipedRightArmwear.render(f5);
            this.bipedLeftArm.render(f5);
            this.bipedLeftArmwear.render(f5);
            this.bipedRightLeg.render(f5);
            this.bipedRightLegwear.render(f5);
            this.bipedLeftLeg.render(f5);
            this.bipedLeftLegwear.render(f5);
            GL11.glPopMatrix();
        } else {
            this.bipedHead.render(f5);
            this.bipedHeadwear.render(f5);
            this.bipedBody.render(f5);
            this.bipedBodyWear.render(f5);
            this.bipedRightArm.render(f5);
            this.bipedRightArmwear.render(f5);
            this.bipedLeftArm.render(f5);
            this.bipedLeftArmwear.render(f5);
            this.bipedRightLeg.render(f5);
            this.bipedRightLegwear.render(f5);
            this.bipedLeftLeg.render(f5);
            this.bipedLeftLegwear.render(f5);
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
