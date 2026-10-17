package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButtonWood;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockNeorismButton extends BlockButtonWood {

    private final String textureName;

    public BlockNeorismButton(String name, Block modelBlock, String textureName, CreativeTabs tab) {
        super();
        this.textureName = textureName;
        setBlockName("neorism." + name);
        if (textureName != null && !textureName.isEmpty()) {
            setBlockTextureName(Neorism.MODID + ":" + textureName);
        }
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
        setHardness(0.5F);
        setStepSound(soundTypeWood);
    }

    public BlockNeorismButton(String name, Block modelBlock, CreativeTabs tab) {
        this(name, modelBlock, name, tab);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        if (textureName != null && !textureName.isEmpty()) {
            this.blockIcon = register.registerIcon(Neorism.MODID + ":" + textureName);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return this.blockIcon != null ? this.blockIcon : super.getIcon(side, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return this.blockIcon != null ? this.blockIcon : super.getIcon(world, x, y, z, side);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getFallbackParticleIcon() {
        return this.blockIcon;
    }

    @Override
    public String getItemIconName() {
        return null;
    }
}
