package com.marrybye.neorism.client.gui.overlay;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.api.action.IAction;
import com.marrybye.neorism.common.action.ActionHandler;
import com.marrybye.neorism.common.action.ActionRegistry;

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

/**
 * Action Cooldown & Active Buffs HUD Overlay.
 * Canonical port of TeamLapen's ItemChargeOverlay / ActionHUD.
 * Renders active ability icons, duration counters, and cooldown badges on the screen.
 */
@SideOnly(Side.CLIENT)
public class ActionCooldownOverlay {

    private final Minecraft mc;

    public ActionCooldownOverlay(Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }

        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        ActionHandler handler = ActionHandler.get(player);
        if (handler == null) return;

        java.util.Collection<IAction> allActions = ActionRegistry.getAllActions();
        if (allActions == null || allActions.isEmpty()) return;

        ScaledResolution res = event.resolution;
        int screenWidth = res.getScaledWidth();
        int screenHeight = res.getScaledHeight();

        // Position: right side of the screen, middle height
        int iconSize = 18;
        int spacing = 22;
        int startX = screenWidth - iconSize - 4;
        int startY = screenHeight / 2 - 40;
        int index = 0;

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        for (IAction action : allActions) {
            boolean active = handler.isActive(action);
            boolean onCd = handler.isOnCooldown(action);

            if (!active && !onCd) {
                continue;
            }

            int curY = startY + index * spacing;
            ResourceLocation icon = action.getIcon();
            if (icon == null) {
                icon = new ResourceLocation("neorism", "textures/actions/" + action.getId() + ".png");
            }

            mc.getTextureManager().bindTexture(icon);
            if (active) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.8F);
            }

            drawTexturedRect(startX, curY, iconSize, iconSize);

            // Cooldown overlay or duration overlay
            if (onCd) {
                int cdTicks = handler.getCooldownRemaining(action);
                int cdSec = (cdTicks + 19) / 20;
                String cdText = cdSec + "s";
                int textW = mc.fontRenderer.getStringWidth(cdText);
                mc.fontRenderer.drawStringWithShadow(cdText, startX + (iconSize - textW) / 2, curY + 5, 0xFFFF5555);
            } else if (active) {
                int leftTicks = handler.getActiveTicksLeft(action);
                if (leftTicks > 0) {
                    int sec = (leftTicks + 19) / 20;
                    String secText = sec + "s";
                    int textW = mc.fontRenderer.getStringWidth(secText);
                    mc.fontRenderer.drawStringWithShadow(secText, startX + (iconSize - textW) / 2, curY + 5, 0xFF55FF55);
                }
            }

            index++;
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawTexturedRect(int x, int y, int w, int h) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV(x, y + h, 0.0D, 0.0D, 1.0D);
        tess.addVertexWithUV(x + w, y + h, 0.0D, 1.0D, 1.0D);
        tess.addVertexWithUV(x + w, y, 0.0D, 1.0D, 0.0D);
        tess.addVertexWithUV(x, y, 0.0D, 0.0D, 0.0D);
        tess.draw();
    }
}
