package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.common.core.ModFluids;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.tileentity.TileEntityBloodContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class BlockBloodContainer extends BlockModeled implements ITileEntityProvider {

    public BlockBloodContainer() {
        super("blood_container", "blood_container_side", Material.glass);
        setHardness(1.5F);
        setStepSound(soundTypeGlass);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 1.0F, 0.875F);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityBloodContainer();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityBloodContainer)) return false;

        TileEntityBloodContainer container = (TileEntityBloodContainer) te;
        ItemStack held = player.getHeldItem();

        if (held != null) {
            // Bucket interaction
            if (held.getItem() == Items.bucket && container.getBloodAmount() >= 1000) {
                container.drainBlood(1000);
                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                    if (!player.inventory.addItemStackToInventory(new ItemStack(ModFluids.bloodBucket))) {
                        player.dropPlayerItemWithRandomChoice(new ItemStack(ModFluids.bloodBucket), false);
                    }
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.potion.smash", 0.8F, 1.0F);
                return true;
            } else if (held.getItem() == ModFluids.bloodBucket && container.getBloodAmount() <= container.getCapacity() - 1000) {
                container.addBlood(1000);
                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                    if (!player.inventory.addItemStackToInventory(new ItemStack(Items.bucket))) {
                        player.dropPlayerItemWithRandomChoice(new ItemStack(Items.bucket), false);
                    }
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "liquid.water", 0.8F, 1.0F);
                return true;
            } else if (held.getItem() == ModItems.bloodBottle) {
                // Add bottle of blood (200 mB)
                int added = container.addBlood(200);
                if (added > 0) {
                    if (!player.capabilities.isCreativeMode) {
                        held.stackSize--;
                        player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
                    }
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "liquid.water", 0.7F, 1.2F);
                    return true;
                }
            }
        }

        // Status check
        player.addChatMessage(new ChatComponentTranslation("chat.neorism.blood_container_status",
            container.getBloodAmount(), container.getCapacity()));
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }
}
