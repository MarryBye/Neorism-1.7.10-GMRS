package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.core.ModFluids;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.tileentity.TileEntityAltarInspiration;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockAltarInspiration extends BlockModeled implements ITileEntityProvider {

    public BlockAltarInspiration() {
        super("altar_inspiration", "cursed_spruce_log", Material.rock);
        setHardness(3.0F);
        setStepSound(soundTypeStone);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityAltarInspiration();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityAltarInspiration)) return false;

        TileEntityAltarInspiration altar = (TileEntityAltarInspiration) te;
        ItemStack held = player.getHeldItem();

        if (held != null) {
            // Fill from blood bucket
            if (held.getItem() == ModFluids.bloodBucket && altar.getBloodAmount() <= altar.getCapacity() - 1000) {
                altar.fill(null, new net.minecraftforge.fluids.FluidStack(ModFluids.fluidBlood, 1000), true);
                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                    if (!player.inventory.addItemStackToInventory(new ItemStack(Items.bucket))) {
                        player.dropPlayerItemWithRandomChoice(new ItemStack(Items.bucket), false);
                    }
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "liquid.water", 0.8F, 1.0F);
                return true;
            } else if (held.getItem() == ModItems.bloodBottle) {
                // Fill from blood bottle (200 mB)
                int filled = altar.fill(null, new net.minecraftforge.fluids.FluidStack(ModFluids.fluidBlood, 200), true);
                if (filled > 0) {
                    if (!player.capabilities.isCreativeMode) {
                        held.stackSize--;
                        player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
                    }
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "liquid.water", 0.7F, 1.2F);
                    return true;
                }
            }
        }

        // Level-up interaction
        return altar.tryLevelUp(player);
    }
}
