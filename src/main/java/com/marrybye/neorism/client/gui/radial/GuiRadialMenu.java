package com.marrybye.neorism.client.gui.radial;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

/**
 * Radial action selection menu (Action Rose) for Minecraft 1.7.10.
 * Faithfully ports TeamLapen's circular sector rendering, smooth entrance animation,
 * mouse direction detection, and key release activation.
 */
@SideOnly(Side.CLIENT)
public class GuiRadialMenu extends GuiScreen {

    public static final float PRECISION = 3.0f;
    public static final float OPEN_ANIMATION_LENGTH = 0.12f;

    protected final RadialMenu radialMenu;
    protected final KeyBinding triggerKey;
    protected final List<IRadialMenuSlot> slots;

    protected int selectedItem = -1;
    protected long openTime = 0;
    protected boolean closing = false;

    public GuiRadialMenu(RadialMenu radialMenu, KeyBinding triggerKey) {
        this.radialMenu = radialMenu;
        this.triggerKey = triggerKey;
        this.slots = radialMenu.getSlots();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.openTime = System.currentTimeMillis();
        this.selectedItem = -1;
        this.closing = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float elapsed = (System.currentTimeMillis() - openTime) / 1000.0f;
        float progress = Math.min(1.0f, elapsed / OPEN_ANIMATION_LENGTH);
        // Smooth ease out cubic
        float animProgress = 1.0f - (float) Math.pow(1.0f - progress, 3);

        float radiusIn = Math.max(0.1f, 40.0f * animProgress);
        float radiusOut = radiusIn * 2.1f;
        float itemRadius = (radiusIn + radiusOut) * 0.5f;

        int centerX = width / 2;
        int centerY = height / 2;
        int numberOfSlices = slots.size();
        if (numberOfSlices == 0) return;

        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double mouseDist = Math.sqrt(dx * dx + dy * dy);
        double mouseAngle = Math.toDegrees(Math.atan2(dy, dx));

        float slot0 = (((0 - 0.5f) / (float) numberOfSlices) + 0.25f) * 360.0f;
        if (mouseAngle < slot0) {
            mouseAngle += 360.0f;
        }

        // Determine hovered slice
        selectedItem = -1;
        if (mouseDist >= 12.0) {
            for (int i = 0; i < numberOfSlices; i++) {
                float sliceLeft = (((i - 0.5f) / (float) numberOfSlices) + 0.25f) * 360.0f;
                float sliceRight = (((i + 0.5f) / (float) numberOfSlices) + 0.25f) * 360.0f;
                if (mouseAngle >= sliceLeft && mouseAngle < sliceRight) {
                    selectedItem = i;
                    break;
                }
            }
        }

        // Draw slices
        for (int i = 0; i < numberOfSlices; i++) {
            float sliceLeft = (((i - 0.5f) / (float) numberOfSlices) + 0.25f) * 360.0f;
            float sliceRight = (((i + 0.5f) / (float) numberOfSlices) + 0.25f) * 360.0f;
            IRadialMenuSlot slot = slots.get((i + numberOfSlices / 2) % numberOfSlices);

            if (selectedItem == i) {
                // Highlighted slice (cyan/blue glow)
                drawSlice(centerX, centerY, 0, radiusIn, radiusOut, sliceLeft, sliceRight, 60, 160, 220, 140);
            } else {
                // Normal slice (dark translucent)
                drawSlice(centerX, centerY, 0, radiusIn, radiusOut, sliceLeft, sliceRight, 10, 10, 15, 100);
            }
        }

        // Draw active or cooldown overlays for each slot
        drawSlotDecorations(centerX, centerY, radiusIn, radiusOut, numberOfSlices);

        // Center hover text
        if (selectedItem != -1) {
            int adjusted = ((selectedItem + (numberOfSlices / 2 + 1)) % numberOfSlices) - 1;
            adjusted = adjusted == -1 ? numberOfSlices - 1 : adjusted;
            IRadialMenuSlot slot = slots.get(adjusted);
            if (slot != null && slot.getSlotName() != null) {
                drawCenteredString(fontRendererObj, slot.getSlotName(), centerX, centerY - 4, 0xFFFFFF);
            }
        }

        // Draw icons and numbers
        for (int i = 0; i < numberOfSlices; i++) {
            float angle = ((i / (float) numberOfSlices) - 0.25f) * 2.0f * (float) Math.PI;
            if (numberOfSlices % 2 != 0) {
                angle += (float) (Math.PI / numberOfSlices);
            }
            float posX = centerX - 8 + itemRadius * (float) Math.cos(angle);
            float posY = centerY - 8 + itemRadius * (float) Math.sin(angle);

            IRadialMenuSlot slot = slots.get(i);
            drawSlotIcon(slot, (int) posX, (int) posY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void drawSlotDecorations(int centerX, int centerY, float radiusIn, float radiusOut, int numberOfSlices) {
    }

    protected void drawSlotIcon(IRadialMenuSlot slot, int x, int y) {
        ResourceLocation icon = slot.getIcon();
        if (icon != null) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            mc.getTextureManager().bindTexture(icon);
            func_146110_a(x, y, 0, 0, 16, 16, 16, 16); // drawModalRectWithCustomSizedTexture
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        }
    }

    public static void drawSlice(float x, float y, float z, float radiusIn, float radiusOut,
                                 float startAngle, float endAngle, int r, int g, int b, int a) {
        float startDeg = startAngle % 360.0f;
        if (startDeg < 0) startDeg += 360.0f;
        float endDeg = endAngle % 360.0f;
        if (endDeg < 0) endDeg += 360.0f;
        float sweepDeg = endDeg - startDeg;
        if (sweepDeg <= 0.0f) {
            sweepDeg += 360.0f;
        }

        int sections = Math.max(1, (int) Math.ceil(sweepDeg / PRECISION));
        float startRad = (float) Math.toRadians(startDeg);
        float endRad = (float) Math.toRadians(startDeg + sweepDeg);
        float angleStep = (endRad - startRad) / (float) sections;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_QUADS);
        tessellator.setColorRGBA(r, g, b, a);

        for (int i = 0; i < sections; i++) {
            float angle1 = startRad + i * angleStep;
            float angle2 = startRad + (i + 1) * angleStep;

            float c1 = (float) Math.cos(angle1);
            float s1 = (float) Math.sin(angle1);
            float c2 = (float) Math.cos(angle2);
            float s2 = (float) Math.sin(angle2);

            float pos1InX = x + radiusIn * c1;
            float pos1InY = y + radiusIn * s1;
            float pos1OutX = x + radiusOut * c1;
            float pos1OutY = y + radiusOut * s1;

            float pos2OutX = x + radiusOut * c2;
            float pos2OutY = y + radiusOut * s2;
            float pos2InX = x + radiusIn * c2;
            float pos2InY = y + radiusIn * s2;

            tessellator.addVertex(pos1OutX, pos1OutY, z);
            tessellator.addVertex(pos1InX, pos1InY, z);
            tessellator.addVertex(pos2InX, pos2InY, z);
            tessellator.addVertex(pos2OutX, pos2OutY, z);
        }
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) { // Left click selects
            activateSelected();
        } else if (button == 1) { // Right click cancels
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        // If trigger key was held and now released, activate selected and close!
        if (triggerKey != null) {
            int keyCode = triggerKey.getKeyCode();
            boolean isDown = (keyCode >= 0 && keyCode < 256) ? Keyboard.isKeyDown(keyCode) : false;
            if (!isDown) {
                activateSelected();
            }
        }
    }

    protected void activateSelected() {
        if (selectedItem != -1 && !slots.isEmpty()) {
            int numberOfSlices = slots.size();
            int adjusted = ((selectedItem + (numberOfSlices / 2 + 1)) % numberOfSlices) - 1;
            adjusted = adjusted == -1 ? numberOfSlices - 1 : adjusted;
            radialMenu.setCurrentSlot(adjusted);
        }
        mc.displayGuiScreen(null);
    }
}
