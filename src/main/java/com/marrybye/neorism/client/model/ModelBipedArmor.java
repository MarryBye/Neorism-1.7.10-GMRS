package com.marrybye.neorism.client.model;

import com.marrybye.neorism.common.items.ItemCrossbow;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * 64x32 humanoid armor model supporting both wide (4px) and slim (3px) arms,
 * plus crossbow aiming pose alignment for NPCs.
 */
@SideOnly(Side.CLIENT)
public class ModelBipedArmor extends ModelBiped {

    public final boolean slim;

    public ModelBipedArmor(float modelSize, boolean slim) {
        super(modelSize, 0.0F, 64, 32);
        this.slim = slim;

        if (slim) {
            this.boxList.remove(this.bipedRightArm);
            this.boxList.remove(this.bipedLeftArm);

            this.bipedRightArm = new ModelRenderer(this, 40, 16);
            this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);

            this.bipedLeftArm = new ModelRenderer(this, 40, 16);
            this.bipedLeftArm.mirror = true;
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {
            ItemStack held = ((EntityLivingBase) entity).getHeldItem();
            this.aimedBow = (held != null && held.getItem() instanceof ItemCrossbow);
        }
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
    }
}
