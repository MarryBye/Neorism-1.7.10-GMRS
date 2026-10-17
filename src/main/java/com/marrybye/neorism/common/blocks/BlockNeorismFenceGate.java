package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class BlockNeorismFenceGate extends BlockFenceGate {

    private final Block modelBlock;

    public BlockNeorismFenceGate(String name, Block modelBlock, CreativeTabs tab) {
        super();
        this.modelBlock = modelBlock;
        setBlockName("neorism." + name);
        if (modelBlock != null && modelBlock.getItemIconName() != null) {
            setBlockTextureName(modelBlock.getItemIconName());
        }
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
        setHardness(2.0F);
        setResistance(5.0F);
        setStepSound(soundTypeWood);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        String tex = getTextureName();
        if (tex == null && modelBlock != null) {
            tex = modelBlock.getItemIconName();
        }
        if (tex != null) {
            this.blockIcon = register.registerIcon(tex);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (this.blockIcon != null) return this.blockIcon;
        return modelBlock != null ? modelBlock.getIcon(side, 0) : super.getIcon(side, meta);
    }
}
