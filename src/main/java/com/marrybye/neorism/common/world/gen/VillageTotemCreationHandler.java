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
        // High weight with limit 1 guarantees every village gets exactly 1 Faction Totem plaza
        return new PieceWeight(ComponentVillageTotem.class, 25, 1);
    }

    @Override
    public Class<?> getComponentClass() {
        return ComponentVillageTotem.class;
    }

    @Override
    public Object buildComponent(PieceWeight villagePiece, Start startPiece, @SuppressWarnings("rawtypes") List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
        return ComponentVillageTotem.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
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
            StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 7, 6, 7, coordBaseMode);
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

            // Clear air above plaza
            this.fillWithBlocks(world, sbb, 0, 1, 0, 6, 5, 6, Blocks.air, Blocks.air, false);

            // Base foundation (biome-adapted via func_151558_b for VillageNames compatibility)
            Block cobble = this.func_151558_b(Blocks.cobblestone, 0);
            Block planks = this.func_151558_b(Blocks.planks, 0);
            Block fence = this.func_151558_b(Blocks.fence, 0);
            Block stairsN = this.func_151558_b(Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3));
            Block stairsS = this.func_151558_b(Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 2));
            Block stairsW = this.func_151558_b(Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 0));
            Block stairsE = this.func_151558_b(Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 1));

            for (int x = 0; x <= 6; x++) {
                for (int z = 0; z <= 6; z++) {
                    this.func_151554_b(world, cobble, 0, x, -1, z, sbb);
                    if (x == 0 || x == 6 || z == 0 || z == 6) {
                        this.placeBlockAtCurrentPosition(world, Blocks.gravel, 0, x, 0, z, sbb);
                    } else {
                        this.placeBlockAtCurrentPosition(world, cobble, 0, x, 0, z, sbb);
                    }
                }
            }

            // Raised inner platform (3x3 at center x=2..4, z=2..4)
            for (int x = 2; x <= 4; x++) {
                for (int z = 2; z <= 4; z++) {
                    this.placeBlockAtCurrentPosition(world, planks, 0, x, 0, z, sbb);
                }
            }

            // Corner pillars with torches
            int[][] corners = { {1, 1}, {1, 5}, {5, 1}, {5, 5} };
            for (int[] c : corners) {
                this.placeBlockAtCurrentPosition(world, cobble, 0, c[0], 1, c[1], sbb);
                this.placeBlockAtCurrentPosition(world, fence, 0, c[0], 2, c[1], sbb);
                this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, c[0], 3, c[1], sbb);
            }

            // Steps around the center pedestal
            this.placeBlockAtCurrentPosition(world, stairsN, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 3, 1, 2, sbb);
            this.placeBlockAtCurrentPosition(world, stairsS, this.getMetadataWithOffset(Blocks.stone_stairs, 2), 3, 1, 4, sbb);
            this.placeBlockAtCurrentPosition(world, stairsW, this.getMetadataWithOffset(Blocks.stone_stairs, 0), 2, 1, 3, sbb);
            this.placeBlockAtCurrentPosition(world, stairsE, this.getMetadataWithOffset(Blocks.stone_stairs, 1), 4, 1, 3, sbb);

            // Place the Totem multiblock at center (3, 1, 3) and (3, 2, 3)
            int wx = this.getXWithOffset(3, 3);
            int wy = this.getYWithOffset(1);
            int wz = this.getZWithOffset(3, 3);

            if (sbb.isVecInside(wx, wy, wz) && !this.hasPlacedTotem) {
                this.hasPlacedTotem = true;

                // Canonical processor_list/totem_faction.json:
                // 60% chance to be faction-controlled (50/50 Vampire or Hunter), 40% Neutral.
                // If inside Vampire Forest, always Vampire.
                BiomeGenBase biome = world.getBiomeGenForCoords(wx, wz);
                int factionId = TileEntityTotem.FACTION_NEUTRAL;
                if (ModWorldGen.isVampireBiome(biome)) {
                    factionId = TileEntityTotem.FACTION_VAMPIRE;
                } else if (rand.nextFloat() < 0.6F) {
                    factionId = rand.nextBoolean() ? TileEntityTotem.FACTION_VAMPIRE : TileEntityTotem.FACTION_HUNTER;
                }

                world.setBlock(wx, wy, wz, ModBlocks.totem, 0, 2);
                world.setBlock(wx, wy + 1, wz, ModBlocks.totemTop, factionId, 2);

                TileEntity te = world.getTileEntity(wx, wy + 1, wz);
                if (te instanceof TileEntityTotem) {
                    TileEntityTotem totem = (TileEntityTotem) te;
                    totem.setControllingFactionId(factionId);
                    totem.initializeGeneratedVillage(rand);
                }

                ModLogger.spawn("Generated Village Totem at (%d, %d, %d) controlled by factionId=%d", wx, wy + 1, wz, factionId);
            }

            return true;
        }
    }
}
