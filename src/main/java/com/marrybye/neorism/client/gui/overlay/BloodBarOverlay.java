package com.marrybye.neorism.client.gui.overlay;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.api.entity.player.IBloodStats;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

/**
 * Renders the vampire blood drop icons on the player HUD replacing the vanilla hunger bar.
 */
@SideOnly(Side.CLIENT)
public class BloodBarOverlay {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Neorism.MODID, "textures/gui/sprites/blood_bar/background.png");
    private static final ResourceLocation QUARTER = new ResourceLocation(Neorism.MODID, "textures/gui/sprites/blood_bar/quarter.png");
    private static final ResourceLocation HALF = new ResourceLocation(Neorism.MODID, "textures/gui/sprites/blood_bar/half.png");
    private static final ResourceLocation THREE_QUARTER = new ResourceLocation(Neorism.MODID, "textures/gui/sprites/blood_bar/three_quarter.png");
    private static final ResourceLocation FULL = new ResourceLocation(Neorism.MODID, "textures/gui/sprites/blood_bar/full.png");

    private final Minecraft mc;

    public BloodBarOverlay(Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent
    public void onRenderOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (event.type == RenderGameOverlayEvent.ElementType.FOOD) {
            EntityPlayer player = mc.thePlayer;
            if (player != null && Helper.isVampire(player)) {
                // Cancel vanilla hunger drumsticks
                event.setCanceled(true);

                if (mc.playerController != null && mc.playerController.shouldDrawHUD()) {
                    renderBloodBar(event.resolution);
                }
            }
        }
    }

    private void renderBloodBar(ScaledResolution res) {
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        VampirePlayer vp = VampirePlayer.get(player);
        if (vp == null) return;

        IBloodStats stats = vp.getBloodStats();
        if (stats == null) return;

        int width = res.getScaledWidth();
        int height = res.getScaledHeight();

        int left = width / 2 + 91;
        int top = height - 39;

        int blood = stats.getBloodLevel();
        int blood2 = blood - 20;

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i < 10; ++i) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;

            // Draw Background icon (9x9)
            drawSprite(BACKGROUND, x, top);

            if (idx < blood) {
                drawSprite(idx < blood2 ? FULL : HALF, x, top);
                if (idx == blood2) {
                    drawSprite(THREE_QUARTER, x, top);
                }
            } else if (idx == blood) {
                drawSprite(QUARTER, x, top);
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        // Bind vanilla GUI texture back to ensure no state contamination
        mc.getTextureManager().bindTexture(mc.ingameGUI.icons);
    }

    private void drawSprite(ResourceLocation texture, int x, int y) {
        mc.getTextureManager().bindTexture(texture);
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV(x, y + 9, 0, 0.0D, 1.0D);
        tess.addVertexWithUV(x + 9, y + 9, 0, 1.0D, 1.0D);
        tess.addVertexWithUV(x + 9, y, 0, 1.0D, 0.0D);
        tess.addVertexWithUV(x, y, 0, 0.0D, 0.0D);
        tess.draw();
    }
}
