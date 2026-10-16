package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Base Block class for Neorism blocks in 1.7.10.
 */
public class BlockNeorism extends Block {

    public BlockNeorism(String name, Material material) {
        super(material);
        setBlockName("neorism." + name);
        setBlockTextureName(Neorism.MODID + ":" + name);
        setCreativeTab(ModCreativeTabs.tabMain);
    }
}
