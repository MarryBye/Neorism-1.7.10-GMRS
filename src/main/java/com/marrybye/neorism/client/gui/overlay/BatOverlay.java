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
 * Bat Form HUD Vignette Overlay.
 * Canonical port of TeamLapen's BatOverlay.
 * Renders gothic bat shadow borders when player is flying in bat transformation.
 */
@SideOnly(Side.CLIENT)
public class BatOverlay {

    private static final ResourceLocation BAT_VIGNETTE = new ResourceLocation(Neorism.MODID, "textures/gui/overlay/bat.png");
    private final Minecraft mc;

    public BatOverlay(Minecraft mc) {
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
        if (handler == null || !handler.isActive(ActionRegistry.getAction("bat_form"))) {
            return;
        }

        ScaledResolution res = event.resolution;
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();

        SunOverlay.renderVignette(BAT_VIGNETTE, width, height, 0.75F, 0.15F, 0.15F, 0.25F);
    }
}
