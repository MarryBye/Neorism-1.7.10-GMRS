package com.marrybye.neorism.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * Canonical port of TeamLapen's DraculaPhase1Model (256x256 texture).
 */
@SideOnly(Side.CLIENT)
public class ModelDraculaPhase1 extends ModelBase {

    public final ModelRenderer body;
    public final ModelRenderer upperBody;
    public final ModelRenderer neck;
    public final ModelRenderer head;
    public final ModelRenderer leftLeg;
    public final ModelRenderer rightLeg;

    public ModelDraculaPhase1() {
        this.textureWidth = 256;
        this.textureHeight = 256;

        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, -12.0F, -1.0F);
        setRotation(this.body, -0.0262F, 0.0F, 0.0F);

        this.upperBody = new ModelRenderer(this, 0, 0);
        this.upperBody.setRotationPoint(0.0F, -14.0F, 4.0F);
        setRotation(this.upperBody, -0.0524F, 0.0F, 0.0F);
        this.body.addChild(this.upperBody);

        ModelRenderer upperBodyShirt = new ModelRenderer(this, 92, 26);
        upperBodyShirt.addBox(-6.0F, -8.0F, -6.2F, 12, 8, 6, 1.0F);
        this.upperBody.addChild(upperBodyShirt);

        ModelRenderer upperBodyShirtCloak = new ModelRenderer(this, 92, 58);
        upperBodyShirtCloak.addBox(-6.0F, -8.0F, -6.2F, 12, 8, 6, 1.01F);
        upperBodyShirt.addChild(upperBodyShirtCloak);

        ModelRenderer cloakCollar = new ModelRenderer(this, 66, 0);
        cloakCollar.addBox(-7.0F, -8.0F, 0.0F, 14, 10, 0, 0.0F);
        cloakCollar.setRotationPoint(0.0F, -9.0F, 1.0F);
        setRotation(cloakCollar, -0.1745F, 0.0F, 0.0F);
        this.upperBody.addChild(cloakCollar);

        ModelRenderer cloakCollarRight = new ModelRenderer(this, 80, 4);
        cloakCollarRight.addBox(-7.0F, -8.0F, -8.0F, 0, 10, 8, 0.0F);
        cloakCollar.addChild(cloakCollarRight);

        ModelRenderer cloakCollarLeft = new ModelRenderer(this, 64, 4);
        cloakCollarLeft.addBox(7.0F, -8.0F, -8.0F, 0, 10, 8, 0.0F);
        cloakCollar.addChild(cloakCollarLeft);

        ModelRenderer bowTie = new ModelRenderer(this, 104, 16);
        bowTie.addBox(-1.0F, -1.0F, -0.1266F, 2, 2, 0, 0.0F);
        bowTie.setRotationPoint(0.0F, -8.0F, -7.5F);
        this.upperBody.addChild(bowTie);

        ModelRenderer bowTieRight = new ModelRenderer(this, 108, 18);
        bowTieRight.addBox(-0.442F, -1.0F, -0.0056F, 6, 6, 0, 0.0F);
        setRotation(bowTieRight, -0.141F, 0.0849F, 0.3929F);
        bowTie.addChild(bowTieRight);

        ModelRenderer bowTieLeft = new ModelRenderer(this, 96, 18);
        bowTieLeft.addBox(-5.558F, -1.0F, -0.0055F, 6, 6, 0, 0.0F);
        setRotation(bowTieLeft, -0.141F, -0.0849F, -0.3929F);
        bowTie.addChild(bowTieLeft);

        this.neck = new ModelRenderer(this, 120, 16);
        this.neck.addBox(-1.0F, -5.0F, 0.0F, 2, 6, 2, 0.5F);
        this.neck.setRotationPoint(0.0F, -9.0F, -6.0F);
        setRotation(this.neck, -0.1309F, 0.0F, 0.0F);
        this.upperBody.addChild(this.neck);

