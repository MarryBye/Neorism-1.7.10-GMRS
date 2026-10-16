package com.marrybye.neorism.client.gui.skills;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.network.packets.ForgetSkillPacket;
import com.marrybye.neorism.common.network.packets.ResetAllSkillsPacket;
import com.marrybye.neorism.common.network.packets.UnlockSkillPacket;
import com.marrybye.neorism.common.skills.SkillHandler;
import com.marrybye.neorism.common.skills.SkillRegistry;
import com.marrybye.neorism.common.skills.SkillTree;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillTree;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * Interactive DAG Skill Tree Screen for Neorism.
 * Matches TeamLapen FactionAPI SkillsScreen with panning canvas, tree tabs,
 * node connections, click-and-hold unlock/reset, and dynamic tooltips.
 */
@SideOnly(Side.CLIENT)
public class SkillsScreen extends GuiScreen {

    public static final int WINDOW_WIDTH = 252;
    public static final int WINDOW_HEIGHT = 219;
    public static final int CANVAS_X = 9;
    public static final int CANVAS_Y = 18;
    public static final int CANVAS_W = 234;
    public static final int CANVAS_H = 173;

    private static final ResourceLocation WINDOW_TEX = new ResourceLocation("factionapi", "textures/gui/skills/window.png");
    private static final int UNLOCK_HOLD_TICKS = 8;
    private static final int RESET_HOLD_TICKS = 16;

    private final EntityPlayer player;
    private final SkillHandler skillHandler;
    private final GuiScreen backScreen;

    private final List<SkillsTabComponent> tabs = new ArrayList<SkillsTabComponent>();
    private SkillsTabComponent selectedTab;

    private int guiLeft;
    private int guiTop;

    // Mouse drag tracking
    private boolean isDraggingCanvas = false;
    private int lastMouseX = 0;
    private int lastMouseY = 0;

    // Click & hold tracking
    private int holdingButton = -1; // 0 = left (unlock), 1 = right (reset)
    private SkillSegmentComponent heldComponent = null;
    private int holdingTicks = 0;

    private GuiButton btnDone;
    private GuiButton btnResetAll;
    private GuiButton btnBack;

    public SkillsScreen(EntityPlayer player, GuiScreen backScreen) {
        this.player = player;
        this.skillHandler = SkillHandler.get(player);
        this.backScreen = backScreen;
    }

    public SkillsScreen(EntityPlayer player) {
        this(player, null);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (width - WINDOW_WIDTH) / 2;
        this.guiTop = (height - WINDOW_HEIGHT) / 2;

        this.tabs.clear();
        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph != null) {
            Collection<ISkillTree> factionTrees = SkillRegistry.getTreesForFaction(fph.getCurrentFaction());
            for (ISkillTree t : factionTrees) {
                if (t instanceof SkillTree) {
                    SkillsTabComponent tab = new SkillsTabComponent((SkillTree) t, skillHandler);
                    this.tabs.add(tab);
                }
            }
        }

        if (!tabs.isEmpty() && selectedTab == null) {
            selectedTab = tabs.get(0);
        }

        // Add action buttons
        this.buttonList.clear();
        int btnY = guiTop + 194;
        if (backScreen != null) {
            btnBack = new GuiButton(0, guiLeft + 8, btnY, 70, 20, StatCollector.translateToLocal("gui.back"));
            buttonList.add(btnBack);
        }

        btnResetAll = new GuiButton(1, guiLeft + 82, btnY, 82, 20, StatCollector.translateToLocal("gui.factionapi.skills.resetall"));
        buttonList.add(btnResetAll);

