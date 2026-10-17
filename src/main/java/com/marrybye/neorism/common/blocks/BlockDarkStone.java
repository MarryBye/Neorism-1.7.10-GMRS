package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

/**
 * Dark Stone and decorative vampire stone blocks.
 * Canonical port of TeamLapen's DarkStoneBlock.
 */
public class BlockDarkStone extends BlockNeorism {

    private final String textureName;

    public BlockDarkStone(String name) {
        super(name, Material.rock);
        this.textureName = name;
        setHardness(2.0F);
        setResistance(10.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(Neorism.MODID + ":" + textureName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return blockIcon;
    }
}
