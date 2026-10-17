package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.client.model.ModelBiped64;
import com.marrybye.neorism.common.entity.minion.EntityVampireMinion;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Renderer for Vampire Minion entities with 64x64 skin variations.
 */
public class RenderVampireMinion extends RenderBiped {

    private static final ResourceLocation[] SKINS = new ResourceLocation[] {
        new ResourceLocation(Neorism.MODID, "textures/entity/minion/vampire/vampire1_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/minion/vampire/vampire2_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/minion/vampire/vampire3_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/minion/vampire/vampire4_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/minion/vampire/vampire5_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/minion/vampire/vampire6_slim.png")
    };

    public RenderVampireMinion() {
        super(new ModelBiped64(0.0F), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityVampireMinion) {
            int skin = ((EntityVampireMinion) entity).getSkinType();
            int idx = Math.abs(skin) % SKINS.length;
            return SKINS[idx];
        }
        return SKINS[0];
    }
}