        btnDone = new GuiButton(2, guiLeft + 168, btnY, 76, 20, StatCollector.translateToLocal("gui.done"));
        buttonList.add(btnDone);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0 && backScreen != null) {
            mc.displayGuiScreen(backScreen);
        } else if (button.id == 1) {
            PacketHandler.sendToServer(new ResetAllSkillsPacket());
            mc.thePlayer.playSound("random.levelup", 1.0f, 0.8f);
        } else if (button.id == 2) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // 1. Draw Tab Buttons at top
        drawTabs(mouseX, mouseY);

        // 2. Draw Inside Canvas
        float holdProgress = 0.0f;
        if (heldComponent != null && holdingButton >= 0) {
            int maxTicks = holdingButton == 0 ? UNLOCK_HOLD_TICKS : RESET_HOLD_TICKS;
            holdProgress = Math.min(1.0f, (float) holdingTicks / maxTicks);
        }

        if (selectedTab != null) {
            selectedTab.drawContents(guiLeft, guiTop, CANVAS_X, CANVAS_Y, CANVAS_W, CANVAS_H,
                mouseX, mouseY, holdProgress, heldComponent);
        }

        // 3. Draw Window Border & Header
        mc.getTextureManager().bindTexture(WINDOW_TEX);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        func_146110_a(guiLeft, guiTop, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, 256, 256);

        // Header Title
        String title = StatCollector.translateToLocal("gui.factionapi.faction_menu.skill_screen");
        fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + title, guiLeft + 12, guiTop + 6, 0x404040);

        // Remaining Skill Points counter
        int pts = skillHandler != null ? skillHandler.getSkillPoints() : 0;
        String ptsText = StatCollector.translateToLocal("gui.factionapi.skills.points") + ": " + EnumChatFormatting.GOLD + pts;
        int ptsW = fontRendererObj.getStringWidth(ptsText);
        fontRendererObj.drawString(ptsText, guiLeft + 242 - ptsW, guiTop + 6, 0x404040);

        // 4. Draw Buttons
        super.drawScreen(mouseX, mouseY, partialTicks);

        // 5. Draw Tooltip for hovered node (if not dragging)
        if (!isDraggingCanvas && selectedTab != null) {
            SkillSegmentComponent hovered = selectedTab.getComponentAt(mouseX, mouseY, guiLeft, guiTop, CANVAS_X, CANVAS_Y, CANVAS_W, CANVAS_H);
            if (hovered != null) {
                hovered.drawTooltip(mouseX, mouseY, fontRendererObj);
            }
        }
    }

    private void drawTabs(int mouseX, int mouseY) {
        if (tabs.size() <= 1) return;

        int tabX = guiLeft + 10;
        int tabY = guiTop - 24;

        for (int i = 0; i < tabs.size(); i++) {
            SkillsTabComponent tab = tabs.get(i);
            boolean isSelected = (tab == selectedTab);
            int tabW = 28;
            int tabH = 26;

            // Tab background box
            int color = isSelected ? 0xFFC6C6C6 : 0xFF8B8B8B;
            drawRect(tabX, tabY, tabX + tabW, tabY + tabH, 0xFF373737);
            drawRect(tabX + 1, tabY + 1, tabX + tabW - 1, isSelected ? tabY + tabH + 2 : tabY + tabH - 1, color);

            // Tab icon (item stack)
            if (tab.getDisplayStack() != null) {
                RenderItem itemRender = new RenderItem();
                itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), tab.getDisplayStack(), tabX + 6, tabY + 5);
            }

            // Tab hover tooltip
            if (mouseX >= tabX && mouseX <= tabX + tabW && mouseY >= tabY && mouseY <= tabY + tabH) {
                drawCreativeTabHoveringText(tab.getTitle(), mouseX, mouseY);
            }

            tabX += 30;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        // Check tab clicks
        if (tabs.size() > 1 && mouseY >= guiTop - 26 && mouseY <= guiTop) {
            int tabX = guiLeft + 10;
            for (SkillsTabComponent tab : tabs) {
                if (mouseX >= tabX && mouseX <= tabX + 28) {
                    this.selectedTab = tab;
                    mc.thePlayer.playSound("random.click", 0.5f, 1.0f);
                    return;
                }
                tabX += 30;
            }
        }

        // Check node click or canvas drag start
        if (isInsideCanvas(mouseX, mouseY)) {
            if (selectedTab != null) {
                SkillSegmentComponent comp = selectedTab.getComponentAt(mouseX, mouseY, guiLeft, guiTop, CANVAS_X, CANVAS_Y, CANVAS_W, CANVAS_H);
                if (comp != null) {
                    this.heldComponent = comp;
                    this.holdingButton = button;
                    this.holdingTicks = 0;
                    return;
                }
            }

            // Otherwise, start canvas dragging
            this.isDraggingCanvas = true;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        super.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        if (isDraggingCanvas && selectedTab != null) {
            int dx = mouseX - lastMouseX;
            int dy = mouseY - lastMouseY;
            selectedTab.scroll(dx, dy, CANVAS_W, CANVAS_H);
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which) {
        super.mouseMovedOrUp(mouseX, mouseY, which);
        isDraggingCanvas = false;
        heldComponent = null;
        holdingButton = -1;
        holdingTicks = 0;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        // Increment hold ticks while holding down on a node
        if (heldComponent != null && holdingButton >= 0) {
            holdingTicks++;
            int targetTicks = (holdingButton == 0) ? UNLOCK_HOLD_TICKS : RESET_HOLD_TICKS;
            if (holdingTicks >= targetTicks) {
                ISkill skill = heldComponent.getSkill();
                if (skill != null) {
                    if (holdingButton == 0 && heldComponent.isAvailable()) {
                        // Unlock
                        PacketHandler.sendToServer(new UnlockSkillPacket(skill.getSkillId()));
                        mc.thePlayer.playSound("random.orb", 0.7f, 1.2f);
                    } else if (holdingButton == 1 && heldComponent.isUnlocked()) {
                        // Reset/forget individual skill
                        PacketHandler.sendToServer(new ForgetSkillPacket(skill.getSkillId()));
                        mc.thePlayer.playSound("random.break", 0.7f, 0.9f);
                    }
                }
                heldComponent = null;
                holdingButton = -1;
                holdingTicks = 0;
            }
        }
    }

    private boolean isInsideCanvas(int x, int y) {
        return x >= guiLeft + CANVAS_X && x <= guiLeft + CANVAS_X + CANVAS_W &&
               y >= guiTop + CANVAS_Y && y <= guiTop + CANVAS_Y + CANVAS_H;
    }
}
