package com.marrybye.neorism.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockAltarPillar extends BlockModeled {

    public BlockAltarPillar() {
        super("altar_pillar", Material.rock);
        setHardness(2.0F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 1.0F, 0.875F);
    }

    public static float getPillarValue(int meta) {
        switch (meta & 7) {
            case 1: return 1.0F; // Stone
            case 2: return 1.5F; // Bone / Quartz
            case 3: return 2.0F; // Iron
            case 4: return 3.0F; // Gold
            default: return 0.0F;
        }
    }

    public static ItemStack getFillerItem(int meta) {
        switch (meta & 7) {
            case 1: return new ItemStack(Blocks.stonebrick);
            case 2: return new ItemStack(Blocks.quartz_block);
            case 3: return new ItemStack(Blocks.iron_block);
            case 4: return new ItemStack(Blocks.gold_block);
            default: return null;
        }
    }

    public static boolean isGlued(int meta) {
        return (meta & 8) != 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        int rawMeta = world.getBlockMetadata(x, y, z);
        int fillType = rawMeta & 7;
        boolean glued = (rawMeta & 8) != 0;
        ItemStack held = player.getHeldItem();

        // 1. Glue with slime ball (Items.slime_ball)
        if (held != null && held.getItem() == Items.slime_ball) {
            if (fillType != 0 && !glued) {
                if (!world.isRemote) {
                    world.setBlockMetadataWithNotify(x, y, z, fillType | 8, 3);
                    if (!player.capabilities.isCreativeMode) {
                        held.stackSize--;
                        if (held.stackSize <= 0) {
                            player.setCurrentItemOrArmor(0, null);
                        }
                    }
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "mob.slime.small", 1.0F, 1.0F);
                }
                return true;
            }
            return false;
        }

        // 2. Remove glue with Axe (ItemAxe)
        if (held != null && held.getItem() instanceof ItemAxe && glued) {
            if (!world.isRemote) {
                world.setBlockMetadataWithNotify(x, y, z, fillType, 3);
                held.damageItem(1, player);
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "dig.wood", 1.0F, 1.0F);
            }
            return true;
        }

        // 3. Insert filler block into empty unglued pillar
        if (fillType == 0 && held != null && !glued) {
            int newMeta = 0;
            Block heldBlock = Block.getBlockFromItem(held.getItem());
            if (heldBlock == Blocks.stonebrick) {
                newMeta = 1;
            } else if (heldBlock == Blocks.quartz_block || (heldBlock != null && heldBlock.getUnlocalizedName().contains("bone"))) {
                newMeta = 2;
            } else if (heldBlock == Blocks.iron_block) {
                newMeta = 3;
            } else if (heldBlock == Blocks.gold_block) {
                newMeta = 4;
            }

            if (newMeta > 0) {
                if (!world.isRemote) {
                    world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
                    if (!player.capabilities.isCreativeMode) {
                        held.stackSize--;
                        if (held.stackSize <= 0) {
                            player.setCurrentItemOrArmor(0, null);
                        }
                    }
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "dig.stone", 1.0F, 1.0F);
                }
                return true;
            }
        } else if (fillType > 0 && held == null && !glued) {
            // 4. Remove filler block with empty hand when not glued
            if (!world.isRemote) {
                ItemStack filler = getFillerItem(fillType);
                world.setBlockMetadataWithNotify(x, y, z, 0, 3);
                if (filler != null) {
                    if (!player.inventory.addItemStackToInventory(filler)) {
                        player.dropPlayerItemWithRandomChoice(filler, false);
                    }
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "dig.stone", 1.0F, 0.8F);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        int fillType = world.getBlockMetadata(x, y, z) & 7;
        switch (fillType) {
            case 1: return 1; // Stone
            case 2: return 2; // Bone / Quartz
            case 3: return 4; // Iron
            case 4: return 5; // Gold
            default: return 0;
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote && (meta & 7) > 0) {
            ItemStack filler = getFillerItem(meta & 7);
            if (filler != null) {
                EntityItem entity = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, filler);
                world.spawnEntityInWorld(entity);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }
}
