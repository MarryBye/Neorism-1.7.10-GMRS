package com.marrybye.neorism.common.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.entity.vampire.EntityVampireMother;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * Mother Tree boss structure generator for Vampire Forest.
 * Canonical port of TeamLapen's MotherStructure & MotherPiece (18x14x18 cursed root mass + canopy + Vampire Mother).
 */
public class WorldGenMotherTree extends WorldGenerator {

    private static final int[][] REMAINS_COORDS = {
        {1,0,9},{2,0,12},{2,0,13},{6,0,12},{6,0,13},{6,0,14},{8,0,3},{8,0,4},{9,0,2},{9,0,3},
        {0,1,5},{1,1,5},{1,1,9},{2,1,5},{2,1,6},{2,1,7},{2,1,9},{2,1,11},{2,1,12},{3,1,11},
        {5,1,1},{5,1,2},{5,1,11},{5,1,12},{6,1,2},{6,1,3},{6,1,4},{6,1,12},{8,1,0},{8,1,1},
        {8,1,4},{8,1,5},{8,1,7},{10,1,10},{10,1,11},{11,1,1},{11,1,2},{11,1,15},{11,1,16},{12,1,4},
        {12,1,5},{12,1,10},{12,1,16},{13,1,3},{13,1,4},{13,1,10},{14,1,6},{14,1,14},{15,1,4},{15,1,6},
        {15,1,14},{16,1,6},{16,1,11},{16,1,14},{17,1,6},{17,1,11},
        {2,2,3},{2,2,4},{2,2,7},{2,2,8},{2,2,9},{2,2,10},{3,2,4},{3,2,5},{3,2,8},{3,2,11},
        {4,2,11},{5,2,10},{5,2,11},{6,2,4},{6,2,5},{8,2,1},{8,2,5},{8,2,7},{8,2,8},{9,2,1},
        {9,2,2},{9,2,5},{9,2,6},{9,2,8},{9,2,16},{9,2,17},{10,2,2},{10,2,3},{10,2,9},{10,2,10},
        {10,2,13},{10,2,14},{10,2,15},{11,2,2},{11,2,5},{11,2,6},{11,2,8},{11,2,15},{12,2,5},{12,2,8},
        {12,2,9},{12,2,10},{13,2,6},{13,2,14},{14,2,5},{14,2,6},{14,2,14},{15,2,4},{15,2,5},{15,2,9},
        {15,2,11},{16,2,8},{16,2,9},{16,2,11},{17,2,8},
        {3,3,5},{3,3,6},{3,3,7},{3,3,8},{4,3,5},{4,3,6},{5,3,10},{6,3,5},{6,3,6},{6,3,7},
        {6,3,8},{6,3,9},{6,3,10},{6,3,14},{7,3,12},{7,3,14},{8,3,11},{8,3,12},{8,3,14},{9,3,6},
        {9,3,8},{9,3,15},{9,3,16},{10,3,3},{10,3,6},{10,3,8},{10,3,9},{10,3,12},{10,3,13},{11,3,3},
        {11,3,4},{11,3,6},{11,3,8},{12,3,11},{12,3,12},{12,3,13},{12,3,14},{13,3,6},{13,3,7},{13,3,10},
        {13,3,14},{14,3,9},{14,3,10},{14,3,11},{15,3,9},{15,3,11},{15,3,12},{16,3,12},
        {4,4,6},{5,4,6},{6,4,7},{6,4,8},{8,4,10},{8,4,11},{8,4,14},{9,4,9},{9,4,10},{9,4,14},
        {9,4,15},{10,4,6},{10,4,7},{10,4,8},{10,4,12},{11,4,4},{11,4,5},{11,4,7},{11,4,8},{12,4,7},
        {12,4,8},{12,4,10},{12,4,11},{13,4,7},{13,4,10},{13,4,11},{14,4,11},
        {5,5,6},{5,5,7},{6,5,6},{6,5,7},{6,5,8},{7,5,7},{9,5,7},{9,5,8},{9,5,9},{9,5,14},
        {10,5,6},{10,5,7},{10,5,8},{10,5,12},{10,5,13},{10,5,14},{11,5,5},{11,5,6},{11,5,7},{11,5,8},
        {12,5,8},{12,5,9},{12,5,10},
        {7,6,7},{8,6,7},{9,6,7},{9,6,8},{9,6,9},{10,6,7},{10,6,8},{10,6,9},{10,6,10},{10,6,11},
        {10,6,12},{11,6,7},{11,6,8},{11,6,9},{12,6,8},{12,6,9},
        {9,7,7},{9,7,8},{9,7,9},{10,7,7},{10,7,8},{10,7,9},{11,7,7},{11,7,8},{11,7,9},
        {9,8,7},{9,8,8},{9,8,9},{10,8,7},{10,8,8},{10,8,9},{11,8,7},{11,8,8},{11,8,9},
        {9,9,7},{9,9,8},{9,9,9},{10,9,7},{10,9,9},{11,9,7},{11,9,8},{11,9,9},
        {9,10,7},{9,10,8},{9,10,9},{10,10,7},{10,10,8},{10,10,9},{11,10,7},{11,10,8},{11,10,9}
    };

