package com.marrybye.neorism.client.gui.skills;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.skills.SkillHandler;
import com.marrybye.neorism.common.skills.SkillSegment;
import com.marrybye.neorism.common.util.Helper;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * Visual node representation of a skill segment on the interactive skill tree canvas.
 */
@SideOnly(Side.CLIENT)
public class SkillSegmentComponent {

    private static final ResourceLocation TEX_NODE = new ResourceLocation("factionapi", "textures/gui/sprites/skills_screen/node.png");
    private static final ResourceLocation TEX_START_NODE = new ResourceLocation("factionapi", "textures/gui/sprites/skills_screen/start_node.png");
    private static final ResourceLocation TEX_UNLOCK = new ResourceLocation("factionapi", "textures/gui/sprites/skills_screen/node_unlock.png");
    private static final ResourceLocation TEX_RESET = new ResourceLocation("factionapi", "textures/gui/sprites/skills_screen/node_reset.png");

    private final SkillSegment segment;
    private final SkillHandler skillHandler;
    private final List<SkillSegmentComponent> parents = new ArrayList<SkillSegmentComponent>();
    private final List<SkillSegmentComponent> children = new ArrayList<SkillSegmentComponent>();

    public SkillSegmentComponent(SkillSegment segment, SkillHandler skillHandler) {
        this.segment = segment;
        this.skillHandler = skillHandler;
    }

    public SkillSegment getSegment() {
        return segment;
    }

    public ISkill getSkill() {
        return segment.getSkill();
    }

    public int getX() {
        return segment.getX();
    }

    public int getY() {
        return segment.getY();
    }

    public List<SkillSegmentComponent> getParents() {
        return parents;
    }

    public List<SkillSegmentComponent> getChildren() {
        return children;
    }

    public boolean isUnlocked() {
        ISkill skill = getSkill();
        return skill != null && skillHandler != null && skillHandler.isSkillUnlocked(skill.getSkillId());
    }

