package com.marrybye.neorism.client.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.client.gui.skills.SkillsScreen;
import com.marrybye.neorism.client.input.KeyBindings;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * Faction Menu / Character Dashboard (opened with [P]).
 * Displays 3D player model, faction level, action/skill tree button, and task info.
 */
@SideOnly(Side.CLIENT)
public class FactionMenuScreen extends GuiScreen {

    public static final int MENU_WIDTH = 234;
    public static final int MENU_HEIGHT = 205;

    private static final ResourceLocation BACKGROUND = new ResourceLocation("factionapi", "textures/gui/container/faction_menu.png");
    private static final ResourceLocation SKILLS_ICON = new ResourceLocation("factionapi", "textures/gui/sprites/widget/skills.png");

    private final EntityPlayer player;
    private final FactionPlayerHandler factionPlayer;

    private int guiLeft;
    private int guiTop;
    private GuiButton btnSkills;

    public FactionMenuScreen(EntityPlayer player) {
        this.player = player;
        this.factionPlayer = FactionPlayerHandler.get(player);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (width - MENU_WIDTH) / 2;
        this.guiTop = (height - MENU_HEIGHT) / 2;

        this.buttonList.clear();
        // Skills button (20x20 button at left panel)
        btnSkills = new GuiButton(1, guiLeft + 7, guiTop + 90, 20, 20, "") {
            @Override
            public void drawButton(net.minecraft.client.Minecraft mc, int mouseX, int mouseY) {
                if (this.visible) {
                    mc.getTextureManager().bindTexture(SKILLS_ICON);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition &&
                                      mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                    if (hovered) {
                        GL11.glColor4f(1.0f, 0.9f, 0.4f, 1.0f);
                    }
                    func_146110_a(this.xPosition, this.yPosition, 0, 0, 20, 20, 20, 20);
                }
            }
        };
        buttonList.add(btnSkills);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            mc.displayGuiScreen(new SkillsScreen(player, this));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // 1. Draw Menu Background
        mc.getTextureManager().bindTexture(BACKGROUND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        func_146110_a(guiLeft, guiTop, 0, 0, MENU_WIDTH, MENU_HEIGHT, 256, 256);

        // 2. Draw 3D Player Avatar
        int modelX = guiLeft + 31;
        int modelY = guiTop + 75;
        GuiInventory.func_147046_a(modelX, modelY, 30, (float) (modelX) - mouseX, (float) (modelY - 50) - mouseY, player);

        // 3. Draw Level and Faction Label
        String factionName = factionPlayer != null ? StatCollector.translateToLocal(factionPlayer.getCurrentFaction().getTranslationKey()) : "Neutral";
        int level = factionPlayer != null ? factionPlayer.getCurrentLevel() : 0;
        String levelText = factionName + " - " + StatCollector.translateToLocal("text.neorism.level") + " " + level;
        int textW = fontRendererObj.getStringWidth(levelText);
        fontRendererObj.drawString(EnumChatFormatting.GOLD + levelText, Math.max(guiLeft + 5, guiLeft + 31 - textW / 2), guiTop + 81, 0xFFFFFF);

        // 4. Draw Right Panel: Task List / Status
        String taskTitle = StatCollector.translateToLocal("gui.factionapi.faction_menu.tasks");
        fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + EnumChatFormatting.BOLD.toString() + taskTitle, guiLeft + 86, guiTop + 10, 0x333333);

        String noTasks = StatCollector.translateToLocal("gui.factionapi.faction_menu.no_tasks");
        fontRendererObj.drawString(EnumChatFormatting.GRAY + noTasks, guiLeft + 90, guiTop + 30, 0x888888);

        // 5. Draw Buttons
        super.drawScreen(mouseX, mouseY, partialTicks);

        // 6. Draw Tooltips
        if (btnSkills.func_146115_a()) { // isHovered
            String tooltip = StatCollector.translateToLocal("gui.factionapi.faction_menu.skill_screen");
            drawCreativeTabHoveringText(tooltip, mouseX, mouseY);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE || (KeyBindings.keyFactionMenu != null && keyCode == KeyBindings.keyFactionMenu.getKeyCode())) {
            mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }
}
