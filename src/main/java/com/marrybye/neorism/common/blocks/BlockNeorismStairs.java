package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockNeorismStairs extends BlockStairs {

    private final Block modelBlockRef;
    private final int modelMetaRef;

    public BlockNeorismStairs(String name, Block modelBlock, int modelMeta, CreativeTabs tab) {
        super(modelBlock, modelMeta);
        this.modelBlockRef = modelBlock;
        this.modelMetaRef = modelMeta;
        setBlockName("neorism." + name);
        if (modelBlock != null && modelBlock.getItemIconName() != null) {
            setBlockTextureName(modelBlock.getItemIconName());
        }
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
        this.useNeighborBrightness = true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        // Do NOT register MISSING_ICON_BLOCK_ here.
        // In 1.7.10 vanilla BlockStairs has an empty registerBlockIcons and delegates getIcon to modelBlock.
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return modelBlockRef != null ? modelBlockRef.getIcon(side, modelMetaRef) : super.getIcon(side, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return modelBlockRef != null ? modelBlockRef.getIcon(world, x, y, z, side) : super.getIcon(world, x, y, z, side);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getFallbackParticleIcon() {
        return modelBlockRef != null ? modelBlockRef.getIcon(0, modelMetaRef) : null;
    }

    @Override
    public String getItemIconName() {
        return modelBlockRef != null ? modelBlockRef.getItemIconName() : super.getItemIconName();
    }
}
