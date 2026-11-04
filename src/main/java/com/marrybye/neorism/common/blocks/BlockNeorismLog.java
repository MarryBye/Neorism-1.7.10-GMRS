package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class BlockNeorismLog extends BlockRotatedPillar {

    private final String sideTextureName;
    private final String topTextureName;

    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;

    public BlockNeorismLog(String name, String sideTextureName, String topTextureName, CreativeTabs tab) {
        super(Material.wood);
        this.sideTextureName = sideTextureName;
        this.topTextureName = topTextureName;
        setBlockName("neorism." + name);
        setHardness(2.0F);
        setStepSound(soundTypeWood);
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        sideIcon = register.registerIcon(Neorism.MODID + ":" + sideTextureName);
        topIcon = register.registerIcon(Neorism.MODID + ":" + topTextureName);
        blockIcon = sideIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        int axis = meta & 12;
        if (axis == 0 && (side == 0 || side == 1)) return topIcon;
        if (axis == 4 && (side == 4 || side == 5)) return topIcon;
        if (axis == 8 && (side == 2 || side == 3)) return topIcon;
        return sideIcon;
    }

    @Override
    public boolean canSustainLeaves(net.minecraft.world.IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean isWood(net.minecraft.world.IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public void breakBlock(net.minecraft.world.World world, int x, int y, int z, net.minecraft.block.Block block, int meta) {
        byte b0 = 4;
        int i1 = b0 + 1;
        if (world.checkChunksExist(x - i1, y - i1, z - i1, x + i1, y + i1, z + i1)) {
            for (int dx = -b0; dx <= b0; ++dx) {
                for (int dy = -b0; dy <= b0; ++dy) {
                    for (int dz = -b0; dz <= b0; ++dz) {
                        net.minecraft.block.Block b = world.getBlock(x + dx, y + dy, z + dz);
                        if (b.isLeaves(world, x + dx, y + dy, z + dz)) {
                            b.beginLeavesDecay(world, x + dx, y + dy, z + dz);
                        }
                    }
                }
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected IIcon getSideIcon(int side) {
        return sideIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected IIcon getTopIcon(int side) {
        return topIcon;
    }
}
