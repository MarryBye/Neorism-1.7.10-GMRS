package com.marrybye.neorism.common.world.dimensions.velmorra;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * Custom dimension teleporter for Velmorra.
 * Safely places player at target portal coordinates without creating unwanted obsidian frames.
 */
public class TeleporterVelmorra extends Teleporter {

    private final WorldServer worldServer;
    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public TeleporterVelmorra(WorldServer world, double x, double y, double z) {
        super(world);
        this.worldServer = world;
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float yaw) {
        entity.setLocationAndAngles(this.targetX, this.targetY, this.targetZ, entity.rotationYaw, 0.0F);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float yaw) {
        placeInPortal(entity, x, y, z, yaw);
        return true;
    }

    @Override
    public boolean makePortal(Entity entity) {
        return true;
    }

    @Override
    public void removeStalePortalLocations(long worldTime) {
    }
}
