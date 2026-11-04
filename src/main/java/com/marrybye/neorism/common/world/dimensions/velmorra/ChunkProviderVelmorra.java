package com.marrybye.neorism.common.world.dimensions.velmorra;

import java.util.List;
import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.world.gen.WorldGenDraculaCastle;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Chunk provider for the Velmorra pocket dimension.
 * Generates an ancient gothic platform suspended in the dark veil, home to Dracula's Castle.
 */
public class ChunkProviderVelmorra implements IChunkProvider {

    private final World worldObj;
    private final Random rand;
    private boolean castleGenerated = false;

    public ChunkProviderVelmorra(World world, long seed) {
        this.worldObj = world;
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

        // Generate platform in central region (-4..4 chunks = ~140 blocks across)
        if (chunkX >= -4 && chunkX <= 4 && chunkZ >= -4 && chunkZ <= 4) {
            int cx = chunkX * 16;
            int cz = chunkZ * 16;

            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    int wx = cx + x;
                    int wz = cz + z;
                    double distSq = (double) (wx * wx + wz * wz);
                    double maxDistSq = 64.0D * 64.0D;

                    if (distSq <= maxDistSq) {
                        int yFloor = 60;
                        int yTop = 64;

                        // Taper bottom towards center
                        int bottomThickness = (int) (6.0D * (1.0D - distSq / maxDistSq));
                        int minY = yFloor - bottomThickness;

                        for (int y = minY; y <= yTop; ++y) {
                            int idx = (x * 16 + z) * 256 + y;
                            if (y == yTop) {
                                blocks[idx] = ModBlocks.polishedDarkStone != null ? ModBlocks.polishedDarkStone : Blocks.stonebrick;
                            } else if (y >= yFloor) {
                                blocks[idx] = ModBlocks.darkStoneBricks != null ? ModBlocks.darkStoneBricks : Blocks.stonebrick;
                            } else {
                                blocks[idx] = ModBlocks.darkStone != null ? ModBlocks.darkStone : Blocks.cobblestone;
                            }
                        }
                    }
                }
            }
        }

        Chunk chunk = new Chunk(worldObj, blocks, meta, chunkX, chunkZ);
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public Chunk loadChunk(int chunkX, int chunkZ) {
        return provideChunk(chunkX, chunkZ);
    }

    @Override
    public void populate(IChunkProvider provider, int chunkX, int chunkZ) {
        // Generate Dracula's Castle once in chunk (0, 0)
        if (chunkX == 0 && chunkZ == 0 && !castleGenerated) {
            castleGenerated = true;
            WorldGenDraculaCastle castle = new WorldGenDraculaCastle();
            castle.generate(worldObj, rand, 0, 65, 0);

            // Generate return portal back to Overworld near entrance at (0, 65, -28)
            placeReturnPortal(0, 65, -28);
        }
    }

    private void placeReturnPortal(int x, int y, int z) {
        // Gothic Arch Portal Frame
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = 0; dy <= 4; dy++) {
                boolean frame = (dx == -2 || dx == 2 || dy == 0 || dy == 4);
                int bx = x + dx;
                int by = y + dy;
                int bz = z;
                if (frame) {
                    worldObj.setBlock(bx, by, bz, ModBlocks.chiseledDarkStoneBricks != null ? ModBlocks.chiseledDarkStoneBricks : Blocks.stonebrick, 0, 2);
                } else {
                    worldObj.setBlock(bx, by, bz, ModBlocks.velmorraPortal, 0, 2);
                }
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
        return null;
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
