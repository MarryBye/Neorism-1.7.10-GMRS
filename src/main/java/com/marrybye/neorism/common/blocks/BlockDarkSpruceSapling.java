package com.marrybye.neorism.common.blocks;

import java.util.Random;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockDarkSpruceSapling extends BlockBush implements IGrowable {

    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public BlockDarkSpruceSapling() {
        super();
        setBlockName("neorism.dark_spruce_sapling");
        setStepSound(soundTypeGrass);
        setCreativeTab(ModCreativeTabs.tabMain);
        float f = 0.4F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        super.updateTick(world, x, y, z, rand);
        if (world.getBlockLightValue(x, y + 1, z) >= 7 && rand.nextInt(7) == 0) {
            growTree(world, x, y, z, rand);
        }
    }

    public void growTree(World world, int x, int y, int z, Random rand) {
        world.setBlock(x, y, z, Blocks.air, 0, 4);

        int height = 5 + rand.nextInt(3);
        for (int dy = 0; dy < height; dy++) {
            world.setBlock(x, y + dy, z, ModBlocks.darkSpruceLog, 0, 3);
        }

        for (int dy = height - 3; dy <= height; dy++) {
            int radius = dy == height ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius || Math.abs(dz) != radius || rand.nextInt(2) == 0) {
                        if (world.getBlock(x + dx, y + dy, z + dz) == Blocks.air) {
                            world.setBlock(x + dx, y + dy, z + dz, ModBlocks.darkSpruceLeaves, 0, 3);
                        }
                    }
                }
            }
        }
        world.setBlock(x, y + height + 1, z, ModBlocks.darkSpruceLeaves, 0, 3);
    }

    @Override
    public boolean func_149851_a(World world, int x, int y, int z, boolean isRemote) {
        return true;
    }

    @Override
    public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
        return (double) world.rand.nextFloat() < 0.45D;
    }

    @Override
    public void func_149853_b(World world, Random rand, int x, int y, int z) {
        growTree(world, x, y, z, rand);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icon = register.registerIcon(Neorism.MODID + ":dark_spruce_sapling");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icon;
    }
}
