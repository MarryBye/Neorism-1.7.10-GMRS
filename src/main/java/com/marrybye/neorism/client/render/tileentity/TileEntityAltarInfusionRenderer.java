package com.marrybye.neorism.client.render.tileentity;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.client.model.BloodSphereModel;
import com.marrybye.neorism.common.tileentity.TileEntityAltarInfusion;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

/**
 * TESR for the Altar of Infusion.
 * Renders the floating rotating blood sphere and ritual energy beams connecting tips and player.
 * Ported from TeamLapen's AltarInfusionRenderer.java.
 */
public class TileEntityAltarInfusionRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation SPHERE_TEXTURE = new ResourceLocation("neorism", "textures/entity/blood_sphere.png");
    private static final ResourceLocation INFUSION_BEAM_LOCATION = new ResourceLocation("neorism", "textures/entity/infusion_beam.png");

    private static final int BEAM_SIDES = 8;
    private static final float BEAM_NEAR_RADIUS = 0.2F;

    private final BloodSphereModel sphereModel = new BloodSphereModel();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        if (!(te instanceof TileEntityAltarInfusion)) return;
        TileEntityAltarInfusion altar = (TileEntityAltarInfusion) te;

        renderSphere(altar, x, y, z, partialTick);
        renderBeams(altar, x, y, z, partialTick);
    }

    private void renderSphere(TileEntityAltarInfusion altar, double x, double y, double z, float partialTick) {
        GL11.glPushMatrix();

        float bobbing = altar.verticalOffset;
        GL11.glTranslated(x + 0.5D, y + 0.75D + bobbing, z + 0.5D);

        float rotDelta = altar.rotation - altar.prevRotation;
        while (rotDelta >= (float) Math.PI) {
            rotDelta -= (float) (Math.PI * 2);
        }
        while (rotDelta < (float) -Math.PI) {
            rotDelta += (float) (Math.PI * 2);
        }
        float interpolatedRot = altar.prevRotation + rotDelta * partialTick;
        GL11.glRotatef(-interpolatedRot * (180.0F / (float) Math.PI), 0.0F, 1.0F, 0.0F);

        this.bindTexture(SPHERE_TEXTURE);

        // Render solid / translucent emissive sphere
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);

        this.sphereModel.render(0.0625F);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void renderBeams(TileEntityAltarInfusion altar, double x, double y, double z, float partialTick) {
        TileEntityAltarInfusion.Phase phase = altar.getCurrentPhase();
        if (phase != TileEntityAltarInfusion.Phase.BEAM_CONNECT && phase != TileEntityAltarInfusion.Phase.BEAM_PLAYER) return;

        double centerX = altar.xCoord + 0.5D;
        double centerY = altar.yCoord + 3.0D;
        double centerZ = altar.zCoord + 0.5D;
        float textureOffset = -(altar.getRunTime() + partialTick);

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y + 3.0D, z + 0.5D);

        List<ChunkCoordinates> tips = altar.getTips();
        if (tips != null) {
            for (ChunkCoordinates tip : tips) {
                float dx = (float) (tip.posX + 0.5D - centerX);
                float dy = (float) (tip.posY + 0.5D - centerY);
                float dz = (float) (tip.posZ + 0.5D - centerZ);
                drawSingleBeam(textureOffset, dx, dy, dz);
            }
        }

        if (phase == TileEntityAltarInfusion.Phase.BEAM_PLAYER) {
            EntityPlayer player = altar.getPlayer();
            if (player != null) {
                float dx = (float) (player.posX - centerX);
                float dy = (float) (player.posY + 1.2D - centerY);
                float dz = (float) (player.posZ - centerZ);
                drawSingleBeam(textureOffset, dx, dy, dz);
            }
        }

        GL11.glPopMatrix();
    }

    private void drawSingleBeam(float textureOffset, float dx, float dy, float dz) {
        float distFlat = MathHelper.sqrt_float(dx * dx + dz * dz);
        float dist = MathHelper.sqrt_float(dx * dx + dy * dy + dz * dz);
        if (dist < 0.01F) return;

        this.bindTexture(INFUSION_BEAM_LOCATION);

        float uvScrollV = textureOffset * 0.05F;
        float uvScrollVFar = dist / 32.0F + textureOffset * 0.05F;

        GL11.glPushMatrix();
        GL11.glRotatef((float) (-Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef((float) (-Math.atan2(distFlat, dy) * 180.0D / Math.PI) - 90.0F, 1.0F, 0.0F, 0.0F);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);

        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setBrightness(0xF000F0);

        float prevSin = 0.0F;
        float prevCos = BEAM_NEAR_RADIUS;
        float prevU = 0.0F;

        for (int side = 1; side <= BEAM_SIDES; side++) {
            float angle = side * ((float) Math.PI * 2.0F) / BEAM_SIDES;
            float sin = MathHelper.sin(angle) * BEAM_NEAR_RADIUS;
            float cos = MathHelper.cos(angle) * BEAM_NEAR_RADIUS;
            float u = (float) side / (float) BEAM_SIDES;

            tess.setColorRGBA(75, 0, 0, 220);
            tess.addVertexWithUV(prevSin, prevCos, 0.0D, prevU, uvScrollV);

            tess.setColorRGBA(255, 30, 30, 220);
            tess.addVertexWithUV(prevSin * 0.5D, prevCos * 0.5D, dist, prevU, uvScrollVFar);

            tess.setColorRGBA(255, 30, 30, 220);
            tess.addVertexWithUV(sin * 0.5D, cos * 0.5D, dist, u, uvScrollVFar);

            tess.setColorRGBA(75, 0, 0, 220);
            tess.addVertexWithUV(sin, cos, 0.0D, u, uvScrollV);

            prevSin = sin;
            prevCos = cos;
            prevU = u;
        }

        tess.draw();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
