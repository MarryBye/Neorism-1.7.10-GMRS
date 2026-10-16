package com.marrybye.neorism.common.core;

import com.marrybye.neorism.common.blocks.BlockFluidBlood;
import com.marrybye.neorism.common.items.ItemNeorism;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Fluid registry for Neorism blood fluid mechanics.
 */
public class ModFluids {

    public static Fluid fluidBlood;
    public static BlockFluidBlood blockBlood;
    public static Item bloodBucket;

    public static void init() {
        fluidBlood = new Fluid("blood")
            .setDensity(1100)
            .setViscosity(1500)
            .setLuminosity(0);

        if (!FluidRegistry.registerFluid(fluidBlood)) {
            fluidBlood = FluidRegistry.getFluid("blood");
        }

        blockBlood = new BlockFluidBlood(fluidBlood);
        GameRegistry.registerBlock(blockBlood, "blood_fluid");
        fluidBlood.setBlock(blockBlood);

        bloodBucket = new ItemNeorism("blood_bucket").setMaxStackSize(1);
        GameRegistry.registerItem(bloodBucket, "blood_bucket");

        FluidContainerRegistry.registerFluidContainer(
            new FluidStack(fluidBlood, FluidContainerRegistry.BUCKET_VOLUME),
            new ItemStack(bloodBucket),
            new ItemStack(Items.bucket)
        );

        ModLogger.debug("Initialized Neorism blood fluid and bucket container");
    }
}
