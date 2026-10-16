package com.marrybye.neorism.common.world.garlic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

/**
 * World and chunk garlic emitter manager.
 * Tracks stationary garlic blocks, diffusers, and crops, and evaluates local garlic strength.
 */
public class GarlicHandler {

    private static final GarlicHandler INSTANCE = new GarlicHandler();

    public static GarlicHandler instance() {
        return INSTANCE;
    }

    // Map of dimensionId -> Map of block coordinate key -> GarlicEntry
    private final Map<Integer, Map<Long, GarlicEntry>> emitters = new HashMap<Integer, Map<Long, GarlicEntry>>();

    public static class GarlicEntry {
        public final int x;
        public final int y;
        public final int z;
        public final EnumStrength strength;
        public final int radius;

        public GarlicEntry(int x, int y, int z, EnumStrength strength) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.strength = strength;
            switch (strength) {
                case STRONG:
                    this.radius = 16;
                    break;
                case MEDIUM:
                    this.radius = 8;
                    break;
                case WEAK:
                default:
                    this.radius = 4;
                    break;
            }
        }
    }

    private static long blockKey(int x, int y, int z) {
        return (((long) x & 0x3FFFFFF) << 38) | (((long) y & 0xFFF) << 26) | ((long) z & 0x3FFFFFF);
    }

    public synchronized void registerGarlicBlock(World world, int x, int y, int z, EnumStrength strength) {
        if (world == null || strength == null || strength == EnumStrength.NONE) return;
        int dim = world.provider.dimensionId;
        Map<Long, GarlicEntry> dimMap = emitters.get(dim);
        if (dimMap == null) {
            dimMap = new HashMap<Long, GarlicEntry>();
            emitters.put(dim, dimMap);
        }
        dimMap.put(blockKey(x, y, z), new GarlicEntry(x, y, z, strength));
        ModLogger.debug("Registered garlic emitter at [%d, %d, %d] (strength: %s) in dim %d", x, y, z, strength.getName(), dim);
    }

    public synchronized void removeGarlicBlock(World world, int x, int y, int z) {
        if (world == null) return;
        int dim = world.provider.dimensionId;
        Map<Long, GarlicEntry> dimMap = emitters.get(dim);
        if (dimMap != null) {
            dimMap.remove(blockKey(x, y, z));
            ModLogger.debug("Removed garlic emitter at [%d, %d, %d] in dim %d", x, y, z, dim);
        }
    }

    /**
     * Calculates the garlic strength at a specific world coordinate.
     */
    public synchronized EnumStrength getGarlicStrength(World world, int x, int y, int z) {
        if (world == null) return EnumStrength.NONE;
        int dim = world.provider.dimensionId;
        Map<Long, GarlicEntry> dimMap = emitters.get(dim);
        if (dimMap == null || dimMap.isEmpty()) return EnumStrength.NONE;

        EnumStrength maxStrength = EnumStrength.NONE;
        for (GarlicEntry entry : dimMap.values()) {
            double dx = entry.x - x;
            double dy = entry.y - y;
            double dz = entry.z - z;
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq <= entry.radius * entry.radius) {
                if (entry.strength.isStrongerThan(maxStrength)) {
                    maxStrength = entry.strength;
                    if (maxStrength == EnumStrength.STRONG) {
                        break;
                    }
                }
            }
        }
        return maxStrength;
    }

    /**
     * Checks if a player is carrying garlic in their inventory or hands.
     */
    public boolean hasGarlicInInventory(EntityPlayer player) {
        if (player == null || player.inventory == null) return false;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() != null) {
                String unloc = stack.getItem().getUnlocalizedName();
                if (unloc != null && unloc.toLowerCase().contains("garlic")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a player is currently holding garlic in their hand.
     */
    public boolean isHoldingGarlic(EntityPlayer player) {
        if (player == null) return false;
        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() != null) {
            String unloc = held.getItem().getUnlocalizedName();
            return unloc != null && unloc.toLowerCase().contains("garlic");
        }
        return false;
    }

    public synchronized void clearCache() {
        emitters.clear();
    }
}
