package com.marrybye.neorism.client.gui;

import java.util.Collections;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.inventory.ContainerAltarInfusion;
import com.marrybye.neorism.common.tileentity.TileEntityAltarInfusion;
import com.marrybye.neorism.common.vampire.VampireLeveling;
import com.marrybye.neorism.common.vampire.VampireLeveling.AltarInfusionRequirement;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * Screen for the Altar of Infusion.
 * Ported from TeamLapen's AltarInfusionScreen.java.
 */
public class GuiAltarInfusion extends GuiContainer {

    private static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("neorism", "textures/gui/container/altar_of_infusion.png");
    private static final ResourceLocation GHOST_PURE_BLOOD = new ResourceLocation("neorism", "textures/gui/sprites/container/slot/pure_blood_bottle.png");
    private static final ResourceLocation GHOST_HUMAN_HEART = new ResourceLocation("neorism", "textures/gui/sprites/container/slot/human_heart.png");
    private static final ResourceLocation GHOST_VAMPIRE_BOOK = new ResourceLocation("neorism", "textures/gui/sprites/container/slot/vampire_book.png");

    private final TileEntityAltarInfusion altar;

    public GuiAltarInfusion(InventoryPlayer playerInventory, TileEntityAltarInfusion altar) {
        super(new ContainerAltarInfusion(playerInventory, altar));
        this.altar = altar;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = StatCollector.translateToLocal("container.neorism.altar_infusion");
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

        // Draw ghost icons for empty slots
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.45F);

        if (altar.getStackInSlot(0) == null) {
            this.mc.getTextureManager().bindTexture(GHOST_PURE_BLOOD);
            func_146110_a(k + 44, l + 34, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
        }
        if (altar.getStackInSlot(1) == null) {
            this.mc.getTextureManager().bindTexture(GHOST_HUMAN_HEART);
            func_146110_a(k + 80, l + 34, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
        }
        if (altar.getStackInSlot(2) == null) {
            this.mc.getTextureManager().bindTexture(GHOST_VAMPIRE_BOOK);
            func_146110_a(k + 116, l + 34, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Hover tooltip on slots
        Slot hovered = null;
        for (Object obj : this.inventorySlots.inventorySlots) {
            Slot slot = (Slot) obj;
            if (isMouseOverSlot(slot, mouseX, mouseY)) {
                hovered = slot;
                break;
            }
        }

        if (hovered != null && hovered.slotNumber >= 0 && hovered.slotNumber < 3) {
            FactionPlayerHandler fph = FactionPlayerHandler.get(this.mc.thePlayer);
            int targetLevel = (fph != null ? fph.getCurrentLevel() : 0) + 1;
            AltarInfusionRequirement req = VampireLeveling.getInfusionRequirement(targetLevel);

            if (req != null) {
                int slotIndex = hovered.slotNumber;
                ItemStack stack = hovered.getStack();
                String tooltip = null;

                if (slotIndex == 0) {
                    int reqQty = req.getPureBloodQuantity();
                    int reqPurity = req.getPureBloodLevel();
                    if (reqQty > 0) {
                        if (stack != null && stack.getItemDamage() < reqPurity) {
                            tooltip = StatCollector.translateToLocalFormatted("gui.neorism.altar_infusion.ritual_wrong_purity", reqPurity + 1);
                        } else if (stack == null || stack.stackSize < reqQty) {
                            int missing = reqQty - (stack != null ? stack.stackSize : 0);
                            tooltip = StatCollector.translateToLocalFormatted("gui.neorism.altar_infusion.ritual_missing_pure_blood", missing, reqPurity + 1);
                        }
                    }
                } else if (slotIndex == 1) {
                    int reqQty = req.getHumanHeartQuantity();
                    if (reqQty > 0) {
                        if (stack == null || stack.stackSize < reqQty) {
                            int missing = reqQty - (stack != null ? stack.stackSize : 0);
                            tooltip = StatCollector.translateToLocalFormatted("gui.neorism.altar_infusion.ritual_missing_human_heart", missing);
                        }
                    }
                } else if (slotIndex == 2) {
                    int reqQty = req.getVampireBookQuantity();
                    if (reqQty > 0) {
                        if (stack == null || stack.stackSize < reqQty) {
                            int missing = reqQty - (stack != null ? stack.stackSize : 0);
                            tooltip = StatCollector.translateToLocalFormatted("gui.neorism.altar_infusion.ritual_missing_vampire_book", missing);
                        }
                    }
                }

                if (tooltip != null) {
                    this.drawHoveringText(Collections.singletonList(tooltip), mouseX, mouseY, this.fontRendererObj);
                }
            }
        }
    }

    private boolean isMouseOverSlot(Slot slot, int mouseX, int mouseY) {
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        return mouseX >= k + slot.xDisplayPosition && mouseX <= k + slot.xDisplayPosition + 16 &&
               mouseY >= l + slot.yDisplayPosition && mouseY <= l + slot.yDisplayPosition + 16;
    }
}
