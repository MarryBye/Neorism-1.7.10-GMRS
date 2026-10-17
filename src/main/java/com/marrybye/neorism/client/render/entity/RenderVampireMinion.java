package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.entity.minion.EntityVampireMinion;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Renderer for Vampire Minion entities with skin variations.
 */
public class RenderVampireMinion extends RenderBiped {

    private static final ResourceLocation[] SKINS = new ResourceLocation[7];

    static {
        for (int i = 1; i <= 6; ++i) {
            SKINS[i] = new ResourceLocation(Neorism.MODID, "textures/entity/minion/vampire/vampire" + i + "_slim.png");
        }
    }

    public RenderVampireMinion() {
        super(new ModelBiped(0.0F), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityVampireMinion) {
            int skin = ((EntityVampireMinion) entity).getSkinType();
            if (skin >= 1 && skin <= 6 && SKINS[skin] != null) {
                return SKINS[skin];
            }
        }
        return SKINS[1];
    }
}
