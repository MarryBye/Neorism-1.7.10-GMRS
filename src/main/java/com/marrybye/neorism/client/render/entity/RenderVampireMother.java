package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.client.model.ModelBaron;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Angelica-compatible renderer for Vampire Mother boss encounter.
 */
public class RenderVampireMother extends RenderBiped64 {

    private static final ResourceLocation TEXTURE =
        new ResourceLocation(Neorism.MODID, "textures/entity/baron/baroness_enraged.png");

    public RenderVampireMother() {
        this(new ModelBaron(true));
    }

    private RenderVampireMother(ModelBaron slimModel) {
        super(slimModel, slimModel, 0.65F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTickTime) {
        super.preRenderCallback(entity, partialTickTime);
        GL11.glScalef(1.2F, 1.2F, 1.2F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}
