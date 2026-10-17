package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

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
        if (tex != null && !tex.startsWith("MISSING_ICON_BLOCK_")) {
            this.blockIcon = register.registerIcon(tex);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (modelBlock != null) {
            return modelBlock.getIcon(side, 0);
        }
        return this.blockIcon != null ? this.blockIcon : super.getIcon(side, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        if (modelBlock != null) {
            return modelBlock.getIcon(side, 0);
        }
        return getIcon(side, world.getBlockMetadata(x, y, z));
    }

    @SideOnly(Side.CLIENT)
    public IIcon getFallbackParticleIcon() {
        return modelBlock != null ? modelBlock.getIcon(0, 0) : this.blockIcon;
    }

    @Override
    public String getItemIconName() {
        if (modelBlock != null) {
            String name = modelBlock.getItemIconName();
            if (name != null && !name.startsWith("MISSING_ICON_BLOCK_")) {
                return name;
            }
        }
        String own = super.getItemIconName();
        return (own != null && !own.startsWith("MISSING_ICON_BLOCK_")) ? own : null;
    }
}
