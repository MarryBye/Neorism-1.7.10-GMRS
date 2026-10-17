package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

/**
 * Cursed Roots vegetation growing on vampire ground and trees.
 * Canonical port of TeamLapen's CursedRootsBlock.
 */
public class BlockCursedRoots extends BlockBush {

    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public BlockCursedRoots() {
        super();
        setBlockName("neorism.cursed_roots");
        setStepSound(soundTypeGrass);
        setCreativeTab(ModCreativeTabs.tabMain);
        float f = 0.3F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.4F, 0.5F + f);
    }

    @Override
    protected boolean canPlaceBlockOn(Block ground) {
        return ground == ModBlocks.cursedGrass || ground == ModBlocks.cursedEarth || ground == ModBlocks.darkStone || ground == Blocks.grass || ground == Blocks.dirt;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icon = register.registerIcon(Neorism.MODID + ":cursed_roots");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icon;
    }
}
