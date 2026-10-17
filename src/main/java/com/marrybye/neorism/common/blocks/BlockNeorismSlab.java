package com.marrybye.neorism.common.blocks;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockNeorismSlab extends Block {

    private final String textureName;

    public BlockNeorismSlab(String name, Block modelBlock, String textureName, Material mat, CreativeTabs tab) {
        super(mat);
        this.textureName = textureName;
        setBlockName("neorism." + name);
        if (textureName != null && !textureName.isEmpty()) {
            setBlockTextureName(Neorism.MODID + ":" + textureName);
        }
        setCreativeTab(tab != null ? tab : ModCreativeTabs.tabVampirism);
        setHardness(mat == Material.wood ? 2.0F : 1.5F);
        setResistance(mat == Material.wood ? 5.0F : 10.0F);
        if (modelBlock != null && modelBlock.stepSound != null) {
            setStepSound(modelBlock.stepSound);
        }
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        this.useNeighborBrightness = true;
    }

    public BlockNeorismSlab(String name, Block modelBlock, Material mat, CreativeTabs tab) {
        this(name, modelBlock, name, mat, tab);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return (side != 0 && (side == 1 || (double) hitY <= 0.5D)) ? 0 : 8;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & 8) != 0) {
            setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        setBlockBoundsBasedOnState(world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
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
    public void registerBlockIcons(IIconRegister register) {
        if (textureName != null && !textureName.isEmpty()) {
            this.blockIcon = register.registerIcon(Neorism.MODID + ":" + textureName);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return this.blockIcon != null ? this.blockIcon : super.getIcon(side, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return this.blockIcon != null ? this.blockIcon : super.getIcon(world, x, y, z, side);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getFallbackParticleIcon() {
        return this.blockIcon;
    }

    @Override
    public String getItemIconName() {
        return null;
    }
}
