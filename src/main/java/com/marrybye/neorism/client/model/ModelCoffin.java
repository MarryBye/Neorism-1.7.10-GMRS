package com.marrybye.neorism.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * Canonical 3D Coffin Model for Forge 1.7.10.
 * Faithful port of TeamLapen's CoffinModel with dual opening lids and handles.
 */
public class ModelCoffin extends ModelBase {

    public ModelRenderer leftPlate;
    public ModelRenderer rightPlate;
    public ModelRenderer backPlate;
    public ModelRenderer topPlate;
    public ModelRenderer bottomPlate;
    public ModelRenderer leftLid;
    public ModelRenderer rightLid;
    public ModelRenderer leftHandle;
    public ModelRenderer rightHandle;

    public ModelCoffin() {
        this.textureWidth = 256;
        this.textureHeight = 128;

        this.leftPlate = new ModelRenderer(this, 0, 64);
        this.leftPlate.mirror = true;
        this.leftPlate.addBox(7.0F, -12.0F, 0.0F, 1, 12, 32);
        this.leftPlate.setRotationPoint(0.0F, 23.0F, -8.0F);

        this.rightPlate = new ModelRenderer(this, 66, 64);
        this.rightPlate.mirror = true;
        this.rightPlate.addBox(-8.0F, -12.0F, 0.0F, 1, 12, 32);
        this.rightPlate.setRotationPoint(0.0F, 23.0F, -8.0F);

        this.backPlate = new ModelRenderer(this, 0, 0);
        this.backPlate.mirror = true;
        this.backPlate.addBox(-8.0F, 0.0F, 0.0F, 16, 1, 32);
        this.backPlate.setRotationPoint(0.0F, 23.0F, -8.0F);

        this.topPlate = new ModelRenderer(this, 0, 0);
        this.topPlate.mirror = true;
        this.topPlate.addBox(-7.0F, -12.0F, 31.0F, 14, 12, 1);
        this.topPlate.setRotationPoint(0.0F, 23.0F, -8.0F);

        this.bottomPlate = new ModelRenderer(this, 0, 15);
        this.bottomPlate.mirror = true;
        this.bottomPlate.addBox(-7.0F, -11.0F, 0.0F, 14, 12, 1);
        this.bottomPlate.setRotationPoint(0.0F, 22.0F, -8.0F);

        this.leftLid = new ModelRenderer(this, 0, 33);
        this.leftLid.mirror = true;
        this.leftLid.addBox(0.0F, 0.0F, 0.0F, 7, 1, 30);
        this.leftLid.setRotationPoint(-7.0F, 11.0F, -7.0F);

        this.leftHandle = new ModelRenderer(this, 64, 0);
        this.leftHandle.mirror = true;
        this.leftHandle.addBox(5.5F, -0.5F, 15.0F, 1, 1, 4);
        this.leftLid.addChild(this.leftHandle);

        this.rightLid = new ModelRenderer(this, 74, 33);
        this.rightLid.mirror = true;
        this.rightLid.addBox(-7.0F, 0.0F, 0.0F, 7, 1, 30);
        this.rightLid.setRotationPoint(7.0F, 11.0F, -7.0F);

        this.rightHandle = new ModelRenderer(this, 74, 0);
        this.rightHandle.mirror = true;
        this.rightHandle.addBox(-6.5F, -0.5F, 15.0F, 1, 1, 4);
        this.rightLid.addChild(this.rightHandle);
    }

    public void render(float lidAngle, float scale) {
        this.leftLid.rotateAngleZ = -lidAngle * 1.4F;
        this.rightLid.rotateAngleZ = lidAngle * 1.4F;

        this.leftPlate.render(scale);
        this.rightPlate.render(scale);
        this.backPlate.render(scale);
        this.topPlate.render(scale);
        this.bottomPlate.render(scale);
        this.leftLid.render(scale);
        this.rightLid.render(scale);
    }
}
