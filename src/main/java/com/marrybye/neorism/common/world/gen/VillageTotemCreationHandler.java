package com.marrybye.neorism.common.world.gen;

import java.util.List;
import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.entity.converted.EntityConvertedVillager;
import com.marrybye.neorism.common.entity.hunter.EntityHunter;
import com.marrybye.neorism.common.entity.hunter.EntityHunterTrainer;
import com.marrybye.neorism.common.entity.hunter.EntityTaskmaster;
import com.marrybye.neorism.common.entity.vampire.EntityVampire;
import com.marrybye.neorism.common.tileentity.TileEntityTotem;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

/**
 * Canonical port of TeamLapen's VanillaStructureModifications (village/totem + totem_faction processor)
 * adapted for 1.7.10 VillagerRegistry and VillageNames compatibility.
 * Generates the Faction Totem plaza in every village (1 per village) with canonical 60% faction probability
 * (30% Vampire, 30% Hunter, 40% Neutral; or 100% Vampire in Vampire Forest), and populates initial faction villagers/guards.
 */
public class VillageTotemCreationHandler implements IVillageCreationHandler {

    public static void init() {
        MapGenStructureIO.func_143031_a(ComponentVillageTotem.class, "Neorism:VillageTotem");
        VillagerRegistry.instance().registerVillageCreationHandler(new VillageTotemCreationHandler());
    }

    @Override
    public PieceWeight getVillagePieceWeight(Random random, int size) {
        // High weight with limit 1 guarantees villages prioritize placing the Faction Totem plaza
        return new PieceWeight(ComponentVillageTotem.class, 50, 1);
    }

    @Override
    public Class<?> getComponentClass() {
        return ComponentVillageTotem.class;
    }

    @Override
    public Object buildComponent(PieceWeight villagePiece, Start startPiece, @SuppressWarnings("rawtypes") List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
        return ComponentVillageTotem.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
    }

    /**
     * Deterministically selects the initial controlling faction for a village at (wx, wz).
     * 100% Vampire in Vampire Forest; otherwise 35% Vampire, 35% Hunter, 30% Neutral.
     */
    public static int chooseVillageFaction(World world, int wx, int wz) {
        BiomeGenBase biome = world.getBiomeGenForCoords(wx, wz);
        if (ModWorldGen.isVampireBiome(biome)) {
            return TileEntityTotem.FACTION_VAMPIRE;
        }
        long seed = world.getSeed() ^ ((long) (wx >> 5) * 341873128712L + (long) (wz >> 5) * 132897987541L);
        Random seededRand = new Random(seed);
        int roll = seededRand.nextInt(100);
        if (roll < 35) {
            return TileEntityTotem.FACTION_VAMPIRE;
        } else if (roll < 70) {
            return TileEntityTotem.FACTION_HUNTER;
        }
        return TileEntityTotem.FACTION_NEUTRAL;
    }

    /**
     * Places a complete 2-block Totem structure at (wx, wy, wz) and initializes the village population.
     */
    public static TileEntityTotem placeTotemAt(World world, int wx, int wy, int wz, Random rand) {
        int factionId = chooseVillageFaction(world, wx, wz);

        world.setBlock(wx, wy - 1, wz, Blocks.cobblestone, 0, 2);
        world.setBlock(wx, wy, wz, ModBlocks.totem, 0, 2);
        world.setBlock(wx, wy + 1, wz, ModBlocks.totemTop, factionId, 2);

        TileEntity te = world.getTileEntity(wx, wy + 1, wz);
        if (te instanceof TileEntityTotem) {
            TileEntityTotem totem = (TileEntityTotem) te;
            totem.setControllingFactionId(factionId);
            totem.initializeGeneratedVillage(rand);
            ModLogger.spawn("Generated Village Totem at (%d, %d, %d) controlled by factionId=%d", wx, wy + 1, wz, factionId);
            return totem;
        }
        return null;
    }

