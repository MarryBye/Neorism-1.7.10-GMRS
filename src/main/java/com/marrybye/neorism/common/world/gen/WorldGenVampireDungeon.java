package com.marrybye.neorism.common.world.gen;

import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * Underground Vampire Dungeon feature.
 * Canonical port of TeamLapen's VampireDungeonFeature & loot_table/chests/dungeon/vampire_dungeon.json.
 */
public class WorldGenVampireDungeon extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int j = rand.nextInt(2) + 2;
        int minX = -j - 1;
        int maxX = j + 1;
        int k1 = rand.nextInt(2) + 2;
        int minZ = -k1 - 1;
        int maxZ = k1 + 1;
        int openings = 0;

        for (int dx = minX; dx <= maxX; ++dx) {
            for (int dy = -1; dy <= 4; ++dy) {
                for (int dz = minZ; dz <= maxZ; ++dz) {
                    int wx = x + dx;
                    int wy = y + dy;
                    int wz = z + dz;
                    if (wy <= 2 || wy >= 250) {
                        return false;
                    }
                    Material mat = world.getBlock(wx, wy, wz).getMaterial();
                    boolean solid = mat.isSolid();
                    if (dy == -1 && !solid) return false;
                    if (dy == 4 && !solid) return false;

                    if ((dx == minX || dx == maxX || dz == minZ || dz == maxZ)
                        && dy == 0
                        && world.isAirBlock(wx, wy, wz)
                        && world.isAirBlock(wx, wy + 1, wz)) {
                        ++openings;
                    }
                }
            }
        }

        if (openings < 1 || openings > 5) {
            return false;
        }

        Block brickFloor = ModBlocks.darkStoneBricks != null ? ModBlocks.darkStoneBricks : Blocks.mossy_cobblestone;
        Block accentFloor = ModBlocks.crackedDarkStoneBricks != null ? ModBlocks.crackedDarkStoneBricks : brickFloor;

        for (int dx = minX; dx <= maxX; ++dx) {
            for (int dy = 3; dy >= -1; --dy) {
                for (int dz = minZ; dz <= maxZ; ++dz) {
                    int wx = x + dx;
                    int wy = y + dy;
                    int wz = z + dz;
                    Block cur = world.getBlock(wx, wy, wz);

                    if (dx != minX && dy != -1 && dz != minZ && dx != maxX && dy != 4 && dz != maxZ) {
                        if (cur != Blocks.chest && cur != Blocks.mob_spawner) {
                            world.setBlockToAir(wx, wy, wz);
                        }
                    } else if (wy >= 2 && !world.getBlock(wx, wy - 1, wz).getMaterial().isSolid()) {
                        world.setBlockToAir(wx, wy, wz);
                    } else if (cur.getMaterial().isSolid() && cur != Blocks.chest) {
                        if (dy == -1 && rand.nextInt(4) != 0) {
                            world.setBlock(wx, wy, wz, rand.nextInt(20) == 0 ? accentFloor : brickFloor, 0, 2);
                        } else {
                            // Canonical VampireDungeonFeature: spruce planks (meta 1 in 1.7.10)
                            world.setBlock(wx, wy, wz, Blocks.planks, 1, 2);
                        }
                    }
                }
            }
        }

        // Up to 2 loot chests along solid chamber walls
        for (int c = 0; c < 2; ++c) {
            for (int tries = 0; tries < 3; ++tries) {
                int cx = x + rand.nextInt(j * 2 + 1) - j;
                int cz = z + rand.nextInt(k1 * 2 + 1) - k1;
                if (world.isAirBlock(cx, y, cz)) {
                    int adjacentSolid = 0;
                    if (world.getBlock(cx - 1, y, cz).getMaterial().isSolid()) adjacentSolid++;
                    if (world.getBlock(cx + 1, y, cz).getMaterial().isSolid()) adjacentSolid++;
                    if (world.getBlock(cx, y, cz - 1).getMaterial().isSolid()) adjacentSolid++;
                    if (world.getBlock(cx, y, cz + 1).getMaterial().isSolid()) adjacentSolid++;

                    if (adjacentSolid == 1) {
                        world.setBlock(cx, y, cz, Blocks.chest, 0, 2);
                        TileEntity te = world.getTileEntity(cx, y, cz);
                        if (te instanceof TileEntityChest) {
                            populateDungeonChest((TileEntityChest) te, rand);
                        }
                        break;
                    }
                }
            }
        }

        // Central Vampire Mob Spawner
        world.setBlock(x, y, z, Blocks.mob_spawner, 0, 2);
        TileEntity spawnerTe = world.getTileEntity(x, y, z);
        if (spawnerTe instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner) spawnerTe).func_145881_a().setEntityName("neorism.vampire");
        }
        ModLogger.spawn("Generated Vampire Dungeon at (%d, %d, %d)", x, y, z);
        return true;
    }

    private static void populateDungeonChest(TileEntityChest chest, Random rand) {
        // Pool 1: 7 rolls — vampire_fang (35), blood_bottle max=5 (20)
        for (int i = 0; i < 7; i++) {
            int slot = rand.nextInt(chest.getSizeInventory());
            if (rand.nextInt(55) < 35) {
                if (ModItems.vampireFang != null) {
                    chest.setInventorySlotContents(slot, new ItemStack(ModItems.vampireFang, 1));
                }
            } else if (ModItems.bloodBottle != null) {
                chest.setInventorySlotContents(slot, new ItemStack(ModItems.bloodBottle, 1, 5));
            }
        }

        // Pool 2: 70% vampire_book
        if (rand.nextInt(100) < 70 && ModItems.vampireBook != null) {
            chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.vampireBook, 1, 0));
        }

        // Pool 3: 60% vampire sword (70% enhanced/diamond tier 1, 30% ultimate/netherite tier 2)
        if (rand.nextInt(100) < 60) {
            int tier = rand.nextInt(10) < 7 ? 1 : 2;
            BlockOrItem: {
                if (rand.nextBoolean() && ModItems.heartSeeker != null) {
                    chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.heartSeeker, 1, tier));
                    break BlockOrItem;
                }
                if (ModItems.heartStriker != null) {
                    chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.heartStriker, 1, tier));
                }
            }
        }

        // Pool 4: 3 rolls — amulet / ring / obi_belt
        for (int i = 0; i < 3; i++) {
            int pick = rand.nextInt(3);
            net.minecraft.item.Item ref = pick == 0 ? ModItems.amulet : (pick == 1 ? ModItems.ring : ModItems.obiBelt);
            if (ref != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ref, 1));
            }
        }
    }
}
