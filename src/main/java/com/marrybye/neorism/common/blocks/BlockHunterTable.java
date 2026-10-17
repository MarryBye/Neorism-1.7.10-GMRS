package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.faction.FactionRestriction;
import com.marrybye.neorism.common.network.ModGuiHandler;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Hunter Table workstation.
 * Canonical port of TeamLapen's HunterTableBlock.
 * Used by hunters to research intelligence documents for level progression.
 */
public class BlockHunterTable extends BlockModeled {

    public BlockHunterTable() {
        super("hunter_table", "hunter_table", Material.wood);
        setHardness(2.5F);
        setResistance(5.0F);
        setStepSound(soundTypeWood);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        if (Helper.isHunter(player)) {
            player.openGui(Neorism.instance, ModGuiHandler.GUI_HUNTER_TABLE, world, x, y, z);
        } else {
            player.addChatMessage(FactionRestriction.getFactionRestrictionMessage(ModFactions.HUNTER));
        }

        return true;
    }
}
