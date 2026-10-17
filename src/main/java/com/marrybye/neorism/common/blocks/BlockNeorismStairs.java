package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.core.ModCreativeTabs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.creativetab.CreativeTabs;

public class BlockNeorismStairs extends BlockStairs {

    public BlockNeorismStairs(String name, Block modelBlock, int modelMeta, CreativeTabs tab) {
        super(modelBlock, modelMeta);
        setBlockName("neorism." + name);
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
        this.useNeighborBrightness = true;
    }
}
