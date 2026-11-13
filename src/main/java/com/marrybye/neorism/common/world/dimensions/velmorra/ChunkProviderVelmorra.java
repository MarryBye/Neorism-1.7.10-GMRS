package com.marrybye.neorism.common.world.dimensions.velmorra;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.marrybye.neorism.common.core.ModBiomes;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModFluids;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.world.gen.WorldGenDarkSpruceTree;
import com.marrybye.neorism.common.world.gen.WorldGenDraculaCastle;
import com.marrybye.neorism.common.world.gen.WorldGenGiantRoot;
import com.marrybye.neorism.common.world.gen.WorldGenMotherTree;
import com.marrybye.neorism.common.world.gen.WorldGenVampireAltarRuins;
import com.marrybye.neorism.common.world.gen.WorldGenVampireCrypt;
import com.marrybye.neorism.common.world.gen.WorldGenVampireHut;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Chunk provider for the Velmorra dimension.
 * Generates an endless cursed forest of dark hills, blood lakes, giant stone roots,
 * eerie gothic ruins, and the central plateau crowned by Lord Dracula's Grand Castle.
 */
public class ChunkProviderVelmorra implements IChunkProvider {

    private static final int CITADEL_Y = 65;
    private static final int BLOOD_SEA_LEVEL = 58;

    private final World worldObj;
    private final Random rand;
    private final long seed;
    private boolean castleGenerated = false;

    public ChunkProviderVelmorra(World world, long seed) {
        this.worldObj = world;
        this.seed = seed;
        this.rand = new Random(seed);
    }

