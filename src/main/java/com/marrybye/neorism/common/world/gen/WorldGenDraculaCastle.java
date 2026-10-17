package com.marrybye.neorism.common.world.gen;

import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.vampire.EntityDracula;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * Procedural Dracula Castle Boss Arena.
 * Canonical port of TeamLapen's DraculaCastlePieces.
 * Generates an imposing gothic fortress with towers, throne room, and Dracula boss spawn in the Vampire Forest.
 */
public class WorldGenDraculaCastle extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        y = world.getTopSolidOrLiquidBlock(x, z);
        if (y < 55 || y > 140) {
            return false;
        }

        int halfW = 12; // 25 blocks wide
        int halfD = 12; // 25 blocks deep
        int wallH = 7;

        // Level foundation
        for (int dx = -halfW; dx <= halfW; ++dx) {
            for (int dz = -halfD; dz <= halfD; ++dz) {
                int curY = world.getTopSolidOrLiquidBlock(x + dx, z + dz);
                for (int fillY = curY - 1; fillY <= y - 1; ++fillY) {
                    world.setBlock(x + dx, fillY, z + dz, ModBlocks.darkStone != null ? ModBlocks.darkStone : Blocks.stone, 0, 2);
                }
                world.setBlock(x + dx, y - 1, z + dz, ModBlocks.polishedDarkStone != null ? ModBlocks.polishedDarkStone : Blocks.stonebrick, 0, 2);
                for (int cy = y; cy <= y + wallH + 4; ++cy) {
                    world.setBlock(x + dx, cy, z + dz, Blocks.air, 0, 2);
                }
            }
        }

        // Perimeter Walls and Ceiling
        for (int dx = -halfW; dx <= halfW; ++dx) {
            for (int dy = 0; dy <= wallH; ++dy) {
                for (int dz = -halfD; dz <= halfD; ++dz) {
                    int curX = x + dx;
                    int curY = y + dy;
                    int curZ = z + dz;

                    boolean isPerimeter = (dx == -halfW || dx == halfW || dz == -halfD || dz == halfD);
                    if (isPerimeter) {
                        if (dy == wallH) {
                            // Battlements / crenellations
                            if ((dx + dz) % 2 == 0) {
                                world.setBlock(curX, curY + 1, curZ, ModBlocks.darkStoneBricks != null ? ModBlocks.darkStoneBricks : Blocks.stonebrick, 0, 2);
                            }
                        } else if (dy >= 2 && dy <= 4 && (dx % 4 == 0 || dz % 4 == 0) && (dx != -halfW && dx != halfW || dz != -halfD && dz != halfD)) {
                            // Gothic slit windows
                            world.setBlock(curX, curY, curZ, Blocks.iron_bars, 0, 2);
                        } else {
                            world.setBlock(curX, curY, curZ, ModBlocks.darkStoneBricks != null ? ModBlocks.darkStoneBricks : Blocks.stonebrick, 0, 2);
                        }
                    } else if (dy == wallH) {
                        // High ceiling
                        world.setBlock(curX, curY, curZ, ModBlocks.darkStoneBricks != null ? ModBlocks.darkStoneBricks : Blocks.stonebrick, 0, 2);
                    }
                }
            }
        }

        // 4 Corner Towers (height wallH + 4)
        int[][] corners = {{-halfW, -halfD}, {halfW, -halfD}, {-halfW, halfD}, {halfW, halfD}};
        for (int[] c : corners) {
            for (int dy = 0; dy <= wallH + 3; ++dy) {
                for (int tdx = -1; tdx <= 1; ++tdx) {
                    for (int tdz = -1; tdz <= 1; ++tdz) {
                        world.setBlock(x + c[0] + tdx, y + dy, z + c[1] + tdz, ModBlocks.darkStoneBricks != null ? ModBlocks.darkStoneBricks : Blocks.stonebrick, 0, 2);
                    }
                }
            }
            // Top fire brazier in tower
            world.setBlock(x + c[0], y + wallH + 4, z + c[1], Blocks.netherrack, 0, 2);
            world.setBlock(x + c[0], y + wallH + 5, z + c[1], Blocks.fire, 0, 2);
        }

        // Throne Room Interior
        // Red Carpet along center aisle from entrance to throne
        for (int dz = -halfD + 1; dz <= halfD - 3; ++dz) {
            world.setBlock(x, y, z + dz, Blocks.carpet, 14, 2); // Red carpet
            world.setBlock(x - 1, y, z + dz, Blocks.carpet, 15, 2); // Black border carpet
            world.setBlock(x + 1, y, z + dz, Blocks.carpet, 15, 2);
        }

        // Entrance Gate (iron bars / archway)
        world.setBlock(x - 1, y, z - halfD, Blocks.air, 0, 2);
        world.setBlock(x, y, z - halfD, Blocks.air, 0, 2);
        world.setBlock(x + 1, y, z - halfD, Blocks.air, 0, 2);
        world.setBlock(x - 1, y + 1, z - halfD, Blocks.air, 0, 2);
        world.setBlock(x, y + 1, z - halfD, Blocks.air, 0, 2);
        world.setBlock(x + 1, y + 1, z - halfD, Blocks.air, 0, 2);
        world.setBlock(x, y + 2, z - halfD, Blocks.iron_bars, 0, 2);

        // Raised Throne Dais at (x, z + halfD - 3)
        int throneZ = z + halfD - 4;
        for (int dx = -3; dx <= 3; ++dx) {
            for (int dz = 0; dz <= 3; ++dz) {
                world.setBlock(x + dx, y, throneZ + dz, ModBlocks.chiseledDarkStoneBricks != null ? ModBlocks.chiseledDarkStoneBricks : Blocks.stonebrick, 0, 2);
            }
        }
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = 1; dz <= 3; ++dz) {
                world.setBlock(x + dx, y + 1, throneZ + dz, ModBlocks.chiseledDarkStoneBricks != null ? ModBlocks.chiseledDarkStoneBricks : Blocks.stonebrick, 0, 2);
            }
        }

        // Dracula's Grand Throne
        world.setBlock(x, y + 2, throneZ + 2, Blocks.nether_brick_stairs, 2, 2);
        world.setBlock(x - 1, y + 2, throneZ + 2, Blocks.nether_brick_fence, 0, 2);
        world.setBlock(x + 1, y + 2, throneZ + 2, Blocks.nether_brick_fence, 0, 2);
        world.setBlock(x, y + 3, throneZ + 2, Blocks.nether_brick, 0, 2);
        world.setBlock(x, y + 4, throneZ + 2, Blocks.nether_brick_fence, 0, 2);

        // Blood Pedestals flanking the throne
        if (ModBlocks.bloodPedestal != null) {
            world.setBlock(x - 2, y + 2, throneZ + 2, ModBlocks.bloodPedestal, 0, 2);
            world.setBlock(x + 2, y + 2, throneZ + 2, ModBlocks.bloodPedestal, 0, 2);
        }

        // Royal Vampire Loot Chests behind the throne
        placeRoyalChest(world, rand, x - 2, y + 2, throneZ + 3);
        placeRoyalChest(world, rand, x + 2, y + 2, throneZ + 3);

        // Spawn Dracula Boss at the Throne
        EntityDracula dracula = new EntityDracula(world);
        dracula.setLocationAndAngles(x + 0.5D, y + 2, throneZ + 1.5D, 180.0F, 0.0F);
        dracula.onSpawnWithEgg(null);
        world.spawnEntityInWorld(dracula);

        return true;
    }

    private void placeRoyalChest(World world, Random rand, int cx, int cy, int cz) {
        world.setBlock(cx, cy, cz, Blocks.chest, 0, 2);
        TileEntityChest chest = (TileEntityChest) world.getTileEntity(cx, cy, cz);
        if (chest != null) {
            if (ModItems.sovereignBlood != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.sovereignBlood));
            }
            if (ModItems.pureBlood != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.pureBlood, 2 + rand.nextInt(3)));
            }
            if (ModItems.vampireBook != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.vampireBook, 1 + rand.nextInt(2)));
            }
            if (ModItems.bloodInfusedNetherite != null && rand.nextFloat() < 0.4F) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.bloodInfusedNetherite));
            }
            chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(Items.diamond, 2 + rand.nextInt(4)));
            chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(Items.emerald, 3 + rand.nextInt(6)));
            chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(Blocks.gold_block, 1 + rand.nextInt(2)));
        }
    }
}
