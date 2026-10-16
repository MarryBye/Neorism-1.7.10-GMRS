package com.marrybye.neorism.client.render.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.marrybye.neorism.common.core.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

/**
 * Client renderer for EntityDarkBloodProjectile.
 */
@SideOnly(Side.CLIENT)
public class RenderDarkBloodProjectile extends Render {

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTick) {
        IIcon icon = ModItems.pureBlood != null ? ModItems.pureBlood.getIconFromDamage(0) : null;
        if (icon == null) return;

        bindEntityTexture(entity);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.4F, 0.4F, 0.4F);
        GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        Tessellator tessellator = Tessellator.instance;
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.setColorRGBA_F(0.6F, 0.0F, 0.1F, 0.9F);
        tessellator.addVertexWithUV(-0.5D, -0.25D, 0.0D, minU, maxV);
        tessellator.addVertexWithUV(0.5D, -0.25D, 0.0D, maxU, maxV);
        tessellator.addVertexWithUV(0.5D, 0.75D, 0.0D, maxU, minV);
        tessellator.addVertexWithUV(-0.5D, 0.75D, 0.0D, minU, minV);
        tessellator.draw();

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.locationItemsTexture;
    }
}
