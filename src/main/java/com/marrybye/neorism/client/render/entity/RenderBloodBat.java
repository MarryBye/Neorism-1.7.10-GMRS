package com.marrybye.neorism.client.render.entity;

import net.minecraft.client.model.ModelBat;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Angelica-compatible renderer for hostile Blood Bats with blood-red crimson tinting.
 */
public class RenderBloodBat extends RenderLiving {

    private static final ResourceLocation BAT_TEXTURE = new ResourceLocation("textures/entity/bat.png");

    public RenderBloodBat() {
        super(new ModelBat(), 0.35F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTickTime) {
        super.preRenderCallback(entity, partialTickTime);
        GL11.glScalef(0.65F, 0.65F, 0.65F);
        // Blood bat crimson hue
        GL11.glColor4f(0.85F, 0.2F, 0.2F, 1.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return BAT_TEXTURE;
    }
}
