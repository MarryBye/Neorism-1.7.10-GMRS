package com.marrybye.neorism.client.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.client.gui.radial.SelectActionRadialScreen;
import com.marrybye.neorism.client.gui.skills.SkillsScreen;
import com.marrybye.neorism.client.input.KeyBindings;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.items.ItemRefinement;
import com.marrybye.neorism.common.util.Helper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * Full Faction Dashboard & Character Sheet (opened with [P]).
 * 100% faithful backport of TeamLapen's FactionMenuScreen.
 * Features:
 * - 3D player avatar rendering
 * - Level and Altar of Inspiration level progress bar
 * - 3 Canonical Accessory Refinement slots (Amulet, Ring, Obi Belt)
 * - Skills tree launcher button
 * - Appearance customization launcher button
 * - Action Rose configuration button
 * - Right panel: Faction objectives, lord rank, and stats
 */
@SideOnly(Side.CLIENT)
public class FactionMenuScreen extends GuiScreen {

    public static final int MENU_WIDTH = 234;
    public static final int MENU_HEIGHT = 205;

    private static final ResourceLocation BACKGROUND_REFINEMENTS =
        new ResourceLocation(Neorism.MODID, "textures/gui/container/faction_menu_refinements.png");
    private static final ResourceLocation SKILLS_ICON =
        new ResourceLocation(Neorism.MODID, "textures/gui/sprites/widget/skills.png");
    private static final ResourceLocation APPEARANCE_ICON =
        new ResourceLocation(Neorism.MODID, "textures/gui/sprites/widget/appearance.png");
    private static final ResourceLocation SETTINGS_ICON =
        new ResourceLocation(Neorism.MODID, "textures/gui/sprites/widget/settings.png");

    private static final RenderItem itemRender = new RenderItem();

    private final EntityPlayer player;
    private final FactionPlayerHandler factionPlayer;

    private int guiLeft;
    private int guiTop;
    private GuiButton btnSkills;
    private GuiButton btnAppearance;
    private GuiButton btnSettings;

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

        // 1. Skills button (20x20 at guiLeft + 7, guiTop + 90)
        btnSkills = new CustomImageButton(1, guiLeft + 7, guiTop + 90, 20, 20, SKILLS_ICON);
        buttonList.add(btnSkills);

        // 2. Appearance button (20x20 at guiLeft + 29, guiTop + 90)
        btnAppearance = new CustomImageButton(2, guiLeft + 29, guiTop + 90, 20, 20, APPEARANCE_ICON);
        buttonList.add(btnAppearance);

