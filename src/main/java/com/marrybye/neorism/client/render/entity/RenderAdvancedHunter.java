package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.client.model.ModelBiped64;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Angelica-compatible renderer for advanced hunter marksmen with 64x64 skins.
 */
public class RenderAdvancedHunter extends RenderBiped {

    private static final ResourceLocation TEXTURE =
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/hunter.png");

    public RenderAdvancedHunter() {
        super(new ModelBiped64(0.0F), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}
