package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockCursedEarthPath extends Block {

    public BlockCursedEarthPath() {
        super(Material.ground);
        setBlockName("neorism.cursed_earth_path");
        setBlockTextureName(Neorism.MODID + ":cursed_earth_path");
        setCreativeTab(ModCreativeTabs.tabVampirism);
        setHardness(0.6F);
        setStepSound(soundTypeGravel);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
        setLightOpacity(255);
        this.useNeighborBrightness = true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 0.9375F, z + 1);
    }
}