    private static final int[][] WOOD_Y_COORDS = {
        {8,8,7},{8,8,8},{8,8,9},{11,8,10},{8,9,7},{8,9,8},{8,9,9},{12,9,7},
        {8,10,7},{8,10,8},{8,10,9},{10,10,10},{11,10,6},{11,10,10},
        {8,11,7},{8,11,8},{8,11,9},{9,11,7},{9,11,8},{9,11,9},{9,11,10},
        {10,11,7},{10,11,8},{10,11,9},{10,11,10},{11,11,7},{11,11,8},{11,11,9},{11,11,10},
        {12,11,7},{12,11,8},{12,11,9}
    };

    private static final int[][] WOOD_Z_COORDS = {
        {9,8,6},{9,8,10},{10,8,6},{10,8,10},{11,8,6},
        {9,9,6},{9,9,10},{10,9,6},{10,9,10},{11,9,6},{11,9,10},
        {9,10,6},{9,10,10},{10,10,6},
        {9,11,6},{10,11,6},{11,11,6}
    };

    private static final int[][] WOOD_X_COORDS = {
        {12,8,7},{12,8,8},{12,8,9},{12,9,8},{12,9,9},{12,10,7},{12,10,8},{12,10,9}
    };

    private static final int[][] TOP_LOG_COORDS = {
        {8,12,7},{8,12,8},{8,12,9},{9,12,6},{9,12,7},{9,12,8},{9,12,9},{9,12,10},
        {10,12,6},{10,12,9},{10,12,10},{11,12,9},{11,12,10},{12,12,8},{12,12,9},
        {8,13,7},{8,13,8},{8,13,9},{9,13,9},{9,13,10},{10,13,10},{11,13,10}
    };

    private static final int[][] AIR_COORDS = {
        {12,12,6},{8,13,5},{9,13,5},{10,13,5},{10,13,6},{10,13,7},{10,13,8},
        {11,13,5},{11,13,6},{11,13,7},{11,13,8},{12,13,6},{12,13,7},{12,13,8},
        {13,13,7},{13,13,8},{13,13,9},{13,13,10}
    };

    private static final int[][] VULNERABLE_CANDIDATES = {
        {0,1,5},{5,1,1},{8,1,7},{10,1,11},{3,2,11},{3,3,5},{7,3,12},{10,3,3},
        {12,3,14},{15,3,9},{16,3,12},{9,4,15},{6,5,8},{11,5,5},{12,5,10}
    };

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        // Anchor all placed blocks ([0..17] footprint + crown + shrine) strictly inside the 2x2 population window [chunkStart .. chunkStart + 31]
        int chunkStartX = x & ~15;
        int chunkStartZ = z & ~15;
        int baseX = chunkStartX + 7;
        int baseZ = chunkStartZ + 7;
        int trunkX = baseX + 10;
        int trunkZ = baseZ + 8;

        int surfaceY = world.getTopSolidOrLiquidBlock(trunkX, trunkZ);
        if (surfaceY < 56 || surfaceY > 120) {
            return false;
        }

        Block ground = world.getBlock(trunkX, surfaceY - 1, trunkZ);
        if (ground != ModBlocks.cursedGrass && ground != ModBlocks.cursedEarth && ground != Blocks.grass && ground != Blocks.dirt) {
            return false;
        }

        // Canonical updateAverageGroundHeight(level, box, -12)
        int baseY = surfaceY - 12;
        if (baseY < 8) {
            return false;
        }

