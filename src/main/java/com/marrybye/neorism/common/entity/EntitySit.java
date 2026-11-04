package com.marrybye.neorism.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Invisible mount entity used for sitting on chairs and thrones.
 * Ported from TeamLapen's SitEntity for Minecraft 1.7.10.
 */
public class EntitySit extends Entity {

    private int blockX;
    private int blockY;
    private int blockZ;

    public EntitySit(World world) {
        super(world);
        this.setSize(0.0F, 0.0F);
        this.noClip = true;
    }

    public EntitySit(World world, double x, double y, double z, float yaw) {
        this(world);
        this.setPosition(x, y, z);
        this.rotationYaw = yaw;
        this.prevRotationYaw = yaw;
        this.blockX = (int) Math.floor(x);
        this.blockY = (int) Math.floor(y);
        this.blockZ = (int) Math.floor(z);
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!this.worldObj.isRemote) {
            // Despawn if rider leaves or throne is destroyed
            if (this.riddenByEntity == null || !(this.riddenByEntity instanceof EntityPlayer)) {
                this.setDead();
            } else if (this.worldObj.isAirBlock(this.blockX, this.blockY, this.blockZ)) {
                this.setDead();
            }
        }
    }

    @Override
    public double getMountedYOffset() {
        return 0.0D;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        this.blockX = tag.getInteger("BlockX");
        this.blockY = tag.getInteger("BlockY");
        this.blockZ = tag.getInteger("BlockZ");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("BlockX", this.blockX);
        tag.setInteger("BlockY", this.blockY);
        tag.setInteger("BlockZ", this.blockZ);
    }
}
