package com.marrybye.neorism.common.world.gen;

import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.vampire.EntityVampire;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * Vampire Hut stilt cabin world generator.
 * Canonical port of TeamLapen's VampireHutStructure / VampireHutPieces & loot_table/chests/vampire_hut.json.
 */
public class WorldGenVampireHut extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int baseY = world.getTopSolidOrLiquidBlock(x, z);
        if (baseY < 58 || baseY > 140) {
            return false;
        }

        Block ground = world.getBlock(x, baseY - 1, z);
        if (ground != ModBlocks.cursedGrass && ground != ModBlocks.cursedEarth && ground != Blocks.grass && ground != Blocks.dirt) {
            return false;
        }

        // Check slope around 5x5 hut footprint
        for (int dx = -2; dx <= 2; dx += 2) {
            for (int dz = -2; dz <= 2; dz += 2) {
                int sy = world.getTopSolidOrLiquidBlock(x + dx, z + dz);
                if (Math.abs(sy - baseY) > 4) {
                    return false;
                }
            }
        }

        Block log = ModBlocks.darkSpruceLog != null ? ModBlocks.darkSpruceLog : Blocks.log;
        Block planks = ModBlocks.darkSprucePlanks != null ? ModBlocks.darkSprucePlanks : Blocks.planks;
        Block stairs = ModBlocks.darkSpruceStairs != null ? ModBlocks.darkSpruceStairs : Blocks.spruce_stairs;
        Block fence = ModBlocks.darkSpruceFence != null ? ModBlocks.darkSpruceFence : Blocks.fence;

        int floorY = baseY + 1;

        // Canonical VampireHutPieces "log" data marker: 4 corner support pillars extending down to solid ground
        int[][] corners = {{-2, -2}, {2, -2}, {-2, 2}, {2, 2}};
        for (int[] c : corners) {
            int px = x + c[0];
            int pz = z + c[1];
            for (int py = floorY + 3; py >= floorY; py--) {
                world.setBlock(px, py, pz, log, 0, 2);
            }
            int downY = floorY - 1;
            while (downY > 4 && world.getBlock(px, downY, pz).isReplaceable(world, px, downY, pz)) {
                world.setBlock(px, downY, pz, log, 0, 2);
                downY--;
            }
        }

        // Cabin floor & walls (5x5 outer, 3x3 interior)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlock(x + dx, floorY, z + dz, planks, 0, 2);
                boolean isCorner = Math.abs(dx) == 2 && Math.abs(dz) == 2;
                boolean isWall = !isCorner && (Math.abs(dx) == 2 || Math.abs(dz) == 2);

                if (isWall) {
                    // Doorway at South (dz == 2, dx == 0)
                    if (dz == 2 && dx == 0) {
                        world.setBlockToAir(x + dx, floorY + 1, z + dz);
                        world.setBlockToAir(x + dx, floorY + 2, z + dz);
                        world.setBlock(x + dx, floorY + 3, z + dz, planks, 0, 2);
                    } else if (dx == 0 || dz == 0) {
                        world.setBlock(x + dx, floorY + 1, z + dz, planks, 0, 2);
                        world.setBlock(x + dx, floorY + 2, z + dz, fence, 0, 2);
                        world.setBlock(x + dx, floorY + 3, z + dz, planks, 0, 2);
                    } else {
                        world.setBlock(x + dx, floorY + 1, z + dz, planks, 0, 2);
                        world.setBlock(x + dx, floorY + 2, z + dz, planks, 0, 2);
                        world.setBlock(x + dx, floorY + 3, z + dz, planks, 0, 2);
                    }
                } else if (!isCorner) {
                    // Clear interior air
                    for (int iy = 1; iy <= 3; iy++) {
                        world.setBlockToAir(x + dx, floorY + iy, z + dz);
                    }
                }
            }
        }

        // Gabled roof
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (dx == -2) {
                    world.setBlock(x + dx, floorY + 4, z + dz, stairs, 0, 2);
                } else if (dx == 2) {
                    world.setBlock(x + dx, floorY + 4, z + dz, stairs, 1, 2);
                } else if (Math.abs(dx) == 1) {
                    world.setBlock(x + dx, floorY + 4, z + dz, planks, 0, 2);
                } else {
                    world.setBlock(x + dx, floorY + 4, z + dz, planks, 0, 2);
                    world.setBlock(x + dx, floorY + 5, z + dz, stairs, 2, 2);
                }
            }
        }

        // Entry stairs at South doorway
        world.setBlock(x, baseY, z + 3, stairs, 3, 2);

        // Interior Coffin along West wall
        if (ModBlocks.coffin != null) {
            WorldGenVampireCrypt.placeCoffin(world, x - 1, floorY + 1, z - 1, 0, ModBlocks.coffin);
        }

        // Loot Chest in NE corner with canonical chests/vampire_hut.json loot
        world.setBlock(x + 1, floorY + 1, z - 1, Blocks.chest, 3, 2);
        TileEntity te = world.getTileEntity(x + 1, floorY + 1, z - 1);
        if (te instanceof TileEntityChest) {
            populateHutChest((TileEntityChest) te, rand);
        }

        // Spawn resident Vampire
        EntityVampire vampire = new EntityVampire(world);
        vampire.setLocationAndAngles(x + 0.5D, floorY + 1, z + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
        vampire.onSpawnWithEgg(null);
        vampire.setHomeArea(x, floorY + 1, z, 10);
        world.spawnEntityInWorld(vampire);

        ModLogger.spawn("Generated Vampire Hut at (%d, %d, %d)", x, floorY, z);
        return true;
    }

    private static void populateHutChest(TileEntityChest chest, Random rand) {
        // Pool 1: 3..5 rolls — vampire_fang (20), blood_bottle meta 0..3 (20)
        int rolls = 3 + rand.nextInt(3);
        for (int i = 0; i < rolls; i++) {
            int slot = rand.nextInt(chest.getSizeInventory());
            if (rand.nextBoolean() && ModItems.vampireFang != null) {
                chest.setInventorySlotContents(slot, new ItemStack(ModItems.vampireFang, 1));
            } else if (ModItems.bloodBottle != null) {
                chest.setInventorySlotContents(slot, new ItemStack(ModItems.bloodBottle, 1, rand.nextInt(4)));
            }
        }

        // Pool 2: 0..1 vampire_book (weight 70)
        if (rand.nextInt(100) < 70 && ModItems.vampireBook != null) {
            chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.vampireBook, 1, 0));
        }

        // Pool 3: 2 rolls — amulet / ring / obi_belt
        for (int i = 0; i < 2; i++) {
            int pick = rand.nextInt(3);
            Item ref = pick == 0 ? ModItems.amulet : (pick == 1 ? ModItems.ring : ModItems.obiBelt);
            if (ref != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ref, 1));
            }
        }

        // Pool 4: 50% vampire sword (21:9 -> 70% tier 1, 30% tier 2)
        if (rand.nextBoolean()) {
            int tier = rand.nextInt(10) < 7 ? 1 : 2;
            Item weapon = rand.nextBoolean() ? ModItems.heartSeeker : ModItems.heartStriker;
            if (weapon != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(weapon, 1, tier));
            }
        }
    }
}
