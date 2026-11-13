package com.marrybye.neorism.common.world.gen;

import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * Giant Dark Stone Root world feature for Vampire Forest.
 * Canonical port of TeamLapen's GiantRootFeature & giant_dark_stone_root.json:
 * minTopRadius=3, maxTopRadius=5, topOffset=5, minLowestReachY=40, maxLowestReachY=1,
 * tipRadius=0.6, taperExponent=0.9, maxHorizontalOffset=4.0.
 */
public class WorldGenGiantRoot extends WorldGenerator {

    private static final int MIN_TOP_RADIUS = 3;
    private static final int MAX_TOP_RADIUS = 5;
    private static final int TOP_OFFSET = 5;
    private static final int MIN_LOWEST_REACH_Y = 40;
    private static final int MAX_LOWEST_REACH_Y = 1;
    private static final double TIP_RADIUS = 0.6D;
    private static final double TAPER_EXPONENT = 0.9D;
    private static final double MAX_HORIZONTAL_OFFSET = 4.0D;

    @Override
    public boolean generate(World world, Random rand, int originX, int originY, int originZ) {
        Block rootBlock = ModBlocks.darkStone != null ? ModBlocks.darkStone : Blocks.stone;
        if (originY <= 0) {
            originY = world.getTopSolidOrLiquidBlock(originX, originZ);
        }

        int topRadius = MIN_TOP_RADIUS + rand.nextInt(MAX_TOP_RADIUS - MIN_TOP_RADIUS + 1);
        double thickness = (double) (topRadius - MIN_TOP_RADIUS) / (double) (MAX_TOP_RADIUS - MIN_TOP_RADIUS);
        int startY = originY - TOP_OFFSET;
        double lerpedY = MIN_LOWEST_REACH_Y + thickness * (MAX_LOWEST_REACH_Y - MIN_LOWEST_REACH_Y);
        int bottomY = MathHelper.clamp_int((int) Math.round(lerpedY) + rand.nextInt(5) - 2, MAX_LOWEST_REACH_Y, startY - 4);
        if (startY <= bottomY) {
            return false;
        }

        double centerX = originX + 0.5D;
        double centerZ = originZ + 0.5D;
        int bodyHeight = startY - bottomY;
        int topY = Math.min(254, startY + topRadius);
        boolean placedAny = false;

        for (int y = topY; y >= bottomY; y--) {
            double baseRadius;
            if (y > startY) {
                double capRadiusSq = (double) topRadius * topRadius - (double) (y - startY) * (y - startY);
                baseRadius = capRadiusSq > 0.0D ? Math.sqrt(capRadiusSq) : 0.0D;
            } else {
                double taperProgress = (double) (y - bottomY) / (double) bodyHeight;
                baseRadius = TIP_RADIUS + (topRadius - TIP_RADIUS) * Math.pow(taperProgress, TAPER_EXPONENT);
            }
            double radius = baseRadius + (rand.nextDouble() - 0.5D) * 0.6D;

            centerX = MathHelper.clamp_double(centerX + (rand.nextDouble() - 0.5D) * 0.4D,
                originX + 0.5D - MAX_HORIZONTAL_OFFSET, originX + 0.5D + MAX_HORIZONTAL_OFFSET);
            centerZ = MathHelper.clamp_double(centerZ + (rand.nextDouble() - 0.5D) * 0.4D,
                originZ + 0.5D - MAX_HORIZONTAL_OFFSET, originZ + 0.5D + MAX_HORIZONTAL_OFFSET);

            int centerBlockX = MathHelper.floor_double(centerX);
            int centerBlockZ = MathHelper.floor_double(centerZ);
            int radiusCeil = MathHelper.ceiling_double_int(radius);
            double innerSq = (radius - 0.7D) * (radius - 0.7D);
            double radiusSq = radius * radius;

            for (int dx = -radiusCeil; dx <= radiusCeil; dx++) {
                for (int dz = -radiusCeil; dz <= radiusCeil; dz++) {
                    int bx = centerBlockX + dx;
                    int bz = centerBlockZ + dz;
                    if (Math.abs(bx - originX) > 10 || Math.abs(bz - originZ) > 10) continue;
                    double offsetX = bx + 0.5D - centerX;
                    double offsetZ = bz + 0.5D - centerZ;
                    double distSq = offsetX * offsetX + offsetZ * offsetZ;

                    if (distSq > radiusSq) continue;
                    if (distSq > innerSq && rand.nextInt(3) == 0) continue;

                    if (canReplace(world, bx, y, bz)) {
                        world.setBlock(bx, y, bz, rootBlock, 0, 2);
                        placedAny = true;
                    }
                }
            }
        }

        return placedAny;
    }

    private static boolean canReplace(World world, int x, int y, int z) {
        if (y <= 1 || y >= 255) return false;
        Block b = world.getBlock(x, y, z);
        if (b == Blocks.bedrock || b == Blocks.coal_ore || b == Blocks.iron_ore || b == Blocks.gold_ore
            || b == Blocks.redstone_ore || b == Blocks.diamond_ore || b == Blocks.emerald_ore || b == Blocks.lapis_ore) {
            return false;
        }
        if (b.isAir(world, x, y, z) || b == Blocks.water || b == Blocks.flowing_water || b == Blocks.lava || b == Blocks.flowing_lava) {
            return y < world.getTopSolidOrLiquidBlock(x, z);
        }
        if (b == Blocks.dirt || b == Blocks.grass || b == ModBlocks.cursedEarth || b == ModBlocks.cursedGrass) {
            return y < world.getHeightValue(x, z) - 4;
        }
        return b == Blocks.stone || b == ModBlocks.darkStone;
    }
}
