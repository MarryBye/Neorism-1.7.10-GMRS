package com.marrybye.neorism.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.hunter.HunterLeveling;
import com.marrybye.neorism.common.hunter.HunterLeveling.HunterTableRequirement;
import com.marrybye.neorism.common.inventory.ContainerHunterTable;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Client Screen for the Hunter Table workstation.
 * Canonical port of TeamLapen's HunterTableScreen.java.
 */
public class GuiHunterTable extends GuiContainer {

    private static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("neorism", "textures/gui/container/hunter_table.png");
    private static final ResourceLocation GHOST_BOOK = new ResourceLocation("neorism", "textures/gui/sprites/container/slot/book.png");
    private static final ResourceLocation GHOST_FANG = new ResourceLocation("neorism", "textures/gui/sprites/container/slot/fang.png");
    private static final ResourceLocation GHOST_PURE_BLOOD = new ResourceLocation("neorism", "textures/gui/sprites/container/slot/pure_blood_bottle.png");
    private static final ResourceLocation GHOST_VAMPIRE_BOOK = new ResourceLocation("neorism", "textures/gui/sprites/container/slot/vampire_book.png");

    public GuiHunterTable(InventoryPlayer playerInventory, World world, int x, int y, int z) {
        super(new ContainerHunterTable(playerInventory, world, x, y, z));
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = StatCollector.translateToLocal("container.neorism.hunter_table");
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
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);

        // Slot 0: Book
        if (this.inventorySlots.getSlot(0).getStack() == null) {
            this.mc.getTextureManager().bindTexture(GHOST_BOOK);
            func_146110_a(k + 15, l + 28, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
        }
        // Slot 1: Fang
        if (this.inventorySlots.getSlot(1).getStack() == null) {
            this.mc.getTextureManager().bindTexture(GHOST_FANG);
            func_146110_a(k + 42, l + 28, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
        }
        // Slot 2: Pure Blood
        if (this.inventorySlots.getSlot(2).getStack() == null) {
            this.mc.getTextureManager().bindTexture(GHOST_PURE_BLOOD);
            func_146110_a(k + 69, l + 28, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
        }
        // Slot 3: Vampire Book
        if (this.inventorySlots.getSlot(3).getStack() == null) {
            this.mc.getTextureManager().bindTexture(GHOST_VAMPIRE_BOOK);
            func_146110_a(k + 96, l + 28, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        Slot hovered = null;
        for (Object obj : this.inventorySlots.inventorySlots) {
            Slot slot = (Slot) obj;
            if (isMouseOverSlot(slot, mouseX, mouseY)) {
                hovered = slot;
                break;
            }
        }

        if (hovered != null && hovered.slotNumber >= 0 && hovered.slotNumber < 4) {
            FactionPlayerHandler fph = FactionPlayerHandler.get(this.mc.thePlayer);
            int targetLevel = (fph != null ? fph.getCurrentLevel() : 0) + 1;
            HunterTableRequirement req = HunterLeveling.getTableRequirement(targetLevel);

            if (req != null) {
                int slotIndex = hovered.slotNumber;
                ItemStack stack = hovered.getStack();
                int currentCount = stack != null ? stack.stackSize : 0;
                List<String> tooltip = new ArrayList<String>();

                if (slotIndex == 0) {
                    int missing = req.bookQuantity - currentCount;
                    if (missing > 0) {
                        tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("gui.neorism.hunter_table.ritual_missing_book", missing));
                    }
                } else if (slotIndex == 1) {
                    int missing = req.vampireFangQuantity - currentCount;
                    if (missing > 0) {
                        tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("gui.neorism.hunter_table.ritual_missing_vampire_fang", missing));
                    }
                } else if (slotIndex == 2) {
                    if (req.pureBloodQuantity > 0) {
                        int missing = req.pureBloodQuantity - currentCount;
                        if (missing > 0) {
                            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("gui.neorism.hunter_table.ritual_missing_pure_blood", missing, req.pureBloodLevel + 1));
                        }
                    }
                } else if (slotIndex == 3) {
                    if (req.vampireBookQuantity > 0) {
                        int missing = req.vampireBookQuantity - currentCount;
                        if (missing > 0) {
                            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("gui.neorism.hunter_table.ritual_missing_vampire_book", missing));
                        }
                    }
                }

                if (!tooltip.isEmpty()) {
                    this.drawHoveringText(tooltip, mouseX, mouseY, this.fontRendererObj);
                }
            }
        }
    }

    private boolean isMouseOverSlot(Slot slot, int mouseX, int mouseY) {
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        int slotX = k + slot.xDisplayPosition;
        int slotY = l + slot.yDisplayPosition;
        return mouseX >= slotX && mouseX <= slotX + 16 && mouseY >= slotY && mouseY <= slotY + 16;
    }
}
