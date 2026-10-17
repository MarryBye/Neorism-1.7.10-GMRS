package com.marrybye.neorism.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.tileentity.TileEntityTotem;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

/**
 * TESR for the Faction Totem crown.
 * Canonical port of TeamLapen's TotemRenderer.
 * Renders the vertical sky beacon beam with faction alignment colors and raid pulsation.
 * 100% Angelica and Sodium safe.
 */
public class TileEntityTotemRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("textures/entity/beacon_beam.png");

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        if (!(te instanceof TileEntityTotem)) return;
        TileEntityTotem totem = (TileEntityTotem) te;

        int factionId = totem.getControllingFactionId();
        if (factionId == TileEntityTotem.FACTION_NEUTRAL && !totem.isRaidActive()) {
            return;
        }

        float r = 0.8F, g = 0.8F, b = 0.8F;
        if (factionId == TileEntityTotem.FACTION_VAMPIRE) {
            r = 0.95F; g = 0.15F; b = 0.15F;
        } else if (factionId == TileEntityTotem.FACTION_HUNTER) {
            r = 0.2F; g = 0.55F; b = 1.0F;
        }

        if (totem.isRaidActive()) {
            int attackingId = totem.getAttackingFactionId();
            float pulse = (float) Math.sin((totem.getWorldObj().getTotalWorldTime() + partialTick) * 0.2F) * 0.5F + 0.5F;
            if (attackingId == TileEntityTotem.FACTION_VAMPIRE) {
                r = MathHelper.clamp_float(r * (1.0F - pulse) + 0.95F * pulse, 0.0F, 1.0F);
                g = MathHelper.clamp_float(g * (1.0F - pulse) + 0.15F * pulse, 0.0F, 1.0F);
                b = MathHelper.clamp_float(b * (1.0F - pulse) + 0.15F * pulse, 0.0F, 1.0F);
            } else if (attackingId == TileEntityTotem.FACTION_HUNTER) {
                r = MathHelper.clamp_float(r * (1.0F - pulse) + 0.2F * pulse, 0.0F, 1.0F);
                g = MathHelper.clamp_float(g * (1.0F - pulse) + 0.55F * pulse, 0.0F, 1.0F);
                b = MathHelper.clamp_float(b * (1.0F - pulse) + 1.0F * pulse, 0.0F, 1.0F);
            }
        }

        renderBeam(x, y, z, partialTick, totem.getWorldObj().getTotalWorldTime(), r, g, b, 0.7F);
    }

    private void renderBeam(double x, double y, double z, float partialTick, long totalWorldTime, float r, float g, float b, float alpha) {
        Tessellator tess = Tessellator.instance;
        this.bindTexture(BEAM_TEXTURE);

        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 1, 0);
        GL11.glDepthMask(false);

        float animTime = (float) totalWorldTime + partialTick;
        float vOffset = -animTime * 0.2F - (float) MathHelper.floor_float(-animTime * 0.1F);

        double radius = 0.2D;
        double height = 64.0D;

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y + 0.85D, z + 0.5D);

        tess.startDrawingQuads();
        tess.setColorRGBA_F(r, g, b, alpha);

        // 4 quad faces
        tess.addVertexWithUV(-radius, height, -radius, 1.0D, height + vOffset);
        tess.addVertexWithUV(-radius, 0.0D, -radius, 1.0D, vOffset);
        tess.addVertexWithUV(radius, 0.0D, -radius, 0.0D, vOffset);
        tess.addVertexWithUV(radius, height, -radius, 0.0D, height + vOffset);

        tess.addVertexWithUV(radius, height, radius, 1.0D, height + vOffset);
        tess.addVertexWithUV(radius, 0.0D, radius, 1.0D, vOffset);
        tess.addVertexWithUV(-radius, 0.0D, radius, 0.0D, vOffset);
        tess.addVertexWithUV(-radius, height, radius, 0.0D, height + vOffset);

        tess.addVertexWithUV(-radius, height, radius, 1.0D, height + vOffset);
        tess.addVertexWithUV(-radius, 0.0D, radius, 1.0D, vOffset);
        tess.addVertexWithUV(-radius, 0.0D, -radius, 0.0D, vOffset);
        tess.addVertexWithUV(-radius, height, -radius, 0.0D, height + vOffset);

        tess.addVertexWithUV(radius, height, -radius, 1.0D, height + vOffset);
        tess.addVertexWithUV(radius, 0.0D, -radius, 1.0D, vOffset);
        tess.addVertexWithUV(radius, 0.0D, radius, 0.0D, vOffset);
        tess.addVertexWithUV(radius, height, radius, 0.0D, height + vOffset);

        tess.draw();

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
