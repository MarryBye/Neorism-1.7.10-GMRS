package com.marrybye.neorism.client.gui.skills;

import java.util.*;

import com.marrybye.neorism.common.skills.SkillTreeLayout;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.Gui;

/**
 * Canonical port of de.teamlapen.faction.client.gui.screens.skills.SkillSegmentConnection.
 * Renders orthogonal connections between parent and child skill nodes in two passes (outer border then inner fill).
 */
@SideOnly(Side.CLIENT)
public class SkillSegmentConnection {

    private static final int ROW_GAP = SkillTreeLayout.ROW_HEIGHT - SkillTreeLayout.SKILL_SIZE;
    private static final int UPPER_OFFSET = ROW_GAP - ROW_GAP / 3;
    private static final int LOWER_OFFSET = ROW_GAP / 3;

    public enum State {
        VISIBLE(0xFF222222, 0xFF3F3F3F, 0),
        LOCKED(0xFFCF0000, 0xFF6A0000, 1),
        AVAILABLE(0xFFA7A7A7, 0xFFFFFFFF, 2),
        UNLOCKED(0xFF008711, 0xFF005304, 3);

        public final int innerColor;
        public final int outerColor;
        public final int priority;

        State(int innerColor, int outerColor, int priority) {
            this.innerColor = innerColor;
            this.outerColor = outerColor;
            this.priority = priority;
        }

        public int pathColor(boolean outer) {
            return outer ? this.outerColor : this.innerColor;
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
                Set<SkillSegmentComponent> parentSet = new LinkedHashSet<SkillSegmentComponent>(child.getParents());
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

    public void draw(int offsetX, int offsetY, boolean outerLine) {
        if (parents.isEmpty() || children.isEmpty()) return;

        int childY = children.get(0).getY() + offsetY;
        int upperY = childY - UPPER_OFFSET;
        int lowerY = childY - LOWER_OFFSET;

        double sumX = 0;
        for (SkillSegmentComponent p : parents) {
            sumX += p.getX();
        }
        int trunkX = (int) Math.round(sumX / parents.size()) + offsetX;
        int sharedColor = sharedState(children).pathColor(outerLine);

        List<SkillSegmentComponent> lockedParents = new ArrayList<SkillSegmentComponent>();
        List<SkillSegmentComponent> unlockedParents = new ArrayList<SkillSegmentComponent>();
        for (SkillSegmentComponent p : parents) {
            if (p.getConnectionState() == State.UNLOCKED) {
                unlockedParents.add(p);
            } else {
                lockedParents.add(p);
            }
        }

        drawStems(lockedParents, offsetX, offsetY, trunkX, upperY, State.VISIBLE.pathColor(outerLine), outerLine);
        drawStems(unlockedParents, offsetX, offsetY, trunkX, upperY, sharedColor, outerLine);

        verticalLine(trunkX, upperY, lowerY, sharedColor, outerLine);

        List<SkillSegmentComponent> sortedChildren = new ArrayList<SkillSegmentComponent>(children);
        Collections.sort(sortedChildren, new Comparator<SkillSegmentComponent>() {
            @Override
            public int compare(SkillSegmentComponent a, SkillSegmentComponent b) {
                return Integer.compare(a.getConnectionState().priority, b.getConnectionState().priority);
            }
        });

        for (SkillSegmentComponent child : sortedChildren) {
            int childX = child.getX() + offsetX;
            int color = child.getConnectionState().pathColor(outerLine);
            if (childX != trunkX) {
                horizontalLine(Math.min(trunkX, childX), Math.max(trunkX, childX), lowerY, color, outerLine);
            }
            verticalLine(childX, lowerY, childY, color, outerLine);
        }
    }

    private static void drawStems(List<SkillSegmentComponent> parents, int offsetX, int offsetY, int trunkX, int upperY, int color, boolean outerLine) {
        if (parents.isEmpty()) return;

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        for (SkillSegmentComponent parent : parents) {
            int px = parent.getX() + offsetX;
            int py = parent.getY() + offsetY + SkillTreeLayout.SKILL_SIZE - 1;
            verticalLine(px, py, upperY, color, outerLine);
            if (px < minX) minX = px;
            if (px > maxX) maxX = px;
        }

        int barLeft = Math.min(trunkX, minX);
        int barRight = Math.max(trunkX, maxX);
        if (barLeft != barRight) {
            horizontalLine(barLeft, barRight, upperY, color, outerLine);
        }
    }

    private static State sharedState(List<SkillSegmentComponent> visibleChildren) {
        State best = State.VISIBLE;
        for (SkillSegmentComponent c : visibleChildren) {
            State s = c.getConnectionState();
            if (s.priority > best.priority) {
                best = s;
            }
        }
        return best;
    }

    private static void horizontalLine(int left, int right, int y, int color, boolean outerLine) {
        if (outerLine) {
            fillHLine(left - 1, right + 1, y - 1, color);
            fillHLine(left - 1, right + 1, y + 1, color);
        } else {
            fillHLine(left, right, y, color);
        }
    }

    private static void verticalLine(int x, int top, int bottom, int color, boolean outerLine) {
        if (outerLine) {
            fillVLine(x - 1, top - 2, bottom + 2, color);
            fillVLine(x + 1, top - 2, bottom + 2, color);
        } else {
            fillVLine(x, top - 1, bottom + 1, color);
        }
    }

    private static void fillHLine(int x1, int x2, int y, int color) {
        int l = Math.min(x1, x2);
        int r = Math.max(x1, x2);
        Gui.drawRect(l, y, r + 1, y + 1, color);
    }

    private static void fillVLine(int x, int y1, int y2, int color) {
        int t = Math.min(y1, y2);
        int b = Math.max(y1, y2);
        Gui.drawRect(x, t + 1, x + 1, b, color);
    }
}
