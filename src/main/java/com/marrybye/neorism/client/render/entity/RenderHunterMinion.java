package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.client.model.ModelBiped64;
import com.marrybye.neorism.common.entity.minion.EntityHunterMinion;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Renderer for Hunter Minion entities with 64x64 skin variations.
 */
public class RenderHunterMinion extends RenderBiped {

    private static final ResourceLocation[] SKINS = new ResourceLocation[] {
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter1.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter2.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter3.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter4.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter5_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter6_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter7_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter8_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter9.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter10.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter11.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter12_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter13.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter14.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter15.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter16.png")
    };

    public RenderHunterMinion() {
        super(new ModelBiped64(0.0F), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityHunterMinion) {
            int skin = ((EntityHunterMinion) entity).getSkinType();
            int idx = Math.abs(skin) % SKINS.length;
            return SKINS[idx];
        }
        return SKINS[0];
    }
}
