package com.marrybye.neorism.common.network;

import com.marrybye.neorism.client.gui.GuiAltarInfusion;
import com.marrybye.neorism.common.inventory.ContainerAltarInfusion;
import com.marrybye.neorism.common.tileentity.TileEntityAltarInfusion;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ModGuiHandler implements IGuiHandler {

    public static final int GUI_ALTAR_INFUSION = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_ALTAR_INFUSION) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityAltarInfusion) {
                return new ContainerAltarInfusion(player.inventory, (TileEntityAltarInfusion) te);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_ALTAR_INFUSION) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityAltarInfusion) {
                return new GuiAltarInfusion(player.inventory, (TileEntityAltarInfusion) te);
            }
        }
        return null;
    }
}
