package com.marrybye.neorism.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.network.packets.SelectActionPacket;
import com.marrybye.neorism.common.network.packets.UnlockSkillPacket;
import com.marrybye.neorism.common.skills.SkillHandler;
import com.marrybye.neorism.common.util.Helper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

/**
 * Interactive Gothic Skill Tree GUI for Neorism.
 * Allows viewing faction progression, unlocking abilities with skill points, and binding actions to hotkeys [R] and [Z].
 */
@SideOnly(Side.CLIENT)
public class GuiSkillTree extends GuiScreen {

    public static class SkillEntry {
        public final String id;
        public final String name;
        public final String desc;
        public final int requiredLevel;

        public SkillEntry(String id, String name, String desc, int requiredLevel) {
            this.id = id;
            this.name = name;
            this.desc = desc;
            this.requiredLevel = requiredLevel;
        }
    }

    private final List<SkillEntry> availableSkills = new ArrayList<SkillEntry>();
    private int scrollOffset = 0;

    @Override
    public void initGui() {
        super.initGui();
        loadFactionSkills();
        refreshButtons();
    }

    private void loadFactionSkills() {
        availableSkills.clear();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        if (Helper.isVampire(player)) {
            availableSkills.add(new SkillEntry("night_vision", "Ночное зрение", "Позволяет четко видеть в полной темноте без искажений.", 1));
            availableSkills.add(new SkillEntry("regen", "Вампирская регенерация", "Мгновенная вспышка исцеления за счет насыщения крови.", 2));
            availableSkills.add(new SkillEntry("vampire_rage", "Вампирская ярость", "Увеличивает скорость и урон ценой повышенного расхода крови.", 4));
            availableSkills.add(new SkillEntry("dark_blood_projectile", "Кровавый снаряд", "Дальний сгусток тёмной крови, крадущий здоровье цели.", 5));
            availableSkills.add(new SkillEntry("bat", "Облик летучей мыши", "Превращение в маленькую летучую мышь со свободой полёта.", 6));
            availableSkills.add(new SkillEntry("sunscreen", "Солнцезащитная аура", "Временное сопротивление палящим лучам дневного солнца.", 7));
            availableSkills.add(new SkillEntry("teleport", "Теневой скачок", "Мгновенное перемещение вперед сквозь воздух и решетки.", 8));
            availableSkills.add(new SkillEntry("vampire_invisibility", "Теневая невидимость", "Полное сокрытие от глаз мобов и вражеских игроков.", 9));
            availableSkills.add(new SkillEntry("freeze", "Ледяная хватка", "Замораживает противников в радиусе вокруг вампира.", 10));
            availableSkills.add(new SkillEntry("summon_bats", "Призыв стаи мышей", "Призывает кровожадную стаю мышей, терзающих жертву.", 11));
        } else if (Helper.isHunter(player)) {
            availableSkills.add(new SkillEntry("awareness_hunter", "Обострённые чувства", "Подсвечивает ближайших вампиров сквозь стены и блоки.", 1));
            availableSkills.add(new SkillEntry("potion_resistance_hunter", "Стойкость охотника", "Дарует иммунитет к слабости и ядам нечисти.", 4));
        }
    }

