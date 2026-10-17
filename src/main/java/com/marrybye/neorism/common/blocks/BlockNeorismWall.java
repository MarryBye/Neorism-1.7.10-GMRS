package com.marrybye.neorism.common.blocks;

import java.util.List;

import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockNeorismWall extends BlockWall {

    private final Block modelBlock;

    public BlockNeorismWall(String name, Block modelBlock, CreativeTabs tab) {
        super(modelBlock);
        this.modelBlock = modelBlock;
        setBlockName("neorism." + name);
        if (modelBlock != null && modelBlock.getItemIconName() != null) {
            setBlockTextureName(modelBlock.getItemIconName());
        }
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
        setHardness(2.0F);
        setResistance(10.0F);
        setStepSound(soundTypeStone);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        // Do NOT register icons here. Like vanilla BlockWall, delegate to modelBlock.
    }

    @SideOnly(Side.CLIENT)
    public IIcon getFallbackParticleIcon() {
        return modelBlock != null ? modelBlock.getIcon(0, 0) : null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return modelBlock != null ? modelBlock.getIcon(side, 0) : super.getIcon(side, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return modelBlock != null ? modelBlock.getIcon(world, x, y, z, side) : getIcon(side, world.getBlockMetadata(x, y, z));
    }

    @Override
    public String getItemIconName() {
        return modelBlock != null ? modelBlock.getItemIconName() : super.getItemIconName();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
    }
}