        this.head = new ModelRenderer(this, 96, 0);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 1.0F);
        this.head.setRotationPoint(0.0F, -5.0F, 1.0F);
        setRotation(this.head, 0.1745F, 0.0F, 0.0F);
        this.neck.addChild(this.head);

        ModelRenderer lowerBody = new ModelRenderer(this, 0, 0);
        lowerBody.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.body.addChild(lowerBody);

        ModelRenderer shirtLeft = new ModelRenderer(this, 80, 40);
        shirtLeft.addBox(-0.3F, -12.0F, -2.31F, 6, 12, 6, 0.9F);
        setRotation(shirtLeft, 0.0F, 0.0F, 0.0175F);
        lowerBody.addChild(shirtLeft);

        ModelRenderer lowerBodyLeftCloak = new ModelRenderer(this, 80, 72);
        lowerBodyLeftCloak.addBox(0.0F, -12.01F, -2.31F, 6, 12, 6, 1.0F);
        lowerBody.addChild(lowerBodyLeftCloak);

        ModelRenderer shirtRight = new ModelRenderer(this, 104, 40);
        shirtRight.addBox(-5.7F, -12.0F, -2.3F, 6, 12, 6, 0.9F);
        setRotation(shirtRight, 0.0F, 0.0F, -0.0175F);
        lowerBody.addChild(shirtRight);

        ModelRenderer lowerBodyRightCloak = new ModelRenderer(this, 104, 72);
        lowerBodyRightCloak.addBox(-6.0F, -12.01F, -2.3F, 6, 12, 6, 1.0F);
        lowerBody.addChild(lowerBodyRightCloak);

        this.leftLeg = new ModelRenderer(this, 40, 0);
        this.leftLeg.addBox(0.0F, -7.0F, -3.0F, 6, 44, 6, 0.0F);
        this.leftLeg.setRotationPoint(0.0F, -13.0F, -0.2F);
        setRotation(this.leftLeg, 0.0436F, 0.0F, -0.0087F);

        ModelRenderer leftLegCloakTop = new ModelRenderer(this, 32, 50);
        leftLegCloakTop.addBox(0.0F, 7.99F, -3.01F, 6, 24, 6, 1.0F);
        leftLegCloakTop.setRotationPoint(0.0F, -7.0F, 0.0F);
        setRotation(leftLegCloakTop, 0.0F, 0.0F, 0.0087F);
        this.leftLeg.addChild(leftLegCloakTop);

        ModelRenderer leftLegCloakFront = new ModelRenderer(this, 56, 102);
        leftLegCloakFront.addBox(-0.2F, -0.5F, -3.21F, 6, 16, 6, 0.9F);
        leftLegCloakFront.setRotationPoint(0.0F, 19.0F, 0.0F);
        setRotation(leftLegCloakFront, -0.0436F, 0.0F, 0.0F);
        this.leftLeg.addChild(leftLegCloakFront);

        ModelRenderer leftLegCloakSide = new ModelRenderer(this, 32, 80);
        leftLegCloakSide.addBox(-0.2F, 0.54F, -3.0F, 6, 16, 6, 1.0F);
        leftLegCloakSide.setRotationPoint(0.0F, 18.0F, 0.0F);
        setRotation(leftLegCloakSide, 0.0873F, 0.0F, -0.096F);
        this.leftLeg.addChild(leftLegCloakSide);

        this.rightLeg = new ModelRenderer(this, 40, 0);
        this.rightLeg.addBox(-6.0F, -7.0F, -3.0F, 6, 44, 6, 0.0F);
        this.rightLeg.setRotationPoint(0.0F, -13.0F, -0.2F);
        setRotation(this.rightLeg, 0.0436F, 0.0F, 0.0087F);

        ModelRenderer rightLegCloakTop = new ModelRenderer(this, 56, 50);
        rightLegCloakTop.addBox(-6.0F, 8.0F, -3.0F, 6, 24, 6, 1.0F);
        rightLegCloakTop.setRotationPoint(0.0F, -7.0F, 0.0F);
        setRotation(rightLegCloakTop, 0.0F, 0.0F, -0.0087F);
        this.rightLeg.addChild(rightLegCloakTop);

        ModelRenderer rightLegCloakFront = new ModelRenderer(this, 32, 102);
        rightLegCloakFront.addBox(-5.8F, -0.5F, -3.2F, 6, 16, 6, 0.9F);
        rightLegCloakFront.setRotationPoint(0.0F, 19.0F, 0.0F);
        setRotation(rightLegCloakFront, -0.0436F, 0.0F, 0.0F);
        this.rightLeg.addChild(rightLegCloakFront);

        ModelRenderer rightLegCloakSide = new ModelRenderer(this, 56, 80);
        rightLegCloakSide.addBox(-5.8F, 0.54F, -3.0F, 6, 16, 6, 1.0F);
        rightLegCloakSide.setRotationPoint(0.0F, 18.0F, 0.0F);
        setRotation(rightLegCloakSide, 0.0873F, 0.0F, 0.096F);
        this.rightLeg.addChild(rightLegCloakSide);
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        this.head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180.0F);
        this.head.rotateAngleX = 0.1745F + headPitch * ((float) Math.PI / 180.0F);

        float walkSwing = MathHelper.cos(limbSwing * 0.6662F) * 0.6F * limbSwingAmount;
        this.rightLeg.rotateAngleX = 0.0436F + walkSwing;
        this.leftLeg.rotateAngleX = 0.0436F - walkSwing;
        this.upperBody.rotateAngleX = -0.0524F + MathHelper.sin(ageInTicks * 0.05F) * 0.02F;
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
