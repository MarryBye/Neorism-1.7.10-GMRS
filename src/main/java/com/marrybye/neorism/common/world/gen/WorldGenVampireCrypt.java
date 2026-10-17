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
        }

        // Central Dais with red coffins
        world.setBlock(x, y + 1, z, ModBlocks.coffin, 0, 2);
        if (ModBlocks.coffin != null) {
            world.setBlock(x - 1, y + 1, z, ModBlocks.coffin, 0, 2);
            world.setBlock(x + 1, y + 1, z, ModBlocks.coffin, 0, 2);
        }

        // Side Tombs with Loot Chests
        placeCryptChest(world, rand, x - width + 1, y + 1, z);
        placeCryptChest(world, rand, x + width - 1, y + 1, z);
        placeCryptChest(world, rand, x, y + 1, z - depth + 1);
        placeCryptChest(world, rand, x, y + 1, z + depth - 1);

        // Spawn Vampire Baron boss inside the chamber
        EntityVampireBaron baron = new EntityVampireBaron(world);
        baron.setLocationAndAngles(x + 0.5D, y + 1, z + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
        baron.onSpawnWithEgg(null);
        world.spawnEntityInWorld(baron);

        // Iron bars in entrance alcoves
        world.setBlock(x, y + 1, z - depth, Blocks.iron_bars, 0, 2);
        world.setBlock(x, y + 2, z - depth, Blocks.iron_bars, 0, 2);

        return true;
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
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.pureBlood, 1 + rand.nextInt(2)));
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
