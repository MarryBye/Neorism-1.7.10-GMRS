package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.entity.minion.EntityHunterMinion;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Renderer for Hunter Minion entities with skin variations.
 */
public class RenderHunterMinion extends RenderBiped {

    private static final ResourceLocation[] SKINS = new ResourceLocation[17];

    static {
        for (int i = 1; i <= 16; ++i) {
            SKINS[i] = new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter" + i + ".png");
        }
    }

    public RenderHunterMinion() {
        super(new ModelBiped(0.0F), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityHunterMinion) {
            int skin = ((EntityHunterMinion) entity).getSkinType();
            if (skin >= 1 && skin <= 16 && SKINS[skin] != null) {
                return SKINS[skin];
            }
        }
        return SKINS[1];
    }
}
