package com.marrybye.neorism.client.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.network.packets.AppearancePacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

/**
 * Vampire Player Appearance Customization Screen.
 * Faithful 1.7.10 backport of TeamLapen's VampirePlayerAppearanceScreen.
 * Configures fangs (0-6), eyes (0-18), glowing eyes toggle, and wings toggle & style.
 */
@SideOnly(Side.CLIENT)
public class VampirePlayerAppearanceScreen extends GuiScreen {

    private final EntityPlayer player;
    private final GuiScreen parentScreen;
    private final FactionPlayerHandler fph;

    private int fangType;
    private int eyeType;
    private boolean glowingEyes;
    private boolean wings;
    private int wingsType;

    private static final String[] WINGS_STYLES = {"Default", "Castle Contest", "Developer"};

    public VampirePlayerAppearanceScreen(EntityPlayer player, GuiScreen parentScreen) {
        this.player = player;
        this.parentScreen = parentScreen;
        this.fph = FactionPlayerHandler.get(player);
        if (fph != null) {
            this.fangType = fph.getFangType();
            this.eyeType = fph.getEyeType();
            this.glowingEyes = fph.hasGlowingEyes();
            this.wings = fph.hasWings();
            this.wingsType = fph.getWingsType();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        int centerX = width / 2;
        int centerY = height / 2;

        // Fang Selection Button (ID 1)
        buttonList.add(new GuiButton(1, centerX + 10, centerY - 60, 140, 20, getFangText()));

        // Eye Selection Button (ID 2)
        buttonList.add(new GuiButton(2, centerX + 10, centerY - 35, 140, 20, getEyeText()));

        // Glowing Eyes Button (ID 3)
        buttonList.add(new GuiButton(3, centerX + 10, centerY - 10, 140, 20, getGlowingText()));

        // Wings Toggle Button (ID 4)
        buttonList.add(new GuiButton(4, centerX + 10, centerY + 15, 140, 20, getWingsText()));

        // Wings Style Button (ID 5)
        buttonList.add(new GuiButton(5, centerX + 10, centerY + 40, 140, 20, getWingsStyleText()));

        // Done / Save Button (ID 6)
        buttonList.add(new GuiButton(6, centerX - 60, centerY + 75, 120, 20, StatCollector.translateToLocal("gui.done")));
    }

    private String getFangText() {
        return "Fangs: Style " + fangType;
    }

    private String getEyeText() {
        return "Eyes: Style " + eyeType;
    }

    private String getGlowingText() {
        return "Glowing Eyes: " + (glowingEyes ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
    }

    private String getWingsText() {
        return "Vampire Wings: " + (wings ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
    }

    private String getWingsStyleText() {
        int idx = Math.max(0, Math.min(WINGS_STYLES.length - 1, wingsType));
        return "Wings: " + WINGS_STYLES[idx];
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        if (btn.id == 1) { // Cycle fangs 0..6
            fangType = (fangType + 1) % 7;
            if (fph != null) fph.setFangType(fangType);
            btn.displayString = getFangText();
        } else if (btn.id == 2) { // Cycle eyes 0..18
            eyeType = (eyeType + 1) % 19;
            if (fph != null) fph.setEyeType(eyeType);
            btn.displayString = getEyeText();
        } else if (btn.id == 3) { // Toggle glowing eyes
            glowingEyes = !glowingEyes;
            if (fph != null) fph.setGlowingEyes(glowingEyes);
            btn.displayString = getGlowingText();
        } else if (btn.id == 4) { // Toggle wings
            wings = !wings;
            if (fph != null) fph.setWings(wings);
            btn.displayString = getWingsText();
        } else if (btn.id == 5) { // Cycle wings style 0..2
            wingsType = (wingsType + 1) % WINGS_STYLES.length;
            if (fph != null) fph.setWingsType(wingsType);
            btn.displayString = getWingsStyleText();
        } else if (btn.id == 6) { // Done
            saveAndClose();
        }
    }

    private void saveAndClose() {
        if (fph != null) {
            fph.setFangType(fangType);
            fph.setEyeType(eyeType);
            fph.setGlowingEyes(glowingEyes);
            fph.setWings(wings);
            fph.setWingsType(wingsType);
        }
        PacketHandler.sendToServer(new AppearancePacket(fangType, eyeType, glowingEyes, wings, wingsType));
        mc.displayGuiScreen(parentScreen);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int centerX = width / 2;
        int centerY = height / 2;

        // Title
        String title = StatCollector.translateToLocal("gui.vampirism.appearance");
        if (title.equals("gui.vampirism.appearance")) title = "Vampire Appearance Customization";
        drawCenteredString(fontRendererObj, EnumChatFormatting.GOLD + EnumChatFormatting.BOLD.toString() + title, centerX, centerY - 85, 0xFFFFFF);

        // 3D Player Model on Left
        int modelX = centerX - 80;
        int modelY = centerY + 55;
        GuiInventory.func_147046_a(modelX, modelY, 45, (float) (modelX) - mouseX, (float) (modelY - 70) - mouseY, player);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            saveAndClose();
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }
}
