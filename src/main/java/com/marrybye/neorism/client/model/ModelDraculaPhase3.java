package com.marrybye.neorism.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * Canonical port of TeamLapen's DraculaPhase3Model (256x256 texture, winged blood rage form).
 */
@SideOnly(Side.CLIENT)
public class ModelDraculaPhase3 extends ModelBase {

    public final ModelRenderer body;
    public final ModelRenderer neck;
    public final ModelRenderer head;
    public final ModelRenderer wings;
    public final ModelRenderer wingsLeft;
    public final ModelRenderer wingsRight;
    public final ModelRenderer leftArm;
    public final ModelRenderer rightArm;
    public final ModelRenderer leftLeg;
    public final ModelRenderer rightLeg;

    public ModelDraculaPhase3() {
        this.textureWidth = 256;
        this.textureHeight = 256;

        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, -20.0F, 0.0F);
        setRotation(this.body, 0.6109F, 0.0F, 0.0F);

        this.neck = new ModelRenderer(this, 120, 16);
        this.neck.addBox(-1.0F, -5.0F, -0.5F, 2, 7, 2, 0.5F);
        this.neck.setRotationPoint(0.0F, -19.0F, 0.0F);
        setRotation(this.neck, -0.1309F, 0.0F, 0.0F);
        this.body.addChild(this.neck);

        this.head = new ModelRenderer(this, 96, 0);
        this.head.addBox(-4.0F, -6.5F, -4.0F, 8, 8, 8, 1.0F);
        this.head.setRotationPoint(0.0F, -5.5F, 0.0F);
        setRotation(this.head, -0.3054F, 0.0F, 0.0F);
        this.neck.addChild(this.head);

        this.wings = new ModelRenderer(this, 0, 0);
        this.wings.setRotationPoint(0.0F, -13.0F, 4.0F);
        this.body.addChild(this.wings);

        this.wingsLeft = new ModelRenderer(this, 128, 31);
        this.wingsLeft.mirror = true;
        this.wingsLeft.addBox(3.2F, -6.0F, -0.5F, 64, 30, 0, 0.0F);
        setRotation(this.wingsLeft, 0.258F, -0.5333F, -0.4824F);
        this.wings.addChild(this.wingsLeft);

        this.wingsRight = new ModelRenderer(this, 128, 1);
        this.wingsRight.addBox(-67.2F, -6.0F, -0.5F, 64, 30, 0, 0.0F);
        setRotation(this.wingsRight, 0.258F, 0.5333F, 0.4824F);
        this.wings.addChild(this.wingsRight);

        this.leftArm = new ModelRenderer(this, 40, 43);
        this.leftArm.addBox(0.0F, 0.0F, -2.77F, 4, 28, 6, 0.0F);
        this.leftArm.setRotationPoint(6.0F, -19.0F, 0.5F);
        setRotation(this.leftArm, -0.0349F, 0.0F, -0.0436F);
        this.body.addChild(this.leftArm);

        ModelRenderer leftArmCuff = new ModelRenderer(this, 80, 52);
        leftArmCuff.addBox(0.0F, 0.0F, -2.77F, 4, 28, 6, 0.2F);
        this.leftArm.addChild(leftArmCuff);

        this.rightArm = new ModelRenderer(this, 60, 43);
        this.rightArm.addBox(-4.0F, 0.0F, -2.77F, 4, 28, 6, 0.0F);
        this.rightArm.setRotationPoint(-6.0F, -19.0F, 0.5F);
        setRotation(this.rightArm, -0.0349F, 0.0F, 0.0436F);
        this.body.addChild(this.rightArm);

        ModelRenderer rightArmCuff = new ModelRenderer(this, 100, 52);
        rightArmCuff.addBox(-4.0F, 0.0F, -2.77F, 4, 28, 6, 0.2F);
        this.rightArm.addChild(rightArmCuff);

        ModelRenderer shirt = new ModelRenderer(this, 0, 0);
        this.body.addChild(shirt);

