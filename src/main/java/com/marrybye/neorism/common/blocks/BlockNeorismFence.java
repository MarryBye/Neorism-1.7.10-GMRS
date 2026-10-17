package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockNeorismFence extends BlockFence {

    public BlockNeorismFence(String name, String texture, Material material, CreativeTabs tab) {
        super(Neorism.MODID + ":" + texture, material);
        setBlockName("neorism." + name);
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
        setHardness(2.0F);
        setResistance(5.0F);
        setStepSound(soundTypeWood);
    }
}
