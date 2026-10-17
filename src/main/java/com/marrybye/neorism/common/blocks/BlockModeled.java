package com.marrybye.neorism.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;

/**
 * Base class for blocks that render using GTNHLib JSON models.
 */
public class BlockModeled extends BlockNeorism {

    public BlockModeled(String name, Material material) {
        super(name, material);
    }

    public BlockModeled(String name, String textureName, Material material) {
        super(name, textureName, material);
    }

    public BlockModeled(String name, String textureName, Material material, net.minecraft.creativetab.CreativeTabs tab) {
        super(name, textureName, material, tab);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;
    }
}
