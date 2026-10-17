package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class BlockNeorismLog extends BlockRotatedPillar {

    private final String sideTextureName;
    private final String topTextureName;

    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;

    public BlockNeorismLog(String name, String sideTextureName, String topTextureName, CreativeTabs tab) {
        super(Material.wood);
        this.sideTextureName = sideTextureName;
        this.topTextureName = topTextureName;
        setBlockName("neorism." + name);
        setHardness(2.0F);
        setStepSound(soundTypeWood);
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        sideIcon = register.registerIcon(Neorism.MODID + ":" + sideTextureName);
        topIcon = register.registerIcon(Neorism.MODID + ":" + topTextureName);
        blockIcon = sideIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        int axis = meta & 12;
        if (axis == 0 && (side == 0 || side == 1)) return topIcon;
        if (axis == 4 && (side == 4 || side == 5)) return topIcon;
        if (axis == 8 && (side == 2 || side == 3)) return topIcon;
        return sideIcon;
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