        // 3. Action radial configuration button (20x20 at guiLeft + 7, guiTop + 115)
        btnSettings = new CustomImageButton(3, guiLeft + 7, guiTop + 115, 20, 20, SETTINGS_ICON);
        buttonList.add(btnSettings);
    }

    private static class CustomImageButton extends GuiButton {
        private final ResourceLocation icon;

        public CustomImageButton(int id, int x, int y, int w, int h, ResourceLocation icon) {
            super(id, x, y, w, h, "");
            this.icon = icon;
        }

        @Override
        public void drawButton(net.minecraft.client.Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                mc.getTextureManager().bindTexture(icon);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition &&
                                  mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                if (hovered) {
                    GL11.glColor4f(1.0f, 0.9f, 0.4f, 1.0f);
                } else {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                func_146110_a(this.xPosition, this.yPosition, 0, 0, this.width, this.height, this.width, this.height);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) { // Skills
            mc.displayGuiScreen(new SkillsScreen(player, this));
        } else if (button.id == 2) { // Appearance
            mc.displayGuiScreen(new VampirePlayerAppearanceScreen(player, this));
        } else if (button.id == 3) { // Actions
            if (KeyBindings.keyAction != null) {
                SelectActionRadialScreen.show(KeyBindings.keyAction);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // 1. Draw Menu Background (with refinement accessory slots)
        mc.getTextureManager().bindTexture(BACKGROUND_REFINEMENTS);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        func_146110_a(guiLeft, guiTop, 0, 0, MENU_WIDTH, MENU_HEIGHT, 256, 256);

        // 2. Draw 3D Player Avatar
        int modelX = guiLeft + 31;
        int modelY = guiTop + 72;
        GuiInventory.func_147046_a(modelX, modelY, 28, (float) (modelX) - mouseX, (float) (modelY - 45) - mouseY, player);

        // 3. Draw Level Progress Bar & Label
        String factionName = factionPlayer != null ? StatCollector.translateToLocal(factionPlayer.getCurrentFaction().getTranslationKey()) : "Neutral";
        int curLevel = factionPlayer != null ? factionPlayer.getCurrentLevel() : 0;
        int maxLevel = 14;

        // Level text
        String levelText = factionName + " - Lvl " + curLevel;
        fontRendererObj.drawString(EnumChatFormatting.GOLD + levelText, guiLeft + 8, guiTop + 76, 0xFFFFFF);

        // Progress bar background & fill (width 46, height 3 at guiLeft + 8, guiTop + 85)
        int barX = guiLeft + 8;
        int barY = guiTop + 85;
        int barW = 46;
        int barH = 3;
        drawRect(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF222222);
        drawRect(barX, barY, barX + barW, barY + barH, 0xFF444444);
        int fillW = (int) (barW * Math.min(1.0f, (float) curLevel / (float) maxLevel));
        if (fillW > 0) {
            int barColor = Helper.isVampire(player) ? 0xFFA01020 : (Helper.isHunter(player) ? 0xFF2060C0 : 0xFF20A030);
            drawRect(barX, barY, barX + fillW, barY + barH, barColor);
        }

        // 4. Render 3 Accessory Refinement Items (Amulet, Ring, Obi Belt)
        if (factionPlayer != null) {
            int[][] slotPos = {{58, 8}, {58, 26}, {58, 44}};
            for (int i = 0; i < 3; i++) {
                ItemStack refStack = factionPlayer.getRefinement(i);
                int sx = guiLeft + slotPos[i][0];
                int sy = guiTop + slotPos[i][1];
                if (refStack != null) {
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), refStack, sx, sy);
                    itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.getTextureManager(), refStack, sx, sy);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glPopMatrix();
                }
            }
        }

        // 5. Draw Right Panel: Tasks & Character Information
        int rightX = guiLeft + 88;
        int rightY = guiTop + 10;
        String taskTitle = StatCollector.translateToLocal("gui.factionapi.faction_menu.tasks");
        if (taskTitle.equals("gui.factionapi.faction_menu.tasks")) taskTitle = "Faction Tasks & Stats";
        fontRendererObj.drawString(EnumChatFormatting.DARK_RED + EnumChatFormatting.BOLD.toString() + taskTitle, rightX, rightY, 0x440000);

        // Status lines
        int lineY = rightY + 16;
        int lordLvl = factionPlayer != null ? factionPlayer.getLordLevel() : 0;
        fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + "Lord Rank: " + EnumChatFormatting.BLACK + (lordLvl > 0 ? "Rank " + lordLvl : "None"), rightX, lineY, 0x222222);
        lineY += 12;

        if (Helper.isVampire(player)) {
            com.marrybye.neorism.common.vampire.VampirePlayer vp = com.marrybye.neorism.common.vampire.VampirePlayer.get(player);
            int blood = vp != null ? vp.getBloodStats().getBloodLevel() : 0;
            int maxBlood = vp != null ? vp.getBloodStats().getMaxBlood() : 20;
            fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + "Blood: " + EnumChatFormatting.RED + blood + "/" + maxBlood, rightX, lineY, 0x222222);
            lineY += 12;

            fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + "Next Tier: " + EnumChatFormatting.DARK_PURPLE +
                (curLevel < 4 ? "Altar of Inspiration" : (curLevel < 14 ? "Altar of Infusion" : "Maximum")), rightX, lineY, 0x222222);
            lineY += 16;

            fontRendererObj.drawString(EnumChatFormatting.GRAY + "- Quench thirst by biting prey", rightX, lineY, 0x555555);
            lineY += 11;
            fontRendererObj.drawString(EnumChatFormatting.GRAY + "- Avoid direct scorching sunlight", rightX, lineY, 0x555555);
            lineY += 11;
            fontRendererObj.drawString(EnumChatFormatting.GRAY + "- Infuse weapons at vampire altar", rightX, lineY, 0x555555);
        } else if (Helper.isHunter(player)) {
            fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + "Equipment: " + EnumChatFormatting.BLUE + "Hunter Arsenal", rightX, lineY, 0x222222);
            lineY += 12;
            fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + "Next Tier: " + EnumChatFormatting.BLUE +
                (curLevel < 14 ? "Research Table" : "Master Hunter"), rightX, lineY, 0x222222);
            lineY += 16;

            fontRendererObj.drawString(EnumChatFormatting.GRAY + "- Slay night vampires with axes", rightX, lineY, 0x555555);
            lineY += 11;
            fontRendererObj.drawString(EnumChatFormatting.GRAY + "- Study hunter intel at table", rightX, lineY, 0x555555);
            lineY += 11;
            fontRendererObj.drawString(EnumChatFormatting.GRAY + "- Craft holy water & crucifixes", rightX, lineY, 0x555555);
        } else {
            fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + "Status: " + EnumChatFormatting.DARK_GREEN + "Neutral Wanderer", rightX, lineY, 0x222222);
            lineY += 16;
            fontRendererObj.drawString(EnumChatFormatting.GRAY + "- Be bitten to become a vampire", rightX, lineY, 0x555555);
            lineY += 11;
            fontRendererObj.drawString(EnumChatFormatting.GRAY + "- Seek hunter instructor for training", rightX, lineY, 0x555555);
        }

        // 6. Draw Buttons
        super.drawScreen(mouseX, mouseY, partialTicks);

        // 7. Draw Tooltips
        if (btnSkills.func_146115_a()) {
            String tooltip = StatCollector.translateToLocal("gui.factionapi.faction_menu.skill_screen");
            if (tooltip.equals("gui.factionapi.faction_menu.skill_screen")) tooltip = "Skill Tree ([K])";
            drawCreativeTabHoveringText(tooltip, mouseX, mouseY);
        } else if (btnAppearance.func_146115_a()) {
            String tooltip = StatCollector.translateToLocal("gui.factionapi.faction_menu.appearance_menu");
            if (tooltip.equals("gui.factionapi.faction_menu.appearance_menu")) tooltip = "Customize Appearance";
            drawCreativeTabHoveringText(tooltip, mouseX, mouseY);
        } else if (btnSettings.func_146115_a()) {
            String tooltip = StatCollector.translateToLocal("gui.factionapi.faction_menu.edit_actions");
            if (tooltip.equals("gui.factionapi.faction_menu.edit_actions")) tooltip = "Configure Action Radial ([R])";
            drawCreativeTabHoveringText(tooltip, mouseX, mouseY);
        }

        // 8. Refinement Slot Tooltips
        if (factionPlayer != null) {
            int[][] slotPos = {{58, 8}, {58, 26}, {58, 44}};
            String[] slotNames = {"Amulet Slot", "Ring Slot", "Obi Belt Slot"};
            for (int i = 0; i < 3; i++) {
                int sx = guiLeft + slotPos[i][0];
                int sy = guiTop + slotPos[i][1];
                if (mouseX >= sx && mouseX < sx + 16 && mouseY >= sy && mouseY < sy + 16) {
                    ItemStack refStack = factionPlayer.getRefinement(i);
                    if (refStack != null) {
                        renderToolTip(refStack, mouseX, mouseY);
                    } else {
                        drawCreativeTabHoveringText(slotNames[i], mouseX, mouseY);
                    }
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        // Refinement slot click to equip/unequip
        if (button == 0 && factionPlayer != null) {
            int[][] slotPos = {{58, 8}, {58, 26}, {58, 44}};
            ItemRefinement.Type[] expectedTypes = {
                ItemRefinement.Type.AMULET,
                ItemRefinement.Type.RING,
                ItemRefinement.Type.OBI_BELT
            };

            for (int i = 0; i < 3; i++) {
                int sx = guiLeft + slotPos[i][0];
                int sy = guiTop + slotPos[i][1];
                if (mouseX >= sx && mouseX < sx + 16 && mouseY >= sy && mouseY < sy + 16) {
                    ItemStack equipped = factionPlayer.getRefinement(i);
                    if (equipped != null) {
                        // Unequip to player inventory
                        if (player.inventory.addItemStackToInventory(equipped)) {
                            factionPlayer.setRefinement(i, null);
                        }
                    } else {
                        // Look in player inventory for matching refinement item
                        for (int inv = 0; inv < player.inventory.mainInventory.length; inv++) {
                            ItemStack invStack = player.inventory.mainInventory[inv];
                            if (invStack != null && invStack.getItem() instanceof ItemRefinement) {
                                ItemRefinement refItem = (ItemRefinement) invStack.getItem();
                                if (refItem.getAccessoryType() == expectedTypes[i]) {
                                    factionPlayer.setRefinement(i, invStack.copy());
                                    player.inventory.setInventorySlotContents(inv, null);
                                    break;
                                }
                            }
                        }
                    }
                    return;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
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
