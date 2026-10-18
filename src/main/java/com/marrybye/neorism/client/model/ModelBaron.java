package com.marrybye.neorism.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelRenderer;

/**
 * Canonical port of TeamLapen's BaronModel and BaronessModel (64x64 with claws at texOffs 24, 0).
 */
@SideOnly(Side.CLIENT)
public class ModelBaron extends ModelBiped64 {

    public final ModelRenderer clawsRight;
    public final ModelRenderer clawsLeft;

    public ModelBaron(boolean baroness) {
        super(0.0F, baroness);

        int armWidth = baroness ? 3 : 4;
        this.clawsRight = new ModelRenderer(this, 24, 0);
        this.clawsRight.addBox(baroness ? -2.0F : -3.0F, 9.0F, -2.0F, armWidth, 3, 4, 0.0F);
        this.bipedRightArm.addChild(this.clawsRight);

        this.clawsLeft = new ModelRenderer(this, 24, 0);
        this.clawsLeft.addBox(-1.0F, 9.0F, -2.0F, armWidth, 3, 4, 0.0F);
        this.bipedLeftArm.addChild(this.clawsLeft);
    }
}
