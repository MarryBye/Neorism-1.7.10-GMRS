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
    public static final int GUI_HUNTER_TABLE = 1;
    public static final int GUI_ALCHEMICAL_CAULDRON = 2;
    public static final int GUI_MINION = 100;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_ALTAR_INFUSION) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityAltarInfusion) {
                return new ContainerAltarInfusion(player.inventory, (TileEntityAltarInfusion) te);
            }
        } else if (ID == GUI_HUNTER_TABLE) {
            return new com.marrybye.neorism.common.inventory.ContainerHunterTable(player.inventory, world, x, y, z);
        } else if (ID == GUI_ALCHEMICAL_CAULDRON) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof com.marrybye.neorism.common.tileentity.TileEntityAlchemicalCauldron) {
                return new com.marrybye.neorism.common.inventory.ContainerAlchemicalCauldron(player.inventory, (com.marrybye.neorism.common.tileentity.TileEntityAlchemicalCauldron) te);
            }
        } else if (ID == GUI_MINION) {
            net.minecraft.entity.Entity entity = world.getEntityByID(x);
            if (entity instanceof com.marrybye.neorism.common.entity.minion.EntityMinionBase) {
                return new com.marrybye.neorism.common.entity.minion.ContainerMinion(player.inventory, (com.marrybye.neorism.common.entity.minion.EntityMinionBase) entity);
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
        } else if (ID == GUI_HUNTER_TABLE) {
            return new com.marrybye.neorism.client.gui.GuiHunterTable(player.inventory, world, x, y, z);
        } else if (ID == GUI_ALCHEMICAL_CAULDRON) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof com.marrybye.neorism.common.tileentity.TileEntityAlchemicalCauldron) {
                return new com.marrybye.neorism.client.gui.GuiAlchemicalCauldron(player.inventory, (com.marrybye.neorism.common.tileentity.TileEntityAlchemicalCauldron) te);
            }
        } else if (ID == GUI_MINION) {
            net.minecraft.entity.Entity entity = world.getEntityByID(x);
            if (entity instanceof com.marrybye.neorism.common.entity.minion.EntityMinionBase) {
                return new com.marrybye.neorism.client.gui.GuiMinion(player.inventory, (com.marrybye.neorism.common.entity.minion.EntityMinionBase) entity);
            }
        }
        return null;
    }
}
