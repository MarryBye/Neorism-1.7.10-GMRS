package com.marrybye.neorism.common.skills;

import java.util.*;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;

/**
 * Calculates and stores the visual (x, y) layout of a skill tree graph.
 * Faithfully ports TeamLapen's isotonic regression and branch balancing layout engine.
 */
public class SkillTreeLayout {

    public static final int SKILL_SIZE = 26;
    public static final int SKILL_GAP = 10;
    public static final int SEGMENT_GAP = 30;
    public static final int ROW_HEIGHT = 60;

    private static final int RELAX_PASSES = 3;
    private static final double CHAIN_WEIGHT = 4;
    private static final double PIN_WEIGHT = 1000;

    public static class Placement {
        public final SkillSegment segment;
        public int x;
        public int y;
        public final int width;

        public Placement(SkillSegment segment, int x, int y, int width) {
            this.segment = segment;
            this.x = x;
            this.y = y;
            this.width = width;
        }
    }

    private static class Target {
        final double value;
        final double weight;
        Target(double value, double weight) {
            this.value = value;
            this.weight = weight;
        }
    }

    private final Map<SkillSegment, Placement> placements = new LinkedHashMap<SkillSegment, Placement>();
    private int minX;
    private int maxX;
    private int height;

    public static SkillTreeLayout layout(SkillTree tree) {
        SkillTreeLayout layout = new SkillTreeLayout();
        List<SkillSegment> all = new ArrayList<SkillSegment>();
        for (ISkillSegment seg : tree.getAllSegments()) {
            all.add((SkillSegment) seg);
        }

        SortedMap<Integer, List<SkillSegment>> rows = new TreeMap<Integer, List<SkillSegment>>();
        for (SkillSegment seg : all) {
            int d = seg.getDepth();
            List<SkillSegment> row = rows.get(d);
            if (row == null) {
                row = new ArrayList<SkillSegment>();
                rows.put(d, row);
            }
            row.add(seg);
        }

        for (Map.Entry<Integer, List<SkillSegment>> entry : rows.entrySet()) {
            List<SkillSegment> r = entry.getValue();
            Collections.sort(r, new Comparator<SkillSegment>() {
                @Override
                public int compare(SkillSegment a, SkillSegment b) {
                    return a.getId().compareTo(b.getId());
                }
            });
            entry.setValue(orderByPlacement(r));
        }

        for (Map.Entry<Integer, List<SkillSegment>> entry : rows.entrySet()) {
            List<SkillSegment> row = entry.getValue();
            final Map<SkillSegment, Placement> placeMap = layout.placements;
            Collections.sort(row, new Comparator<SkillSegment>() {
                @Override
                public int compare(SkillSegment a, SkillSegment b) {
                    int pa = primaryParentX(a, placeMap);
                    int pb = primaryParentX(b, placeMap);
                    if (pa != pb) return Integer.compare(pa, pb);
                    return a.getId().compareTo(b.getId());
                }
            });

            // Order by placement hint (after)
            row = orderByPlacement(row);
            entry.setValue(row);

            int cursor = -rowWidth(row) / 2;
            for (SkillSegment seg : row) {
                int w = segmentWidth(seg);
                layout.placements.put(seg, new Placement(seg, cursor + w / 2, seg.getDepth() * ROW_HEIGHT, w));
                cursor += w + SEGMENT_GAP;
            }
        }

        List<List<SkillSegment>> rowList = new ArrayList<List<SkillSegment>>(rows.values());
        relax(rowList, layout.placements, Collections.<SkillSegment, Integer>emptyMap());

        Map<SkillSegment, Integer> pinned = spreadFirstBranches(tree, layout.placements);
        if (!pinned.isEmpty()) {
            for (Map.Entry<SkillSegment, Integer> p : pinned.entrySet()) {
                Placement old = layout.placements.get(p.getKey());
                if (old != null) {
                    old.x = p.getValue();
                }
            }
            relax(rowList, layout.placements, pinned);
        }

        // Apply calculated (x, y) to each segment
        int minX = 0;
        int maxX = 0;
        boolean first = true;
        for (Placement p : layout.placements.values()) {
            p.segment.setPosition(p.x, p.y);
            int left = p.x - p.width / 2;
            int right = p.x + p.width / 2;
            if (first) {
                minX = left;
                maxX = right;
                first = false;
            } else {
                minX = Math.min(minX, left);
                maxX = Math.max(maxX, right);
            }
        }

        layout.minX = minX;
        layout.maxX = maxX;
        layout.height = rows.isEmpty() ? 0 : rows.lastKey() * ROW_HEIGHT + SKILL_SIZE;

        return layout;
    }

