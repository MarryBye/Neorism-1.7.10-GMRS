package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;

import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Renderer for converted villagers with glowing red vampiric eye overlay pass.
 */
public class RenderConvertedVillager extends RenderVillager {

    private static final ResourceLocation OVERLAY =
        new ResourceLocation(Neorism.MODID, "textures/entity/overlay/converted_villager.png");

    public RenderConvertedVillager() {
        super();
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
        if (pass == 0) {
            this.bindTexture(OVERLAY);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            return 1;
        }
        return -1;
    }
}
