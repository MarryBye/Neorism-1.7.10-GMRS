package com.marrybye.neorism.client.gui;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.inventory.ContainerAlchemicalCauldron;
import com.marrybye.neorism.common.tileentity.TileEntityAlchemicalCauldron;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * Client Screen for the Alchemical Cauldron workstation.
 * Canonical port of TeamLapen's AlchemicalCauldronScreen.java.
 */
public class GuiAlchemicalCauldron extends GuiContainer {

    private static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("neorism", "textures/gui/container/alchemical_cauldron.png");
    private static final ResourceLocation LIT_PROGRESS = new ResourceLocation("neorism", "textures/gui/sprites/container/alchemical_cauldron/lit_progress.png");
    private static final ResourceLocation BURN_PROGRESS = new ResourceLocation("neorism", "textures/gui/sprites/container/alchemical_cauldron/burn_progress.png");
    private static final ResourceLocation BUBBLES_PROGRESS = new ResourceLocation("neorism", "textures/gui/sprites/container/alchemical_cauldron/bubbles_progress.png");

    private final TileEntityAlchemicalCauldron cauldron;

    public GuiAlchemicalCauldron(InventoryPlayer playerInv, TileEntityAlchemicalCauldron tile) {
        super(new ContainerAlchemicalCauldron(playerInv, tile));
        this.cauldron = tile;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = StatCollector.translateToLocal("container.neorism.alchemical_cauldron");
        this.fontRendererObj.drawString(title, this.xSize / 2 - this.fontRendererObj.getStringWidth(title) / 2, 6, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND_LOCATION);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        // Lit fire progress
        if (this.cauldron.isBurning()) {
            int burnRemaining = getBurnLeftScaled(14);
            this.mc.getTextureManager().bindTexture(LIT_PROGRESS);
            func_146110_a(k + 56, l + 36 + 14 - burnRemaining, 0.0F, (14 - burnRemaining) / 14.0F, 14, burnRemaining, 14.0F, 14.0F);
        }

        // Cooking progress arrow
        int cookProgress = getCookProgressScaled(24);
        if (cookProgress > 0) {
            this.mc.getTextureManager().bindTexture(BURN_PROGRESS);
            func_146110_a(k + 79, l + 35, 0.0F, 0.0F, cookProgress, 16, 24.0F, 16.0F);
        }

        // Bubbles progress
        int bubbleProgress = getCookProgressScaled(29);
        if (bubbleProgress > 0) {
            this.mc.getTextureManager().bindTexture(BUBBLES_PROGRESS);
            func_146110_a(k + 142, l + 28 + 29 - bubbleProgress, 0.0F, (29 - bubbleProgress) / 29.0F, 12, bubbleProgress, 12.0F, 29.0F);
        }
    }

    private int getCookProgressScaled(int pixels) {
        int i = this.cauldron.cookTime;
        int j = TileEntityAlchemicalCauldron.TOTAL_COOK_TIME;
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

    private int getBurnLeftScaled(int pixels) {
        int j = this.cauldron.currentItemBurnTime;
        if (j == 0) {
            j = 200;
        }
        return this.cauldron.burnTime * pixels / j;
    }
}
