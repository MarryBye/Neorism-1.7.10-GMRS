package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.common.entity.projectile.EntityQuarrel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Renderer for EntityQuarrel projectiles using custom quarrel and heavy quarrel textures.
 */
@SideOnly(Side.CLIENT)
public class RenderQuarrel extends RenderArrow {

    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("neorism", "textures/entity/quarrel/quarrel.png");
    private static final ResourceLocation TEXTURE_HEAVY = new ResourceLocation("neorism", "textures/entity/quarrel/heavy_quarrel.png");

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityQuarrel && ((EntityQuarrel) entity).isHeavy()) {
            return TEXTURE_HEAVY;
        }
        return TEXTURE_NORMAL;
    }
}
