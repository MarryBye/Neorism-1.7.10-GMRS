package com.marrybye.neorism.client.gui.skills;

import java.util.*;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.Gui;

/**
 * Renders orthogonal connections between parent and child skill nodes in the skill graph.
 */
@SideOnly(Side.CLIENT)
public class SkillSegmentConnection {

    public enum State {
        LOCKED(0xFF404040, 0xFF181818),
        AVAILABLE(0xFFC0C0C0, 0xFF303030),
        UNLOCKED(0xFF50E050, 0xFF104010);

        public final int innerColor;
        public final int outerColor;

        State(int innerColor, int outerColor) {
            this.innerColor = innerColor;
            this.outerColor = outerColor;
        }
    }

    private final List<SkillSegmentComponent> parents;
    private final List<SkillSegmentComponent> children;

    public SkillSegmentConnection(List<SkillSegmentComponent> parents, List<SkillSegmentComponent> children) {
        this.parents = parents;
        this.children = children;
    }

    public static List<SkillSegmentConnection> buildConnections(Collection<SkillSegmentComponent> components) {
        Map<Set<SkillSegmentComponent>, List<SkillSegmentComponent>> grouped =
            new LinkedHashMap<Set<SkillSegmentComponent>, List<SkillSegmentComponent>>();

        for (SkillSegmentComponent child : components) {
            if (!child.getParents().isEmpty()) {
                Set<SkillSegmentComponent> parentSet = new HashSet<SkillSegmentComponent>(child.getParents());
                List<SkillSegmentComponent> list = grouped.get(parentSet);
                if (list == null) {
                    list = new ArrayList<SkillSegmentComponent>();
                    grouped.put(parentSet, list);
                }
                list.add(child);
            }
        }

        List<SkillSegmentConnection> result = new ArrayList<SkillSegmentConnection>();
        for (Map.Entry<Set<SkillSegmentComponent>, List<SkillSegmentComponent>> entry : grouped.entrySet()) {
            result.add(new SkillSegmentConnection(new ArrayList<SkillSegmentComponent>(entry.getKey()), entry.getValue()));
        }
        return result;
    }

    public void draw(int offsetX, int offsetY) {
        if (parents.isEmpty() || children.isEmpty()) return;

        // Determine highest state among children
        State highestState = State.LOCKED;
        for (SkillSegmentComponent c : children) {
            State cs = c.getConnectionState();
            if (cs.ordinal() > highestState.ordinal()) {
                highestState = cs;
            }
        }

        int childY = children.get(0).getY() + offsetY;
        int upperY = childY - 35;
        int lowerY = childY - 15;

        int trunkSum = 0;
        for (SkillSegmentComponent p : parents) {
            trunkSum += p.getX() + offsetX;
        }
        int trunkX = trunkSum / parents.size();

        // 1. Draw parent stems down to junction
        for (SkillSegmentComponent p : parents) {
            int px = p.getX() + offsetX;
            int py = p.getY() + offsetY + 24;
            drawStem(px, py, upperY, highestState);
        }

        // 2. Horizontal junction bar across parents
        int minParentX = trunkX;
        int maxParentX = trunkX;
        for (SkillSegmentComponent p : parents) {
            int px = p.getX() + offsetX;
            minParentX = Math.min(minParentX, px);
            maxParentX = Math.max(maxParentX, px);
        }
        if (minParentX != maxParentX) {
            drawHLine(minParentX, maxParentX, upperY, highestState);
        }

        // 3. Vertical trunk line from upperY to lowerY
        drawVLine(trunkX, upperY, lowerY, highestState);

        // 4. Branch lines to each child
        for (SkillSegmentComponent child : children) {
            int cx = child.getX() + offsetX;
            State cs = child.getConnectionState();
            if (cx != trunkX) {
                drawHLine(Math.min(trunkX, cx), Math.max(trunkX, cx), lowerY, cs);
            }
            drawVLine(cx, lowerY, child.getY() + offsetY - 2, cs);
        }
    }

    private void drawStem(int x, int y1, int y2, State state) {
        if (y2 >= y1) {
            drawVLine(x, y1, y2, state);
        }
    }

    private void drawVLine(int x, int y1, int y2, State state) {
        int top = Math.min(y1, y2);
        int bottom = Math.max(y1, y2);
        // Outer black borders
        Gui.drawRect(x - 2, top, x - 1, bottom + 1, state.outerColor);
        Gui.drawRect(x + 1, top, x + 2, bottom + 1, state.outerColor);
        // Inner color line
        Gui.drawRect(x - 1, top, x + 1, bottom + 1, state.innerColor);
    }

    private void drawHLine(int x1, int x2, int y, State state) {
        int left = Math.min(x1, x2);
        int right = Math.max(x1, x2);
        // Outer black borders
        Gui.drawRect(left, y - 2, right + 1, y - 1, state.outerColor);
        Gui.drawRect(left, y + 1, right + 1, y + 2, state.outerColor);
        // Inner color line
        Gui.drawRect(left, y - 1, right + 1, y + 1, state.innerColor);
    }
}
