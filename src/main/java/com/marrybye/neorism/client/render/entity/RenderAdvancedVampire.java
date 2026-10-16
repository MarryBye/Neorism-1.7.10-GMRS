package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Angelica-compatible renderer for advanced vampires.
 */
public class RenderAdvancedVampire extends RenderBiped {

    private static final ResourceLocation TEXTURE =
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/vampire.png");

    public RenderAdvancedVampire() {
        super(new ModelBiped(0.0F), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}
