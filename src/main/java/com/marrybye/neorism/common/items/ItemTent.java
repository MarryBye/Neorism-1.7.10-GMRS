package com.marrybye.neorism.common.items;

import com.marrybye.neorism.common.blocks.BlockTent;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.tileentity.TileEntityTent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Tent item for placing the 2x2 hunter tent multiblock structure.
 * Ported 1:1 from TeamLapen's TentItem.java.
 */
public class ItemTent extends ItemNeorism {

    private final boolean spawner;

    public ItemTent(String name, boolean spawner) {
        super(name);
        this.spawner = spawner;
        setMaxStackSize(1);
        setTextureName(com.marrybye.neorism.Neorism.MODID + ":item_tent");
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    public ItemTent(boolean spawner) {
        this(spawner ? "tent_spawner_item" : "tent_item", spawner);
    }

    public boolean isSpawner() {
        return spawner;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        if (side != 1) { // Must click top of block
            return false;
        }

        int targetY = y + 1;
        int dir = MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        // Check if all 4 blocks can be placed
        if (BlockTent.canPlaceTent(world, x, targetY, z, dir)) {
            BlockTent.placeTent(world, x, targetY, z, dir);
            TileEntityTent te = (TileEntityTent) world.getTileEntity(x, targetY, z);
            if (te != null && spawner) {
                te.setSpawner(true);
            }
            world.playSoundEffect(x + 0.5D, targetY + 0.5D, z + 0.5D, "dig.cloth", 1.0F, 1.0F);

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
            return true;
        }

        return false;
    }
}
