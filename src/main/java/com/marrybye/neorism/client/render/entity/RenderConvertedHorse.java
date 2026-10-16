package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;

import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.entity.RenderHorse;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Renderer for converted horses with glowing red eye overlay pass.
 */
public class RenderConvertedHorse extends RenderHorse {

    private static final ResourceLocation OVERLAY =
        new ResourceLocation(Neorism.MODID, "textures/entity/overlay/converted_horse.png");

    public RenderConvertedHorse() {
        super(new ModelHorse(), 0.75F);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
        int base = super.shouldRenderPass(entity, pass, partialTickTime);
        if (base > 0) return base;

        if (pass == 1) {
            this.bindTexture(OVERLAY);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            return 1;
        }
        return -1;
    }
}
