package com.marrybye.neorism.common.tileentity;

import java.util.List;

import com.marrybye.neorism.common.entity.hunter.EntityAdvancedHunter;
import com.marrybye.neorism.common.entity.hunter.EntityHunter;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

/**
 * Spawns hunters for tents at Hunter Camps and Outposts.
 * Canonical port of TeamLapen's TentBlockEntity.
 */
public class TileEntityTent extends TileEntity {

    private boolean spawn = false;
    private boolean advanced = false;
    private int spawnCooldown = 100;

    public TileEntityTent() {
    }

    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    public void setSpawner(boolean spawn) {
        this.spawn = spawn;
    }

    public boolean isSpawner() {
        return spawn;
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    @Override
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote || !spawn) {
            return;
        }

        if (--spawnCooldown <= 0) {
            // Canonical TentBlockEntity: minSpawnDelay=600, maxSpawnDelay=1000
            spawnCooldown = 600 + worldObj.rand.nextInt(400);

            // Check if player is within activation range (64 blocks)
            if (worldObj.getClosestPlayer(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 64.0D) == null) {
                return;
            }

            // Canonical SimpleSpawnerLogic: spawnRange = 6, inflate(5) = 11 block check box
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xCoord - 11, yCoord - 8, zCoord - 11, xCoord + 11, yCoord + 8, zCoord + 11);
            List nearbyHunters = worldObj.selectEntitiesWithinAABB(EntityHunter.class, aabb, IEntitySelector.selectAnything);

            if (nearbyHunters.size() < 2) {
                EntityHunter hunter = new EntityHunter(worldObj);
                hunter.setLocationAndAngles(xCoord + 0.5D + (worldObj.rand.nextDouble() - 0.5D) * 4.0D, yCoord, zCoord + 0.5D + (worldObj.rand.nextDouble() - 0.5D) * 4.0D, worldObj.rand.nextFloat() * 360.0F, 0.0F);
                hunter.onSpawnWithEgg(null);
                hunter.setHomeArea(xCoord, yCoord, zCoord, 10);
                worldObj.spawnEntityInWorld(hunter);
            }

            if (advanced) {
                List nearbyAdvHunters = worldObj.selectEntitiesWithinAABB(EntityAdvancedHunter.class, aabb, IEntitySelector.selectAnything);
                if (nearbyAdvHunters.isEmpty()) {
                    EntityAdvancedHunter advHunter = new EntityAdvancedHunter(worldObj);
                    advHunter.setLocationAndAngles(xCoord + 0.5D + (worldObj.rand.nextDouble() - 0.5D) * 4.0D, yCoord, zCoord + 0.5D + (worldObj.rand.nextDouble() - 0.5D) * 4.0D, worldObj.rand.nextFloat() * 360.0F, 0.0F);
                    advHunter.onSpawnWithEgg(null);
                    advHunter.setHomeArea(xCoord, yCoord, zCoord, 10);
                    worldObj.spawnEntityInWorld(advHunter);
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        spawn = nbt.getBoolean("spawn");
        advanced = nbt.getBoolean("advanced");
        spawnCooldown = nbt.getInteger("spawnCooldown");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("spawn", spawn);
        nbt.setBoolean("advanced", advanced);
        nbt.setInteger("spawnCooldown", spawnCooldown);
    }
}
