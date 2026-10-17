package com.marrybye.neorism.client.gui.skills;

import java.util.*;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.skills.SkillHandler;
import com.marrybye.neorism.common.skills.SkillRegistry;
import com.marrybye.neorism.common.skills.SkillSegment;
import com.marrybye.neorism.common.skills.SkillTree;
import com.marrybye.neorism.common.skills.SkillTreeLayout;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * Controller for a single Skill Tree tab canvas (panning, background, DAG nodes and connections).
 */
@SideOnly(Side.CLIENT)
public class SkillsTabComponent {

    private final SkillTree tree;
    private final SkillHandler skillHandler;
    private final List<SkillSegmentComponent> components = new ArrayList<SkillSegmentComponent>();
    private final List<SkillSegmentConnection> connections;
    private final SkillTreeLayout layout;

    private float scrollX = 0;
    private float scrollY = 20;

    public SkillsTabComponent(SkillTree tree, SkillHandler skillHandler) {
        this.tree = tree;
        this.skillHandler = skillHandler;
        this.layout = SkillRegistry.getGraph().getLayout(tree.getTreeId());

        Map<String, SkillSegmentComponent> compMap = new HashMap<String, SkillSegmentComponent>();
        for (ISkillSegment iseg : tree.getAllSegments()) {
            SkillSegment seg = (SkillSegment) iseg;
            SkillSegmentComponent comp = new SkillSegmentComponent(seg, skillHandler);
            components.add(comp);
            compMap.put(seg.getId(), comp);
        }

        // Link parent and child components
        for (SkillSegmentComponent comp : components) {
            for (ISkillSegment pSeg : comp.getSegment().getParents()) {
                SkillSegmentComponent pComp = compMap.get(pSeg.getId());
                if (pComp != null) {
                    comp.getParents().add(pComp);
                    pComp.getChildren().add(comp);
                }
            }
        }

        this.connections = SkillSegmentConnection.buildConnections(components);
        clampScroll(234, 173);
    }

    public SkillTree getTree() {
        return tree;
    }

    public String getTitle() {
        return StatCollector.translateToLocal(tree.getTitleTranslationKey());
    }

    public ItemStack getDisplayStack() {
        return tree.getDisplayStack();
    }

    public void scroll(float dx, float dy, int viewW, int viewH) {
        this.scrollX += dx;
        this.scrollY += dy;
        clampScroll(viewW, viewH);
    }

    private void clampScroll(int viewW, int viewH) {
        int halfW = viewW / 2;
        int minAllowedX = -100 - halfW;
        int maxAllowedX = 100 + halfW;
        if (layout != null) {
            minAllowedX = -layout.getMaxX() - 40 + halfW;
            maxAllowedX = -layout.getMinX() + 40 + halfW;
        }
        scrollX = MathHelper.clamp_float(scrollX, Math.min(minAllowedX, maxAllowedX), Math.max(minAllowedX, maxAllowedX));
        scrollY = MathHelper.clamp_float(scrollY, -300, 60);
    }

    public void drawContents(int guiLeft, int guiTop, int viewX, int viewY, int viewW, int viewH,
                             int mouseX, int mouseY, float holdProgress, SkillSegmentComponent holdingComp) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int scale = sr.getScaleFactor();

        int sx = (guiLeft + viewX) * scale;
        int sy = mc.displayHeight - (guiTop + viewY + viewH) * scale;
        int sw = viewW * scale;
        int sh = viewH * scale;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(sx, sy, sw, sh);

        // 1. Draw tiled background inside canvas
        int offsetX = (int) (guiLeft + viewX + viewW / 2 + scrollX);
        int offsetY = (int) (guiTop + viewY + 30 + scrollY);
        drawTiledBackground(guiLeft + viewX, guiTop + viewY, viewW, viewH);

        // 2. Draw connections
        for (SkillSegmentConnection conn : connections) {
            conn.draw(offsetX, offsetY);
        }

        // 3. Draw components
        for (SkillSegmentComponent comp : components) {
            boolean isHoldingThis = (comp == holdingComp);
            comp.draw(offsetX, offsetY, holdProgress, isHoldingThis);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void drawTiledBackground(int x, int y, int w, int h) {
        Minecraft mc = Minecraft.getMinecraft();
        ResourceLocation bg = tree.getBackground();
        if (bg == null) {
            bg = new ResourceLocation("factionapi", "textures/gui/skills/backgrounds/level.png");
        }
        mc.getTextureManager().bindTexture(bg);
        GL11.glColor4f(0.5f, 0.5f, 0.5f, 1.0f); // Balanced dimming so nodes pop out

        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        float u1 = -scrollX / 16.0f;
        float v1 = -scrollY / 16.0f;
        float u2 = u1 + w / 16.0f;
        float v2 = v1 + h / 16.0f;
        tess.addVertexWithUV(x, y + h, 0, u1, v2);
        tess.addVertexWithUV(x + w, y + h, 0, u2, v2);
        tess.addVertexWithUV(x + w, y, 0, u2, v1);
        tess.addVertexWithUV(x, y, 0, u1, v1);
        tess.draw();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public SkillSegmentComponent getComponentAt(int mouseX, int mouseY, int guiLeft, int guiTop, int viewX, int viewY, int viewW, int viewH) {
        if (mouseX < guiLeft + viewX || mouseX > guiLeft + viewX + viewW ||
            mouseY < guiTop + viewY || mouseY > guiTop + viewY + viewH) {
            return null;
        }

        int offsetX = (int) (guiLeft + viewX + viewW / 2 + scrollX);
        int offsetY = (int) (guiTop + viewY + 30 + scrollY);

        for (SkillSegmentComponent comp : components) {
            if (comp.isMouseOver(mouseX, mouseY, offsetX, offsetY)) {
                return comp;
            }
        }
        return null;
    }
}
