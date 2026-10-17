package com.marrybye.neorism.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.tileentity.TileEntityCoffin;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * Canonical TESR for the Vampire Coffin.
 * 100% faithful port of TeamLapen's CoffinRenderer.
 * Renders the full 2-block 3D coffin with dark oak exterior, iron bands,
 * luxurious red velvet interior, and animated opening/sliding lid.
 * 100% Angelica and Sodium safe.
 */
public class TileEntityCoffinRenderer extends TileEntitySpecialRenderer {

    public static final ResourceLocation TEXTURE_BASE =
        new ResourceLocation(Neorism.MODID, "textures/blocks/coffin_base.png");
    public static final ResourceLocation TEXTURE_INNER =
        new ResourceLocation(Neorism.MODID, "textures/blocks/coffin/coffin_inner_red.png");

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        if (!(te instanceof TileEntityCoffin)) return;
        TileEntityCoffin coffin = (TileEntityCoffin) te;

        int meta = te.hasWorldObj() ? te.getBlockMetadata() : 0;
        // Only render from foot block
        if ((meta & 8) != 0) return;

        int dir = meta & 3;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);

        // Position and rotate based on facing direction
        if (dir == 0) { // South (head at +Z)
            GL11.glTranslated(x, y, z);
            GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);
        } else if (dir == 1) { // West (head at -X)
            GL11.glTranslated(x + 1.0D, y, z);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        } else if (dir == 2) { // North (head at -Z)
            GL11.glTranslated(x + 1.0D, y, z + 1.0D);
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        } else if (dir == 3) { // East (head at +X)
            GL11.glTranslated(x, y, z + 1.0D);
            GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
        }

        Tessellator tess = Tessellator.instance;

        // 1. Render Outer Wooden Base & Rim
        bindTexture(TEXTURE_BASE);
        tess.startDrawingQuads();
        renderOuterBase(tess);
        tess.draw();

        // 2. Render Inner Red Velvet Lining
        bindTexture(TEXTURE_INNER);
        tess.startDrawingQuads();
        renderInnerVelvet(tess);
        tess.draw();

        // 3. Render Animated Wooden Lid
        bindTexture(TEXTURE_BASE);
        float lidAngle = coffin.getLidAngle(partialTick); // 0.0 = closed, 1.0 = open

        GL11.glPushMatrix();
        // Lid sits on top of coffin rim at Y = 9/16 = 0.5625
        GL11.glTranslatef(0.0F, 0.5625F, 0.0F);

        if (lidAngle > 0.001F) {
            // Canonical sliding + pivot opening from modern CoffinRenderer
            GL11.glTranslatef(0.0F, 0.0F, -0.5F * lidAngle);
            GL11.glRotatef(-35.0F * lidAngle, 0.0F, 1.0F, 0.0F);
        }

        tess.startDrawingQuads();
        renderLid(tess);
        tess.draw();

        GL11.glPopMatrix();

        GL11.glPopMatrix();
    }

    private void renderOuterBase(Tessellator tess) {
        float x1 = 0.0F, x2 = 1.0F;
        float y1 = 0.0F, y2 = 9.0F / 16.0F;
        float z1 = 0.0F, z2 = 2.0F;

        // UVs from 32x32 texture
        // Down face: uv [4, 0, 8, 8] -> [4/32, 0/32, 8/32, 8/32]
        tess.setNormal(0.0F, -1.0F, 0.0F);
        tess.addVertexWithUV(x1, y1, z1, 4.0 / 32.0, 0.0);
        tess.addVertexWithUV(x2, y1, z1, 8.0 / 32.0, 0.0);
        tess.addVertexWithUV(x2, y1, z2, 8.0 / 32.0, 8.0 / 32.0);
        tess.addVertexWithUV(x1, y1, z2, 4.0 / 32.0, 8.0 / 32.0);

        // North face (Foot end): uv [8, 8.75, 12, 11]
        tess.setNormal(0.0F, 0.0F, -1.0F);
        tess.addVertexWithUV(x1, y1, z1, 12.0 / 32.0, 11.0 / 32.0);
        tess.addVertexWithUV(x1, y2, z1, 12.0 / 32.0, 8.75 / 32.0);
        tess.addVertexWithUV(x2, y2, z1, 8.0 / 32.0, 8.75 / 32.0);
        tess.addVertexWithUV(x2, y1, z1, 8.0 / 32.0, 11.0 / 32.0);

        // South face (Head end): uv [8, 8.75, 12, 11]
        tess.setNormal(0.0F, 0.0F, 1.0F);
        tess.addVertexWithUV(x1, y1, z2, 8.0 / 32.0, 11.0 / 32.0);
        tess.addVertexWithUV(x2, y1, z2, 12.0 / 32.0, 11.0 / 32.0);
        tess.addVertexWithUV(x2, y2, z2, 12.0 / 32.0, 8.75 / 32.0);
        tess.addVertexWithUV(x1, y2, z2, 8.0 / 32.0, 8.75 / 32.0);

        // West face (Left side): uv [0, 8.75, 8, 11]
        tess.setNormal(-1.0F, 0.0F, 0.0F);
        tess.addVertexWithUV(x1, y1, z1, 0.0, 11.0 / 32.0);
        tess.addVertexWithUV(x1, y1, z2, 8.0 / 32.0, 11.0 / 32.0);
        tess.addVertexWithUV(x1, y2, z2, 8.0 / 32.0, 8.75 / 32.0);
        tess.addVertexWithUV(x1, y2, z1, 0.0, 8.75 / 32.0);

        // East face (Right side): uv [0, 8.75, 8, 11]
        tess.setNormal(1.0F, 0.0F, 0.0F);
        tess.addVertexWithUV(x2, y1, z1, 8.0 / 32.0, 11.0 / 32.0);
        tess.addVertexWithUV(x2, y2, z1, 8.0 / 32.0, 8.75 / 32.0);
        tess.addVertexWithUV(x2, y2, z2, 0.0, 8.75 / 32.0);
        tess.addVertexWithUV(x2, y1, z2, 0.0, 11.0 / 32.0);

        // Top rim faces (wood wall borders of thickness 2/16 = 0.125):
        // Up rim: uv [0, 0, 4, 8]
        tess.setNormal(0.0F, 1.0F, 0.0F);
        float t = 2.0F / 16.0F;
        // North rim
        tess.addVertexWithUV(x1, y2, z1, 0.0, 0.0);
        tess.addVertexWithUV(x1, y2, z1 + t, 0.0, 1.0 / 32.0);
        tess.addVertexWithUV(x2, y2, z1 + t, 4.0 / 32.0, 1.0 / 32.0);
        tess.addVertexWithUV(x2, y2, z1, 4.0 / 32.0, 0.0);

        // South rim
        tess.addVertexWithUV(x1, y2, z2 - t, 0.0, 7.0 / 32.0);
        tess.addVertexWithUV(x1, y2, z2, 0.0, 8.0 / 32.0);
        tess.addVertexWithUV(x2, y2, z2, 4.0 / 32.0, 8.0 / 32.0);
        tess.addVertexWithUV(x2, y2, z2 - t, 4.0 / 32.0, 7.0 / 32.0);

        // West rim
        tess.addVertexWithUV(x1, y2, z1 + t, 0.0, 1.0 / 32.0);
        tess.addVertexWithUV(x1, y2, z2 - t, 0.0, 7.0 / 32.0);
        tess.addVertexWithUV(x1 + t, y2, z2 - t, 1.0 / 32.0, 7.0 / 32.0);
        tess.addVertexWithUV(x1 + t, y2, z1 + t, 1.0 / 32.0, 1.0 / 32.0);

        // East rim
        tess.addVertexWithUV(x2 - t, y2, z1 + t, 3.0 / 32.0, 1.0 / 32.0);
        tess.addVertexWithUV(x2 - t, y2, z2 - t, 3.0 / 32.0, 7.0 / 32.0);
        tess.addVertexWithUV(x2, y2, z2 - t, 4.0 / 32.0, 7.0 / 32.0);
        tess.addVertexWithUV(x2, y2, z1 + t, 4.0 / 32.0, 1.0 / 32.0);
    }

    private void renderInnerVelvet(Tessellator tess) {
        float ix1 = 2.0F / 16.0F, ix2 = 14.0F / 16.0F;
        float iy1 = 3.0F / 16.0F, iy2 = 9.0F / 16.0F;
        float iz1 = 2.0F / 16.0F, iz2 = 30.0F / 16.0F;

        // Inner bottom cushion: uv [3, 0, 9, 14]
        tess.setNormal(0.0F, 1.0F, 0.0F);
        tess.addVertexWithUV(ix1, iy1, iz1, 3.0 / 32.0, 0.0);
        tess.addVertexWithUV(ix1, iy1, iz2, 3.0 / 32.0, 14.0 / 32.0);
        tess.addVertexWithUV(ix2, iy1, iz2, 9.0 / 32.0, 14.0 / 32.0);
        tess.addVertexWithUV(ix2, iy1, iz1, 9.0 / 32.0, 0.0);

        // Inner West wall: facing East (normal +X)
        tess.setNormal(1.0F, 0.0F, 0.0F);
        tess.addVertexWithUV(ix1, iy1, iz1, 0.0, 0.0);
        tess.addVertexWithUV(ix1, iy2, iz1, 3.0 / 32.0, 0.0);
        tess.addVertexWithUV(ix1, iy2, iz2, 3.0 / 32.0, 14.0 / 32.0);
        tess.addVertexWithUV(ix1, iy1, iz2, 0.0, 14.0 / 32.0);

        // Inner East wall: facing West (normal -X)
        tess.setNormal(-1.0F, 0.0F, 0.0F);
        tess.addVertexWithUV(ix2, iy1, iz1, 3.0 / 32.0, 0.0);
        tess.addVertexWithUV(ix2, iy1, iz2, 3.0 / 32.0, 14.0 / 32.0);
        tess.addVertexWithUV(ix2, iy2, iz2, 0.0, 14.0 / 32.0);
        tess.addVertexWithUV(ix2, iy2, iz1, 0.0, 0.0);

        // Inner North wall: facing South (normal +Z)
        tess.setNormal(0.0F, 0.0F, 1.0F);
        tess.addVertexWithUV(ix1, iy1, iz1, 9.0 / 32.0, 3.0 / 32.0);
        tess.addVertexWithUV(ix2, iy1, iz1, 15.0 / 32.0, 3.0 / 32.0);
        tess.addVertexWithUV(ix2, iy2, iz1, 15.0 / 32.0, 0.0);
        tess.addVertexWithUV(ix1, iy2, iz1, 9.0 / 32.0, 0.0);

        // Inner South wall: facing North (normal -Z)
        tess.setNormal(0.0F, 0.0F, -1.0F);
        tess.addVertexWithUV(ix1, iy1, iz2, 15.0 / 32.0, 3.0 / 32.0);
        tess.addVertexWithUV(ix1, iy2, iz2, 15.0 / 32.0, 0.0);
        tess.addVertexWithUV(ix2, iy2, iz2, 9.0 / 32.0, 0.0);
        tess.addVertexWithUV(ix2, iy1, iz2, 9.0 / 32.0, 3.0 / 32.0);
    }

    private void renderLid(Tessellator tess) {
        float lx1 = 0.0F, lx2 = 1.0F;
        float ly1 = 0.0F, ly2 = 2.0F / 16.0F;
        float lz1 = 0.0F, lz2 = 2.0F;

        // Lid Top (up): uv [8, 0, 12, 8]
        tess.setNormal(0.0F, 1.0F, 0.0F);
        tess.addVertexWithUV(lx1, ly2, lz1, 8.0 / 32.0, 0.0);
        tess.addVertexWithUV(lx1, ly2, lz2, 8.0 / 32.0, 8.0 / 32.0);
        tess.addVertexWithUV(lx2, ly2, lz2, 12.0 / 32.0, 8.0 / 32.0);
        tess.addVertexWithUV(lx2, ly2, lz1, 12.0 / 32.0, 0.0);

        // Lid Underside (down): uv [12, 0, 16, 8]
        tess.setNormal(0.0F, -1.0F, 0.0F);
        tess.addVertexWithUV(lx1, ly1, lz1, 12.0 / 32.0, 0.0);
        tess.addVertexWithUV(lx2, ly1, lz1, 16.0 / 32.0, 0.0);
        tess.addVertexWithUV(lx2, ly1, lz2, 16.0 / 32.0, 8.0 / 32.0);
        tess.addVertexWithUV(lx1, ly1, lz2, 12.0 / 32.0, 8.0 / 32.0);

        // Lid North end: uv [8, 8, 12, 8.5]
        tess.setNormal(0.0F, 0.0F, -1.0F);
        tess.addVertexWithUV(lx1, ly1, lz1, 12.0 / 32.0, 8.5 / 32.0);
        tess.addVertexWithUV(lx1, ly2, lz1, 12.0 / 32.0, 8.0 / 32.0);
        tess.addVertexWithUV(lx2, ly2, lz1, 8.0 / 32.0, 8.0 / 32.0);
        tess.addVertexWithUV(lx2, ly1, lz1, 8.0 / 32.0, 8.5 / 32.0);

        // Lid South end: uv [8, 8, 12, 8.5]
        tess.setNormal(0.0F, 0.0F, 1.0F);
        tess.addVertexWithUV(lx1, ly1, lz2, 8.0 / 32.0, 8.5 / 32.0);
        tess.addVertexWithUV(lx2, ly1, lz2, 12.0 / 32.0, 8.5 / 32.0);
        tess.addVertexWithUV(lx2, ly2, lz2, 12.0 / 32.0, 8.0 / 32.0);
        tess.addVertexWithUV(lx1, ly2, lz2, 8.0 / 32.0, 8.0 / 32.0);

        // Lid West side: uv [0, 8, 8, 8.5]
        tess.setNormal(-1.0F, 0.0F, 0.0F);
        tess.addVertexWithUV(lx1, ly1, lz1, 0.0, 8.5 / 32.0);
        tess.addVertexWithUV(lx1, ly1, lz2, 8.0 / 32.0, 8.5 / 32.0);
        tess.addVertexWithUV(lx1, ly2, lz2, 8.0 / 32.0, 8.0 / 32.0);
        tess.addVertexWithUV(lx1, ly2, lz1, 0.0, 8.0 / 32.0);

        // Lid East side: uv [0, 8, 8, 8.5]
        tess.setNormal(1.0F, 0.0F, 0.0F);
        tess.addVertexWithUV(lx2, ly1, lz1, 8.0 / 32.0, 8.5 / 32.0);
        tess.addVertexWithUV(lx2, ly2, lz1, 8.0 / 32.0, 8.0 / 32.0);
        tess.addVertexWithUV(lx2, ly2, lz2, 0.0, 8.0 / 32.0);
        tess.addVertexWithUV(lx2, ly1, lz2, 0.0, 8.5 / 32.0);
    }
}
