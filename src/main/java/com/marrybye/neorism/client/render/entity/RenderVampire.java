package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.client.model.ModelBiped64;
import com.marrybye.neorism.common.entity.vampire.EntityVampire;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Angelica-compatible renderer for basic vampires with multi-skin selection in 64x64 format.
 */
public class RenderVampire extends RenderBiped {

    private static final ResourceLocation[] SKINS = new ResourceLocation[] {
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire1.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire3.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire4.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire5.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire6.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire8.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire9.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire10.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire11.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire12.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire13.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire14.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire15.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire16.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire17.png")
    };

    public RenderVampire() {
        super(new ModelBiped64(0.0F), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityVampire) {
            int skin = ((EntityVampire) entity).getSkinType();
            int idx = Math.abs(skin) % SKINS.length;
            return SKINS[idx];
        }
        return SKINS[0];
    }
}
