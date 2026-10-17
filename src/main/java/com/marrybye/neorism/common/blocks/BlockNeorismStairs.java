package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

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
        String tex = getTextureName();
        if (tex == null && modelBlockRef != null) {
            tex = modelBlockRef.getItemIconName();
        }
        if (tex != null) {
            this.blockIcon = register.registerIcon(tex);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (this.blockIcon != null) return this.blockIcon;
        return modelBlockRef != null ? modelBlockRef.getIcon(side, modelMetaRef) : super.getIcon(side, meta);
    }
}
