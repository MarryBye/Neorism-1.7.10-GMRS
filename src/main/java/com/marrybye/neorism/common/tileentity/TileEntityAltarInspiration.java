package com.marrybye.neorism.common.tileentity;

import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModFluids;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * Canonical TileEntity for the Altar of Inspiration.
 * Handles early vampire progression (levels 1-4) by consuming consecrated blood.
 * Ported 1:1 from TeamLapen's AltarInspirationBlockEntity.java.
 */
public class TileEntityAltarInspiration extends TileEntityNeorism implements IFluidHandler {

    public static final int CAPACITY = 10000;
    // Canonical requirements from VampireLeveling: Level 2 = 4000 mB, Level 3 = 7000 mB, Level 4 = 10000 mB
    private static final int[] BLOOD_REQUIREMENTS = {4000, 7000, 10000};
    private static final int RITUAL_TIME = 60;
    private static final int LIGHTNING_TICK = 5;
    private static final int LEVELUP_TICK = 1;

    protected final FluidTank tank;
    private int ritualTicksLeft = 0;
    private int targetLevel = 0;
    private String ritualPlayerName = "";

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

    public boolean isRunning() {
        return this.ritualTicksLeft > 0;
    }

    public EntityPlayer getRitualPlayer() {
        if (worldObj == null || ritualPlayerName == null || ritualPlayerName.isEmpty()) return null;
        return worldObj.getPlayerEntityByName(ritualPlayerName);
    }

    public void sendBloodStatus(EntityPlayer player) {
        if (player == null) return;
        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        int curLevel = (fph != null && Helper.isVampire(player)) ? fph.getCurrentLevel() : 0;
        int nextLevel = curLevel + 1;
        int reqIndex = curLevel - 1;
        int reqBlood = (reqIndex >= 0 && reqIndex < BLOOD_REQUIREMENTS.length) ? BLOOD_REQUIREMENTS[reqIndex] : 0;

        if (curLevel >= 4) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.altar_inspiration_status_maxed", tank.getFluidAmount(), CAPACITY));
        } else if (reqBlood > 0) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.altar_inspiration_status", tank.getFluidAmount(), CAPACITY, reqBlood, nextLevel));
        } else {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.altar_inspiration_status_simple", tank.getFluidAmount(), CAPACITY));
        }
    }

    public boolean tryLevelUp(EntityPlayer player) {
        if (worldObj.isRemote || player == null || isRunning()) return false;

        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null || !Helper.isVampire(player)) {
            player.addChatMessage(com.marrybye.neorism.common.faction.FactionRestriction.getFactionRestrictionMessage(ModFactions.VAMPIRE));
            return false;
        }

        int curLevel = fph.getCurrentLevel();
        if (curLevel >= 4) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.altar_inspiration_maxed"));
            return false;
        }

        int reqIndex = curLevel - 1; // Level 1 needs index 0 (Level 2 req)
        if (reqIndex < 0 || reqIndex >= BLOOD_REQUIREMENTS.length) return false;

        int reqBlood = BLOOD_REQUIREMENTS[reqIndex];
        if (tank.getFluidAmount() < reqBlood) {
            int missing = reqBlood - tank.getFluidAmount();
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.altar_inspiration_need_blood", missing));
            sendBloodStatus(player);
            return false;
        }

        // Start 60-tick canonical ritual
        this.targetLevel = curLevel + 1;
        this.ritualPlayerName = player.getCommandSenderName();
        this.ritualTicksLeft = RITUAL_TIME;

        ModSounds.playAtEntity(player, ModSounds.BEAM_ENTER, 1.0F, 0.8F);
        ModLogger.rituals("Altar of Inspiration ritual started for %s -> Level %d", ritualPlayerName, targetLevel);

        markForUpdate();
        markDirty();
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (isRunning()) {
            if (!worldObj.isRemote) {
                EntityPlayer player = getRitualPlayer();
                if (player == null || player.isDead ||
                    player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) > 64.0D) {
                    cancelRitual();
                    return;
                }

                if (this.ritualTicksLeft == LIGHTNING_TICK) {
                    strikeLightning(player);
                }

                if (this.ritualTicksLeft == LEVELUP_TICK) {
                    applyLevelUp(player);
                }
            }

            this.ritualTicksLeft--;

            if (this.ritualTicksLeft <= 0) {
                this.ritualTicksLeft = 0;
                this.ritualPlayerName = "";
                markForUpdate();
                markDirty();
            }
        }
    }

    private void strikeLightning(EntityPlayer player) {
        EntityLightningBolt bolt = new EntityLightningBolt(worldObj, xCoord + 0.5D, yCoord + 1.0D, zCoord + 0.5D);
        worldObj.spawnEntityInWorld(bolt);
        player.setHealth(player.getMaxHealth());
    }

    private void applyLevelUp(EntityPlayer player) {
        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null) return;

        int reqIndex = this.targetLevel - 2;
        if (reqIndex >= 0 && reqIndex < BLOOD_REQUIREMENTS.length) {
            int reqBlood = BLOOD_REQUIREMENTS[reqIndex];
            tank.drain(reqBlood, true);
        }

        // Grant Regeneration effect: targetLevel * 10 * 20 ticks
        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, this.targetLevel * 10 * 20, 1));

        // Advance level
        fph.setFaction(ModFactions.VAMPIRE, this.targetLevel);

        // Refill blood to maximum
        VampirePlayer vp = VampirePlayer.get(player);
        if (vp != null && vp.getBloodStats() != null) {
            vp.getBloodStats().setBloodLevel(vp.getBloodStats().getMaxBlood());
        }

        ModSounds.playAtEntity(player, ModSounds.LEVEL_UP, 1.2F, 1.0F);
        player.addChatMessage(new ChatComponentTranslation("chat.neorism.vampire_leveled_up", this.targetLevel));
        ModLogger.leveling("Player %s leveled up to Vampire Level %d at Altar of Inspiration",
            player.getCommandSenderName(), this.targetLevel);

        // Visual FX
        worldObj.playAuxSFX(2002, (int) player.posX, (int) player.posY + 1, (int) player.posZ, 0x990000);
    }

    private void cancelRitual() {
        this.ritualTicksLeft = 0;
        this.ritualPlayerName = "";
        this.targetLevel = 0;
        markForUpdate();
        markDirty();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagCompound tankTag = new NBTTagCompound();
        tank.writeToNBT(tankTag);
        tag.setTag("BloodTank", tankTag);
        tag.setInteger("ritualTicksLeft", ritualTicksLeft);
        tag.setInteger("targetLevel", targetLevel);
        if (ritualPlayerName != null) {
            tag.setString("ritualPlayerName", ritualPlayerName);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("BloodTank")) {
            tank.readFromNBT(tag.getCompoundTag("BloodTank"));
        }
        this.ritualTicksLeft = tag.getInteger("ritualTicksLeft");
        this.targetLevel = tag.getInteger("targetLevel");
        this.ritualPlayerName = tag.getString("ritualPlayerName");
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
