package com.marrybye.neorism.common.world.gen;

import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

/**
 * World generator for canonical Vampirism Dark Spruce Trees.
 * Ported from StraightTrunkPlacer(11, 2, 2) and SpruceFoliagePlacer.
 */
public class WorldGenDarkSpruceTree extends WorldGenAbstractTree {

    public WorldGenDarkSpruceTree(boolean notify) {
        super(notify);
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = 11 + rand.nextInt(5);
        int topLeavesHeight = 2 + rand.nextInt(2);
        int baseFoliageHeight = height - topLeavesHeight;
        int maxRadius = 2 + rand.nextInt(2);

        if (y < 1 || y + height + 1 > 256) {
            return false;
        }

        // Check ground block
        Block ground = world.getBlock(x, y - 1, z);
        if (ground != ModBlocks.cursedGrass && ground != ModBlocks.cursedEarth && ground != Blocks.grass && ground != Blocks.dirt) {
            return false;
        }

        // Check available vertical space
        for (int curY = y; curY <= y + 1 + height; ++curY) {
            int radius = 1;
            if (curY == y) radius = 0;
            if (curY >= y + 1 + height - 2) radius = 2;

            for (int curX = x - radius; curX <= x + radius; ++curX) {
                for (int curZ = z - radius; curZ <= z + radius; ++curZ) {
                    if (curY >= 0 && curY < 256) {
                        Block b = world.getBlock(curX, curY, curZ);
                        if (!this.isReplaceable(world, curX, curY, curZ)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        // Generate trunk
        for (int h = 0; h < height; ++h) {
            setBlockAndNotifyAdequately(world, x, y + h, z, ModBlocks.darkSpruceLog, 0);
        }

        // Generate foliage (spruce cone)
        int currentRadius = rand.nextInt(2);
        int maxFolRad = 1;
        boolean alternate = false;

        for (int h = 0; h <= baseFoliageHeight; ++h) {
            int folY = y + height - h;

            for (int dx = x - currentRadius; dx <= x + currentRadius; ++dx) {
                int offX = dx - x;
                for (int dz = z - currentRadius; dz <= z + currentRadius; ++dz) {
                    int offZ = dz - z;

                    if (Math.abs(offX) != currentRadius || Math.abs(offZ) != currentRadius || currentRadius <= 0) {
                        Block blk = world.getBlock(dx, folY, dz);
                        if (blk.isAir(world, dx, folY, dz) || blk.canBeReplacedByLeaves(world, dx, folY, dz)) {
                            setBlockAndNotifyAdequately(world, dx, folY, dz, ModBlocks.darkSpruceLeaves, 0);
                        }
                    }
                }
            }

            if (currentRadius >= maxFolRad) {
                currentRadius = alternate ? 1 : 0;
                alternate = true;
                if (++maxFolRad > maxRadius) {
                    maxFolRad = maxRadius;
                }
            } else {
                ++currentRadius;
            }
        }

        // Top-most leaf
        setBlockAndNotifyAdequately(world, x, y + height, z, ModBlocks.darkSpruceLeaves, 0);

        // Decorate tree trunk base with cursed roots
        if (rand.nextFloat() < 0.6F) {
            int[] ox = {-1, 1, 0, 0};
            int[] oz = {0, 0, -1, 1};
            for (int i = 0; i < 4; i++) {
                if (rand.nextBoolean() && world.isAirBlock(x + ox[i], y, z + oz[i])) {
                    setBlockAndNotifyAdequately(world, x + ox[i], y, z + oz[i], ModBlocks.cursedRoots, 0);
                }
            }
        }

        return true;
    }
}
