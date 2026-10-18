package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.blocks.BlockTent;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.tileentity.TileEntityTent;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
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
        setTextureName(Neorism.MODID + ":item_tent");
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    public ItemTent(boolean spawner) {
        this(spawner ? "tent_spawner_item" : "tent_item", spawner);
    }

    public boolean isSpawner() {
        return spawner;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        if (spawner) {
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.vampirism.tent.spawner"));
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (side != 1) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }

        int targetY = y + 1;
        int dir = player == null ? 2 : (MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);

        if (BlockTent.canPlaceTent(world, x, targetY, z, dir)) {
            BlockTent.placeTent(world, x, targetY, z, dir);
            TileEntity te = world.getTileEntity(x, targetY, z);
            if (te instanceof TileEntityTent && spawner) {
                ((TileEntityTent) te).setSpawn(true);
            }
            world.playSoundEffect(x + 0.5D, targetY + 0.5D, z + 0.5D, "dig.cloth", 1.0F, 1.0F);
            ModLogger.debug("Placed hunter tent at [%d, %d, %d] with dir=%d, spawner=%b", x, targetY, z, dir, spawner);

            if (player == null || !player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
            return true;
        }

        return false;
    }
}
