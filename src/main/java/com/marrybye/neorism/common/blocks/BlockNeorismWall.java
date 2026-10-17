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
        String tex = getTextureName();
        if (tex == null && modelBlock != null) {
            tex = modelBlock.getItemIconName();
        }
        if (tex != null) {
            this.blockIcon = register.registerIcon(tex);
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getFallbackParticleIcon() {
        return this.blockIcon != null ? this.blockIcon : (modelBlock != null ? modelBlock.getIcon(0, 0) : null);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (this.blockIcon != null) return this.blockIcon;
        return modelBlock != null ? modelBlock.getIcon(side, 0) : null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
    }
}
