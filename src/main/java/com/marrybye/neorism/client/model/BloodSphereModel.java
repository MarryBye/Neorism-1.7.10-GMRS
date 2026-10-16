package com.marrybye.neorism.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * 3D Model for the floating blood sphere above the Altar of Infusion.
 * Ported from TeamLapen's BloodSphereModel.java.
 */
public class BloodSphereModel extends ModelBase {

    private final ModelRenderer sphere;

    public BloodSphereModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.sphere = new ModelRenderer(this, 0, 0);
        this.sphere.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
        this.sphere.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    public void render(float scale) {
        this.sphere.render(scale);
    }
}