    @Override
    public boolean chunkExists(int chunkX, int chunkZ) {
        return true;
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        Block[] blocks = new Block[65536];
        byte[] meta = new byte[65536];

        Block cursedGrass = blockOr(ModBlocks.cursedGrass, Blocks.grass);
        Block cursedEarth = blockOr(ModBlocks.cursedEarth, Blocks.dirt);
        Block darkStone = blockOr(ModBlocks.darkStone, Blocks.stone);
        Block cobbledDark = blockOr(ModBlocks.cobbledDarkStone, Blocks.cobblestone);
        Block bloodFluid = ModFluids.blockBlood != null ? ModFluids.blockBlood : Blocks.water;

        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int wx = baseX + x;
                int wz = baseZ + z;
                int surfaceY = computeSurfaceHeight(wx, wz);
                int colOffset = (x * 16 + z) * 256;

                blocks[colOffset] = Blocks.bedrock;

                int maxY = Math.max(surfaceY, BLOOD_SEA_LEVEL);
                for (int y = 1; y <= maxY; y++) {
                    if (y <= surfaceY) {
                        if (y == surfaceY) {
                            blocks[colOffset + y] = (surfaceY <= BLOOD_SEA_LEVEL) ? cursedEarth : cursedGrass;
                        } else if (y >= surfaceY - 3) {
                            blocks[colOffset + y] = cursedEarth;
                        } else {
                            long h = hash2D(wx * 31L + y, wz * 31L - y);
                            int roll = (int) (h & 31L);
                            if (y <= 14 && roll == 0) {
                                blocks[colOffset + y] = Blocks.obsidian;
                            } else if (roll == 1 && y < 48) {
                                blocks[colOffset + y] = Blocks.redstone_ore;
                            } else if (roll == 2 && y < 56) {
                                blocks[colOffset + y] = Blocks.iron_ore;
                            } else if (roll <= 4) {
                                blocks[colOffset + y] = cobbledDark;
                            } else {
                                blocks[colOffset + y] = darkStone;
                            }
                        }
                    } else {
                        blocks[colOffset + y] = bloodFluid;
                    }
                }
            }
        }

        Chunk chunk = new Chunk(worldObj, blocks, meta, chunkX, chunkZ);
        if (ModBiomes.velmorra != null) {
            byte[] biomeArray = chunk.getBiomeArray();
            Arrays.fill(biomeArray, (byte) ModBiomes.velmorra.biomeID);
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    private int computeSurfaceHeight(int wx, int wz) {
        double dist = Math.sqrt((double) wx * wx + (double) wz * wz);
        double n1 = fbm(wx * 0.015D, wz * 0.015D);
        double n2 = fbm(wx * 0.045D + 123.4D, wz * 0.045D - 87.2D);
        double terrainH = 65.0D + n1 * 13.0D + n2 * 4.5D;

        if (dist <= 44.0D) {
            return CITADEL_Y - 1;
        }
        if (dist < 76.0D) {
            double t = (dist - 44.0D) / 32.0D;
            t = t * t * (3.0D - 2.0D * t);
            return (int) Math.round((CITADEL_Y - 1) * (1.0D - t) + terrainH * t);
        }
        return (int) Math.max(50.0D, Math.min(92.0D, Math.round(terrainH)));
    }

    private double fbm(double x, double z) {
        double sum = 0.0D;
        double amp = 0.5D;
        double freq = 1.0D;
        for (int i = 0; i < 3; i++) {
            sum += valueNoise(x * freq, z * freq) * amp;
            freq *= 2.0D;
            amp *= 0.5D;
        }
        return sum;
    }

    private double valueNoise(double x, double z) {
        int ix = (int) Math.floor(x);
        int iz = (int) Math.floor(z);
        double fx = x - ix;
        double fz = z - iz;
        double u = fx * fx * (3.0D - 2.0D * fx);
        double v = fz * fz * (3.0D - 2.0D * fz);

        double a = randUnit(ix, iz);
        double b = randUnit(ix + 1, iz);
        double c = randUnit(ix, iz + 1);
        double d = randUnit(ix + 1, iz + 1);
        return (a + (b - a) * u) + ((c + (d - c) * u) - (a + (b - a) * u)) * v;
    }

    private double randUnit(int x, int z) {
        long h = hash2D(x, z);
        return ((h & 0xFFFFL) / 32767.5D) - 1.0D;
    }

    private long hash2D(long x, long z) {
        long h = this.seed ^ (x * 341873128712L) ^ (z * 132897987541L);
        h = (h ^ (h >>> 30)) * 0xBF58476D1CE4E5B9L;
        h = (h ^ (h >>> 27)) * 0x94D049BB133111EBL;
        return h ^ (h >>> 31);
    }

    @Override
    public Chunk loadChunk(int chunkX, int chunkZ) {
        return provideChunk(chunkX, chunkZ);
    }

    @Override
    public void populate(IChunkProvider provider, int chunkX, int chunkZ) {
        long chunkSeed = hash2D(chunkX, chunkZ);
        Random chunkRand = new Random(chunkSeed);

        // Generate Grand Dracula Castle once from origin chunk
        if (chunkX == 0 && chunkZ == 0 && !castleGenerated) {
            castleGenerated = true;
            if (worldObj.getBlock(0, CITADEL_Y + 2, 24) != ModBlocks.throne) {
                new WorldGenDraculaCastle().generate(worldObj, chunkRand, 0, CITADEL_Y, 0);
            }
        }

        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;

        // Avoid cluttering the castle citadel & grand approach avenue
        if (Math.abs(baseX + 8) <= 44 && (baseZ + 8) >= -46 && (baseZ + 8) <= 44) {
            return;
        }

        // 1. Dense Dark Spruce & Cursed Spruce Forest
        WorldGenDarkSpruceTree darkTree = new WorldGenDarkSpruceTree(false, ModBlocks.darkSpruceLog);
        WorldGenDarkSpruceTree cursedTree = new WorldGenDarkSpruceTree(false,
            ModBlocks.cursedSpruceLog != null ? ModBlocks.cursedSpruceLog : ModBlocks.darkSpruceLog);
        int trees = 5 + chunkRand.nextInt(4);
        for (int i = 0; i < trees; i++) {
            int tx = baseX + chunkRand.nextInt(16) + 8;
            int tz = baseZ + chunkRand.nextInt(16) + 8;
            if (isNearCitadel(tx, tz)) continue;
            int ty = worldObj.getTopSolidOrLiquidBlock(tx, tz);
            if (ty > BLOOD_SEA_LEVEL) {
                if (chunkRand.nextInt(3) == 0) {
                    cursedTree.generate(worldObj, chunkRand, tx, ty, tz);
                } else {
                    darkTree.generate(worldObj, chunkRand, tx, ty, tz);
                }
            }
        }

        // 2. Vampire Orchids & Cursed Roots undergrowth
        for (int i = 0; i < 6; i++) {
            int fx = baseX + chunkRand.nextInt(16) + 8;
            int fz = baseZ + chunkRand.nextInt(16) + 8;
            if (isNearCitadel(fx, fz)) continue;
            int fy = worldObj.getTopSolidOrLiquidBlock(fx, fz);
            if (fy > BLOOD_SEA_LEVEL && worldObj.isAirBlock(fx, fy, fz)) {
                Block below = worldObj.getBlock(fx, fy - 1, fz);
                if (below == ModBlocks.cursedGrass || below == ModBlocks.cursedEarth) {
                    Block flora = (chunkRand.nextInt(3) == 0 && ModBlocks.cursedRoots != null)
                        ? ModBlocks.cursedRoots : ModBlocks.vampireOrchid;
                    if (flora != null) {
                        worldObj.setBlock(fx, fy, fz, flora, 0, 2);
                    }
                }
            }
        }

        // 3. Giant Dark Stone Roots (1 in 5 chunks)
        if (chunkRand.nextInt(5) == 0) {
            int rx = baseX + 8;
            int rz = baseZ + 8;
            if (!isNearCitadel(rx, rz)) {
                int ry = worldObj.getTopSolidOrLiquidBlock(rx, rz);
                if (ry > BLOOD_SEA_LEVEL + 1) {
                    new WorldGenGiantRoot().generate(worldObj, chunkRand, rx, ry, rz);
                }
            }
        }

        // 4. Spaced Velmorra Forest Structures (ruined altars, crypts, huts, gothic shrines, rare Mother Trees)
        if (Math.abs(chunkX) >= 4 || Math.abs(chunkZ) >= 4) {
            int gx = positiveMod(chunkX, 5);
            int gz = positiveMod(chunkZ, 5);
            int sx = baseX + 8;
            int sz = baseZ + 8;
            int sy = worldObj.getTopSolidOrLiquidBlock(sx, sz);

            if (gx == 1 && gz == 1 && sy > BLOOD_SEA_LEVEL) {
                new WorldGenVampireAltarRuins().generate(worldObj, chunkRand, sx, sy, sz);
            } else if (gx == 3 && gz == 3 && sy > BLOOD_SEA_LEVEL) {
                new WorldGenVampireHut().generate(worldObj, chunkRand, sx, sy, sz);
            } else if (gx == 2 && gz == 4 && sy > BLOOD_SEA_LEVEL) {
                generateGothicGraveyardRuins(worldObj, chunkRand, sx, sy, sz);
            } else if (gx == 4 && gz == 0) {
                int cryptY = 26 + chunkRand.nextInt(16);
                new WorldGenVampireCrypt().generate(worldObj, chunkRand, sx, cryptY, sz);
            } else if (positiveMod(chunkX, 11) == 6 && positiveMod(chunkZ, 11) == 6 && sy > BLOOD_SEA_LEVEL) {
                new WorldGenMotherTree().generate(worldObj, chunkRand, sx, sy, sz);
            }
        }
    }

    private static int positiveMod(int value, int mod) {
        int r = value % mod;
        return r < 0 ? r + mod : r;
    }

    private void generateGothicGraveyardRuins(World world, Random rand, int x, int y, int z) {
        Block cracked = blockOr(ModBlocks.crackedDarkStoneBricks, Blocks.stonebrick);
        Block chiseled = blockOr(ModBlocks.chiseledDarkStoneBricks, cracked);
        Block wall = blockOr(ModBlocks.darkStoneBricksWall, Blocks.cobblestone_wall);

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if ((Math.abs(dx) == 3 || Math.abs(dz) == 3) && (dx + dz) % 2 == 0) {
                    int h = 1 + rand.nextInt(3);
                    for (int py = 0; py < h; py++) {
                        world.setBlock(x + dx, y + py, z + dz, py == 0 ? chiseled : wall, 0, 2);
                    }
                    if (h == 3 && ModBlocks.vampireSoulLantern != null && rand.nextInt(2) == 0) {
                        world.setBlock(x + dx, y + 3, z + dz, ModBlocks.vampireSoulLantern, 0, 2);
                    }
                }
            }
        }
        if (ModBlocks.tombstoneCross != null) {
            world.setBlock(x - 1, y, z, ModBlocks.tombstoneCross, 0, 2);
        }
        if (ModBlocks.tombstoneMedium != null) {
            world.setBlock(x + 1, y, z - 1, ModBlocks.tombstoneMedium, 0, 2);
        }
        world.setBlock(x, y - 1, z, Blocks.chest, 0, 2);
        TileEntityChest chest = (TileEntityChest) world.getTileEntity(x, y - 1, z);
        if (chest != null) {
            if (ModItems.vampireBloodBottle != null) {
                chest.setInventorySlotContents(1, new ItemStack(ModItems.vampireBloodBottle, 1 + rand.nextInt(2)));
            }
            if (ModItems.pureBlood != null && rand.nextInt(2) == 0) {
                chest.setInventorySlotContents(4, new ItemStack(ModItems.pureBlood, 1, 1 + rand.nextInt(2)));
            }
            chest.setInventorySlotContents(7, new ItemStack(Items.bone, 3 + rand.nextInt(4)));
        }
    }

    private static boolean isNearCitadel(int wx, int wz) {
        return Math.abs(wx) <= 40 && wz >= -44 && wz <= 40;
    }

    private static Block blockOr(Block preferred, Block fallback) {
        return preferred != null ? preferred : fallback;
    }

    public static void placeReturnPortal(World worldObj, int x, int y, int z) {
        Block frameBlock = ModBlocks.chiseledDarkStoneBricks != null ? ModBlocks.chiseledDarkStoneBricks : Blocks.stonebrick;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = 0; dy <= 4; dy++) {
                boolean frame = (dx == -2 || dx == 2 || dy == 0 || dy == 4);
                worldObj.setBlock(x + dx, y + dy, z, frame ? frameBlock : ModBlocks.velmorraPortal, 0, 2);
            }
        }
    }

    @Override
    public boolean saveChunks(boolean all, IProgressUpdate progress) {
        return true;
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String makeString() {
        return "VelmorraLevelSource";
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
        BiomeGenBase biome = worldObj.getBiomeGenForCoords(x, z);
        return biome != null ? biome.getSpawnableList(creatureType) : null;
    }

    @Override
    public ChunkPosition func_147416_a(World world, String name, int x, int y, int z) {
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(int chunkX, int chunkZ) {
    }

    @Override
    public void saveExtraData() {
    }
}
