package com.marrybye.neorism.common.tileentity;

import com.marrybye.neorism.common.core.ModFluids;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * TileEntity for the Blood Container storage tank (up to 16,000 mB capacity).
 */
public class TileEntityBloodContainer extends TileEntityNeorism implements IFluidHandler {

    public static final int CAPACITY = 16000;
    protected final FluidTank tank;

    public TileEntityBloodContainer() {
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

    public int addBlood(int amount) {
        if (amount <= 0) return 0;
        int filled = tank.fill(new FluidStack(ModFluids.fluidBlood, amount), true);
        if (filled > 0) {
            markForUpdate();
        }
        return filled;
    }

    public int drainBlood(int amount) {
        if (amount <= 0 || tank.getFluidAmount() <= 0) return 0;
        FluidStack drained = tank.drain(amount, true);
        int amt = drained != null ? drained.amount : 0;
        if (amt > 0) {
            markForUpdate();
        }
        return amt;
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
