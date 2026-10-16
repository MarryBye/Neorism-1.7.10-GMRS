package com.marrybye.neorism.common.tileentity;

import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModFluids;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * TileEntity for the Altar of Inspiration.
 * Handles early vampire progression (levels 1-4) by consuming consecrated blood.
 */
public class TileEntityAltarInspiration extends TileEntityNeorism implements IFluidHandler {

    public static final int CAPACITY = 10000;
    private static final int[] BLOOD_REQUIREMENTS = {2000, 4000, 6000}; // for levels 2, 3, 4

    protected final FluidTank tank;

    public TileEntityAltarInspiration() {
        this.tank = new FluidTank(CAPACITY);
    }

    public int getBloodAmount() {
        return tank.getFluidAmount();
    }

    public int getCapacity() {
        return tank.getCapacity();
    }

    public float getFluidRatio() {
        return (float) tank.getFluidAmount() / (float) tank.getCapacity();
    }

    public boolean tryLevelUp(EntityPlayer player) {
        if (worldObj.isRemote || player == null) return false;

        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null || !Helper.isVampire(player)) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.not_a_vampire"));
            return false;
        }

        int curLevel = fph.getCurrentLevel();
        if (curLevel >= 4) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.altar_inspiration_maxed"));
            return false;
        }

        int reqIndex = curLevel - 1; // Level 1 needs BLOOD_REQUIREMENTS[0]
        if (reqIndex < 0 || reqIndex >= BLOOD_REQUIREMENTS.length) return false;

        int reqBlood = BLOOD_REQUIREMENTS[reqIndex];
        if (tank.getFluidAmount() < reqBlood) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.altar_inspiration_need_blood",
                reqBlood - tank.getFluidAmount()));
            return false;
        }

        // Consume blood and perform level-up
        tank.drain(reqBlood, true);
        markForUpdate();

        int nextLevel = curLevel + 1;
        fph.setFaction(ModFactions.VAMPIRE, nextLevel);

        ModSounds.playAtEntity(player, ModSounds.BEAM_ENTER, 1.2F, 1.0F);
        player.addChatMessage(new ChatComponentTranslation("chat.neorism.vampire_leveled_up", nextLevel));
        ModLogger.leveling("Player %s leveled up to Vampire Level %d at Altar of Inspiration (consumed %d mB)",
            player.getCommandSenderName(), nextLevel, reqBlood);

        // Visual FX
        worldObj.playAuxSFX(2002, (int) player.posX, (int) player.posY + 1, (int) player.posZ, 0x990000);
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagCompound tankTag = new NBTTagCompound();
        tank.writeToNBT(tankTag);
        tag.setTag("BloodTank", tankTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("BloodTank")) {
            tank.readFromNBT(tag.getCompoundTag("BloodTank"));
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null || resource.getFluid() != ModFluids.fluidBlood) return 0;
        int filled = tank.fill(resource, doFill);
        if (doFill && filled > 0) {
            markForUpdate();
        }
        return filled;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || resource.getFluid() != ModFluids.fluidBlood) return null;
        FluidStack drained = tank.drain(resource.amount, doDrain);
        if (doDrain && drained != null && drained.amount > 0) {
            markForUpdate();
        }
        return drained;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        FluidStack drained = tank.drain(maxDrain, doDrain);
        if (doDrain && drained != null && drained.amount > 0) {
            markForUpdate();
        }
        return drained;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid == ModFluids.fluidBlood;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return fluid == ModFluids.fluidBlood;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{tank.getInfo()};
    }
}