        ModelRenderer shirtTop = new ModelRenderer(this, 90, 20);
        shirtTop.addBox(-6.0F, 0.0F, -2.27F, 12, 8, 6, 0.0F);
        shirtTop.setRotationPoint(0.0F, -19.0F, 0.0F);
        setRotation(shirtTop, -0.0349F, 0.0F, 0.0F);
        shirt.addChild(shirtTop);

        ModelRenderer shirtTopExtra = new ModelRenderer(this, 128, 96);
        shirtTopExtra.addBox(-6.0F, 0.0F, -2.27F, 12, 8, 6, 0.04F);
        shirtTop.addChild(shirtTopExtra);

        ModelRenderer shirtRight = new ModelRenderer(this, 104, 34);
        shirtRight.addBox(-6.0F, 7.0F, -2.5F, 6, 12, 6, 0.0F);
        shirtRight.setRotationPoint(0.0F, -19.0F, 0.0F);
        setRotation(shirtRight, 0.0F, 0.0F, -0.0175F);
        shirt.addChild(shirtRight);

        ModelRenderer shirtRightExtra = new ModelRenderer(this, 152, 110);
        shirtRightExtra.addBox(-6.0F, 7.0F, -2.5F, 6, 12, 6, 0.04F);
        shirtRight.addChild(shirtRightExtra);

        ModelRenderer shirtLeft = new ModelRenderer(this, 80, 34);
        shirtLeft.addBox(0.0F, 7.0F, -2.51F, 6, 12, 6, 0.0F);
        shirtLeft.setRotationPoint(0.0F, -19.0F, 0.0F);
        setRotation(shirtLeft, 0.0F, 0.0F, 0.0175F);
        shirt.addChild(shirtLeft);

        ModelRenderer shirtLeftExtra = new ModelRenderer(this, 128, 110);
        shirtLeftExtra.addBox(0.0F, 7.0F, -2.5F, 6, 12, 6, 0.04F);
        shirtLeft.addChild(shirtLeftExtra);

        this.leftLeg = new ModelRenderer(this, 32, 77);
        this.leftLeg.addBox(0.0F, -0.2F, -3.0F, 6, 44, 6, 0.0F);
        this.leftLeg.setRotationPoint(0.0F, -20.0F, 0.0F);
        setRotation(this.leftLeg, 0.0F, -0.0017F, -0.0436F);

        this.rightLeg = new ModelRenderer(this, 56, 77);
        this.rightLeg.addBox(-6.0F, -0.2F, -3.0F, 6, 44, 6, 0.0F);
        this.rightLeg.setRotationPoint(0.0F, -20.0F, 0.0F);
        setRotation(this.rightLeg, 0.0F, 0.0017F, 0.0436F);
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        this.head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180.0F);
        this.head.rotateAngleX = -0.3054F + headPitch * ((float) Math.PI / 180.0F);

        float legSwing = MathHelper.cos(limbSwing * 0.6662F) * 0.8F * limbSwingAmount;
        this.rightLeg.rotateAngleX = legSwing;
        this.leftLeg.rotateAngleX = -legSwing;

        float armSwing = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.8F * limbSwingAmount;
        this.rightArm.rotateAngleX = -0.6F + armSwing;
        this.leftArm.rotateAngleX = -0.6F - armSwing;

        if (this.onGround > -9990.0F) {
            float swing = MathHelper.sin(this.onGround * (float) Math.PI);
            this.rightArm.rotateAngleX -= swing * 1.4F;
            this.leftArm.rotateAngleX -= swing * 1.4F;
        }

        float wingFlap = MathHelper.sin(ageInTicks * 0.15F) * 0.25F;
        this.wingsLeft.rotateAngleY = -0.5333F + wingFlap;
        this.wingsRight.rotateAngleY = 0.5333F - wingFlap;
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        boolean cullWasEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_CULL_FACE);
        this.body.render(f5);
        this.leftLeg.render(f5);
        this.rightLeg.render(f5);
        if (cullWasEnabled) {
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }
}
