package com.marrybye.neorism.common.tileentity;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.world.garlic.EnumStrength;
import com.marrybye.neorism.common.world.garlic.GarlicHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;

/**
 * TileEntity for Garlic Diffuser blocks.
 * Canonical port of TeamLapen's GarlicDiffuserBlockEntity & DiffuserBlockEntity:
 * - Purified Garlic fuels for 108000 ticks (5400 seconds / 90 minutes).
 * - Boots up for Config.garlicDiffuserStartupTime * 20 ticks (default 100s = 2000 ticks).
 * - Emits chunk-based garlic aura of configured strength (WEAK or MEDIUM) across (2*radius + 1)^2 chunks.
 */
public class TileEntityGarlicDiffuser extends TileEntity {

    /** Canonical burn_duration from data/vampirism/data_maps/item/garlic_diffuser_fuel.json */
    public static final int PURIFIED_GARLIC_BURN_TICKS = 108000;

    private EnumStrength strength;
    private int chunkRadius;
    private int litTime;
    private int bootTimer;
    private boolean registeredEmitter = false;

    public TileEntityGarlicDiffuser() {
        this(EnumStrength.WEAK, Config.garlicDiffuserNormalDist);
    }

    public TileEntityGarlicDiffuser(EnumStrength strength, int chunkRadius) {
        this.strength = strength != null ? strength : EnumStrength.WEAK;
        this.chunkRadius = Math.max(0, chunkRadius);
        this.litTime = 0;
        this.bootTimer = getMaxBootTicks();
    }

    private static int getMaxBootTicks() {
        return Math.max(20, Config.garlicDiffuserStartupTime * 20);
    }

    public EnumStrength getStrength() {
        return strength;
    }

    public int getChunkRadius() {
        return chunkRadius;
    }

    public int getLitTime() {
        return litTime;
    }

    public int getRemainingSeconds() {
        return litTime / 20;
    }

    public int getBootSecondsRemaining() {
        return Math.max(0, bootTimer / 20);
    }

    public boolean isActive() {
        return litTime > 0 && bootTimer <= 0;
    }

    public void addFuel(int ticks) {
        this.litTime += ticks;
        markForUpdate();
    }

    private void markForUpdate() {
        this.markDirty();
        if (worldObj != null && !worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public void onTouched(EntityPlayer player) {
        if (player == null || worldObj == null || worldObj.isRemote || !isActive()) {
            return;
        }
        float damage = strength == EnumStrength.MEDIUM ? 2.0F : 1.0F;
        player.attackEntityFrom(ModDamageSource.GARLIC, damage);
        player.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 100, strength == EnumStrength.MEDIUM ? 1 : 0));
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote) {
            if (litTime > 0) {
                litTime--;
                if (bootTimer > 0) {
                    bootTimer--;
                }
                if (worldObj.rand.nextInt(4) == 0) {
                    double rx = (worldObj.rand.nextDouble() - 0.5D) * 4.0D;
                    double rz = (worldObj.rand.nextDouble() - 0.5D) * 4.0D;
                    worldObj.spawnParticle("happyVillager", xCoord + 0.5D + rx, yCoord + 0.5D, zCoord + 0.5D + rz, 0, 0.05D, 0);
                }
            }
            return;
        }

        if (litTime > 0) {
            litTime--;
            if (bootTimer > 0) {
                bootTimer--;
                if (bootTimer == 0) {
                    activateEmitter();
                    markForUpdate();
                }
            } else if (!registeredEmitter) {
                activateEmitter();
                markForUpdate();
            }
            if (litTime == 0) {
                if (registeredEmitter) {
                    deactivateEmitter();
                }
                bootTimer = getMaxBootTicks();
                markForUpdate();
            }
        } else {
            if (registeredEmitter) {
                deactivateEmitter();
                markForUpdate();
            }
            int maxBoot = getMaxBootTicks();
            if (bootTimer != maxBoot) {
                bootTimer = maxBoot;
            }
        }
    }

    private void activateEmitter() {
        if (!registeredEmitter && worldObj != null && !worldObj.isRemote) {
            GarlicHandler.instance().registerGarlicBlock(worldObj, xCoord, yCoord, zCoord, strength, chunkRadius);
            registeredEmitter = true;
        }
    }

    private void deactivateEmitter() {
        if (registeredEmitter && worldObj != null && !worldObj.isRemote) {
            GarlicHandler.instance().removeGarlicBlock(worldObj, xCoord, yCoord, zCoord);
            registeredEmitter = false;
        }
    }

    @Override
    public void invalidate() {
        deactivateEmitter();
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        deactivateEmitter();
        super.onChunkUnload();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("garlicStrength")) {
            int s = nbt.getInteger("garlicStrength");
            EnumStrength[] vals = EnumStrength.values();
            this.strength = vals[Math.max(0, Math.min(s, vals.length - 1))];
        }
        if (nbt.hasKey("garlicRadius")) {
            this.chunkRadius = Math.max(0, nbt.getInteger("garlicRadius"));
        }
        this.litTime = nbt.getInteger("litTime");
        this.bootTimer = nbt.hasKey("bootTimer") ? nbt.getInteger("bootTimer") : getMaxBootTicks();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("garlicStrength", this.strength.getStrength());
        nbt.setInteger("garlicRadius", this.chunkRadius);
        nbt.setInteger("litTime", this.litTime);
        nbt.setInteger("bootTimer", this.bootTimer);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }
}
