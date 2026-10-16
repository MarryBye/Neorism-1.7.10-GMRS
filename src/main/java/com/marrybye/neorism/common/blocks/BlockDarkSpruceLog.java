package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockDarkSpruceLog extends BlockRotatedPillar {

    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;

    public BlockDarkSpruceLog() {
        super(Material.wood);
        setBlockName("neorism.dark_spruce_log");
        setHardness(2.0F);
        setStepSound(soundTypeWood);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        sideIcon = register.registerIcon(Neorism.MODID + ":dark_spruce_log");
        topIcon = register.registerIcon(Neorism.MODID + ":dark_spruce_log_top");
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected IIcon getSideIcon(int side) {
        return sideIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected IIcon getTopIcon(int side) {
        return topIcon;
    }
}
