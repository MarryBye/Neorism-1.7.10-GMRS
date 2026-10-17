package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockNeorismPressurePlate extends BlockPressurePlate {

    public BlockNeorismPressurePlate(String name, String texture, CreativeTabs tab) {
        super(Neorism.MODID + ":" + texture, Material.wood, Sensitivity.everything);
        setBlockName("neorism." + name);
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
        setHardness(0.5F);
        setStepSound(soundTypeWood);
    }
}
