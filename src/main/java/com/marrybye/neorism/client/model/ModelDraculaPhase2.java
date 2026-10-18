package com.marrybye.neorism.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * Canonical port of TeamLapen's DraculaPhase2Model (256x256 texture).
 */
@SideOnly(Side.CLIENT)
public class ModelDraculaPhase2 extends ModelBase {

    public final ModelRenderer body;
    public final ModelRenderer neck;
    public final ModelRenderer head;
    public final ModelRenderer shirt;
    public final ModelRenderer leftArm;
    public final ModelRenderer rightArm;
    public final ModelRenderer cloak;
    public final ModelRenderer leftLeg;
    public final ModelRenderer rightLeg;

    public ModelDraculaPhase2() {
        this.textureWidth = 256;
        this.textureHeight = 256;

        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, -20.0F, 0.0F);
        setRotation(this.body, -0.0262F, 0.0F, 0.0F);

        this.neck = new ModelRenderer(this, 120, 16);
        this.neck.addBox(-1.0F, -5.0F, -1.0F, 2, 6, 2, 0.5F);
        this.neck.setRotationPoint(0.0F, -19.0F, 0.0F);
        setRotation(this.neck, -0.1309F, 0.0F, 0.0F);
        this.body.addChild(this.neck);

        this.head = new ModelRenderer(this, 96, 0);
        this.head.addBox(-6.0F, -7.0F, -1.5F, 8, 8, 8, 1.0F);
        this.head.setRotationPoint(2.0F, -5.0F, -3.0F);
        setRotation(this.head, 0.2618F, 0.0F, 0.0F);
        this.neck.addChild(this.head);

        this.shirt = new ModelRenderer(this, 0, 0);
        setRotation(this.shirt, -0.0175F, 0.0F, 0.0F);
        this.body.addChild(this.shirt);

        this.leftArm = new ModelRenderer(this, 40, 47);
        this.leftArm.addBox(0.0F, 0.0F, -2.77F, 4, 28, 6, 0.0F);
        this.leftArm.setRotationPoint(6.0F, -19.0F, 0.5F);
        setRotation(this.leftArm, 0.0F, 0.0F, -0.0436F);
        this.shirt.addChild(this.leftArm);

        ModelRenderer leftArmCuff = new ModelRenderer(this, 80, 56);
        leftArmCuff.addBox(0.0F, 0.0F, -2.77F, 4, 28, 6, 0.2F);
        this.leftArm.addChild(leftArmCuff);

        this.rightArm = new ModelRenderer(this, 60, 47);
        this.rightArm.addBox(-4.0F, 0.0F, -2.77F, 4, 28, 6, 0.0F);
        this.rightArm.setRotationPoint(-6.0F, -19.0F, 0.5F);
        setRotation(this.rightArm, 0.0F, 0.0F, 0.0436F);
        this.shirt.addChild(this.rightArm);

        ModelRenderer rightArmCuff = new ModelRenderer(this, 100, 56);
        rightArmCuff.addBox(-4.0F, 0.0F, -2.77F, 4, 28, 6, 0.2F);
        this.rightArm.addChild(rightArmCuff);

        ModelRenderer shirtTop = new ModelRenderer(this, 92, 24);
        shirtTop.addBox(-6.0F, 0.0F, -2.27F, 12, 8, 6, 0.0F);
        shirtTop.setRotationPoint(0.0F, -19.0F, 0.0F);
        setRotation(shirtTop, -0.0349F, 0.0F, 0.0F);
        this.shirt.addChild(shirtTop);

        ModelRenderer bowTie = new ModelRenderer(this, 0, 0);
        bowTie.setRotationPoint(0.0F, 0.0F, -2.8382F);
        shirtTop.addChild(bowTie);

        ModelRenderer bowTieCenter = new ModelRenderer(this, 106, 16);
        bowTieCenter.addBox(-1.0F, -1.0F, 0.2115F, 2, 2, 0, 0.0F);
        setRotation(bowTieCenter, -0.1571F, 0.0F, 0.0F);
        bowTie.addChild(bowTieCenter);

        ModelRenderer bowTieRight = new ModelRenderer(this, 108, 18);
        bowTieRight.addBox(-0.5F, -1.0F, 0.3382F, 6, 6, 0, 0.0F);
        setRotation(bowTieRight, -0.1396F, 0.0698F, 0.384F);
        bowTie.addChild(bowTieRight);

        ModelRenderer bowTieLeft = new ModelRenderer(this, 96, 18);
        bowTieLeft.addBox(-5.5F, -1.0F, 0.3382F, 6, 6, 0, 0.0F);
        setRotation(bowTieLeft, -0.1396F, -0.0698F, -0.384F);
        bowTie.addChild(bowTieLeft);

