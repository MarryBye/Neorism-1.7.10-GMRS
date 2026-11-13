package com.marrybye.neorism.common.world.gen;

import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaron;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * Procedural Subterranean Vampire Crypt.
 * Canonical port of TeamLapen's CryptStructure.
 * Generates underground tombs with coffin chambers, vampire barons, and rare loot.
 */
public class WorldGenVampireCrypt extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        if (y < 15 || y > 50) {
            return false;
        }

        // Room dimensions: 11x6x11
        int width = 5;
        int height = 5;
        int depth = 5;

        // Verify solid stone ceiling and floor
        if (world.getBlock(x, y - 1, z) == Blocks.air || world.getBlock(x, y + height + 1, z) == Blocks.air) {
            return false;
        }

        // Hollow out chamber and lay dark stone brick walls
        for (int dx = -width; dx <= width; ++dx) {
            for (int dy = 0; dy <= height; ++dy) {
                for (int dz = -depth; dz <= depth; ++dz) {
                    int curX = x + dx;
                    int curY = y + dy;
                    int curZ = z + dz;

                    if (dx == -width || dx == width || dz == -depth || dz == depth) {
                        // Outer walls: mix of dark stone bricks, cobbled and cracked
                        int r = rand.nextInt(10);
                        Block wall = r < 5 ? ModBlocks.darkStoneBricks : (r < 8 ? ModBlocks.cobbledDarkStone : ModBlocks.crackedDarkStoneBricks);
                        world.setBlock(curX, curY, curZ, wall, 0, 2);
                    } else if (dy == 0) {
                        // Floor: polished dark stone & chiseled patterns
                        Block floor = (Math.abs(dx) == 2 || Math.abs(dz) == 2) ? ModBlocks.chiseledDarkStoneBricks : ModBlocks.polishedDarkStone;
                        world.setBlock(curX, curY, curZ, floor, 0, 2);
                    } else if (dy == height) {
                        // Vaulted ceiling
                        world.setBlock(curX, curY, curZ, ModBlocks.darkStoneBricks, 0, 2);
                    } else {
                        // Interior air
                        world.setBlock(curX, curY, curZ, Blocks.air, 0, 2);
                    }
                }
            }
        }

        // 4 Support Pillars
        int[][] pillarCoords = {{-3, -3}, {3, -3}, {-3, 3}, {3, 3}};
        for (int[] p : pillarCoords) {
            for (int dy = 1; dy < height; ++dy) {
                world.setBlock(x + p[0], y + dy, z + p[1], dy == 1 ? ModBlocks.chiseledDarkStoneBricks : ModBlocks.darkStoneBricks, 0, 2);
            }
            if (ModBlocks.candelabra != null) {
                world.setBlock(x + p[0], y + 1, z + p[1] + (p[1] < 0 ? 1 : -1), ModBlocks.candelabra, 0, 2);
            }
        }

        // Central Dais with 2-block multiblock coffin
        placeCoffin(world, x, y + 1, z - 1, 0, ModBlocks.coffin);

        // Decorative Tombstones and Bat Cages in alcoves
        if (ModBlocks.tombstoneCross != null) {
            world.setBlock(x - 3, y + 1, z, ModBlocks.tombstoneCross, 0, 2);
        }
        if (ModBlocks.tombstoneShort != null) {
            world.setBlock(x + 3, y + 1, z, ModBlocks.tombstoneShort, 0, 2);
        }
        if (ModBlocks.batCage != null) {
            world.setBlock(x, y + height - 1, z, ModBlocks.batCage, 0, 2);
        }

        // Side Tombs with Loot Chests
        placeCryptChest(world, rand, x - width + 1, y + 1, z);
        placeCryptChest(world, rand, x + width - 1, y + 1, z);
        placeCryptChest(world, rand, x, y + 1, z - depth + 1);
        placeCryptChest(world, rand, x, y + 1, z + depth - 1);

        // Spawn Vampire Baron or Baroness boss inside the chamber with canonical level
        com.marrybye.neorism.common.entity.vampire.EntityVampireBaron baron = rand.nextBoolean()
            ? new com.marrybye.neorism.common.entity.vampire.EntityVampireBaroness(world)
            : new EntityVampireBaron(world);
        baron.setLocationAndAngles(x + 0.5D, y + 1, z + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
        baron.onSpawnWithEgg(null);
        int bLevel = 2 + rand.nextInt(3); // Level 2, 3, or 4 in underground crypts
        baron.setEntityLevel(bLevel);
        baron.setHomeArea(x, y + 1, z, 8);
        baron.func_110163_bv();
        world.spawnEntityInWorld(baron);

        // Iron bars in entrance alcoves
        world.setBlock(x, y + 1, z - depth, Blocks.iron_bars, 0, 2);
        world.setBlock(x, y + 2, z - depth, Blocks.iron_bars, 0, 2);

        return true;
    }

    /**
     * Canonical 2-block multiblock placement for Coffin: foot at (x, y, z) and head at (x+dx, y, z+dz).
     */
    public static void placeCoffin(World world, int x, int y, int z, int dir, Block coffinBlock) {
        if (coffinBlock == null) return;
        int dx = 0;
        int dz = 0;
        if (dir == 0) dz = 1;       // South
        else if (dir == 1) dx = -1; // West
        else if (dir == 2) dz = -1; // North
        else if (dir == 3) dx = 1;  // East

        world.setBlock(x, y, z, coffinBlock, dir, 2);
        world.setBlock(x + dx, y, z + dz, coffinBlock, dir | 8, 2);
    }

    private void placeCryptChest(World world, Random rand, int cx, int cy, int cz) {
        world.setBlock(cx, cy, cz, Blocks.chest, 0, 2);
        TileEntityChest chest = (TileEntityChest) world.getTileEntity(cx, cy, cz);
        if (chest != null) {
            // Fill with canonical crypt loot
            if (ModItems.vampireBook != null && rand.nextFloat() < 0.6F) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.vampireBook));
            }
            if (ModItems.pureBlood != null && rand.nextFloat() < 0.7F) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.pureBlood, 1 + rand.nextInt(2), 2 + rand.nextInt(3)));
            }
            if (ModItems.vampireFang != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.vampireFang, 2 + rand.nextInt(4)));
            }
            if (ModItems.bloodBottle != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.bloodBottle, 1 + rand.nextInt(3)));
            }
            if (rand.nextBoolean()) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(Items.gold_ingot, 2 + rand.nextInt(5)));
            }
            if (rand.nextBoolean()) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(Items.iron_ingot, 3 + rand.nextInt(6)));
            }
            if (rand.nextFloat() < 0.3F) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(Items.emerald, 1 + rand.nextInt(3)));
            }
        }
    }
}
