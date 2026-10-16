package com.marrybye.neorism.common.tileentity;

import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;

/**
 * TileEntity for the Vampire Coffin.
 * Allows vampires to sleep away the burning daytime and wake up at dusk.
 */
public class TileEntityCoffin extends TileEntityNeorism {

    private boolean occupied = false;
    private float lidAngle = 0.0F;
    private float prevLidAngle = 0.0F;

    public boolean isOccupied() {
        return occupied;
    }

    public float getLidAngle(float partialTicks) {
        return prevLidAngle + (lidAngle - prevLidAngle) * partialTicks;
    }

    public boolean trySleep(EntityPlayer player) {
        if (worldObj.isRemote || player == null) return false;

        if (!Helper.isVampire(player)) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.coffin_only_vampires"));
            return false;
        }

        // Vampires can only sleep during day (time between 0 and 12500)
        long time = worldObj.getWorldTime() % 24000;
        if (time > 12500 && time < 23500) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.coffin_only_day"));
            return false;
        }

        // Set spawn chunk
        player.setSpawnChunk(new ChunkCoordinates(xCoord, yCoord, zCoord), true);

        // Advance world time to dusk (13000)
        long currentTotalTime = worldObj.getWorldTime();
        long newTotalTime = (currentTotalTime / 24000L) * 24000L + 13000L;
        if (newTotalTime <= currentTotalTime) {
            newTotalTime += 24000L;
        }
        worldObj.setWorldTime(newTotalTime);

        // Heal vampire
        player.heal(player.getMaxHealth());
        worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D,
            "neorism:cc/block/coffin_lid_slide", 0.9F, 1.0F);

        player.addChatMessage(new ChatComponentTranslation("chat.neorism.coffin_woke_up"));
        ModLogger.combat("Vampire %s slept in coffin at [%d,%d,%d] until dusk",
            player.getCommandSenderName(), xCoord, yCoord, zCoord);

        markForUpdate();
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        prevLidAngle = lidAngle;
        if (occupied && lidAngle < 1.0F) {
            lidAngle = Math.min(1.0F, lidAngle + 0.1F);
        } else if (!occupied && lidAngle > 0.0F) {
            lidAngle = Math.max(0.0F, lidAngle - 0.1F);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("occupied", occupied);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.occupied = tag.getBoolean("occupied");
    }
}
