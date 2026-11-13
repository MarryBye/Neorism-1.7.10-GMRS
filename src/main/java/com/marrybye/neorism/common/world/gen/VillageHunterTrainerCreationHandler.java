package com.marrybye.neorism.common.world.gen;

import java.util.List;
import java.util.Random;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.hunter.EntityHunterTrainer;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

/**
 * Village Hunter Trainer House creation handler.
 * Canonical port of TeamLapen's VanillaStructureModifications (village/houses/hunter_trainer)
 * & loot_table/chests/village/hunter_trainer.json, adapted for 1.7.10 VillagerRegistry and VillageNames.
 */
public class VillageHunterTrainerCreationHandler implements IVillageCreationHandler {

    public static void init() {
        MapGenStructureIO.func_143031_a(ComponentVillageHunterTrainer.class, "Neorism:VillageHunterTrainer");
        VillagerRegistry.instance().registerVillageCreationHandler(new VillageHunterTrainerCreationHandler());
    }

    @Override
    public PieceWeight getVillagePieceWeight(Random random, int size) {
        return new PieceWeight(ComponentVillageHunterTrainer.class, 15, 1);
    }

    @Override
    public Class<?> getComponentClass() {
        return ComponentVillageHunterTrainer.class;
    }

    @Override
    public Object buildComponent(PieceWeight villagePiece, Start startPiece, @SuppressWarnings("rawtypes") List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
        return ComponentVillageHunterTrainer.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
    }

    public static class ComponentVillageHunterTrainer extends StructureVillagePieces.Village {

        private int averageGroundLevel = -1;
        private boolean hasSpawnedTrainer = false;

        public ComponentVillageHunterTrainer() {
        }

        public ComponentVillageHunterTrainer(Start startPiece, int type, Random rand, StructureBoundingBox box, int coordBaseMode) {
            super(startPiece, type);
            this.coordBaseMode = coordBaseMode;
            this.boundingBox = box;
        }

        public static ComponentVillageHunterTrainer buildComponent(Start startPiece, @SuppressWarnings("rawtypes") List pieces, Random rand, int x, int y, int z, int coordBaseMode, int type) {
            StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 7, 6, 7, coordBaseMode);
            if (!canVillageGoDeeper(box) || StructureComponent.findIntersecting(pieces, box) != null) {
                return null;
            }
            return new ComponentVillageHunterTrainer(startPiece, type, rand, box, coordBaseMode);
        }

        @Override
        protected void func_143012_a(NBTTagCompound tag) {
            super.func_143012_a(tag);
            tag.setBoolean("HasSpawnedTrainer", this.hasSpawnedTrainer);
        }

        @Override
        protected void func_143011_b(NBTTagCompound tag) {
            super.func_143011_b(tag);
            this.hasSpawnedTrainer = tag.getBoolean("HasSpawnedTrainer");
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

            Block cobble = this.func_151558_b(Blocks.cobblestone, 0);
            Block planks = this.func_151558_b(Blocks.planks, 0);
            Block log = this.func_151558_b(Blocks.log, 0);
            Block stairs = this.func_151558_b(Blocks.oak_stairs, 0);

            // Clear interior and air above
            this.fillWithBlocks(world, sbb, 0, 1, 0, 6, 5, 6, Blocks.air, Blocks.air, false);

            // Foundation and floor
            for (int x = 0; x <= 6; x++) {
                for (int z = 0; z <= 6; z++) {
                    this.func_151554_b(world, cobble, 0, x, -1, z, sbb);
                    this.placeBlockAtCurrentPosition(world, cobble, 0, x, 0, z, sbb);
                }
            }

            // Walls (1..3 height)
            this.fillWithBlocks(world, sbb, 0, 1, 0, 6, 3, 0, planks, planks, false);
            this.fillWithBlocks(world, sbb, 0, 1, 6, 6, 3, 6, planks, planks, false);
            this.fillWithBlocks(world, sbb, 0, 1, 1, 0, 3, 5, planks, planks, false);
            this.fillWithBlocks(world, sbb, 6, 1, 1, 6, 3, 5, planks, planks, false);

            // Corner logs
            int[][] corners = {{0, 0}, {6, 0}, {0, 6}, {6, 6}};
            for (int[] c : corners) {
                this.fillWithBlocks(world, sbb, c[0], 1, c[1], c[0], 3, c[1], log, log, false);
            }

            // Windows
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 3, 2, 6, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 3, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 6, 2, 3, sbb);

            // Roof
            this.fillWithBlocks(world, sbb, 0, 4, 0, 6, 4, 6, planks, planks, false);
            this.fillWithBlocks(world, sbb, 1, 5, 1, 5, 5, 5, planks, planks, false);