    public static List<SkillSegment> orderByPlacement(List<SkillSegment> row) {
        if (row.size() < 2) {
            return row;
        }
        Map<String, SkillSegment> byKey = new HashMap<String, SkillSegment>();
        final Map<SkillSegment, Integer> indices = new HashMap<SkillSegment, Integer>();
        Map<SkillSegment, List<SkillSegment>> successors = new HashMap<SkillSegment, List<SkillSegment>>();
        Map<SkillSegment, Integer> predecessors = new HashMap<SkillSegment, Integer>();

        for (SkillSegment entry : row) {
            byKey.put(entry.getId(), entry);
            indices.put(entry, indices.size());
            predecessors.put(entry, 0);
        }

        for (SkillSegment entry : row) {
            String after = entry.getOrderAfterKey();
            if (after == null) continue;
            SkillSegment relative = byKey.get(after);
            if (relative == null || relative == entry) continue;
            List<SkillSegment> list = successors.get(relative);
            if (list == null) {
                list = new ArrayList<SkillSegment>();
                successors.put(relative, list);
            }
            list.add(entry);
            predecessors.put(entry, predecessors.get(entry) + 1);
        }

        if (successors.isEmpty()) {
            return row;
        }

        PriorityQueue<SkillSegment> queue = new PriorityQueue<SkillSegment>(row.size(), new Comparator<SkillSegment>() {
            @Override
            public int compare(SkillSegment a, SkillSegment b) {
                return Integer.compare(indices.get(a), indices.get(b));
            }
        });
        for (Map.Entry<SkillSegment, Integer> e : predecessors.entrySet()) {
            if (e.getValue() == 0) {
                queue.add(e.getKey());
            }
        }

        List<SkillSegment> ordered = new ArrayList<SkillSegment>(row.size());
        while (!queue.isEmpty()) {
            SkillSegment entry = queue.poll();
            ordered.add(entry);
            List<SkillSegment> succ = successors.get(entry);
            if (succ != null) {
                for (SkillSegment s : succ) {
                    int rem = predecessors.get(s) - 1;
                    predecessors.put(s, rem);
                    if (rem == 0) {
                        queue.add(s);
                    }
                }
            }
        }

        return ordered.size() == row.size() ? ordered : row;
    }

    private static Map<SkillSegment, Integer> spreadFirstBranches(SkillTree tree, Map<SkillSegment, Placement> placements) {
        Map<SkillSegment, Integer> pinned = new LinkedHashMap<SkillSegment, Integer>();
        for (ISkillSegment iseg : tree.getAllSegments()) {
            SkillSegment entry = (SkillSegment) iseg;
            if (entry.getChildren().size() < 2 || !onStraightStem(entry)) continue;

            List<SkillSegment> children = new ArrayList<SkillSegment>();
            for (ISkillSegment c : entry.getChildren()) {
                children.add((SkillSegment) c);
            }
            final Map<SkillSegment, Placement> placeMap = placements;
            Collections.sort(children, new Comparator<SkillSegment>() {
                @Override
                public int compare(SkillSegment a, SkillSegment b) {
                    Placement pa = placeMap.get(a);
                    Placement pb = placeMap.get(b);
                    int xa = pa != null ? pa.x : 0;
                    int xb = pb != null ? pb.x : 0;
                    return Integer.compare(xa, xb);
                }
            });

            int step = 0;
            for (int i = 1; i < children.size(); i++) {
                Placement pCurr = placements.get(children.get(i));
                Placement pPrev = placements.get(children.get(i - 1));
                if (pCurr != null && pPrev != null) {
                    step = Math.max(step, pCurr.x - pPrev.x);
                }
            }

            Placement pEntry = placements.get(entry);
            int entryX = pEntry != null ? pEntry.x : 0;
            double first = entryX - step * (children.size() - 1) / 2.0;
            for (int i = 0; i < children.size(); i++) {
                pinned.put(children.get(i), (int) Math.round(first + (long) step * i));
            }
        }
        return pinned;
    }

    private static boolean onStraightStem(SkillSegment entry) {
        SkillSegment current = entry;
        while (!current.getParents().isEmpty()) {
            if (current.getParents().size() != 1 || current.getParents().get(0).getChildren().size() != 1) {
                return false;
            }
            current = (SkillSegment) current.getParents().get(0);
        }
        return true;
    }

    private static void relax(List<List<SkillSegment>> rows, Map<SkillSegment, Placement> placements, Map<SkillSegment, Integer> pinned) {
        for (int pass = 0; pass < RELAX_PASSES; pass++) {
            for (List<SkillSegment> row : rows) {
                align(row, placements, true, pinned);
            }
            for (int i = rows.size() - 1; i >= 0; i--) {
                align(rows.get(i), placements, false, pinned);
            }
        }
    }