        ModelRenderer shirtRight = new ModelRenderer(this, 104, 38);
        shirtRight.addBox(-6.0F, 7.0F, -2.5F, 6, 12, 6, 0.0F);
        shirtRight.setRotationPoint(0.0F, -19.0F, 0.0F);
        setRotation(shirtRight, 0.0F, 0.0F, -0.0175F);
        this.shirt.addChild(shirtRight);

        ModelRenderer shirtLeft = new ModelRenderer(this, 80, 38);
        shirtLeft.addBox(0.0F, 7.0F, -2.51F, 6, 12, 6, 0.0F);
        shirtLeft.setRotationPoint(0.0F, -19.0F, 0.0F);
        setRotation(shirtLeft, 0.0F, 0.0F, 0.0175F);
        this.shirt.addChild(shirtLeft);

        ModelRenderer cloakCollar = new ModelRenderer(this, 68, 0);
        cloakCollar.addBox(-6.0F, -9.0F, 1.0F, 12, 10, 0, 0.003F);
        cloakCollar.setRotationPoint(0.0F, -19.0F, 4.0F);
        setRotation(cloakCollar, -0.1745F, 0.0F, 0.0F);
        this.body.addChild(cloakCollar);

        ModelRenderer cloakCollarRight = new ModelRenderer(this, 80, 4);
        cloakCollarRight.addBox(-6.0F, -9.0F, -7.0F, 0, 10, 8, 0.003F);
        cloakCollar.addChild(cloakCollarRight);

        ModelRenderer cloakCollarLeft = new ModelRenderer(this, 64, 4);
        cloakCollarLeft.addBox(6.0F, -9.0F, -7.0F, 0, 10, 8, 0.003F);
        cloakCollar.addChild(cloakCollarLeft);

        this.cloak = new ModelRenderer(this, 0, 0);
        this.cloak.setRotationPoint(0.0F, -19.0F, 5.0F);
        this.body.addChild(this.cloak);

        ModelRenderer cloakCenter = new ModelRenderer(this, 128, 0);
        cloakCenter.addBox(-8.0F, -1.0F, -1.0F, 16, 62, 0, 0.01F);
        setRotation(cloakCenter, 0.1047F, 0.0F, 0.0F);
        this.cloak.addChild(cloakCenter);

        ModelRenderer cloakRight = new ModelRenderer(this, 192, 50);
        cloakRight.addBox(-10.0F, 1.0F, -0.9F, 16, 60, 0, 0.01F);
        setRotation(cloakRight, 0.1047F, 0.0349F, 0.2269F);
        this.cloak.addChild(cloakRight);

        ModelRenderer cloakLeft = new ModelRenderer(this, 160, 50);
        cloakLeft.addBox(-6.0F, 1.0F, -0.9F, 16, 60, 0, 0.01F);
        setRotation(cloakLeft, 0.1047F, -0.0349F, -0.2269F);
        this.cloak.addChild(cloakLeft);

        this.leftLeg = new ModelRenderer(this, 32, 81);
        this.leftLeg.addBox(0.0F, -0.2F, -3.0F, 6, 44, 6, 0.0F);
        this.leftLeg.setRotationPoint(0.0F, -20.0F, 0.0F);
        setRotation(this.leftLeg, 0.0436F, 0.0F, -0.0087F);

        this.rightLeg = new ModelRenderer(this, 56, 81);
        this.rightLeg.addBox(-6.0F, -0.2F, -3.0F, 6, 44, 6, 0.0F);
        this.rightLeg.setRotationPoint(0.0F, -20.0F, 0.0F);
        setRotation(this.rightLeg, 0.0436F, 0.0F, 0.0087F);
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        this.head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180.0F);
        this.head.rotateAngleX = 0.2618F + headPitch * ((float) Math.PI / 180.0F);

        float legSwing = MathHelper.cos(limbSwing * 0.6662F) * 0.8F * limbSwingAmount;
        this.rightLeg.rotateAngleX = 0.0436F + legSwing;
        this.leftLeg.rotateAngleX = 0.0436F - legSwing;

        float armSwing = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.7F * limbSwingAmount;
        this.rightArm.rotateAngleX = armSwing;
        this.leftArm.rotateAngleX = -armSwing;

        if (this.onGround > -9990.0F) {
            float swing = MathHelper.sin(this.onGround * (float) Math.PI);
            this.rightArm.rotateAngleX -= swing * 1.2F;
        }

        this.rightArm.rotateAngleZ = 0.0436F + MathHelper.cos(ageInTicks * 0.06F) * 0.04F;
        this.leftArm.rotateAngleZ = -0.0436F - MathHelper.cos(ageInTicks * 0.06F) * 0.04F;
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
