package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.client.model.ModelBiped64;
import com.marrybye.neorism.client.model.ModelBipedArmor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

/**
 * Shared Angelica-compatible base renderer for 64x64 humanoid NPCs supporting wide (4px) and slim (3px) arms,
 * slim-fitted 64x32 armor pass models, and canonical slim hand held-item translation.
 */
@SideOnly(Side.CLIENT)
public abstract class RenderBiped64 extends RenderBiped {

    protected final ModelBiped wideModel;
    protected final ModelBiped slimModel;

    protected ModelBiped wideArmorChest;
    protected ModelBiped wideArmorLegs;
    protected ModelBiped slimArmorChest;
    protected ModelBiped slimArmorLegs;

    public RenderBiped64(ModelBiped wideModel, ModelBiped slimModel, float shadowSize) {
        super(wideModel, shadowSize);
        this.wideModel = wideModel;
        this.slimModel = (slimModel != null) ? slimModel : wideModel;
    }

    @Override
    protected void func_82421_b() {
        this.wideArmorChest = new ModelBipedArmor(1.0F, false);
        this.wideArmorLegs = new ModelBipedArmor(0.5F, false);
        this.slimArmorChest = new ModelBipedArmor(1.0F, true);
        this.slimArmorLegs = new ModelBipedArmor(0.5F, true);
        this.field_82423_g = this.wideArmorChest;
        this.field_82425_h = this.wideArmorLegs;
    }

    protected boolean isSlim(EntityLiving entity) {
        if (this.wideModel == this.slimModel) {
            return (this.wideModel instanceof ModelBiped64) && ((ModelBiped64) this.wideModel).slim;
        }
        ResourceLocation tex = this.getEntityTexture((net.minecraft.entity.Entity) entity);
        return tex != null && tex.getResourcePath().endsWith("_slim.png");
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        boolean slim = isSlim(entity);
        ModelBiped active = slim ? this.slimModel : this.wideModel;
        this.mainModel = active;
        this.modelBipedMain = active;
        this.field_82423_g = slim ? this.slimArmorChest : this.wideArmorChest;
        this.field_82425_h = slim ? this.slimArmorLegs : this.wideArmorLegs;
        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected void renderEquippedItems(EntityLiving entity, float partialTicks) {
        // Canonical ClothedModel.translateToHand: slim right arm center is 0.5px (0.03125F) inward
        boolean adjustSlim = (this.modelBipedMain instanceof ModelBiped64) && ((ModelBiped64) this.modelBipedMain).slim;
        if (adjustSlim) {
            this.modelBipedMain.bipedRightArm.rotationPointX += 0.5F;
        }
        try {
            super.renderEquippedItems(entity, partialTicks);
        } finally {
            if (adjustSlim) {
                this.modelBipedMain.bipedRightArm.rotationPointX -= 0.5F;
            }
        }
    }
}
