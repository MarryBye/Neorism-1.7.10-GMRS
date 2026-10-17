package com.marrybye.neorism.client.gui.overlay;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.action.ActionHandler;
import com.marrybye.neorism.common.action.ActionRegistry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Vampire Rage HUD Vignette Overlay.
 * Canonical port of TeamLapen's RageOverlay.
 * Displays a blood-red pulsing vignette when the Vampire Rage ability is active.
 */
@SideOnly(Side.CLIENT)
public class RageOverlay {

    private static final ResourceLocation RAGE_VIGNETTE = new ResourceLocation(Neorism.MODID, "textures/gui/overlay/rage.png");
    private final Minecraft mc;

    public RageOverlay(Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        ActionHandler handler = ActionHandler.get(player);
        if (handler == null || !handler.isActive(ActionRegistry.getAction("vampire_rage"))) {
            return;
        }

        ScaledResolution res = event.resolution;
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();

        // Pulsing blood-red intensity
        float pulse = 0.65F + (float) Math.sin((player.ticksExisted + event.partialTicks) * 0.25F) * 0.2F;
        SunOverlay.renderVignette(RAGE_VIGNETTE, width, height, pulse, 1.0F, 0.2F, 0.2F);
    }
}
