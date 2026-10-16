package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Base Block class for Neorism blocks in 1.7.10.
 */
public class BlockNeorism extends Block {

    public BlockNeorism(String name, Material material) {
        this(name, name, material);
    }

    public BlockNeorism(String name, String textureName, Material material) {
        super(material);
        setBlockName("neorism." + name);
        if (textureName != null && !textureName.isEmpty()) {
            setBlockTextureName(Neorism.MODID + ":" + textureName);
        }
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @SideOnly(Side.CLIENT)
    public net.minecraft.util.IIcon getFallbackParticleIcon() {
        return this.blockIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(net.minecraft.world.World world, int x, int y, int z, int meta, net.minecraft.client.particle.EffectRenderer effectRenderer) {
        return com.marrybye.neorism.client.render.BlockParticleHelper.spawnDestroyEffects(this, world, x, y, z, meta, effectRenderer);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(net.minecraft.world.World world, net.minecraft.util.MovingObjectPosition target, net.minecraft.client.particle.EffectRenderer effectRenderer) {
        return com.marrybye.neorism.client.render.BlockParticleHelper.spawnHitEffects(this, world, target, effectRenderer);
    }
}