    public boolean isExclusiveLocked() {
        if (segment == null || skillHandler == null) return false;
        for (ISkillSegment ex : segment.getExclusiveSegments()) {
            for (ISkill exSkill : ex.getSkills()) {
                if (skillHandler.isSkillUnlocked(exSkill.getSkillId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPrerequisitesMet() {
        if (segment == null || segment.isRoot()) return true;
        if (!parents.isEmpty()) {
            for (SkillSegmentComponent p : parents) {
                if (p.isUnlocked()) return true;
            }
            return false;
        }
        // Fallback: check segment's parents directly
        for (ISkillSegment p : segment.getParents()) {
            for (ISkill ps : p.getSkills()) {
                if (skillHandler != null && skillHandler.isSkillUnlocked(ps.getSkillId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasLevel() {
        ISkill skill = getSkill();
        if (skill == null) return true;
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer != null && Helper.getLevel(mc.thePlayer) >= skill.getRequiredLevel();
    }

    public boolean hasPoints() {
        ISkill skill = getSkill();
        if (skill == null) return true;
        return skillHandler != null && skillHandler.getSkillPoints() >= skill.getSkillPointCost();
    }

    public boolean isAvailable() {
        return !isUnlocked() && !isExclusiveLocked() && isPrerequisitesMet();
    }

    public boolean canUnlock() {
        return isAvailable() && hasLevel() && hasPoints();
    }

    public SkillSegmentConnection.State getConnectionState() {
        if (isUnlocked()) return SkillSegmentConnection.State.UNLOCKED;
        if (isAvailable()) return SkillSegmentConnection.State.AVAILABLE;
        return SkillSegmentConnection.State.LOCKED;
    }

    public boolean isMouseOver(int mouseX, int mouseY, int offsetX, int offsetY) {
        int drawX = getX() + offsetX - 13;
        int drawY = getY() + offsetY - 13;
        return mouseX >= drawX && mouseX <= drawX + 26 && mouseY >= drawY && mouseY <= drawY + 26;
    }

    public void draw(int offsetX, int offsetY, float holdProgress, boolean isHoldingThis) {
        Minecraft mc = Minecraft.getMinecraft();
        int drawX = getX() + offsetX - 13;
        int drawY = getY() + offsetY - 13;

        boolean unlocked = isUnlocked();
        boolean available = isAvailable();

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        // 1. Draw node frame
        ResourceLocation frameTex = segment.isRoot() ? TEX_START_NODE : TEX_NODE;
        mc.getTextureManager().bindTexture(frameTex);

        if (unlocked) {
            GL11.glColor4f(0.3f, 1.0f, 0.3f, 1.0f); // Bright green tint
        } else if (available) {
            GL11.glColor4f(1.0f, 0.9f, 0.5f, 1.0f); // Warm yellow tint
        } else {
            GL11.glColor4f(0.5f, 0.5f, 0.5f, 0.8f); // Dark dimmed tint
        }
        Gui.func_146110_a(drawX, drawY, 0, 0, 26, 26, 26, 26);

        // 2. Draw skill icon
        ISkill skill = getSkill();
        if (skill != null && skill.getIcon() != null) {
            mc.getTextureManager().bindTexture(skill.getIcon());
            if (unlocked || available) {
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            } else {
                GL11.glColor4f(0.4f, 0.4f, 0.4f, 0.7f);
            }
            Gui.func_146110_a(drawX + 5, drawY + 5, 0, 0, 16, 16, 16, 16);
        }

        // 3. Draw hold progress indicator
        if (isHoldingThis && holdProgress > 0) {
            float angle = holdProgress * 360.0f;
            GL11.glColor4f(1.0f, 1.0f, 0.2f, 0.6f);
            // Draw radial ring around node
            com.marrybye.neorism.client.gui.radial.GuiRadialMenu.drawSlice(
                drawX + 13, drawY + 13, 0, 13, 16, -90, -90 + angle, 255, 220, 40, 180
            );
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public void drawTooltip(int mouseX, int mouseY, FontRenderer font) {
        ISkill skill = getSkill();
        if (skill == null) return;

        List<String> lines = new ArrayList<String>();
        String name = StatCollector.translateToLocal(skill.getTranslationKey());
        boolean unlocked = isUnlocked();
        boolean exclusive = isExclusiveLocked();
        boolean prereq = isPrerequisitesMet();
        boolean levelMet = hasLevel();
        boolean pointsMet = hasPoints();

        if (unlocked) {
            lines.add(EnumChatFormatting.GREEN + EnumChatFormatting.BOLD.toString() + name);
            lines.add(EnumChatFormatting.DARK_GREEN + "✔ " + StatCollector.translateToLocal("gui.factionapi.skills.unlocked"));
            lines.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("gui.factionapi.skills.refund"));
        } else if (exclusive) {
            lines.add(EnumChatFormatting.GRAY + EnumChatFormatting.BOLD.toString() + name);
            lines.add(EnumChatFormatting.RED + StatCollector.translateToLocal("gui.factionapi.skills.locked_exclusive"));
        } else if (!prereq) {
            lines.add(EnumChatFormatting.GRAY + EnumChatFormatting.BOLD.toString() + name);
            lines.add(EnumChatFormatting.RED + StatCollector.translateToLocal("gui.factionapi.skills.unlock_parent_first"));
            lines.add(EnumChatFormatting.GOLD + String.format(StatCollector.translateToLocal("gui.factionapi.skills.cost"), skill.getSkillPointCost()));
        } else if (!levelMet) {
            lines.add(EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD.toString() + name);
            lines.add(EnumChatFormatting.RED + String.format(StatCollector.translateToLocal("gui.factionapi.skills.requires_level"), skill.getRequiredLevel()));
            lines.add(EnumChatFormatting.GOLD + String.format(StatCollector.translateToLocal("gui.factionapi.skills.cost"), skill.getSkillPointCost()));
        } else if (!pointsMet) {
            lines.add(EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD.toString() + name);
            int currentPts = skillHandler != null ? skillHandler.getSkillPoints() : 0;
            lines.add(EnumChatFormatting.RED + String.format(StatCollector.translateToLocal("gui.factionapi.skills.not_enough_points"), currentPts, skill.getSkillPointCost()));
            lines.add(EnumChatFormatting.GOLD + String.format(StatCollector.translateToLocal("gui.factionapi.skills.cost"), skill.getSkillPointCost()));
        } else {
            lines.add(EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD.toString() + name);
            lines.add(EnumChatFormatting.GOLD + String.format(StatCollector.translateToLocal("gui.factionapi.skills.cost"), skill.getSkillPointCost()));
            lines.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocal("gui.factionapi.skills.hold_to_unlock"));
        }

        // Description
        String descKey = skill.getDescriptionTranslationKey();
        if (StatCollector.canTranslate(descKey)) {
            String desc = StatCollector.translateToLocal(descKey);
            lines.add("");
            // Wrap to 180px
            List<String> wrapped = font.listFormattedStringToWidth(desc, 180);
            for (String w : wrapped) {
                lines.add(EnumChatFormatting.ITALIC.toString() + EnumChatFormatting.GRAY + w);
            }
        }

        // Draw tooltip box
        int tooltipWidth = 0;
        for (String s : lines) {
            int w = font.getStringWidth(s);
            if (w > tooltipWidth) tooltipWidth = w;
        }
        tooltipWidth += 12;
        int tooltipHeight = lines.size() * 10 + 8;

        int tipX = mouseX + 12;
        int tipY = mouseY - 12;

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 300);
        Gui.drawRect(tipX - 1, tipY - 1, tipX + tooltipWidth + 1, tipY + tooltipHeight + 1, 0xFF101018);
        Gui.drawRect(tipX, tipY, tipX + tooltipWidth, tipY + tooltipHeight, 0xF0181824);

        int textY = tipY + 4;
        for (String line : lines) {
            font.drawStringWithShadow(line, tipX + 6, textY, 0xFFFFFF);
            textY += 10;
        }
        GL11.glPopMatrix();
    }
}
