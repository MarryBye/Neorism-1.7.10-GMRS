package com.marrybye.neorism.common.blocks;

import java.util.Random;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.items.ItemHolyWater;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Cursed Grass Block.
 * Canonical port of TeamLapen's CursedGrass.
 * Spreads across cursed earth and dirt in vampire forest.
 */
public class BlockCursedGrass extends BlockNeorism implements IGrowable {

    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;

    public BlockCursedGrass() {
        super("cursed_grass", Material.grass);
        setHardness(0.6F);
        setStepSound(soundTypeGrass);
        setTickRandomly(true);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return ModBlocks.cursedEarth != null ? Item.getItemFromBlock(ModBlocks.cursedEarth) : Item.getItemFromBlock(Blocks.dirt);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        if (!world.isRemote) {
            if (world.getBlockLightValue(x, y + 1, z) < 4 && world.getBlockLightOpacity(x, y + 1, z) > 2) {
                world.setBlock(x, y, z, ModBlocks.cursedEarth != null ? ModBlocks.cursedEarth : Blocks.dirt);
            } else if (world.getBlockLightValue(x, y + 1, z) >= 4) {
                for (int l = 0; l < 4; ++l) {
                    int targetX = x + rand.nextInt(3) - 1;
                    int targetY = y + rand.nextInt(5) - 3;
                    int targetZ = z + rand.nextInt(3) - 1;
                    Block targetBlock = world.getBlock(targetX, targetY, targetZ);

                    if ((targetBlock == ModBlocks.cursedEarth || targetBlock == Blocks.dirt) && world.getBlockLightValue(targetX, targetY + 1, targetZ) >= 4 && world.getBlockLightOpacity(targetX, targetY + 1, targetZ) <= 2) {
                        world.setBlock(targetX, targetY, targetZ, this);
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() instanceof ItemHolyWater) {
            if (!world.isRemote) {
                world.setBlock(x, y, z, Blocks.grass, 0, 3);
                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                    if (held.stackSize <= 0) {
                        player.setCurrentItemOrArmor(0, null);
                    }
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.potion.smash", 0.8F, 1.0F);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable) {
        return true;
    }

    @Override
    public boolean func_149851_a(World world, int x, int y, int z, boolean isRemote) {
        return true;
    }

    @Override
    public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
        return true;
    }

    @Override
    public void func_149853_b(World world, Random rand, int x, int y, int z) {
        int l = 0;

        while (l < 128) {
            int targetX = x;
            int targetY = y + 1;
            int targetZ = z;
            int step = 0;

            while (true) {
                if (step < l / 16) {
                    targetX += rand.nextInt(3) - 1;
                    targetY += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
                    targetZ += rand.nextInt(3) - 1;

                    if (world.getBlock(targetX, targetY - 1, targetZ) == this && !world.getBlock(targetX, targetY, targetZ).isNormalCube()) {
                        ++step;
                        continue;
                    }
                } else if (world.isAirBlock(targetX, targetY, targetZ)) {
                    if (rand.nextInt(8) == 0 && ModBlocks.vampireOrchid != null) {
                        world.setBlock(targetX, targetY, targetZ, ModBlocks.vampireOrchid, 0, 3);
                    } else if (rand.nextInt(4) == 0 && ModBlocks.cursedRoots != null) {
                        world.setBlock(targetX, targetY, targetZ, ModBlocks.cursedRoots, 0, 3);
                    } else {
                        world.setBlock(targetX, targetY, targetZ, Blocks.tallgrass, 1, 3);
                    }
                }

                ++l;
                break;
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        iconTop = register.registerIcon(Neorism.MODID + ":cursed_grass_top");
        iconSide = register.registerIcon(Neorism.MODID + ":cursed_grass_side");
        iconBottom = register.registerIcon(Neorism.MODID + ":cursed_earth");
        blockIcon = iconSide;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 1) return iconTop;
        if (side == 0) return iconBottom;
        return iconSide;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        if (side == 1) return iconTop;
        if (side == 0) return iconBottom;
        return iconSide;
    }
}
