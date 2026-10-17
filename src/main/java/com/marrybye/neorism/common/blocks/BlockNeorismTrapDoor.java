package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockNeorismTrapDoor extends BlockTrapDoor {

    public BlockNeorismTrapDoor(String name, String texture, Material material, CreativeTabs tab) {
        super(material);
        setBlockName("neorism." + name);
        setBlockTextureName(Neorism.MODID + ":" + texture);
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
        setHardness(3.0F);
        setStepSound(soundTypeWood);
    }
}
