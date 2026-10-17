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
 * Vampire Orchid flower growing in vampire biomes.
 * Canonical port of TeamLapen's VampireOrchidBlock.
 */
public class BlockVampireOrchid extends BlockBush {

    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public BlockVampireOrchid() {
        super();
        setBlockName("neorism.vampire_orchid");
        setStepSound(soundTypeGrass);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    protected boolean canPlaceBlockOn(Block ground) {
        return ground == ModBlocks.cursedGrass || ground == ModBlocks.cursedEarth || ground == Blocks.grass || ground == Blocks.dirt;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icon = register.registerIcon(Neorism.MODID + ":vampire_orchid");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icon;
    }
}