        Block remainsBlock = ModBlocks.directCursedBark != null ? ModBlocks.directCursedBark
            : (ModBlocks.darkStone != null ? ModBlocks.darkStone : Blocks.obsidian);
        Block cursedWood = ModBlocks.cursedSpruceWood != null ? ModBlocks.cursedSpruceWood : Blocks.log;
        Block cursedLog = ModBlocks.cursedSpruceLog != null ? ModBlocks.cursedSpruceLog : Blocks.log;
        Block leaves = ModBlocks.darkSpruceLeaves != null ? ModBlocks.darkSpruceLeaves : Blocks.leaves;
        Block accentBlock = ModBlocks.ancientTrophy != null ? ModBlocks.ancientTrophy : ModBlocks.darkStoneBricks;

        for (int[] p : REMAINS_COORDS) {
            world.setBlock(baseX + p[0], baseY + p[1], baseZ + p[2], remainsBlock, 0, 2);
        }
        for (int[] p : WOOD_Y_COORDS) {
            world.setBlock(baseX + p[0], baseY + p[1], baseZ + p[2], cursedWood, 0, 2);
        }
        for (int[] p : WOOD_Z_COORDS) {
            world.setBlock(baseX + p[0], baseY + p[1], baseZ + p[2], cursedWood, 8, 2);
        }
        for (int[] p : WOOD_X_COORDS) {
            world.setBlock(baseX + p[0], baseY + p[1], baseZ + p[2], cursedWood, 4, 2);
        }
        for (int[] p : TOP_LOG_COORDS) {
            world.setBlock(baseX + p[0], baseY + p[1], baseZ + p[2], cursedLog, 0, 2);
        }
        for (int[] p : AIR_COORDS) {
            world.setBlockToAir(baseX + p[0], baseY + p[1], baseZ + p[2]);
        }

        // Core Mother Trophy & 6..8 random vulnerable root accents
        if (accentBlock != null) {
            world.setBlock(baseX + 10, baseY + 9, baseZ + 8, accentBlock, 0, 2);
            List<int[]> pool = new ArrayList<int[]>();
            for (int[] c : VULNERABLE_CANDIDATES) {
                pool.add(c);
            }
            int vulnerabilities = 6 + rand.nextInt(3);
            for (int i = 0; i < vulnerabilities && !pool.isEmpty(); i++) {
                int[] pick = pool.remove(rand.nextInt(pool.size()));
                world.setBlock(baseX + pick[0], baseY + pick[1], baseZ + pick[2], accentBlock, 0, 2);
            }
        }

        // Above-ground ancient cursed spruce crown rising from the hollow root base
        int trunkBaseY = surfaceY + 2;
        int crownHeight = 12;
        for (int dy = 0; dy < crownHeight; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (Math.abs(dx) == 1 && Math.abs(dz) == 1 && dy > 4) continue;
                    world.setBlock(trunkX + dx, trunkBaseY + dy, trunkZ + dz, cursedLog, 0, 2);
                }
            }
        }
        for (int ly = 4; ly <= crownHeight + 2; ly++) {
            int radius = Math.max(1, 5 - (ly - 4) / 2);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz <= radius * radius + 1) {
                        if (world.isAirBlock(trunkX + dx, trunkBaseY + ly, trunkZ + dz)) {
                            world.setBlock(trunkX + dx, trunkBaseY + ly, trunkZ + dz, leaves, 0, 2);
                        }
                    }
                }
            }
        }

        // Clear a surface shrine clearing on the south face and spawn EntityVampireMother
        int spawnX = trunkX;
        int spawnZ = trunkZ - 4;
        int spawnY = world.getTopSolidOrLiquidBlock(spawnX, spawnZ);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.setBlock(spawnX + dx, spawnY - 1, spawnZ + dz,
                    ModBlocks.darkStoneBricks != null ? ModBlocks.darkStoneBricks : Blocks.stonebrick, 0, 2);
                for (int cy = 0; cy <= 3; cy++) {
                    world.setBlockToAir(spawnX + dx, spawnY + cy, spawnZ + dz);
                }
            }
        }

        EntityVampireMother mother = new EntityVampireMother(world);
        mother.setLocationAndAngles(spawnX + 0.5D, spawnY, spawnZ + 0.5D, 180.0F, 0.0F);
        mother.setHomeArea(trunkX, spawnY, trunkZ, 20);
        mother.func_110163_bv();
        world.spawnEntityInWorld(mother);

        ModLogger.spawn("Generated Mother Tree & Vampire Mother boss at (%d, %d, %d)", trunkX, surfaceY, trunkZ);
        return true;
    }
}
