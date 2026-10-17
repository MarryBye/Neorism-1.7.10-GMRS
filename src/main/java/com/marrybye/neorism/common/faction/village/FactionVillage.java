package com.marrybye.neorism.common.faction.village;

import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.util.ModLogger;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.villages.IVillageFaction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Village territory controlled by a faction or neutral.
 * Canonical port of TeamLapen's FactionVillage.
 */
public class FactionVillage implements IVillageFaction {

    private final World world;
    private int centerX;
    private int centerY;
    private int centerZ;
    private int radius = 48;

    private IPlayableFaction controllingFaction = ModFactions.NEUTRAL;
    private IPlayableFaction attackingFaction = null;
    private int captureProgress = 0;
    private boolean isUnderAttack = false;
    private long timeSinceLastRaid = 0;

    public FactionVillage(World world, int x, int y, int z) {
        this.world = world;
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
    }

    @Override
    public IPlayableFaction getControllingFaction() {
        return this.controllingFaction;
    }

    public void setControllingFaction(IPlayableFaction faction) {
        this.controllingFaction = faction != null ? faction : ModFactions.NEUTRAL;
        ModLogger.debug("FactionVillage at [%d, %d, %d] control flipped to %s",
            this.centerX, this.centerY, this.centerZ, this.controllingFaction.getName());
    }

    @Override
    public int getCaptureProgress() {
        return this.captureProgress;
    }

    public void setCaptureProgress(int progress) {
        this.captureProgress = Math.max(0, Math.min(100, progress));
    }

    @Override
    public boolean isUnderAttack() {
        return this.isUnderAttack;
    }

    public void setUnderAttack(boolean underAttack) {
        this.isUnderAttack = underAttack;
    }

    public IPlayableFaction getAttackingFaction() {
        return this.attackingFaction;
    }

    public void setAttackingFaction(IPlayableFaction faction) {
        this.attackingFaction = faction;
    }

    @Override
    public void triggerRaid(IPlayableFaction attackingFaction) {
        this.attackingFaction = attackingFaction;
        this.isUnderAttack = true;
        this.captureProgress = 0;
        this.timeSinceLastRaid = 0;
        ModLogger.debug("Triggered raid on village [%d, %d, %d] by %s",
            this.centerX, this.centerY, this.centerZ, attackingFaction.getName());
    }

    public void endRaid(boolean captured) {
        if (captured && this.attackingFaction != null) {
            this.setControllingFaction(this.attackingFaction);
        }
        this.isUnderAttack = false;
        this.attackingFaction = null;
        this.captureProgress = 0;
    }

    public AxisAlignedBB getArea() {
        return AxisAlignedBB.getBoundingBox(
            this.centerX - this.radius, this.centerY - 24, this.centerZ - this.radius,
            this.centerX + this.radius, this.centerY + 32, this.centerZ + this.radius
        );
    }

    public boolean isInsideVillage(double x, double y, double z) {
        double dx = x - this.centerX;
        double dz = z - this.centerZ;
        return (dx * dx + dz * dz) <= (this.radius * this.radius) && Math.abs(y - this.centerY) <= 32;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getCenterZ() {
        return centerZ;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public long getTimeSinceLastRaid() {
        return timeSinceLastRaid;
    }

    public void incrementRaidTimer() {
        this.timeSinceLastRaid++;
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("CenterX", this.centerX);
        tag.setInteger("CenterY", this.centerY);
        tag.setInteger("CenterZ", this.centerZ);
        tag.setInteger("Radius", this.radius);
        tag.setString("ControllingFaction", this.controllingFaction != null ? this.controllingFaction.getName() : "neutral");
        tag.setInteger("CaptureProgress", this.captureProgress);
        tag.setBoolean("IsUnderAttack", this.isUnderAttack);
        tag.setLong("TimeSinceLastRaid", this.timeSinceLastRaid);
    }

    public void readFromNBT(NBTTagCompound tag) {
        this.centerX = tag.getInteger("CenterX");
        this.centerY = tag.getInteger("CenterY");
        this.centerZ = tag.getInteger("CenterZ");
        this.radius = tag.getInteger("Radius");
        String factionId = tag.getString("ControllingFaction");
        if ("vampire".equalsIgnoreCase(factionId)) {
            this.controllingFaction = ModFactions.VAMPIRE;
        } else if ("hunter".equalsIgnoreCase(factionId)) {
            this.controllingFaction = ModFactions.HUNTER;
        } else {
            this.controllingFaction = ModFactions.NEUTRAL;
        }
        this.captureProgress = tag.getInteger("CaptureProgress");
        this.isUnderAttack = tag.getBoolean("IsUnderAttack");
        this.timeSinceLastRaid = tag.getLong("TimeSinceLastRaid");
    }
}