    private static void align(List<SkillSegment> row, Map<SkillSegment, Placement> placements, boolean useParents, Map<SkillSegment, Integer> pinned) {
        if (row.isEmpty()) return;

        final Map<SkillSegment, Integer> desired = new HashMap<SkillSegment, Integer>();
        Map<SkillSegment, Double> weights = new HashMap<SkillSegment, Double>();

        for (SkillSegment entry : row) {
            if (pinned.containsKey(entry)) {
                desired.put(entry, pinned.get(entry));
                weights.put(entry, PIN_WEIGHT);
                continue;
            }
            List<ISkillSegment> related = useParents ? entry.getParents() : entry.getChildren();
            double sum = 0;
            int count = 0;
            for (ISkillSegment r : related) {
                Placement p = placements.get(r);
                if (p != null) {
                    sum += p.x;
                    count++;
                }
            }
            Placement self = placements.get(entry);
            desired.put(entry, count > 0 ? (int) Math.round(sum / count) : (self != null ? self.x : 0));
            weights.put(entry, related.size() == 1 ? CHAIN_WEIGHT : 1.0);
        }

        Collections.sort(row, new Comparator<SkillSegment>() {
            @Override
            public int compare(SkillSegment a, SkillSegment b) {
                Integer da = desired.get(a);
                Integer db = desired.get(b);
                return Integer.compare(da != null ? da : 0, db != null ? db : 0);
            }
        });

        int[] offsets = new int[row.size()];
        for (int i = 1; i < row.size(); i++) {
            offsets[i] = offsets[i - 1] + spacing(row.get(i - 1), row.get(i));
        }

        List<Target> targets = new ArrayList<Target>(row.size());
        for (int i = 0; i < row.size(); i++) {
            SkillSegment seg = row.get(i);
            Integer des = desired.get(seg);
            Double w = weights.get(seg);
            targets.add(new Target((des != null ? des : 0) - offsets[i], w != null ? w : 1.0));
        }

        double[] resolved = isotonic(targets);
        for (int i = 0; i < row.size(); i++) {
            SkillSegment entry = row.get(i);
            Placement placement = placements.get(entry);
            if (placement != null) {
                placement.x = (int) Math.round(resolved[i]) + offsets[i];
            }
        }
    }

    private static double[] isotonic(List<Target> targets) {
        List<List<Target>> blocks = new ArrayList<List<Target>>();
        List<Double> medians = new ArrayList<Double>();

        for (Target target : targets) {
            List<Target> block = new ArrayList<Target>();
            block.add(target);
            double median = target.value;

            while (!medians.isEmpty() && medians.get(medians.size() - 1) > median) {
                block.addAll(blocks.remove(blocks.size() - 1));
                medians.remove(medians.size() - 1);
                median = median(block);
            }

            blocks.add(block);
            medians.add(median);
        }

        double[] resolved = new double[targets.size()];
        int index = 0;
        for (int i = 0; i < blocks.size(); i++) {
            for (int j = 0; j < blocks.get(i).size(); j++) {
                resolved[index++] = medians.get(i);
            }
        }
        return resolved;
    }

    private static double median(List<Target> values) {
        List<Target> sorted = new ArrayList<Target>(values);
        Collections.sort(sorted, new Comparator<Target>() {
            @Override
            public int compare(Target a, Target b) {
                return Double.compare(a.value, b.value);
            }
        });
        double total = 0;
        for (Target t : sorted) total += t.weight;
        double accumulated = 0;

        for (int i = 0; i < sorted.size(); i++) {
            accumulated += sorted.get(i).weight;
            if (accumulated * 2 > total) {
                return sorted.get(i).value;
            }
            if (accumulated * 2 == total && i + 1 < sorted.size()) {
                return (sorted.get(i).value + sorted.get(i + 1).value) / 2.0;
            }
        }
        return sorted.get(sorted.size() - 1).value;
    }

    private static int primaryParentX(SkillSegment entry, Map<SkillSegment, Placement> placements) {
        int min = Integer.MAX_VALUE;
        for (ISkillSegment p : entry.getParents()) {
            Placement place = placements.get(p);
            if (place != null) {
                min = Math.min(min, place.x);
            }
        }
        return min != Integer.MAX_VALUE ? min : 0;
    }

    private static int spacing(SkillSegment left, SkillSegment right) {
        return segmentWidth(left) / 2 + segmentWidth(right) / 2 + SEGMENT_GAP;
    }

    private static int segmentWidth(SkillSegment entry) {
        int count = entry.getSkills().size();
        return count * SKILL_SIZE + (count - 1) * SKILL_GAP;
    }

    private static int rowWidth(List<SkillSegment> row) {
        int sum = 0;
        for (SkillSegment seg : row) {
            sum += segmentWidth(seg);
        }
        return sum + (row.size() - 1) * SEGMENT_GAP;
    }

    public Placement getPlacement(SkillSegment segment) {
        return placements.get(segment);
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getWidth() {
        return maxX - minX;
    }

    public int getHeight() {
        return height;
    }
}
