package com.marrybye.neorism.common.world.gen;

import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.hunter.EntityHunterTrainer;
import com.marrybye.neorism.common.tileentity.TileEntityTent;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * Procedural Hunter Camp and Outpost World Generator.
 * Canonical port of TeamLapen's HunterCampPieces.
 * Generates surface campfire, hunter tents, weapon workstations, and Hunter Trainers.
 */
public class WorldGenHunterCamp extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        // Find surface Y
        y = world.getTopSolidOrLiquidBlock(x, z);
        if (y < 60 || y > 140) {
            return false;
        }

        Block ground = world.getBlock(x, y - 1, z);
        if (ground != Blocks.grass && ground != Blocks.dirt && ground != ModBlocks.cursedGrass && ground != Blocks.sand) {
            return false;
        }

        // Verify relatively flat area (variance <= 3)
        for (int dx = -5; dx <= 5; dx += 5) {
            for (int dz = -5; dz <= 5; dz += 5) {
                int checkY = world.getTopSolidOrLiquidBlock(x + dx, z + dz);
                if (Math.abs(checkY - y) > 3) {
                    return false;
                }
            }
        }

        // Level foundation
        for (int dx = -6; dx <= 6; ++dx) {
            for (int dz = -6; dz <= 6; ++dz) {
                int curY = world.getTopSolidOrLiquidBlock(x + dx, z + dz);
                // Fill ground below
                for (int fillY = curY - 1; fillY <= y - 1; ++fillY) {
                    world.setBlock(x + dx, fillY, z + dz, Blocks.dirt, 0, 2);
                }
                // Clear air above
                for (int clearY = y; clearY <= y + 4; ++clearY) {
                    world.setBlock(x + dx, clearY, z + dz, Blocks.air, 0, 2);
                }
                // Camp floor: coarse gravel/dirt path mix
                if (rand.nextInt(3) == 0) {
                    world.setBlock(x + dx, y - 1, z + dz, Blocks.gravel, 0, 2);
                }
            }
        }

        // Central Campfire
        world.setBlock(x, y - 1, z, Blocks.netherrack, 0, 2);
        world.setBlock(x, y, z, Blocks.fire, 0, 2);
        int[][] fireSurround = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] p : fireSurround) {
            world.setBlock(x + p[0], y, z + p[1], Blocks.stone_slab, 3, 2); // Cobble slab
        }

        // Canonical HunterCampPieces.Fireplace: 1-in-3 chance of advanced camp (1 advanced tent + 1 normal tent + 0..2 extra),
        // otherwise normal camp (1 normal tent + 50% 1 extra)
        int[][] tentOffsets = { {-3, -3, 2}, {3, -3, 3}, {-3, 3, 1}, {3, 3, 0} };
        boolean advancedCamp = rand.nextInt(3) == 0;

        if (advancedCamp) {
            placeTent(world, x + tentOffsets[0][0], y, z + tentOffsets[0][1], tentOffsets[0][2], true);
            placeTent(world, x + tentOffsets[1][0], y, z + tentOffsets[1][1], tentOffsets[1][2], false);
            int extra = rand.nextInt(4);
            if (extra < 2) {
                placeTent(world, x + tentOffsets[2][0], y, z + tentOffsets[2][1], tentOffsets[2][2], true);
            }
            if (extra < 1) {
                placeTent(world, x + tentOffsets[3][0], y, z + tentOffsets[3][1], tentOffsets[3][2], true);
            }
        } else {
            placeTent(world, x + tentOffsets[0][0], y, z + tentOffsets[0][1], tentOffsets[0][2], false);
            if (rand.nextInt(2) == 0) {
                placeTent(world, x + tentOffsets[1][0], y, z + tentOffsets[1][1], tentOffsets[1][2], false);
            }
        }

        // Workstation Area (Crafting Table + Weapon Table + Chest)
        world.setBlock(x + 4, y, z, Blocks.crafting_table, 0, 2);
        world.setBlock(x + 4, y, z + 1, Blocks.furnace, 2, 2);
        world.setBlock(x + 4, y, z - 1, Blocks.chest, 0, 2);

        TileEntityChest chest = (TileEntityChest) world.getTileEntity(x + 4, y, z - 1);
        if (chest != null) {
            if (ModItems.stake != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.stake, 4 + rand.nextInt(5)));
            }
            if (ModItems.holyWaterBottle != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.holyWaterBottle, 1 + rand.nextInt(3)));
            }
            if (ModItems.garlic != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.garlic, 3 + rand.nextInt(6)));
            }
            if (ModItems.quarrel != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.quarrel, 8 + rand.nextInt(12)));
            }
            if (ModItems.hunterHat != null && rand.nextBoolean()) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.hunterHat));
            }
            chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(Items.bread, 3 + rand.nextInt(4)));
            chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(Items.iron_ingot, 2 + rand.nextInt(4)));
        }

        // Perimeter Log Posts / Torches
        for (int dx = -6; dx <= 6; dx += 3) {
            placePerimeterPost(world, x + dx, y, z - 6);
            placePerimeterPost(world, x + dx, y, z + 6);
        }
        for (int dz = -6; dz <= 6; dz += 3) {
            placePerimeterPost(world, x - 6, y, z + dz);
            placePerimeterPost(world, x + 6, y, z + dz);
        }

        // Canonical HunterOutpostPools.TRAINER: Spawn Hunter Trainer NPC in advanced camps / outposts (50% in normal camps)
        if (advancedCamp || rand.nextBoolean()) {
            EntityHunterTrainer trainer = new EntityHunterTrainer(world);
            trainer.setLocationAndAngles(x + 1.5D, y, z + 1.5D, rand.nextFloat() * 360.0F, 0.0F);
            trainer.onSpawnWithEgg(null);
            trainer.setHomeArea(x, y, z, 10);
            trainer.func_110163_bv();
            world.spawnEntityInWorld(trainer);
        }

        return true;
    }

    private void placeTent(World world, int tx, int ty, int tz, int dir, boolean advanced) {
        if (ModBlocks.tent != null) {
            com.marrybye.neorism.common.blocks.BlockTent.placeTent(world, tx, ty, tz, dir);
            TileEntityTent tentTile = (TileEntityTent) world.getTileEntity(tx, ty, tz);
            if (tentTile != null) {
                tentTile.setSpawn(true);
                tentTile.setAdvanced(advanced);
            }
        }
    }

    private void placePerimeterPost(World world, int px, int py, int pz) {
        world.setBlock(px, py, pz, Blocks.fence, 0, 2);
        world.setBlock(px, py + 1, pz, Blocks.torch, 5, 2);
    }
}
