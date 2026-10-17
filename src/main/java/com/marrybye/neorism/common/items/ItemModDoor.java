package com.marrybye.neorism.common.items;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.blocks.BlockModDoor;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemModDoor extends Item {

    private final BlockModDoor doorBlock;

    public ItemModDoor(String name, BlockModDoor doorBlock) {
        super();
        this.doorBlock = doorBlock;
        this.doorBlock.setDoorItem(this);
        setUnlocalizedName("neorism." + name);
        setTextureName(Neorism.MODID + ":" + name);
        setCreativeTab(ModCreativeTabs.tabVampirism);
        setMaxStackSize(64);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (side != 1) return false;
        ++y;

        if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
            if (!this.doorBlock.canPlaceBlockAt(world, x, y, z)) {
                return false;
            }

            int dir = MathHelper.floor_double((double) ((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
            placeDoorBlock(world, x, y, z, dir, this.doorBlock);
            --stack.stackSize;
            return true;
        }
        return false;
    }

    public static void placeDoorBlock(World world, int x, int y, int z, int dir, Block door) {
        byte dx = 0;
        byte dz = 0;

        if (dir == 0) dz = 1;
        if (dir == 1) dx = -1;
        if (dir == 2) dz = -1;
        if (dir == 3) dx = 1;

        int leftX = (world.getBlock(x - dx, y, z - dz).isNormalCube() ? 1 : 0) + (world.getBlock(x - dx, y + 1, z - dz).isNormalCube() ? 1 : 0);
        int rightX = (world.getBlock(x + dx, y, z + dz).isNormalCube() ? 1 : 0) + (world.getBlock(x + dx, y + 1, z + dz).isNormalCube() ? 1 : 0);
        boolean isLeftDoor = world.getBlock(x - dx, y, z - dz) == door || world.getBlock(x - dx, y + 1, z - dz) == door;
        boolean isRightDoor = world.getBlock(x + dx, y, z + dz) == door || world.getBlock(x + dx, y + 1, z + dz) == door;
        boolean isHingeLeft = false;

        if (isLeftDoor && !isRightDoor) {
            isHingeLeft = true;
        } else if (rightX > leftX) {
            isHingeLeft = true;
        }

        world.setBlock(x, y, z, door, dir, 2);
        world.setBlock(x, y + 1, z, door, 8 | (isHingeLeft ? 16 : 0), 2);
        world.notifyBlocksOfNeighborChange(x, y, z, door);
        world.notifyBlocksOfNeighborChange(x, y + 1, z, door);
    }
}
