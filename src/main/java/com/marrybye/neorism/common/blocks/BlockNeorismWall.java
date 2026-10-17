package com.marrybye.neorism.common.blocks;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockNeorismWall extends BlockWall {

    private final String textureName;

    public BlockNeorismWall(String name, Block modelBlock, String textureName, CreativeTabs tab) {
        super(modelBlock);
        this.textureName = textureName;
        setBlockName("neorism." + name);
        if (textureName != null && !textureName.isEmpty()) {
            setBlockTextureName(Neorism.MODID + ":" + textureName);
        }
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
        setHardness(2.0F);
        setResistance(10.0F);
        setStepSound(soundTypeStone);
    }

    public BlockNeorismWall(String name, Block modelBlock, CreativeTabs tab) {
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
    public IIcon getFallbackParticleIcon() {
        return this.blockIcon;
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

    @Override
    public String getItemIconName() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
    }
}
