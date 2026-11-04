package com.marrybye.neorism.common.world.gen;

import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.hunter.EntityAdvancedHunter;
import com.marrybye.neorism.common.entity.hunter.EntityHunter;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * Procedural Hunter Outpost Tower World Generator.
 * Canonical port of TeamLapen's HunterOutpost watchtowers.
 * Generates 3-story fortified outpost towers guarded by Vampire Hunters with canonical loot.
 */
public class WorldGenHunterTower extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        y = world.getTopSolidOrLiquidBlock(x, z);
        if (y < 60 || y > 140) {
            return false;
        }

        Block ground = world.getBlock(x, y - 1, z);
        if (ground != Blocks.grass && ground != Blocks.dirt && ground != Blocks.sand && ground != ModBlocks.cursedGrass) {
            return false;
        }

        // Verify terrain flatness across 7x7 area (max elevation difference 3)
        for (int dx = -3; dx <= 3; dx += 3) {
            for (int dz = -3; dz <= 3; dz += 3) {
                int checkY = world.getTopSolidOrLiquidBlock(x + dx, z + dz);
                if (Math.abs(checkY - y) > 3) {
                    return false;
                }
            }
        }

        // 1. Foundation: fill down to bedrock/solid ground so tower never floats on hillsides
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                int curY = world.getTopSolidOrLiquidBlock(x + dx, z + dz);
                for (int fillY = curY - 1; fillY <= y - 1; fillY++) {
                    world.setBlock(x + dx, fillY, z + dz, Blocks.cobblestone, 0, 2);
                }
                // Clear interior/air above foundation
                for (int clearY = y; clearY <= y + 11; clearY++) {
                    world.setBlock(x + dx, clearY, z + dz, Blocks.air, 0, 2);
                }
            }
        }

        // 2. Floor 1 (Cobblestone foundation & walls: y to y + 3)
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                world.setBlock(x + dx, y - 1, z + dz, Blocks.cobblestone, 0, 2);

                boolean isOuter = (dx == -3 || dx == 3 || dz == -3 || dz == 3);
                boolean isCorner = (Math.abs(dx) == 3 && Math.abs(dz) == 3);

                if (isOuter) {
                    for (int dy = 0; dy <= 3; dy++) {
                        if (isCorner) {
                            world.setBlock(x + dx, y + dy, z + dz, Blocks.log, 0, 2);
                        } else {
                            world.setBlock(x + dx, y + dy, z + dz, Blocks.cobblestone, 0, 2);
                        }
                    }
                }
            }
        }

        // Doorway on South side (z = 3, dx = 0)
        world.setBlock(x, y, z + 3, Blocks.air, 0, 2);
        world.setBlock(x, y + 1, z + 3, Blocks.air, 0, 2);

        // Ground floor interior decorations
        world.setBlock(x + 2, y, z - 2, Blocks.crafting_table, 0, 2);
        world.setBlock(x - 2, y, z - 2, Blocks.furnace, 3, 2);
        world.setBlock(x, y + 2, z - 2, Blocks.torch, 4, 2); // Torch on North wall

        // 3. Floor 2 (Wooden walls and floor: y + 4 to y + 7)
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                // Wooden plank ceiling / Floor 2 floor
                if (dx != -2 || dz != 0) { // Keep hole for ladder
                    world.setBlock(x + dx, y + 4, z + dz, Blocks.planks, 0, 2);
                }

                boolean isOuter = (dx == -3 || dx == 3 || dz == -3 || dz == 3);
                boolean isCorner = (Math.abs(dx) == 3 && Math.abs(dz) == 3);

                if (isOuter) {
                    for (int dy = 5; dy <= 7; dy++) {
                        if (isCorner) {
                            world.setBlock(x + dx, y + dy, z + dz, Blocks.log, 0, 2);
                        } else if (dy == 6 && (dx == 0 || dz == 0)) {
                            // Arrow slits / windows on 4 sides
                            world.setBlock(x + dx, y + dy, z + dz, Blocks.iron_bars, 0, 2);
                        } else {
                            world.setBlock(x + dx, y + dy, z + dz, Blocks.planks, 0, 2);
                        }
                    }
                }
            }
        }

        // Floor 2 Chest with canonical Hunter Outpost loot
        world.setBlock(x + 2, y + 5, z + 2, Blocks.chest, 0, 2);
        TileEntityChest chest = (TileEntityChest) world.getTileEntity(x + 2, y + 5, z + 2);
        if (chest != null) {
            populateHunterChest(chest, rand);
        }

        // Floor 2 Torch
        world.setBlock(x, y + 6, z - 2, Blocks.torch, 4, 2);

        // 4. Floor 3 (Roof Observation Deck: y + 8 to y + 9)
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (dx != -2 || dz != 0) {
                    world.setBlock(x + dx, y + 8, z + dz, Blocks.planks, 0, 2);
                }

                boolean isOuter = (dx == -3 || dx == 3 || dz == -3 || dz == 3);
                boolean isCorner = (Math.abs(dx) == 3 && Math.abs(dz) == 3);

                if (isOuter) {
                    world.setBlock(x + dx, y + 9, z + dz, Blocks.fence, 0, 2);
                    if (isCorner) {
                        // Torch on fence corner posts
                        world.setBlock(x + dx, y + 10, z + dz, Blocks.torch, 5, 2);
                    }
                }
            }
        }

        // Trapdoor over ladder hole
        world.setBlock(x - 2, y + 8, z, Blocks.trapdoor, 0, 2);

        // 5. Ladder from Floor 1 to Roof Deck (x - 2, z = 0, facing East: metadata 5)
        for (int dy = 0; dy <= 8; dy++) {
            world.setBlock(x - 2, y + dy, z, Blocks.ladder, 5, 2);
        }

        // 6. Spawn Hunter Guards on the observation deck
        spawnHunterGuards(world, rand, x, y + 9, z);

        return true;
    }

    private void populateHunterChest(TileEntityChest chest, Random rand) {
        int size = chest.getSizeInventory();

        // Crossbow & Quiver
        if (ModItems.crossbow != null) {
            chest.setInventorySlotContents(rand.nextInt(size), new ItemStack(ModItems.crossbow));
        }
        if (ModItems.quarrel != null) {
            chest.setInventorySlotContents(rand.nextInt(size), new ItemStack(ModItems.quarrel, 12 + rand.nextInt(16)));
        }

        // Hunter Weapons & Equipment
        if (ModItems.hunterAxe != null && rand.nextBoolean()) {
            chest.setInventorySlotContents(rand.nextInt(size), new ItemStack(ModItems.hunterAxe));
        }
        if (ModItems.stake != null) {
            chest.setInventorySlotContents(rand.nextInt(size), new ItemStack(ModItems.stake, 2 + rand.nextInt(3)));
        }
        if (ModItems.holyWaterBottle != null) {
            chest.setInventorySlotContents(rand.nextInt(size), new ItemStack(ModItems.holyWaterBottle, 1 + rand.nextInt(2)));
        }
        if (ModItems.garlic != null) {
            chest.setInventorySlotContents(rand.nextInt(size), new ItemStack(ModItems.garlic, 3 + rand.nextInt(5)));
        }
        if (ModItems.hunterIntel != null && rand.nextBoolean()) {
            chest.setInventorySlotContents(rand.nextInt(size), new ItemStack(ModItems.hunterIntel, 1 + rand.nextInt(2)));
        }
        if (ModItems.hunterHat != null && rand.nextInt(4) == 0) {
            chest.setInventorySlotContents(rand.nextInt(size), new ItemStack(ModItems.hunterHat));
        }

        // Standard supplies
        chest.setInventorySlotContents(rand.nextInt(size), new ItemStack(Items.iron_ingot, 2 + rand.nextInt(4)));
        chest.setInventorySlotContents(rand.nextInt(size), new ItemStack(Items.bread, 3 + rand.nextInt(4)));
        chest.setInventorySlotContents(rand.nextInt(size), new ItemStack(Items.potionitem, 1, 8261)); // Healing potion
    }

    private void spawnHunterGuards(World world, Random rand, int x, int y, int z) {
        // Guard 1: Basic Hunter on roof
        EntityHunter hunter1 = new EntityHunter(world);
        hunter1.setLocationAndAngles(x + 1.5D, y, z + 1.5D, rand.nextFloat() * 360.0F, 0.0F);
        hunter1.onSpawnWithEgg(null);
        hunter1.setHomeArea(x, y, z, 8);
        hunter1.func_110163_bv(); // persistent
        world.spawnEntityInWorld(hunter1);

        // Guard 2: Advanced Hunter or 2nd Basic Hunter
        if (rand.nextBoolean()) {
            EntityAdvancedHunter hunter2 = new EntityAdvancedHunter(world);
            hunter2.setLocationAndAngles(x - 1.5D, y, z - 1.5D, rand.nextFloat() * 360.0F, 0.0F);
            hunter2.onSpawnWithEgg(null);
            hunter2.setHomeArea(x, y, z, 8);
            hunter2.func_110163_bv();
            world.spawnEntityInWorld(hunter2);
        } else {
            EntityHunter hunter2 = new EntityHunter(world);
            hunter2.setLocationAndAngles(x - 1.5D, y, z - 1.5D, rand.nextFloat() * 360.0F, 0.0F);
            hunter2.onSpawnWithEgg(null);
            hunter2.setHomeArea(x, y, z, 8);
            hunter2.func_110163_bv();
            world.spawnEntityInWorld(hunter2);
        }
    }
}
