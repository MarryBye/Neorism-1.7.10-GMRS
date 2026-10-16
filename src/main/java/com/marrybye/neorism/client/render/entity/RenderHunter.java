package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.entity.hunter.EntityHunter;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Angelica-compatible renderer for hunter patrols with skin variants.
 */
public class RenderHunter extends RenderBiped {

    private static final ResourceLocation[] SKINS = new ResourceLocation[10];

    static {
        for (int i = 1; i <= 9; ++i) {
            SKINS[i] = new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter" + i + ".png");
        }
    }

    public RenderHunter() {
        super(new ModelBiped(0.0F), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityHunter) {
            int skin = ((EntityHunter) entity).getSkinType();
            if (skin >= 1 && skin <= 9 && SKINS[skin] != null) {
                return SKINS[skin];
            }
        }
        return SKINS[1];
    }
}
