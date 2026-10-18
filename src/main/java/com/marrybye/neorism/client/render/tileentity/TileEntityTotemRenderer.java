package com.marrybye.neorism.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.tileentity.TileEntityTotem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * TESR for the Faction Totem crown.
 * Canonical port of TeamLapen's TotemRenderer:
 * - Renders the two-part vertical sky beacon beam (factionapi:textures/entity/totem_beam.png) during active raids:
 *   lower segment (0 .. progress * HEIGHT / 100) in attacking faction color,
 *   upper segment (offset .. HEIGHT) in defending faction color.
 * - When not under attack and controlled by a faction, renders the floating faction/village nameplate above the totem.
 * 100% Angelica and Sodium safe.
 */
public class TileEntityTotemRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation TOTEM_BEAM_TEXTURE = new ResourceLocation("factionapi", "textures/entity/totem_beam.png");
    private static final double HEIGHT = 100.0D;

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        if (!(te instanceof TileEntityTotem)) return;
        TileEntityTotem totem = (TileEntityTotem) te;
        if (totem.getWorldObj() == null) return;

        float textureScale = totem.shouldRenderBeam();
        int controllingId = totem.getControllingFactionId();

        if (textureScale > 0.0F) {
            int captureProgress = MathHelper.clamp_int(totem.getRaidProgress(), 0, 100);
            int attackingId = totem.getAttackingFactionId();

            float[] baseColor = getFactionRGB(controllingId);
            float[] captureColor = getFactionRGB(attackingId);

            double offset = 0.0D;
            long totalTime = totem.getWorldObj().getTotalWorldTime();

            if (captureProgress > 0) {
                offset = (captureProgress * HEIGHT) / 100.0D;
                renderBeamSegment(x, y, z, partialTick, textureScale, totalTime, 0.0D, offset,
                    captureColor[0], captureColor[1], captureColor[2]);
            }
            if (HEIGHT - offset > 0.0D) {
                renderBeamSegment(x, y, z, partialTick, textureScale, totalTime, offset, HEIGHT - offset,
                    baseColor[0], baseColor[1], baseColor[2]);
            }
        } else if (controllingId != TileEntityTotem.FACTION_NEUTRAL) {
            double distSq = totem.getDistanceFrom(
                RenderManager.instance.viewerPosX,
                RenderManager.instance.viewerPosY,
                RenderManager.instance.viewerPosZ
            );
            if (distSq <= 1024.0D) {
                String label = getFactionLabel(controllingId);
                renderFloatingNameTag(label, x + 0.5D, y + 1.35D, z + 0.5D);
            }
        }
    }

    private float[] getFactionRGB(int factionId) {
        if (factionId == TileEntityTotem.FACTION_VAMPIRE) {
            // Canonical Vampire faction color (dark crimson/purple-red)
            return new float[] { 0.85F, 0.10F, 0.15F };
        } else if (factionId == TileEntityTotem.FACTION_HUNTER) {
            // Canonical Hunter faction color (royal blue)
            return new float[] { 0.15F, 0.45F, 0.95F };
        }
        // Neutral white
        return new float[] { 0.95F, 0.95F, 0.95F };
    }

    private String getFactionLabel(int factionId) {
        if (factionId == TileEntityTotem.FACTION_VAMPIRE) {
            return EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("faction.neorism.vampire.plural");
        } else if (factionId == TileEntityTotem.FACTION_HUNTER) {
            return EnumChatFormatting.BLUE + StatCollector.translateToLocal("faction.neorism.hunter.plural");
        }
        return "";
    }

    private void renderBeamSegment(double x, double y, double z, float partialTick, float textureScale, long totalWorldTime,
                                   double yOffset, double segmentHeight, float r, float g, float b) {
        Tessellator tess = Tessellator.instance;
        this.bindTexture(TOTEM_BEAM_TEXTURE);

        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        OpenGlHelper.glBlendFunc(770, 1, 1, 0);

        float time = (float) totalWorldTime + partialTick;
        float texV = -time * 0.2F - (float) MathHelper.floor_float(-time * 0.1F);
        double angle = (double) time * 0.025D * -1.5D;

        double innerRadius = 0.2D * textureScale;
        double d1 = 0.5D + Math.cos(angle + 2.356194490192345D) * innerRadius;
        double d2 = 0.5D + Math.sin(angle + 2.356194490192345D) * innerRadius;
        double d3 = 0.5D + Math.cos(angle + (Math.PI / 4D)) * innerRadius;
        double d4 = 0.5D + Math.sin(angle + (Math.PI / 4D)) * innerRadius;
        double d5 = 0.5D + Math.cos(angle + 3.9269908169872414D) * innerRadius;
        double d6 = 0.5D + Math.sin(angle + 3.9269908169872414D) * innerRadius;
        double d7 = 0.5D + Math.cos(angle + 5.497787143782138D) * innerRadius;
        double d8 = 0.5D + Math.sin(angle + 5.497787143782138D) * innerRadius;

        double vMin = (double) (-1.0F + texV);
        double vMax = segmentHeight * (0.5D / innerRadius) + vMin;

        tess.startDrawingQuads();
        tess.setColorRGBA_F(r, g, b, 1.0F);
        tess.addVertexWithUV(x + d1, y + yOffset + segmentHeight, z + d2, 1.0D, vMax);
        tess.addVertexWithUV(x + d1, y + yOffset, z + d2, 1.0D, vMin);
        tess.addVertexWithUV(x + d3, y + yOffset, z + d4, 0.0D, vMin);
        tess.addVertexWithUV(x + d3, y + yOffset + segmentHeight, z + d4, 0.0D, vMax);

        tess.addVertexWithUV(x + d7, y + yOffset + segmentHeight, z + d8, 1.0D, vMax);
        tess.addVertexWithUV(x + d7, y + yOffset, z + d8, 1.0D, vMin);
        tess.addVertexWithUV(x + d5, y + yOffset, z + d6, 0.0D, vMin);
        tess.addVertexWithUV(x + d5, y + yOffset + segmentHeight, z + d6, 0.0D, vMax);

        tess.addVertexWithUV(x + d3, y + yOffset + segmentHeight, z + d4, 1.0D, vMax);
        tess.addVertexWithUV(x + d3, y + yOffset, z + d4, 1.0D, vMin);
        tess.addVertexWithUV(x + d7, y + yOffset, z + d8, 0.0D, vMin);
        tess.addVertexWithUV(x + d7, y + yOffset + segmentHeight, z + d8, 0.0D, vMax);

        tess.addVertexWithUV(x + d5, y + yOffset + segmentHeight, z + d6, 1.0D, vMax);
        tess.addVertexWithUV(x + d5, y + yOffset, z + d6, 1.0D, vMin);
        tess.addVertexWithUV(x + d1, y + yOffset, z + d2, 0.0D, vMin);
        tess.addVertexWithUV(x + d1, y + yOffset + segmentHeight, z + d2, 0.0D, vMax);
        tess.draw();

        // Outer translucent glow
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDepthMask(false);

        double outerMin = 0.5D - 0.25D * textureScale;
        double outerMax = 0.5D + 0.25D * textureScale;
        double vMinOuter = (double) (-1.0F + texV);
        double vMaxOuter = segmentHeight + vMinOuter;

        tess.startDrawingQuads();
        tess.setColorRGBA_F(r, g, b, 0.25F);
        tess.addVertexWithUV(x + outerMin, y + yOffset + segmentHeight, z + outerMin, 1.0D, vMaxOuter);
        tess.addVertexWithUV(x + outerMin, y + yOffset, z + outerMin, 1.0D, vMinOuter);
        tess.addVertexWithUV(x + outerMax, y + yOffset, z + outerMin, 0.0D, vMinOuter);
        tess.addVertexWithUV(x + outerMax, y + yOffset + segmentHeight, z + outerMin, 0.0D, vMaxOuter);

        tess.addVertexWithUV(x + outerMax, y + yOffset + segmentHeight, z + outerMax, 1.0D, vMaxOuter);
        tess.addVertexWithUV(x + outerMax, y + yOffset, z + outerMax, 1.0D, vMinOuter);
        tess.addVertexWithUV(x + outerMin, y + yOffset, z + outerMax, 0.0D, vMinOuter);
        tess.addVertexWithUV(x + outerMin, y + yOffset + segmentHeight, z + outerMax, 0.0D, vMaxOuter);

        tess.addVertexWithUV(x + outerMax, y + yOffset + segmentHeight, z + outerMin, 1.0D, vMaxOuter);
        tess.addVertexWithUV(x + outerMax, y + yOffset, z + outerMin, 1.0D, vMinOuter);
        tess.addVertexWithUV(x + outerMax, y + yOffset, z + outerMax, 0.0D, vMinOuter);
        tess.addVertexWithUV(x + outerMax, y + yOffset + segmentHeight, z + outerMax, 0.0D, vMaxOuter);

        tess.addVertexWithUV(x + outerMin, y + yOffset + segmentHeight, z + outerMax, 1.0D, vMaxOuter);
        tess.addVertexWithUV(x + outerMin, y + yOffset, z + outerMax, 1.0D, vMinOuter);
        tess.addVertexWithUV(x + outerMin, y + yOffset, z + outerMin, 0.0D, vMinOuter);
        tess.addVertexWithUV(x + outerMin, y + yOffset + segmentHeight, z + outerMin, 0.0D, vMaxOuter);
        tess.draw();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderFloatingNameTag(String text, double x, double y, double z) {
        if (text == null || text.isEmpty()) return;
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        if (fontrenderer == null) return;

        float scale = 0.02666667F;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        int halfWidth = fontrenderer.getStringWidth(text) / 2;
        tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
        tessellator.addVertex(-halfWidth - 1, -1, 0.0D);
        tessellator.addVertex(-halfWidth - 1, 8, 0.0D);
        tessellator.addVertex(halfWidth + 1, 8, 0.0D);
        tessellator.addVertex(halfWidth + 1, -1, 0.0D);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, 0, -1);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
