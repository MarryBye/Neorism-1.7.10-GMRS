package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.entity.vampire.EntityVampire;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Angelica-compatible renderer for basic vampires with multi-skin selection.
 */
public class RenderVampire extends RenderBiped {

    private static final ResourceLocation[] SKINS = new ResourceLocation[18];

    static {
        for (int i = 1; i <= 17; ++i) {
            SKINS[i] = new ResourceLocation(Neorism.MODID, "textures/entity/vampire/vampire" + i + ".png");
        }
    }

    public RenderVampire() {
        super(new ModelBiped(0.0F), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityVampire) {
            int skin = ((EntityVampire) entity).getSkinType();
            if (skin >= 1 && skin <= 17 && SKINS[skin] != null) {
                return SKINS[skin];
            }
        }
        return SKINS[1];
    }
}
