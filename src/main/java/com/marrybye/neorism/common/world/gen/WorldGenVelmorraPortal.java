package com.marrybye.neorism.common.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * Procedural Velmorra Portal Ruins in the Vampire Forest.
 * Canonical port of TeamLapen's velmorra_portal jigsaw structure.
 * Features a weathered gothic gateway arch, sacrificial dais, and Velmorra Altar.
 */
public class WorldGenVelmorraPortal extends WorldGenerator {

    public static final List<ChunkCoordinates> GENERATED_PORTALS = new ArrayList<ChunkCoordinates>();

    public static void clearCache() {
        GENERATED_PORTALS.clear();
    }

    public static ChunkCoordinates getNearestPortal(int px, int pz) {
        if (GENERATED_PORTALS.isEmpty()) {
            return null;
        }
        ChunkCoordinates nearest = null;
        double minDistSq = Double.MAX_VALUE;
        for (ChunkCoordinates pos : GENERATED_PORTALS) {
            double dx = pos.posX - px;
            double dz = pos.posZ - pz;
            double distSq = dx * dx + dz * dz;
            if (distSq < minDistSq) {
                minDistSq = distSq;
                nearest = pos;
            }
        }
        return nearest;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        y = world.getTopSolidOrLiquidBlock(x, z);
        if (y < 55 || y > 120) {
            return false;
        }

        Block ground = world.getBlock(x, y - 1, z);
        if (ground != ModBlocks.cursedGrass && ground != ModBlocks.cursedEarth && ground != Blocks.grass && ground != Blocks.dirt) {
            return false;
        }

        Block bricks = ModBlocks.darkStoneBricks != null ? ModBlocks.darkStoneBricks : Blocks.stonebrick;
        Block chiseled = ModBlocks.chiseledDarkStoneBricks != null ? ModBlocks.chiseledDarkStoneBricks : Blocks.stonebrick;
        Block cracked = ModBlocks.crackedDarkStoneBricks != null ? ModBlocks.crackedDarkStoneBricks : Blocks.stonebrick;

        // 1. Level foundation (7x7)
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                int curY = world.getTopSolidOrLiquidBlock(x + dx, z + dz);
                for (int fillY = curY - 1; fillY <= y - 1; fillY++) {
                    world.setBlock(x + dx, fillY, z + dz, bricks, 0, 2);
                }
                Block floorBlock = (rand.nextInt(3) == 0) ? cracked : bricks;
                world.setBlock(x + dx, y - 1, z + dz, floorBlock, 0, 2);

                // Clear air above platform
                for (int cy = y; cy <= y + 6; cy++) {
                    world.setBlock(x + dx, cy, z + dz, Blocks.air, 0, 2);
                }
            }
        }

        // 2. Corner Weathered Pillars
        int[][] corners = {{-3, -3}, {3, -3}, {-3, 3}, {3, 3}};
        for (int[] c : corners) {
            int pillarH = 2 + rand.nextInt(3);
            for (int py = 0; py < pillarH; py++) {
                world.setBlock(x + c[0], y + py, z + c[1], (py == 0 ? chiseled : bricks), 0, 2);
            }
            world.setBlock(x + c[0], y + pillarH, z + c[1], Blocks.redstone_torch, 5, 2);
        }

        // 3. Gothic Portal Arch at Z = -1
        int portalZ = z - 1;
        // Left pillar (dx = -2)
        for (int py = 0; py <= 4; py++) {
            world.setBlock(x - 2, y + py, portalZ, (py == 0 || py == 4 ? chiseled : bricks), 0, 2);
        }
        // Right pillar (dx = +2)
        for (int py = 0; py <= 4; py++) {
            world.setBlock(x + 2, y + py, portalZ, (py == 0 || py == 4 ? chiseled : bricks), 0, 2);
        }
        // Top arch
        world.setBlock(x - 1, y + 5, portalZ, chiseled, 0, 2);
        world.setBlock(x, y + 5, portalZ, bricks, 0, 2);
        world.setBlock(x + 1, y + 5, portalZ, chiseled, 0, 2);
        // Base sill
        world.setBlock(x - 1, y, portalZ, chiseled, 0, 2);
        world.setBlock(x, y, portalZ, chiseled, 0, 2);
        world.setBlock(x + 1, y, portalZ, chiseled, 0, 2);

        // 4. Sacrificial Dais and Velmorra Altar at Z = +2
        int altarZ = z + 2;
        // Small raised step
        world.setBlock(x, y, altarZ, ModBlocks.velmorraAltar != null ? ModBlocks.velmorraAltar : bricks, 0, 3);
        world.setBlock(x - 1, y, altarZ, chiseled, 0, 2);
        world.setBlock(x + 1, y, altarZ, chiseled, 0, 2);

        // Blood containers or soul lanterns flanking altar
        if (ModBlocks.vampireSoulLantern != null) {
            world.setBlock(x - 2, y, altarZ, ModBlocks.vampireSoulLantern, 0, 2);
            world.setBlock(x + 2, y, altarZ, ModBlocks.vampireSoulLantern, 0, 2);
        }

        GENERATED_PORTALS.add(new ChunkCoordinates(x, y, z));
        return true;
    }
}
