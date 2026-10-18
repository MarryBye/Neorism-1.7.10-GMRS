package com.marrybye.neorism.common.world.gen;

import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.vampire.EntityAdvancedVampire;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * Procedural Ancient Vampire Altar Ruins.
 * Canonical port of TeamLapen's VampireAltarPieces.
 * Generates abandoned gothic altar circles in deep forests, guarded by Advanced Vampires.
 */
public class WorldGenVampireAltarRuins extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        y = world.getTopSolidOrLiquidBlock(x, z);
        if (y < 60 || y > 130) {
            return false;
        }

        Block ground = world.getBlock(x, y - 1, z);
        if (ground != Blocks.grass && ground != Blocks.dirt && ground != ModBlocks.cursedGrass) {
            return false;
        }

        int radius = 4;

        // Build circular stone platform
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                if (dx * dx + dz * dz <= radius * radius) {
                    int curY = world.getTopSolidOrLiquidBlock(x + dx, z + dz);
                    for (int fillY = curY - 1; fillY <= y - 1; ++fillY) {
                        world.setBlock(x + dx, fillY, z + dz, ModBlocks.darkStone != null ? ModBlocks.darkStone : Blocks.stone, 0, 2);
                    }
                    // Surface ring: cracked and chiseled dark stone bricks
                    int r = rand.nextInt(10);
                    Block stone = r < 5 ? ModBlocks.darkStoneBricks : (r < 8 ? ModBlocks.crackedDarkStoneBricks : ModBlocks.chiseledDarkStoneBricks);
                    world.setBlock(x + dx, y - 1, z + dz, stone != null ? stone : Blocks.stonebrick, 0, 2);

                    // Clear above
                    for (int cy = y; cy <= y + 4; ++cy) {
                        world.setBlock(x + dx, cy, z + dz, Blocks.air, 0, 2);
                    }
                }
            }
        }

        // 4 Altar Pillars
        int[][] pillars = {{-2, -2}, {2, -2}, {-2, 2}, {2, 2}};
        for (int[] p : pillars) {
            world.setBlock(x + p[0], y, z + p[1], ModBlocks.chiseledDarkStoneBricks, 0, 2);
            world.setBlock(x + p[0], y + 1, z + p[1], ModBlocks.darkStoneBricks, 0, 2);
            world.setBlock(x + p[0], y + 2, z + p[1], ModBlocks.darkStoneBricks, 0, 2);
            world.setBlock(x + p[0], y + 3, z + p[1], Blocks.netherrack, 0, 2);
            world.setBlock(x + p[0], y + 4, z + p[1], Blocks.fire, 0, 2);
        }

        // Central Altar of Inspiration or Blood Pedestal
        if (ModBlocks.altarInspiration != null) {
            world.setBlock(x, y, z, ModBlocks.altarInspiration, 0, 2);
        } else if (ModBlocks.bloodPedestal != null) {
            world.setBlock(x, y, z, ModBlocks.bloodPedestal, 0, 2);
        }

        // Hidden treasure chest beneath the altar
        world.setBlock(x, y - 1, z, Blocks.chest, 0, 2);
        TileEntityChest chest = (TileEntityChest) world.getTileEntity(x, y - 1, z);
        if (chest != null) {
            if (ModItems.pureBlood != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.pureBlood, 1 + rand.nextInt(2)));
            }
            if (ModItems.vampireBook != null && rand.nextBoolean()) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.vampireBook));
            }
            if (ModItems.vampireFang != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.vampireFang, 3 + rand.nextInt(4)));
            }
            if (ModBlocks.vampireOrchid != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModBlocks.vampireOrchid, 2 + rand.nextInt(3)));
            }
            chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(Items.gold_ingot, 2 + rand.nextInt(3)));
        }

        // Canonical VampireAltarPieces: Spawn Advanced Vampire Guardian if protected from sun or in Vampire Forest
        if (ModWorldGen.isVampireBiome(world.getBiomeGenForCoords(x, z)) || !world.isDaytime()) {
            EntityAdvancedVampire guardian = new EntityAdvancedVampire(world);
            guardian.setLocationAndAngles(x + 0.5D, y, z + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
            guardian.onSpawnWithEgg(null);
            guardian.setHomeArea(x, y, z, 12);
            world.spawnEntityInWorld(guardian);
        }

        return true;
    }
}
