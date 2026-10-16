package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.entity.vampire.EntityDracula;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Angelica-compatible renderer for Lord Dracula with dynamic phase textures.
 */
public class RenderDracula extends RenderBiped {

    private static final ResourceLocation PHASE_1 =
        new ResourceLocation(Neorism.MODID, "textures/entity/dracula/phase1.png");
    private static final ResourceLocation PHASE_2 =
        new ResourceLocation(Neorism.MODID, "textures/entity/dracula/phase2.png");
    private static final ResourceLocation PHASE_3 =
        new ResourceLocation(Neorism.MODID, "textures/entity/dracula/phase3.png");

    public RenderDracula() {
        super(new ModelBiped(0.0F), 0.7F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTickTime) {
        super.preRenderCallback(entity, partialTickTime);
        GL11.glScalef(1.25F, 1.25F, 1.25F);

        if (entity instanceof EntityDracula && ((EntityDracula) entity).getPhase() == 3) {
            GL11.glColor4f(1.0F, 0.5F, 0.5F, 1.0F);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityDracula) {
            int phase = ((EntityDracula) entity).getPhase();
            if (phase == 2) return PHASE_2;
            if (phase == 3) return PHASE_3;
        }
        return PHASE_1;
    }
}