            // Entrance doorway at South (x=3, z=0) + stairs
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 3, 1, 0, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 3, 2, 0, sbb);
            this.placeDoorAtCurrentPosition(world, sbb, rand, 3, 1, 0, this.getMetadataWithOffset(Blocks.wooden_door, 1));
            if (this.getBlockAtCurrentPosition(world, 3, 0, -1, sbb).getMaterial() == net.minecraft.block.material.Material.air
                && this.getBlockAtCurrentPosition(world, 3, -1, -1, sbb).getMaterial() != net.minecraft.block.material.Material.air) {
                this.placeBlockAtCurrentPosition(world, stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 3, 0, -1, sbb);
            }

            // Interior lighting & furnishings: Torch, Bed, Hunter Table, Weapon Table
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 3, 3, 1, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.bed, this.getMetadataWithOffset(Blocks.bed, 0), 1, 1, 4, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.bed, this.getMetadataWithOffset(Blocks.bed, 0) | 8, 1, 1, 5, sbb);

            if (ModBlocks.hunterTable != null) {
                this.placeBlockAtCurrentPosition(world, ModBlocks.hunterTable, 0, 5, 1, 5, sbb);
            }
            if (ModBlocks.hunterWeaponTable != null) {
                this.placeBlockAtCurrentPosition(world, ModBlocks.hunterWeaponTable, 0, 4, 1, 5, sbb);
            }

            // Hunter Trainer Chest with canonical loot_table/chests/village/hunter_trainer.json
            int cx = this.getXWithOffset(5, 3);
            int cy = this.getYWithOffset(1);
            int cz = this.getZWithOffset(5, 3);
            if (sbb.isVecInside(cx, cy, cz) && world.getBlock(cx, cy, cz) != Blocks.chest) {
                world.setBlock(cx, cy, cz, Blocks.chest, 0, 2);
                TileEntity te = world.getTileEntity(cx, cy, cz);
                if (te instanceof TileEntityChest) {
                    populateTrainerChest((TileEntityChest) te, rand);
                }
            }

            // Spawn Hunter Trainer inside
            if (!this.hasSpawnedTrainer) {
                int sx = this.getXWithOffset(3, 3);
                int sy = this.getYWithOffset(1);
                int sz = this.getZWithOffset(3, 3);
                if (sbb.isVecInside(sx, sy, sz)) {
                    this.hasSpawnedTrainer = true;
                    EntityHunterTrainer trainer = new EntityHunterTrainer(world);
                    trainer.setLocationAndAngles(sx + 0.5D, sy, sz + 0.5D, 0.0F, 0.0F);
                    trainer.onSpawnWithEgg(null);
                    trainer.setHomeArea(sx, sy, sz, 10);
                    trainer.func_110163_bv();
                    world.spawnEntityInWorld(trainer);
                    ModLogger.spawn("Generated Village Hunter Trainer House and spawned trainer at (%d, %d, %d)", sx, sy, sz);
                }
            }

            return true;
        }

        private static void populateTrainerChest(TileEntityChest chest, Random rand) {
            // Pool 1: 5..9 rolls — iron_ingot (40), vampire_blood_bottle (20), garlic (40)
            int p1 = 5 + rand.nextInt(5);
            for (int i = 0; i < p1; i++) {
                int slot = rand.nextInt(chest.getSizeInventory());
                int w = rand.nextInt(100);
                if (w < 40) {
                    chest.setInventorySlotContents(slot, new ItemStack(Items.iron_ingot, 1));
                } else if (w < 60 && ModItems.vampireBloodBottle != null) {
                    chest.setInventorySlotContents(slot, new ItemStack(ModItems.vampireBloodBottle, 1));
                } else if (ModItems.garlic != null) {
                    chest.setInventorySlotContents(slot, new ItemStack(ModItems.garlic, 1));
                }
            }

            // Pool 2: 50/145 (~34%) hunter vampire_book
            if (rand.nextInt(145) < 50 && ModItems.vampireBook != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.vampireBook, 1, 0));
            }

            // Pool 3: 10/105 (~9.5%) ultimate hunter_axe (tier 2)
            if (rand.nextInt(105) < 10 && ModItems.hunterAxe != null) {
                chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(ModItems.hunterAxe, 1, 2));
            }

            // Pool 4: 40% random piece of ultimate armor_of_swiftness (tier 2)
            if (rand.nextInt(100) < 40) {
                Item[] swift = {ModItems.swiftnessHead, ModItems.swiftnessChest, ModItems.swiftnessLegs, ModItems.swiftnessFeet};
                Item pick = swift[rand.nextInt(swift.length)];
                if (pick != null) {
                    chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(pick, 1, 2));
                }
            }

            // Pool 5: 40% random piece of ultimate hunter_coat (tier 2)
            if (rand.nextInt(100) < 40) {
                Item[] coat = {ModItems.hunterCoatHead, ModItems.hunterCoatChest, ModItems.hunterCoatLegs, ModItems.hunterCoatFeet};
                Item pick = coat[rand.nextInt(coat.length)];
                if (pick != null) {
                    chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()), new ItemStack(pick, 1, 2));
                }
            }

            // Pool 6: 5 rolls — pure_salt (50), holy_water_bottle normal (20), enhanced (20), ultimate (10)
            for (int i = 0; i < 5; i++) {
                int slot = rand.nextInt(chest.getSizeInventory());
                int w = rand.nextInt(100);
                if (w < 50 && ModItems.pureSalt != null) {
                    chest.setInventorySlotContents(slot, new ItemStack(ModItems.pureSalt, 1));
                } else if (ModItems.holyWaterBottle != null) {
                    int tier = w < 70 ? 0 : (w < 90 ? 1 : 2);
                    chest.setInventorySlotContents(slot, new ItemStack(ModItems.holyWaterBottle, 1, tier));
                }
            }
        }
    }
}