    /**
     * Fallback for mini-villages and custom village structures where ComponentVillageTotem had no road slot.
     * Finds a clear outdoor surface spot near (centerX, centerZ) and places a Faction Totem pedestal.
     */
    public static TileEntityTotem ensureVillageTotemNear(World world, int centerX, int centerY, int centerZ) {
        if (world == null || world.isRemote) return null;
        TileEntityTotem existing = com.marrybye.neorism.common.events.VillageFactionHandler.getNearbyTotem(world, centerX, centerY, centerZ, 64.0D);
        if (existing != null) {
            return existing;
        }

        int bestX = centerX;
        int bestZ = centerZ;
        int bestY = -1;

        int[][] offsets = {
            {0, 0}, {3, 0}, {-3, 0}, {0, 3}, {0, -3},
            {4, 4}, {-4, 4}, {4, -4}, {-4, -4},
            {6, 0}, {-6, 0}, {0, 6}, {0, -6}
        };

        for (int[] off : offsets) {
            int tx = centerX + off[0];
            int tz = centerZ + off[1];
            if (!world.getChunkProvider().chunkExists(tx >> 4, tz >> 4)) continue;

            int ty = world.getTopSolidOrLiquidBlock(tx, tz);
            if (ty < 50 || ty > 240) continue;

            Block ground = world.getBlock(tx, ty - 1, tz);
            Block at = world.getBlock(tx, ty, tz);
            Block above = world.getBlock(tx, ty + 1, tz);

            if (ground != null && ground.getMaterial().isSolid()
                && !ground.getMaterial().isLiquid()
                && ground != Blocks.planks
                && ground != Blocks.cobblestone_wall
                && !ground.isLeaves(world, tx, ty - 1, tz)
                && !ground.isWood(world, tx, ty - 1, tz)
                && (at.isAir(world, tx, ty, tz) || at.isReplaceable(world, tx, ty, tz))
                && (above.isAir(world, tx, ty + 1, tz) || above.isReplaceable(world, tx, ty + 1, tz))
                && world.canBlockSeeTheSky(tx, ty + 1, tz)) {
                bestX = tx;
                bestY = ty;
                bestZ = tz;
                break;
            }
        }

        if (bestY < 0) {
            bestY = Math.max(63, world.getTopSolidOrLiquidBlock(bestX, bestZ));
        }

        // Build a small 3x3 cobblestone/planks base under the fallback totem so it looks natural
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int px = bestX + dx;
                int pz = bestZ + dz;
                if (world.getChunkProvider().chunkExists(px >> 4, pz >> 4)) {
                    Block g = world.getBlock(px, bestY - 1, pz);
                    if (g.isAir(world, px, bestY - 1, pz) || g.isReplaceable(world, px, bestY - 1, pz) || g == Blocks.grass || g == Blocks.dirt || g == Blocks.sand || g == Blocks.gravel) {
                        world.setBlock(px, bestY - 1, pz, (dx == 0 && dz == 0) ? Blocks.planks : Blocks.cobblestone, 0, 2);
                    }
                }
            }
        }

        return placeTotemAt(world, bestX, bestY, bestZ, world.rand);
    }

    public static class ComponentVillageTotem extends StructureVillagePieces.Village {

        private int averageGroundLevel = -1;
        private boolean hasPlacedTotem = false;

        public ComponentVillageTotem() {
        }

        public ComponentVillageTotem(Start startPiece, int type, Random rand, StructureBoundingBox box, int coordBaseMode) {
            super(startPiece, type);
            this.coordBaseMode = coordBaseMode;
            this.boundingBox = box;
        }

        public static ComponentVillageTotem buildComponent(Start startPiece, @SuppressWarnings("rawtypes") List pieces, Random rand, int x, int y, int z, int coordBaseMode, int type) {
            StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 5, 6, 5, coordBaseMode);
            if (!canVillageGoDeeper(box) || StructureComponent.findIntersecting(pieces, box) != null) {
                return null;
            }
            return new ComponentVillageTotem(startPiece, type, rand, box, coordBaseMode);
        }

        @Override
        protected void func_143012_a(net.minecraft.nbt.NBTTagCompound tag) {
            super.func_143012_a(tag);
            tag.setBoolean("HasPlacedTotem", this.hasPlacedTotem);
        }

        @Override
        protected void func_143011_b(net.minecraft.nbt.NBTTagCompound tag) {
            super.func_143011_b(tag);
            this.hasPlacedTotem = tag.getBoolean("HasPlacedTotem");
        }

        @Override
        public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
            if (this.averageGroundLevel < 0) {
                this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);
                if (this.averageGroundLevel < 0) {
                    return true;
                }
                this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 6 - 1, 0);
            }

            // Clear air above 5x5 plaza
            this.fillWithBlocks(world, sbb, 0, 1, 0, 4, 5, 4, Blocks.air, Blocks.air, false);

            // Base foundation (biome-adapted via func_151558_b for VillageNames compatibility)
            Block cobble = this.func_151558_b(Blocks.cobblestone, 0);
            Block planks = this.func_151558_b(Blocks.planks, 0);
            Block fence = this.func_151558_b(Blocks.fence, 0);

            for (int x = 0; x <= 4; x++) {
                for (int z = 0; z <= 4; z++) {
                    this.func_151554_b(world, cobble, 0, x, -1, z, sbb);
                    if (x >= 1 && x <= 3 && z >= 1 && z <= 3) {
                        this.placeBlockAtCurrentPosition(world, planks, 0, x, 0, z, sbb);
                    } else {
                        this.placeBlockAtCurrentPosition(world, cobble, 0, x, 0, z, sbb);
                    }
                }
            }

            // Corner pillars with torches at (0,0), (0,4), (4,0), (4,4)
            int[][] corners = { {0, 0}, {0, 4}, {4, 0}, {4, 4} };
            for (int[] c : corners) {
                this.placeBlockAtCurrentPosition(world, cobble, 0, c[0], 1, c[1], sbb);
                this.placeBlockAtCurrentPosition(world, fence, 0, c[0], 2, c[1], sbb);
                this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, c[0], 3, c[1], sbb);
            }

            // Place the Totem multiblock at center (2, 1, 2) and (2, 2, 2)
            int wx = this.getXWithOffset(2, 2);
            int wy = this.getYWithOffset(1);
            int wz = this.getZWithOffset(2, 2);

            if (sbb.isVecInside(wx, wy, wz) && !this.hasPlacedTotem) {
                this.hasPlacedTotem = true;
                placeTotemAt(world, wx, wy, wz, rand);
            }

            return true;
        }
    }
}
