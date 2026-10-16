package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaron;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaroness;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Angelica-compatible renderer for Vampire Baron and Baroness minibosses.
 */
public class RenderVampireBaron extends RenderBiped {

    private static final ResourceLocation TEXTURE_BARON =
        new ResourceLocation(Neorism.MODID, "textures/entity/baron/baron.png");
    private static final ResourceLocation TEXTURE_BARONESS =
        new ResourceLocation(Neorism.MODID, "textures/entity/baron/baroness.png");

    public RenderVampireBaron() {
        super(new ModelBiped(0.0F), 0.6F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTickTime) {
        super.preRenderCallback(entity, partialTickTime);
        // Slightly larger than standard biped
        GL11.glScalef(1.15F, 1.15F, 1.15F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityVampireBaroness) {
            return TEXTURE_BARONESS;
        }
        return TEXTURE_BARON;
    }
}
