package com.marrybye.neorism.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.core.ModFluids;
import com.marrybye.neorism.common.tileentity.TileEntityAltarInspiration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

/**
 * TESR for the Altar of Inspiration.
 * Renders the visible blood pool rising/falling inside the altar basin.
 * Ported from TeamLapen's AltarInspirationRenderer.java.
 */
@SideOnly(Side.CLIENT)
public class TileEntityAltarInspirationRenderer extends TileEntitySpecialRenderer {

    // Basin boundaries in block space (4/16 to 12/16)
    private static final float MIN_X = 0.255F;
    private static final float MAX_X = 0.745F;
    private static final float MIN_Z = 0.255F;
    private static final float MAX_Z = 0.745F;
    private static final float MIN_Y = 0.065F; // Just above bottom 1/16
    private static final float MAX_Y = 0.720F; // Below rim 12/16

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        if (!(te instanceof TileEntityAltarInspiration)) return;
        TileEntityAltarInspiration altar = (TileEntityAltarInspiration) te;

        int blood = altar.getBloodAmount();
        if (blood <= 0) return;

        float ratio = Math.max(0.0F, Math.min(1.0F, (float) blood / (float) altar.getCapacity()));
        float fluidY = MIN_Y + (MAX_Y - MIN_Y) * ratio;

        this.bindTexture(TextureMap.locationBlocksTexture);

        IIcon icon = ModFluids.fluidBlood.getStillIcon();
        if (icon == null) return;

        float uMin = icon.getMinU();
        float uMax = icon.getMaxU();
        float vMin = icon.getMinV();
        float vMax = icon.getMaxV();

        // Adjust UV to the basin footprint
        float u1 = uMin + (uMax - uMin) * MIN_X;
        float u2 = uMin + (uMax - uMin) * MAX_X;
        float v1 = vMin + (vMax - vMin) * MIN_Z;
        float v2 = vMin + (vMax - vMin) * MAX_Z;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        if (altar.isRunning()) {
            GL11.glDisable(GL11.GL_LIGHTING);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        }

        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setColorRGBA_F(0.9F, 0.05F, 0.05F, 0.95F);

        // Top fluid surface
        tess.addVertexWithUV(MIN_X, fluidY, MAX_Z, u1, v2);
        tess.addVertexWithUV(MAX_X, fluidY, MAX_Z, u2, v2);
        tess.addVertexWithUV(MAX_X, fluidY, MIN_Z, u2, v1);
        tess.addVertexWithUV(MIN_X, fluidY, MIN_Z, u1, v1);

        tess.draw();

        if (altar.isRunning()) {
            GL11.glEnable(GL11.GL_LIGHTING);
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
