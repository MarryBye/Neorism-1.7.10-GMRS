package com.marrybye.neorism.common.entity.ai;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.tileentity.TileEntityFogDiffuser;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Random;

/**
 * AI task that causes vampires to run away from direct sunlight into shade, caves, or water.
 */
public class EntityAISunFlee extends EntityAIBase {

    private final EntityCreature theCreature;
    private double shelterX;
    private double shelterY;
    private double shelterZ;
    private final double movementSpeed;
    private final World theWorld;

    public EntityAISunFlee(EntityCreature creature, double speed) {
        this.theCreature = creature;
        this.movementSpeed = speed;
        this.theWorld = creature.worldObj;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.theWorld.isDaytime()) {
            return false;
        }

        if (this.theCreature instanceof IVampireEntity) {
            IVampireEntity v = (IVampireEntity) this.theCreature;
            if (v.isIgnoringSundamage()) {
                return false;
            }
        }

        if (TileEntityFogDiffuser.isEntityProtected(this.theCreature)) {
            return false;
        }

        int x = MathHelper.floor_double(this.theCreature.posX);
        int y = (int) (this.theCreature.posY + 0.5D);
        int z = MathHelper.floor_double(this.theCreature.posZ);

        if (!this.theWorld.canBlockSeeTheSky(x, y, z)) {
            return false;
        }

        Vec3 shelter = this.findShelter();
        if (shelter == null) {
            return false;
        }

        this.shelterX = shelter.xCoord;
        this.shelterY = shelter.yCoord;
        this.shelterZ = shelter.zCoord;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return !this.theCreature.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.theCreature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
        ModLogger.ai("Vampire %s fleeing sunlight towards shelter at (%.1f, %.1f, %.1f)",
            this.theCreature.getCommandSenderName(), this.shelterX, this.shelterY, this.shelterZ);
    }

    private Vec3 findShelter() {
        Random rand = this.theCreature.getRNG();
        int originX = MathHelper.floor_double(this.theCreature.posX);
        int originY = MathHelper.floor_double(this.theCreature.boundingBox.minY);
        int originZ = MathHelper.floor_double(this.theCreature.posZ);

        for (int i = 0; i < 15; ++i) {
            int targetX = originX + rand.nextInt(17) - 8;
            int targetY = originY + rand.nextInt(7) - 3;
            int targetZ = originZ + rand.nextInt(17) - 8;

            if (targetY >= 1 && targetY < 255) {
                if (!this.theWorld.canBlockSeeTheSky(targetX, targetY, targetZ) &&
                    this.theCreature.getBlockPathWeight(targetX, targetY, targetZ) >= 0.0F) {
                    return Vec3.createVectorHelper(targetX + 0.5D, targetY, targetZ + 0.5D);
                }
            }
        }
        return null;
    }
}
