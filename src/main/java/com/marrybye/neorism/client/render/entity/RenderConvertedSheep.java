package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;

import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Renderer for converted sheep with wool layer and glowing red eye overlay pass.
 */
public class RenderConvertedSheep extends RenderSheep {

    private static final ResourceLocation OVERLAY =
        new ResourceLocation(Neorism.MODID, "textures/entity/overlay/converted_sheep.png");

    public RenderConvertedSheep() {
        super(new ModelSheep2(), new ModelSheep1(), 0.7F);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
        int base = super.shouldRenderPass((EntitySheep) entity, pass, partialTickTime);
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
