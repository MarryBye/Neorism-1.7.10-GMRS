package com.marrybye.neorism.client.gui;

import com.marrybye.neorism.common.entity.minion.ContainerMinion;
import com.marrybye.neorism.common.entity.minion.EntityMinionBase;
import com.marrybye.neorism.common.entity.minion.MinionData;
import com.marrybye.neorism.common.entity.minion.tasks.ModMinionTasks;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.network.packets.SelectMinionTaskPacket;
import com.marrybye.neorism.common.network.packets.ToggleMinionTaskLockPacket;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiMinion extends GuiContainer {

    private static final ResourceLocation BACKGROUND =
        new ResourceLocation("neorism", "textures/gui/container/minion/inventory.png");
    private static final ResourceLocation LOCKED_SPRITE =
        new ResourceLocation("neorism", "textures/gui/sprites/container/minion_inventory/locked.png");

    private final EntityMinionBase minion;
    private final ContainerMinion minionContainer;
    private GuiButton taskButton;
    private GuiButton lockButton;
    private final List<IMinionTask> availableTasks = new ArrayList<IMinionTask>();

    public GuiMinion(InventoryPlayer playerInv, EntityMinionBase minion) {
        super(new ContainerMinion(playerInv, minion));
        this.minion = minion;
        this.minionContainer = (ContainerMinion) this.inventorySlots;
        this.xSize = 214;
        this.ySize = 185;

        availableTasks.addAll(ModMinionTasks.getAllTasks().values());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        String currentTaskName = "Task";
        IMinionTask.IMinionTaskDesc currentDesc = minion.getCurrentTask();
        if (currentDesc != null && currentDesc.getTask() != null) {
            currentTaskName = currentDesc.getTask().getName().getFormattedText();
        }

        // Task switch button
        this.taskButton = new GuiButton(0, this.guiLeft + 118, this.guiTop + 22, 88, 20, currentTaskName);
        this.buttonList.add(this.taskButton);

        // Lock button
        String lockLabel = minion.isTaskLocked() ? "[X]" : "[O]";
        this.lockButton = new GuiButton(1, this.guiLeft + 98, this.guiTop + 22, 18, 20, lockLabel);
        this.buttonList.add(this.lockButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            // Cycle through available tasks
            IMinionTask.IMinionTaskDesc currentDesc = minion.getCurrentTask();
            IMinionTask current = currentDesc != null ? currentDesc.getTask() : null;
            int currentIndex = availableTasks.indexOf(current);
            int nextIndex = (currentIndex + 1) % availableTasks.size();
            IMinionTask nextTask = availableTasks.get(nextIndex);

            taskButton.displayString = nextTask.getName().getFormattedText();
            PacketHandler.sendToServer(new SelectMinionTaskPacket(minion.getEntityId(), nextTask.getId()));
        } else if (button.id == 1) {
            boolean currentLock = minion.isTaskLocked();
            boolean newLock = !currentLock;
            lockButton.displayString = newLock ? "[X]" : "[O]";
            PacketHandler.sendToServer(new ToggleMinionTaskLockPacket(minion.getEntityId()));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = minion.getCustomNameTag();
        if (title == null || title.isEmpty()) {
            title = minion.getCommandSenderName();
        }
        this.fontRendererObj.drawString(title, 5, 6, 0x404040);

        String taskLabel = StatCollector.translateToLocal("gui.factionapi.minion.active_task");
        if (taskLabel.startsWith("gui.")) taskLabel = "Active Task:";
        this.fontRendererObj.drawString(taskLabel, 118, 11, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        // Draw locked overlay for locked extra slots
        int extraSlots = minionContainer.getExtraSlots();
        for (int k = extraSlots; k < 15; k++) {
            int col = k / 3;
            int row = k % 3;
            int slotX = this.guiLeft + 27 + 18 * col;
            int slotY = this.guiTop + 42 + 18 * row;

            this.mc.getTextureManager().bindTexture(LOCKED_SPRITE);
            net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;
            tess.startDrawingQuads();
            tess.addVertexWithUV(slotX + 1, slotY + 17, this.zLevel, 0.0D, 1.0D);
            tess.addVertexWithUV(slotX + 17, slotY + 17, this.zLevel, 1.0D, 1.0D);
            tess.addVertexWithUV(slotX + 17, slotY + 1, this.zLevel, 1.0D, 0.0D);
            tess.addVertexWithUV(slotX + 1, slotY + 1, this.zLevel, 0.0D, 0.0D);
            tess.draw();
        }
    }
}