    @SuppressWarnings("unchecked")
    private void refreshButtons() {
        this.buttonList.clear();

        int centerX = width / 2;
        int startY = 60;
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        SkillHandler sh = SkillHandler.get(player);
        int points = sh != null ? sh.getSkillPoints() : 0;
        int playerLevel = Helper.getLevel(player);
        String prim = sh != null ? sh.getPrimaryAction() : "";
        String sec = sh != null ? sh.getSecondaryAction() : "";

        int buttonId = 0;
        for (int i = 0; i < availableSkills.size(); i++) {
            SkillEntry entry = availableSkills.get(i);
            int y = startY + i * 36;
            if (y + 30 > height - 40) break; // Screen space cutoff

            boolean unlocked = sh != null && (sh.isSkillUnlocked(entry.id) || sh.isActionUnlocked(entry.id));
            boolean canUnlock = points > 0 && playerLevel >= entry.requiredLevel && !unlocked;

            if (unlocked) {
                // Bind to R button
                boolean isPrim = entry.id.equals(prim);
                GuiButton btnR = new GuiButton(buttonId++, centerX + 70, y + 2, 50, 16, isPrim ? "[R] ★" : "[R]");
                this.buttonList.add(btnR);

                // Bind to Z button
                boolean isSec = entry.id.equals(sec);
                GuiButton btnZ = new GuiButton(buttonId++, centerX + 125, y + 2, 50, 16, isSec ? "[Z] ★" : "[Z]");
                this.buttonList.add(btnZ);
            } else {
                // Unlock button
                GuiButton btnUnlock = new GuiButton(buttonId++, centerX + 70, y + 2, 105, 16,
                    canUnlock ? "Изучить (1 очк)" : (playerLevel < entry.requiredLevel ? "С ур. " + entry.requiredLevel : "Нет очков"));
                btnUnlock.enabled = canUnlock;
                this.buttonList.add(btnUnlock);
            }
        }

        // Close button
        this.buttonList.add(new GuiButton(999, centerX - 50, height - 30, 100, 20, "Закрыть"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 999) {
            mc.displayGuiScreen(null);
            return;
        }

        EntityPlayer player = mc.thePlayer;
        if (player == null) return;
        SkillHandler sh = SkillHandler.get(player);
        if (sh == null) return;

        int buttonId = 0;
        for (int i = 0; i < availableSkills.size(); i++) {
            SkillEntry entry = availableSkills.get(i);
            boolean unlocked = sh.isSkillUnlocked(entry.id) || sh.isActionUnlocked(entry.id);

            if (unlocked) {
                if (button.id == buttonId) {
                    // Bind R
                    PacketHandler.sendToServer(new SelectActionPacket(1, entry.id));
                    sh.setPrimaryAction(entry.id);
                    refreshButtons();
                    return;
                }
                buttonId++;

                if (button.id == buttonId) {
                    // Bind Z
                    PacketHandler.sendToServer(new SelectActionPacket(2, entry.id));
                    sh.setSecondaryAction(entry.id);
                    refreshButtons();
                    return;
                }
                buttonId++;
            } else {
                if (button.id == buttonId) {
                    // Unlock
                    PacketHandler.sendToServer(new UnlockSkillPacket(entry.id));
                    sh.unlockSkill(entry.id);
                    refreshButtons();
                    return;
                }
                buttonId++;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int centerX = width / 2;
        EntityPlayer player = mc.thePlayer;

        // Header Title
        String title = EnumChatFormatting.DARK_RED + "" + EnumChatFormatting.BOLD + "ДРЕВО НАВЫКОВ ФРАКЦИИ";
        drawCenteredString(fontRendererObj, title, centerX, 15, 0xFFFFFF);

        // Faction and Level Subheader
        if (player != null) {
            FactionPlayerHandler fph = FactionPlayerHandler.get(player);
            SkillHandler sh = SkillHandler.get(player);
            String factionName = fph != null && fph.isVampire() ? "Вампир" : (fph != null && fph.isHunter() ? "Охотник на вампиров" : "Человек");
            int level = fph != null ? fph.getCurrentLevel() : 0;
            int points = sh != null ? sh.getSkillPoints() : 0;

            String status = EnumChatFormatting.GOLD + factionName + " (Уровень " + level + ")" +
                EnumChatFormatting.GRAY + " | Доступно очков: " + EnumChatFormatting.GREEN + points;
            drawCenteredString(fontRendererObj, status, centerX, 32, 0xFFFFFF);

            // Hotkeys hint
            String hint = EnumChatFormatting.DARK_GRAY + "Активация способностей в игре: [R] — Первичная, [Z] — Вторичная";
            drawCenteredString(fontRendererObj, hint, centerX, 44, 0xAAAAAA);

            // Skill items
            int startY = 60;
            for (int i = 0; i < availableSkills.size(); i++) {
                SkillEntry entry = availableSkills.get(i);
                int y = startY + i * 36;
                if (y + 30 > height - 40) break;

                boolean unlocked = sh != null && (sh.isSkillUnlocked(entry.id) || sh.isActionUnlocked(entry.id));
                int nameColor = unlocked ? 0x55FF55 : (level >= entry.requiredLevel ? 0xFFFFFF : 0x888888);

                drawString(fontRendererObj, (unlocked ? "✔ " : "○ ") + entry.name, centerX - 180, y, nameColor);
                drawString(fontRendererObj, EnumChatFormatting.GRAY + entry.desc, centerX - 170, y + 12, 0x888888);
            }

            if (availableSkills.isEmpty()) {
                drawCenteredString(fontRendererObj, EnumChatFormatting.YELLOW + "Вы нейтральны. Вступите в ряды Вампиров или Охотников!", centerX, 100, 0xFFFFFF);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
