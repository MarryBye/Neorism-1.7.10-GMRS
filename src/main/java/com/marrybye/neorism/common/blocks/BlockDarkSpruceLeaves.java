package com.marrybye.neorism.common.blocks;

import java.util.Random;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockDarkSpruceLeaves extends BlockLeaves {

    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public BlockDarkSpruceLeaves() {
        super();
        setBlockName("neorism.dark_spruce_leaves");
        setHardness(0.2F);
        setLightOpacity(1);
        setStepSound(soundTypeGrass);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return ModBlocks.darkSpruceSapling != null ? Item.getItemFromBlock(ModBlocks.darkSpruceSapling) : null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icon = register.registerIcon(Neorism.MODID + ":dark_spruce_leaves");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icon;
    }

    @Override
    public String[] func_150125_e() {
        return new String[]{"dark_spruce"};
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }
}
