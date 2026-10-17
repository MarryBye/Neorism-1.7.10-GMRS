package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockNeorismDecor extends Block {

    private final boolean solidCollision;

    public BlockNeorismDecor(String name, String texture, Material material, CreativeTabs tab) {
        this(name, texture, material, tab, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, true);
    }

    public BlockNeorismDecor(String name, String texture, Material material, CreativeTabs tab,
                             float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                             boolean solidCollision) {
        super(material);
        this.solidCollision = solidCollision;
        setBlockName("neorism." + name);
        if (texture != null && !texture.isEmpty()) {
            setBlockTextureName(Neorism.MODID + ":" + texture);
        }
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
        setHardness(1.5F);
        setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
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
        if (!solidCollision) {
            return null;
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
}
