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
import com.marrybye.neorism.common.items.ItemBloodBottle;
import com.marrybye.neorism.common.items.ItemBloodFlask;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
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
            // 1. Bucket interactions
            if (held.getItem() == Items.bucket && altar.getBloodAmount() >= 1000) {
                altar.drain(ForgeDirection.UNKNOWN, 1000, true);
                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                    if (!player.inventory.addItemStackToInventory(new ItemStack(ModFluids.bloodBucket))) {
                        player.dropPlayerItemWithRandomChoice(new ItemStack(ModFluids.bloodBucket), false);
                    }
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.potion.smash", 0.8F, 1.0F);
                return true;
            } else if (held.getItem() == ModFluids.bloodBucket && altar.getBloodAmount() <= altar.getCapacity() - 1000) {
                altar.fill(ForgeDirection.UNKNOWN, new FluidStack(ModFluids.fluidBlood, 1000), true);
                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                    if (!player.inventory.addItemStackToInventory(new ItemStack(Items.bucket))) {
                        player.dropPlayerItemWithRandomChoice(new ItemStack(Items.bucket), false);
                    }
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "liquid.water", 0.8F, 1.0F);
                return true;
            }

            // 2. Empty vanilla glass bottle -> fill from altar (1 unit = 200 mB)
            if (held.getItem() == Items.glass_bottle && altar.getBloodAmount() >= 200) {
                altar.drain(ForgeDirection.UNKNOWN, 200, true);
                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                    ItemStack bottle = new ItemStack(ModItems.bloodBottle, 1, 1);
                    if (!player.inventory.addItemStackToInventory(bottle)) {
                        player.dropPlayerItemWithRandomChoice(bottle, false);
                    }
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.potion.smash", 0.8F, 1.0F);
                return true;
            }

            // 3. Mod Blood Bottle interaction
            if (held.getItem() == ModItems.bloodBottle) {
                int curDamage = held.getItemDamage();
                if (curDamage > 0 && altar.getBloodAmount() < altar.getCapacity()) {
                    int canAcceptUnits = (altar.getCapacity() - altar.getBloodAmount()) / 100;
                    int transfer = Math.min(curDamage, canAcceptUnits);
                    if (transfer > 0) {
                        altar.fill(ForgeDirection.UNKNOWN, new FluidStack(ModFluids.fluidBlood, transfer * 100), true);
                        held.setItemDamage(curDamage - transfer);
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "liquid.water", 0.8F, 1.0F);
                        return true;
                    }
                } else if (curDamage < ItemBloodBottle.MAX_BLOOD && altar.getBloodAmount() >= 100) {
                    int neededUnits = ItemBloodBottle.MAX_BLOOD - curDamage;
                    int availUnits = altar.getBloodAmount() / 100;
                    int transfer = Math.min(neededUnits, availUnits);
                    if (transfer > 0) {
                        altar.drain(ForgeDirection.UNKNOWN, transfer * 100, true);
                        held.setItemDamage(curDamage + transfer);
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.potion.smash", 0.7F, 1.2F);
                        return true;
                    }
                }
            }

            // 4. Mod Blood Flask interaction
            if (held.getItem() == ModItems.bloodFlask) {
                int curFlaskBlood = ItemBloodFlask.getBlood(held);
                if (curFlaskBlood > 0 && altar.getBloodAmount() < altar.getCapacity()) {
                    int canAcceptUnits = (altar.getCapacity() - altar.getBloodAmount()) / 100;
                    int transfer = Math.min(curFlaskBlood, canAcceptUnits);
                    if (transfer > 0) {
                        altar.fill(ForgeDirection.UNKNOWN, new FluidStack(ModFluids.fluidBlood, transfer * 100), true);
                        ItemBloodFlask.setBlood(held, curFlaskBlood - transfer);
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "liquid.water", 0.8F, 1.0F);
                        return true;
                    }
                } else if (curFlaskBlood < ItemBloodFlask.CAPACITY && altar.getBloodAmount() >= 100) {
                    int spaceUnits = ItemBloodFlask.CAPACITY - curFlaskBlood;
                    int availUnits = altar.getBloodAmount() / 100;
                    int transfer = Math.min(spaceUnits, availUnits);
                    if (transfer > 0) {
                        altar.drain(ForgeDirection.UNKNOWN, transfer * 100, true);
                        ItemBloodFlask.setBlood(held, curFlaskBlood + transfer);
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "liquid.water", 0.8F, 1.2F);
                        return true;
                    }
                }
            }
        }

        // Level-up interaction
        return altar.tryLevelUp(player);
    }
}
