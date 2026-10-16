package com.marrybye.neorism.common.blocks;

import java.util.Random;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * Cursed Earth corrupting surrounding soil and shielding vampires from below.
 */
public class BlockCursedEarth extends BlockNeorism {

    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;

    public BlockCursedEarth() {
        super("cursed_earth", Material.ground);
        setHardness(0.6F);
        setStepSound(soundTypeGrass);
        setTickRandomly(true);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        if (!world.isRemote) {
            // Spread cursed earth to nearby dirt or grass
            for (int i = 0; i < 3; i++) {
                int targetX = x + rand.nextInt(3) - 1;
                int targetY = y + rand.nextInt(3) - 1;
                int targetZ = z + rand.nextInt(3) - 1;
                Block targetBlock = world.getBlock(targetX, targetY, targetZ);

                if ((targetBlock == Blocks.dirt || targetBlock == Blocks.grass) && world.isAirBlock(targetX, targetY + 1, targetZ)) {
                    world.setBlock(targetX, targetY, targetZ, this, 0, 3);
                    break;
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(10) == 0 && world.isAirBlock(x, y + 1, z)) {
            world.spawnParticle("portal", x + rand.nextFloat(), y + 1.05F, z + rand.nextFloat(), 0.0D, 0.02D, 0.0D);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        iconTop = register.registerIcon(Neorism.MODID + ":cursed_earth");
        iconSide = iconTop;
        iconBottom = register.registerIcon("minecraft:dirt");
        blockIcon = iconTop;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 1) return iconTop;
        if (side == 0) return iconBottom;
        return iconSide != null ? iconSide : iconTop;
    }
}
