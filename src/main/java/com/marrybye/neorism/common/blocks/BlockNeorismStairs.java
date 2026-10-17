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
        String tex = getTextureName();
        if (tex != null && !tex.startsWith("MISSING_ICON_BLOCK_")) {
            this.blockIcon = register.registerIcon(tex);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (modelBlockRef != null) {
            return modelBlockRef.getIcon(side, modelMetaRef);
        }
        return this.blockIcon != null ? this.blockIcon : super.getIcon(side, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        if (modelBlockRef != null) {
            return modelBlockRef.getIcon(side, modelMetaRef);
        }
        return getIcon(side, world.getBlockMetadata(x, y, z));
    }

    @SideOnly(Side.CLIENT)
    public IIcon getFallbackParticleIcon() {
        return modelBlockRef != null ? modelBlockRef.getIcon(0, modelMetaRef) : this.blockIcon;
    }

    @Override
    public String getItemIconName() {
        if (modelBlockRef != null) {
            String name = modelBlockRef.getItemIconName();
            if (name != null && !name.startsWith("MISSING_ICON_BLOCK_")) {
                return name;
            }
        }
        String own = super.getItemIconName();
        return (own != null && !own.startsWith("MISSING_ICON_BLOCK_")) ? own : null;
    }
}
