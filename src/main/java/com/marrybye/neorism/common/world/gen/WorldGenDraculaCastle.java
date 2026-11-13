package com.marrybye.neorism.common.world.gen;

import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModFluids;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.vampire.EntityAdvancedVampire;
import com.marrybye.neorism.common.entity.vampire.EntityDracula;
import com.marrybye.neorism.common.entity.vampire.EntityVampire;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.world.dimensions.velmorra.DraculaFightManager;
import com.marrybye.neorism.common.world.dimensions.velmorra.WorldProviderVelmorra;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * Grand Gothic Citadel of Lord Dracula in the Velmorra Dimension.
 * Canonical port of TeamLapen's DraculaCastlePieces expanded into a full
 * multi-wing fortress: gatehouse, 4 corner bastion towers, blood-fountain courtyard,
 * cathedral nave with stained-glass rose window, crypt & library wings, 4 ritual chalices,
 * and dormant Lord Dracula awaiting challengers on his throne.
 */
public class WorldGenDraculaCastle extends WorldGenerator {

    private static final int BASE_Y = 65;

    @Override
    public boolean generate(World world, Random rand, int originX, int originY, int originZ) {
        if (!(world.provider instanceof WorldProviderVelmorra)) {
            return false;
        }

        Block stone = blockOr(ModBlocks.darkStone, Blocks.stone);
        Block bricks = blockOr(ModBlocks.darkStoneBricks, Blocks.stonebrick);
        Block cracked = blockOr(ModBlocks.crackedDarkStoneBricks, bricks);
        Block chiseled = blockOr(ModBlocks.chiseledDarkStoneBricks, bricks);
        Block polished = blockOr(ModBlocks.polishedDarkStone, Blocks.stonebrick);
        Block tiles = blockOr(ModBlocks.darkStoneTiles, polished);
        Block purpleBricks = blockOr(ModBlocks.purpleStoneBricks, bricks);
        Block purpleTiles = blockOr(ModBlocks.purpleStoneTiles, purpleBricks);
        Block wall = blockOr(ModBlocks.darkStoneBricksWall, Blocks.cobblestone_wall);
        Block stairs = blockOr(ModBlocks.darkStoneBricksStairs, Blocks.stone_brick_stairs);
        Block woodLog = blockOr(ModBlocks.darkSpruceLog, Blocks.log);
        Block planks = blockOr(ModBlocks.darkSprucePlanks, Blocks.planks);

        // 1. Solid foundation + interior airspace clear
        for (int dx = -30; dx <= 30; dx++) {
            for (int dz = -26; dz <= 34; dz++) {
                for (int fy = 58; fy < BASE_Y; fy++) {
                    Block fBlock = fy >= 63 ? bricks : stone;
                    world.setBlock(dx, fy, dz, fBlock, 0, 2);
                }
                for (int cy = BASE_Y; cy <= 98; cy++) {
                    world.setBlockToAir(dx, cy, dz);
                }
            }
        }

        // Grand Avenue leading from spawn/portal (0, 65, -34) to Gatehouse (z = -26)
        buildGrandAvenue(world, stone, bricks, polished, tiles, wall);

        // 2. Outer Curtain Walls & Battlements ([-30..30] x [-26..34])
        buildCurtainWalls(world, rand, bricks, cracked, chiseled, wall);

        // 3. 4 Corner Bastion Towers (9x9, y = 65..95)
        buildCornerTower(world, rand, -26, -22, bricks, chiseled, polished, purpleBricks, purpleTiles, tiles, stairs);
        buildCornerTower(world, rand, 26, -22, bricks, chiseled, polished, purpleBricks, purpleTiles, tiles, stairs);
        buildCornerTower(world, rand, -26, 30, bricks, chiseled, polished, purpleBricks, purpleTiles, tiles, stairs);
        buildCornerTower(world, rand, 26, 30, bricks, chiseled, polished, purpleBricks, purpleTiles, tiles, stairs);

        // 4. Twin Gatehouse & Courtyard Blood Fountains
        buildGatehouse(world, bricks, chiseled, polished, purpleBricks, tiles, wall);
        buildCourtyard(world, bricks, chiseled, polished, tiles, wall);

        // 5. Central Gothic Cathedral Keep, Nave, Throne Apse & Spire
        buildCathedralKeep(world, rand, bricks, cracked, chiseled, polished, tiles, purpleBricks, purpleTiles, stairs, planks);

        // 6. East & West Castle Wings (Royal Crypt & Alchemical Library)
        buildWestCryptWing(world, rand, bricks, chiseled, polished, tiles, purpleBricks, woodLog, planks);
        buildEastLibraryWing(world, rand, bricks, chiseled, polished, tiles, purpleBricks, woodLog, planks);

        // 7. Register Chalices & Spawn Dormant Lord Dracula + Sentries
        registerChalicesAndEntities(world, rand);

        ModLogger.info("Generated Grand Gothic Dracula Castle at (0, %d, 0) in Velmorra.", BASE_Y);
        return true;
    }

