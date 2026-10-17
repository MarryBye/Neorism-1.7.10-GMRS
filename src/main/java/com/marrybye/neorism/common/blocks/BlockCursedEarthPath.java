package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockCursedEarthPath extends Block {

    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    private net.minecraft.util.IIcon iconTop;
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    private net.minecraft.util.IIcon iconSide;
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    private net.minecraft.util.IIcon iconBottom;

    public BlockCursedEarthPath() {
        super(Material.ground);
        setBlockName("neorism.cursed_earth_path");
        setBlockTextureName(Neorism.MODID + ":cursed_earth_path_side");
        setCreativeTab(ModCreativeTabs.tabVampirism);
        setHardness(0.6F);
        setStepSound(soundTypeGravel);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
        setLightOpacity(255);
        this.useNeighborBrightness = true;
    }

    @Override
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        this.iconTop = register.registerIcon(Neorism.MODID + ":cursed_earth_path_top");
        this.iconSide = register.registerIcon(Neorism.MODID + ":cursed_earth_path_side");
        this.iconBottom = register.registerIcon(Neorism.MODID + ":cursed_earth");
        this.blockIcon = this.iconSide;
    }

    @Override
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public net.minecraft.util.IIcon getIcon(int side, int meta) {
        if (side == 1) return iconTop != null ? iconTop : blockIcon;
        if (side == 0) return iconBottom != null ? iconBottom : blockIcon;
        return iconSide != null ? iconSide : blockIcon;
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
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 0.9375F, z + 1);
    }
}