    private void buildGrandAvenue(World world, Block stone, Block bricks, Block polished, Block tiles, Block wall) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -40; dz <= -27; dz++) {
                for (int fy = 60; fy < BASE_Y; fy++) {
                    world.setBlock(dx, fy, dz, fy >= 63 ? bricks : stone, 0, 2);
                }
                for (int cy = BASE_Y; cy <= 76; cy++) {
                    world.setBlockToAir(dx, cy, dz);
                }
                if (Math.abs(dx) <= 2) {
                    world.setBlock(dx, BASE_Y - 1, dz, (Math.abs(dx) == 0 || dz % 2 == 0) ? polished : tiles, 0, 2);
                } else if (Math.abs(dx) == 3) {
                    world.setBlock(dx, BASE_Y - 1, dz, bricks, 0, 2);
                    if (dz % 4 != 0) {
                        world.setBlock(dx, BASE_Y, dz, wall, 0, 2);
                    }
                }
            }
        }
        int[] obeliskZ = {-38, -32};
        for (int oz : obeliskZ) {
            for (int side : new int[]{-4, 4}) {
                world.setBlock(side, BASE_Y, oz, bricks, 0, 2);
                world.setBlock(side, BASE_Y + 1, oz, bricks, 0, 2);
                world.setBlock(side, BASE_Y + 2, oz, wall, 0, 2);
                if (ModBlocks.vampireSoulLantern != null) {
                    world.setBlock(side, BASE_Y + 3, oz, ModBlocks.vampireSoulLantern, 0, 2);
                }
            }
        }
    }

    private void buildCurtainWalls(World world, Random rand, Block bricks, Block cracked, Block chiseled, Block wall) {
        int wallTop = BASE_Y + 9;
        for (int dx = -30; dx <= 30; dx++) {
            for (int dz = -26; dz <= 34; dz++) {
                boolean outerRing = (dx == -30 || dx == 30 || dz == -26 || dz == 34);
                boolean innerWalk = (!outerRing && (Math.abs(dx) >= 28 || dz == -25 || dz == -24 || dz >= 32));
                if (outerRing) {
                    for (int y = BASE_Y; y <= wallTop; y++) {
                        boolean isTopTrim = (y == wallTop);
                        Block b = isTopTrim ? chiseled : (rand.nextInt(7) == 0 ? cracked : bricks);
                        world.setBlock(dx, y, dz, b, 0, 2);
                    }
                    // Crenellations
                    if ((dx + dz) % 2 == 0) {
                        world.setBlock(dx, wallTop + 1, dz, wall, 0, 2);
                    }
                } else if (innerWalk) {
                    world.setBlock(dx, wallTop - 2, dz, bricks, 0, 2);
                    if ((Math.abs(dx) == 28 || dz == -24 || dz == 32) && (dx + dz) % 8 == 0 && ModBlocks.vampireSoulLantern != null) {
                        world.setBlock(dx, wallTop - 1, dz, ModBlocks.vampireSoulLantern, 0, 2);
                    }
                }
            }
        }
    }

    private void buildCornerTower(World world, Random rand, int cx, int cz, Block bricks, Block chiseled,
                                  Block polished, Block purpleBricks, Block purpleTiles, Block tiles, Block stairs) {
        int topY = BASE_Y + 22;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                int wx = cx + dx;
                int wz = cz + dz;
                boolean edge = (Math.abs(dx) == 4 || Math.abs(dz) == 4);
                boolean corner = (Math.abs(dx) == 4 && Math.abs(dz) == 4);
                for (int y = BASE_Y; y <= topY; y++) {
                    if (edge) {
                        boolean window = (y == BASE_Y + 4 || y == BASE_Y + 5 || y == BASE_Y + 12 || y == BASE_Y + 13)
                            && (dx == 0 || dz == 0);
                        if (window) {
                            world.setBlock(wx, y, wz, Blocks.iron_bars, 0, 2);
                        } else {
                            world.setBlock(wx, y, wz, (y == BASE_Y || y == topY || corner) ? chiseled : bricks, 0, 2);
                        }
                    } else {
                        if (y == BASE_Y || y == BASE_Y + 8 || y == BASE_Y + 16 || y == topY) {
                            world.setBlock(wx, y, wz, (y == topY) ? purpleTiles : tiles, 0, 2);
                        } else {
                            world.setBlockToAir(wx, y, wz);
                        }
                    }
                }
                if (edge && (dx + dz) % 2 == 0) {
                    world.setBlock(wx, topY + 1, wz, purpleBricks, 0, 2);
                }
            }
        }

        // Pointed Gothic Roof Spire
        for (int layer = 0; layer < 5; layer++) {
            int r = 4 - layer;
            int ry = topY + 2 + layer;
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) == r || Math.abs(dz) == r || layer >= 3) {
                        world.setBlock(cx + dx, ry, cz + dz, purpleBricks, 0, 2);
                    }
                }
            }
        }
        world.setBlock(cx, topY + 7, cz, chiseled, 0, 2);
        if (ModBlocks.vampireSoulLantern != null) {
            world.setBlock(cx, topY + 8, cz, ModBlocks.vampireSoulLantern, 0, 2);
        }

        // Interior ground doorway facing courtyard & ladder up
        int doorDirX = cx < 0 ? 4 : -4;
        int doorDirZ = cz < 0 ? 4 : -4;
        for (int y = BASE_Y; y <= BASE_Y + 2; y++) {
            world.setBlockToAir(cx + doorDirX, y, cz);
            world.setBlockToAir(cx, y, cz + doorDirZ);
        }
        for (int y = BASE_Y; y < topY; y++) {
            world.setBlock(cx, y, cz, polished, 0, 2);
            world.setBlock(cx + 1, y, cz, Blocks.ladder, 5, 2);
        }
        if (ModBlocks.candelabra != null) {
            world.setBlock(cx - 2, BASE_Y + 1, cz - 2, ModBlocks.candelabra, 0, 2);
            world.setBlock(cx - 2, BASE_Y + 9, cz - 2, ModBlocks.candelabra, 0, 2);
        }
        if (cz > 0) {
            placeLootChest(world, rand, cx - 2, BASE_Y + 17, cz + 2, 2);
        }
    }

    private void buildGatehouse(World world, Block bricks, Block chiseled, Block polished,
                                Block purpleBricks, Block tiles, Block wall) {
        // Flanking Gate Towers (x = -8..-4 and 4..8, z = -27..-21)
        for (int side : new int[]{-6, 6}) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    int wx = side + dx;
                    int wz = -24 + dz;
                    boolean outer = (Math.abs(dx) == 2 || Math.abs(dz) == 3);
                    for (int y = BASE_Y; y <= BASE_Y + 19; y++) {
                        if (outer) {
                            boolean arrowSlit = (dx == 0 && (y == BASE_Y + 6 || y == BASE_Y + 7 || y == BASE_Y + 13));
                            world.setBlock(wx, y, wz, arrowSlit ? Blocks.iron_bars : (y % 6 == 0 ? chiseled : bricks), 0, 2);
                        } else if (y == BASE_Y || y == BASE_Y + 10 || y == BASE_Y + 19) {
                            world.setBlock(wx, y, wz, tiles, 0, 2);
                        } else {
                            world.setBlockToAir(wx, y, wz);
                        }
                    }
                    if (outer && (dx + dz) % 2 == 0) {
                        world.setBlock(wx, BASE_Y + 20, wz, purpleBricks, 0, 2);
                    }
                }
            }
            if (ModBlocks.vampireSoulLantern != null) {
                world.setBlock(side, BASE_Y + 21, -24, ModBlocks.vampireSoulLantern, 0, 2);
                world.setBlock(side > 0 ? 4 : -4, BASE_Y + 3, -27, ModBlocks.vampireSoulLantern, 0, 2);
            }
        }

        // Gatehouse Arch Bridge (x = -3..3, z = -27..-21)
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -27; dz <= -21; dz++) {
                world.setBlock(dx, BASE_Y - 1, dz, Math.abs(dx) <= 1 ? polished : tiles, 0, 2);
                for (int y = BASE_Y; y <= BASE_Y + 7; y++) {
                    world.setBlockToAir(dx, y, dz);
                }
                for (int y = BASE_Y + 8; y <= BASE_Y + 13; y++) {
                    world.setBlock(dx, y, dz, y == BASE_Y + 8 ? chiseled : bricks, 0, 2);
                }
                if ((dz == -27 || dz == -21) && Math.abs(dx) % 2 == 0) {
                    world.setBlock(dx, BASE_Y + 14, dz, wall, 0, 2);
                }
            }
        }
        // Raised Portcullis Teeth & Arch Brackets
        for (int dx = -3; dx <= 3; dx++) {
            world.setBlock(dx, BASE_Y + 7, -25, Blocks.iron_bars, 0, 2);
            if (Math.abs(dx) == 3) {
                world.setBlock(dx, BASE_Y + 6, -25, chiseled, 0, 2);
                world.setBlock(dx, BASE_Y + 6, -22, chiseled, 0, 2);
            }
        }
    }

    private void buildCourtyard(World world, Block bricks, Block chiseled, Block polished, Block tiles, Block wall) {
        // Paved Courtyard Floor (x = -27..27, z = -20..-7)
        for (int dx = -27; dx <= 27; dx++) {
            for (int dz = -20; dz <= -7; dz++) {
                boolean centerPath = Math.abs(dx) <= 2;
                Block floor = centerPath ? polished : (((dx + dz) & 1) == 0 ? tiles : bricks);
                world.setBlock(dx, BASE_Y - 1, dz, floor, 0, 2);
                if (centerPath && dz % 2 == 0) {
                    world.setBlock(dx, BASE_Y - 1, dz, ModBlocks.purpleStoneTiles != null ? ModBlocks.purpleStoneTiles : polished, 0, 2);
                }
            }
        }

        // Twin Blood Fountains in Courtyard (x = +-15, z = -14)
        Block bloodBlock = ModFluids.blockBlood != null ? ModFluids.blockBlood : Blocks.water;
        for (int fx : new int[]{-15, 15}) {
            int fz = -14;
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    boolean rim = (Math.abs(dx) == 3 || Math.abs(dz) == 3);
                    if (rim) {
                        world.setBlock(fx + dx, BASE_Y, fz + dz, chiseled, 0, 2);
                        if (Math.abs(dx) == 3 && Math.abs(dz) == 3) {
                            world.setBlock(fx + dx, BASE_Y + 1, fz + dz, wall, 0, 2);
                            if (ModBlocks.candelabra != null) {
                                world.setBlock(fx + dx, BASE_Y + 2, fz + dz, ModBlocks.candelabra, 0, 2);
                            }
                        }
                    } else if (dx == 0 && dz == 0) {
                        world.setBlock(fx, BASE_Y, fz, chiseled, 0, 2);
                        world.setBlock(fx, BASE_Y + 1, fz, bricks, 0, 2);
                        world.setBlock(fx, BASE_Y + 2, fz, chiseled, 0, 2);
                        world.setBlock(fx, BASE_Y + 3, fz, bloodBlock, 0, 2);
                    } else {
                        world.setBlock(fx + dx, BASE_Y, fz + dz, bloodBlock, 0, 2);
                    }
                }
            }
        }

        // Courtyard Grave Alcoves along inner North walls
        if (ModBlocks.tombstoneCross != null && ModBlocks.vampireOrchid != null) {
            for (int gx : new int[]{-22, -19, 19, 22}) {
                world.setBlock(gx, BASE_Y, -21, ModBlocks.tombstoneCross, 0, 2);
                world.setBlock(gx, BASE_Y, -20, ModBlocks.vampireOrchid, 0, 2);
            }
        }
    }

    private void buildCathedralKeep(World world, Random rand, Block bricks, Block cracked, Block chiseled,
                                    Block polished, Block tiles, Block purpleBricks, Block purpleTiles, Block stairs, Block planks) {
        int wallHeight = 14; // y = 65..78
        int naveHalfW = 11;

        for (int dx = -naveHalfW; dx <= naveHalfW; dx++) {
            for (int dz = -6; dz <= 28; dz++) {
                boolean isSideWall = Math.abs(dx) == naveHalfW;
                boolean isFrontOrBack = (dz == -6 || dz == 28);
                boolean isCornerPillar = (Math.abs(dx) == naveHalfW && (dz == -6 || dz == 28 || dz % 6 == 0));

                // Floor
                world.setBlock(dx, BASE_Y - 1, dz, ((dx + dz) & 1) == 0 ? tiles : polished, 0, 2);
                if (Math.abs(dx) <= 1 && dz >= -6 && dz <= 18) {
                    world.setBlock(dx, BASE_Y, dz, Blocks.carpet, 14, 2); // Crimson runner
                }

                // Walls
                if (isSideWall || isFrontOrBack) {
                    for (int y = BASE_Y; y <= BASE_Y + wallHeight; y++) {
                        boolean entranceDoor = (dz == -6 && Math.abs(dx) <= 3 && y <= BASE_Y + 7 - (Math.abs(dx) == 3 ? 2 : 0));
                        boolean wingDoor = (isSideWall && ((dz >= 4 && dz <= 6) || (dz >= 15 && dz <= 17)) && y <= BASE_Y + 4);
                        boolean clerestoryWindow = (isSideWall && !isCornerPillar && (dz % 6 == 3 || dz % 6 == -3)
                            && y >= BASE_Y + 5 && y <= BASE_Y + 10);
                        boolean roseWindow = (dz == 28 && Math.abs(dx) <= 3 && y >= BASE_Y + 6 && y <= BASE_Y + 12
                            && (dx * dx + (y - (BASE_Y + 9)) * (y - (BASE_Y + 9)) <= 10));

                        if (entranceDoor || wingDoor) {
                            world.setBlockToAir(dx, y, dz);
                        } else if (clerestoryWindow) {
                            world.setBlock(dx, y, dz, Blocks.stained_glass_pane, (y % 2 == 0) ? 14 : 10, 2);
                        } else if (roseWindow) {
                            int paneColor = (dx == 0 || y == BASE_Y + 9) ? 14 : 10;
                            world.setBlock(dx, y, dz, Blocks.stained_glass_pane, paneColor, 2);
                        } else if (isCornerPillar || y == BASE_Y || y == BASE_Y + wallHeight) {
                            world.setBlock(dx, y, dz, chiseled, 0, 2);
                        } else {
                            world.setBlock(dx, y, dz, rand.nextInt(8) == 0 ? cracked : bricks, 0, 2);
                        }
                    }
                }
            }
        }

        // Ribbed Interior Nave Pillars (x = +-6) & Hanging Chandeliers
        for (int pz = 0; pz <= 18; pz += 6) {
            for (int px : new int[]{-6, 6}) {
                for (int y = BASE_Y; y <= BASE_Y + wallHeight; y++) {
                    world.setBlock(px, y, pz, (y == BASE_Y || y == BASE_Y + wallHeight) ? chiseled : bricks, 0, 2);
                }
                if (ModBlocks.candelabra != null) {
                    world.setBlock(px + (px < 0 ? 1 : -1), BASE_Y + 3, pz, ModBlocks.candelabra, 0, 2);
                }
            }
            if (ModBlocks.chandelier != null) {
                world.setBlock(0, BASE_Y + wallHeight - 2, pz, ModBlocks.chandelier, 0, 2);
            }
        }

        // Pitched Gothic Roof over Nave (y = 79..86)
        for (int dz = -7; dz <= 29; dz++) {
            for (int step = 0; step <= 6; step++) {
                int rx = naveHalfW + 1 - step * 2;
                int ry = BASE_Y + wallHeight + 1 + step;
                for (int dx = -rx; dx <= rx; dx++) {
                    if (Math.abs(dx) >= rx - 1 || step == 6) {
                        world.setBlock(dx, ry, dz, (Math.abs(dx) == rx) ? purpleBricks : bricks, 0, 2);
                    }
                }
            }
        }

        // Central Gothic Bell Spire above Throne Apse (x = -4..4, z = 14..22, y = 85..97)
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = 14; dz <= 22; dz++) {
                boolean rim = (Math.abs(dx) == 4 || Math.abs(dz - 18) == 4);
                for (int y = BASE_Y + wallHeight + 5; y <= BASE_Y + wallHeight + 13; y++) {
                    if (rim) {
                        boolean lancet = (dx == 0 || dz == 18) && y >= BASE_Y + wallHeight + 7 && y <= BASE_Y + wallHeight + 10;
                        world.setBlock(dx, y, dz, lancet ? Blocks.iron_bars : chiseled, 0, 2);
                    }
                }
            }
        }
        for (int s = 0; s < 5; s++) {
            int sr = 4 - s;
            int sy = BASE_Y + wallHeight + 14 + s;
            for (int dx = -sr; dx <= sr; dx++) {
                for (int dz = 18 - sr; dz <= 18 + sr; dz++) {
                    world.setBlock(dx, sy, dz, purpleTiles, 0, 2);
                }
            }
        }
        if (ModBlocks.vampireSoulLantern != null) {
            world.setBlock(0, BASE_Y + wallHeight + 19, 18, ModBlocks.vampireSoulLantern, 0, 2);
        }

        // Elevated Throne Dais (x = -5..5, z = 19..26)
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = 19; dz <= 26; dz++) {
                world.setBlock(dx, BASE_Y, dz, chiseled, 0, 2);
                if (dz >= 21 && Math.abs(dx) <= 3) {
                    world.setBlock(dx, BASE_Y + 1, dz, purpleTiles, 0, 2);
                }
                if (Math.abs(dx) <= 1 && dz >= 21 && dz <= 24) {
                    world.setBlock(dx, BASE_Y + 2, dz, Blocks.carpet, 14, 2);
                }
            }
        }
        // Steps onto Dais
        for (int dx = -2; dx <= 2; dx++) {
            world.setBlock(dx, BASE_Y, 18, stairs, 2, 2);
            world.setBlock(dx, BASE_Y + 1, 20, stairs, 2, 2);
        }

        // Throne + Royal Regalia
        if (ModBlocks.throne != null) {
            world.setBlock(0, BASE_Y + 2, 24, ModBlocks.throne, 0, 2);
        }
        if (ModBlocks.ancientTrophy != null) {
            world.setBlock(-2, BASE_Y + 2, 25, ModBlocks.ancientTrophy, 0, 2);
            world.setBlock(2, BASE_Y + 2, 25, ModBlocks.ancientTrophy, 0, 2);
        }
        if (ModBlocks.vampireRack != null) {
            world.setBlock(-4, BASE_Y + 1, 25, ModBlocks.vampireRack, 0, 2);
            world.setBlock(4, BASE_Y + 1, 25, ModBlocks.vampireRack, 0, 2);
        }
        if (ModBlocks.candelabra != null) {
            world.setBlock(-3, BASE_Y + 2, 24, ModBlocks.candelabra, 0, 2);
            world.setBlock(3, BASE_Y + 2, 24, ModBlocks.candelabra, 0, 2);
        }

        // 4 Ritual Blood Chalices on Pedestals
        int[][] chaliceCoords = {{-5, 16}, {5, 16}, {-5, 22}, {5, 22}};
        for (int[] c : chaliceCoords) {
            world.setBlock(c[0], BASE_Y, c[1], chiseled, 0, 2);
            world.setBlock(c[0], BASE_Y + 1, c[1], chiseled, 0, 2);
            if (ModBlocks.chalice != null) {
                world.setBlock(c[0], BASE_Y + 2, c[1], ModBlocks.chalice, 0, 2);
            }
        }
    }

    private void buildWestCryptWing(World world, Random rand, Block bricks, Block chiseled, Block polished,
                                    Block tiles, Block purpleBricks, Block log, Block planks) {
        for (int dx = -19; dx <= -12; dx++) {
            for (int dz = 0; dz <= 22; dz++) {
                boolean outerWall = (dx == -19 || dz == 0 || dz == 22);
                world.setBlock(dx, BASE_Y - 1, dz, tiles, 0, 2);
                world.setBlock(dx, BASE_Y + 6, dz, polished, 0, 2);
                world.setBlock(dx, BASE_Y + 12, dz, purpleBricks, 0, 2);

                if (outerWall) {
                    for (int y = BASE_Y; y <= BASE_Y + 11; y++) {
                        boolean window = (dx == -19 && (dz == 6 || dz == 11 || dz == 16)
                            && ((y >= BASE_Y + 2 && y <= BASE_Y + 4) || (y >= BASE_Y + 8 && y <= BASE_Y + 10)));
                        world.setBlock(dx, y, dz, window ? Blocks.iron_bars : bricks, 0, 2);
                    }
                }
            }
        }
        // Coffins & Candelabras inside Crypt Wing
        if (ModBlocks.coffin != null) {
            world.setBlock(-17, BASE_Y, 6, ModBlocks.coffin, 0, 2);
            world.setBlock(-17, BASE_Y, 15, ModBlocks.coffin, 0, 2);
        }
        if (ModBlocks.batCage != null) {
            world.setBlock(-16, BASE_Y + 4, 11, ModBlocks.batCage, 0, 2);
        }
        if (ModBlocks.vampireSoulLantern != null) {
            world.setBlock(-15, BASE_Y + 7, 11, ModBlocks.vampireSoulLantern, 0, 2);
        }
        // Stairs connecting ground & upper gallery
        for (int i = 0; i < 6; i++) {
            world.setBlock(-14, BASE_Y + i, 2 + i, planks, 0, 2);
            if (i > 0) {
                world.setBlockToAir(-14, BASE_Y + 6, 2 + i);
            }
        }
        placeLootChest(world, rand, -17, BASE_Y, 20, 4);
        placeLootChest(world, rand, -17, BASE_Y + 7, 11, 4);
    }

    private void buildEastLibraryWing(World world, Random rand, Block bricks, Block chiseled, Block polished,
                                      Block tiles, Block purpleBricks, Block log, Block planks) {
        for (int dx = 12; dx <= 19; dx++) {
            for (int dz = 0; dz <= 22; dz++) {
                boolean outerWall = (dx == 19 || dz == 0 || dz == 22);
                world.setBlock(dx, BASE_Y - 1, dz, tiles, 0, 2);
                world.setBlock(dx, BASE_Y + 6, dz, polished, 0, 2);
                world.setBlock(dx, BASE_Y + 12, dz, purpleBricks, 0, 2);

                if (outerWall) {
                    for (int y = BASE_Y; y <= BASE_Y + 11; y++) {
                        boolean window = (dx == 19 && (dz == 6 || dz == 11 || dz == 16)
                            && ((y >= BASE_Y + 2 && y <= BASE_Y + 4) || (y >= BASE_Y + 8 && y <= BASE_Y + 10)));
                        world.setBlock(dx, y, dz, window ? Blocks.stained_glass_pane : bricks, window ? 14 : 0, 2);
                    }
                }
            }
        }
        // Blood Alchemical Library Shelves & Pedestals
        for (int bz : new int[]{4, 8, 12, 16}) {
            for (int y = BASE_Y; y <= BASE_Y + 2; y++) {
                world.setBlock(18, y, bz, Blocks.bookshelf, 0, 2);
                world.setBlock(18, y, bz + 1, Blocks.bookshelf, 0, 2);
            }
        }
        if (ModBlocks.bloodContainer != null) {
            world.setBlock(16, BASE_Y, 10, ModBlocks.bloodContainer, 0, 2);
        }
        if (ModBlocks.bloodPedestal != null) {
            world.setBlock(16, BASE_Y, 14, ModBlocks.bloodPedestal, 0, 2);
        }
        if (ModBlocks.candelabra != null) {
            world.setBlock(15, BASE_Y, 12, ModBlocks.candelabra, 0, 2);
            world.setBlock(15, BASE_Y + 7, 12, ModBlocks.candelabra, 0, 2);
        }
        for (int i = 0; i < 6; i++) {
            world.setBlock(14, BASE_Y + i, 2 + i, planks, 0, 2);
            if (i > 0) {
                world.setBlockToAir(14, BASE_Y + 6, 2 + i);
            }
        }
        placeLootChest(world, rand, 17, BASE_Y, 20, 4);
        placeLootChest(world, rand, 17, BASE_Y + 7, 11, 4);
    }

    private void registerChalicesAndEntities(World world, Random rand) {
        DraculaFightManager mgr = DraculaFightManager.get(world);
        if (mgr != null) {
            int[][] chaliceCoords = {{-5, 16}, {5, 16}, {-5, 22}, {5, 22}};
            for (int[] c : chaliceCoords) {
                mgr.registerChalice(c[0], BASE_Y + 2, c[1]);
            }
        }

        // Spawn Dormant Lord Dracula right in front of his Throne so players see him immediately
        EntityDracula dracula = new EntityDracula(world);
        dracula.setLocationAndAngles(0.5D, BASE_Y + 2.0D, 21.5D, 180.0F, 0.0F);
        dracula.onSpawnWithEgg(null);
        dracula.setDormant(true);
        world.spawnEntityInWorld(dracula);

        // Spawn Vampire Castle Sentries in Courtyard & Wings
        int[][] sentries = {{-8, -14}, {8, -14}, {-15, 11}, {15, 11}};
        for (int i = 0; i < sentries.length; i++) {
            int sx = sentries[i][0];
            int sz = sentries[i][1];
            EntityLiving guard = (i % 2 == 0) ? (EntityLiving) new EntityAdvancedVampire(world) : new EntityVampire(world);
            guard.setLocationAndAngles(sx + 0.5D, BASE_Y, sz + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
            guard.onSpawnWithEgg(null);
            world.spawnEntityInWorld(guard);
        }
    }

    private void placeLootChest(World world, Random rand, int x, int y, int z, int tier) {
        world.setBlock(x, y, z, Blocks.chest, 0, 2);
        TileEntityChest chest = (TileEntityChest) world.getTileEntity(x, y, z);
        if (chest == null) return;

        if (ModItems.pureBlood != null) {
            chest.setInventorySlotContents(0, new ItemStack(ModItems.pureBlood, 1 + rand.nextInt(2), Math.min(4, tier)));
        }
        if (ModItems.vampireBloodBottle != null) {
            chest.setInventorySlotContents(3, new ItemStack(ModItems.vampireBloodBottle, 1 + rand.nextInt(3)));
        }
        if (ModItems.bloodInfusedDiamond != null && rand.nextInt(2) == 0) {
            chest.setInventorySlotContents(6, new ItemStack(ModItems.bloodInfusedDiamond, 1 + rand.nextInt(2)));
        } else if (ModItems.bloodInfusedIron != null) {
            chest.setInventorySlotContents(6, new ItemStack(ModItems.bloodInfusedIron, 2 + rand.nextInt(4)));
        }
        if (ModItems.vampireFang != null) {
            chest.setInventorySlotContents(9, new ItemStack(ModItems.vampireFang, 2 + rand.nextInt(3)));
        }
        if (ModItems.heartSeeker != null && rand.nextInt(4) == 0) {
            chest.setInventorySlotContents(13, new ItemStack(ModItems.heartSeeker, 1));
        } else if (ModItems.vampireCloak != null && rand.nextInt(3) == 0) {
            chest.setInventorySlotContents(13, new ItemStack(ModItems.vampireCloak, 1));
        }
        chest.setInventorySlotContents(18, new ItemStack(Items.diamond, 1 + rand.nextInt(3)));
        chest.setInventorySlotContents(21, new ItemStack(Items.gold_ingot, 3 + rand.nextInt(5)));
    }

    private static Block blockOr(Block preferred, Block fallback) {
        return preferred != null ? preferred : fallback;
    }
}
