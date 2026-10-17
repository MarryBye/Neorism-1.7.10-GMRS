package com.marrybye.neorism.common.tileentity;

import java.util.List;

import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.entity.hunter.EntityHunter;
import com.marrybye.neorism.common.entity.vampire.EntityVampire;
import com.marrybye.neorism.common.faction.village.FactionVillage;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.villages.ITotem;
import de.teamlapen.faction.api.factions.villages.IVillageFaction;
import de.teamlapen.faction.api.world.ICaptureAttributes;
import de.teamlapen.faction.api.world.entities.IVillageCaptureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

/**
 * TileEntity for the Faction Totem multiblock crown.
 * Canonical port of TeamLapen's TotemBlockEntity.
 * Manages village territory state machine, siege raids, capture progress, and defense waves.
 */
public class TileEntityTotem extends TileEntity implements ITotem, ICaptureAttributes {

    public static final int FACTION_NEUTRAL = 0;
    public static final int FACTION_VAMPIRE = 1;
    public static final int FACTION_HUNTER = 2;

    private int controllingFactionId = FACTION_NEUTRAL;
    private int attackingFactionId = FACTION_NEUTRAL;
    private boolean isRaidActive = false;
    private int raidProgress = 0; // 0 to 100
    private int raidTimer = 0;
    private int raidDuration = 2400; // 2 minutes max
    private int noAttackerTimer = 0;
    private long timeSinceLastRaid = 0;

    private FactionVillage village;

    public TileEntityTotem() {}

    @Override
    public IVillageFaction getVillage() {
        if (this.village == null && this.worldObj != null) {
            this.village = new FactionVillage(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            this.village.setControllingFaction(getFaction());
        }
        return this.village;
    }

    @Override
    public IPlayableFaction getFaction() {
        return getFactionById(this.controllingFactionId);
    }

    public static IPlayableFaction getFactionById(int id) {
        if (id == FACTION_VAMPIRE) return ModFactions.VAMPIRE;
        if (id == FACTION_HUNTER) return ModFactions.HUNTER;
        return ModFactions.NEUTRAL;
    }

    public static int getIdByFaction(IPlayableFaction faction) {
        if (faction == ModFactions.VAMPIRE) return FACTION_VAMPIRE;
        if (faction == ModFactions.HUNTER) return FACTION_HUNTER;
        return FACTION_NEUTRAL;
    }

    @Override
    public boolean isFactionDefended() {
        return this.controllingFactionId != FACTION_NEUTRAL;
    }

    @Override
    public void onRaidProgress(int percent) {
        this.raidProgress = percent;
    }

    public int getControllingFactionId() {
        return controllingFactionId;
    }

    public void setControllingFactionId(int id) {
        this.controllingFactionId = id;
        if (this.village != null) {
            this.village.setControllingFaction(getFaction());
        }
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, id, 3);
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    public boolean isRaidActive() {
        return isRaidActive;
    }

    public int getRaidProgress() {
        return raidProgress;
    }

    public int getAttackingFactionId() {
        return attackingFactionId;
    }

    public boolean onRightClick(EntityPlayer player) {
        if (this.worldObj.isRemote) {
            return true;
        }

        int playerFactionId = FACTION_NEUTRAL;
        if (Helper.isVampire(player)) playerFactionId = FACTION_VAMPIRE;
        else if (Helper.isHunter(player)) playerFactionId = FACTION_HUNTER;

        if (this.isRaidActive) {
            IPlayableFaction attacking = getFactionById(this.attackingFactionId);
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.totem_raid_in_progress",
                attacking.getName(), this.raidProgress));
            return true;
        }

        if (playerFactionId != FACTION_NEUTRAL && playerFactionId != this.controllingFactionId) {
            // Initiate siege raid!
            this.startRaid(playerFactionId, player);
            return true;
        }

        // Friendly or neutral inspection
        IPlayableFaction current = getFaction();
        player.addChatMessage(new ChatComponentTranslation("chat.neorism.totem_status",
            current.getName(), this.controllingFactionId == FACTION_NEUTRAL ? "Neutral" : "Secured"));

        return true;
    }

    public void startRaid(int attackingFaction, EntityPlayer initiator) {
        this.isRaidActive = true;
        this.attackingFactionId = attackingFaction;
        this.raidProgress = 0;
        this.raidTimer = 0;
        this.noAttackerTimer = 0;

        IPlayableFaction attacking = getFactionById(attackingFaction);
        IPlayableFaction defending = getFaction();

        this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D,
            "random.anvil_land", 1.5F, 0.8F);

        notifyNearby(new ChatComponentTranslation("chat.neorism.totem_raid_started",
            attacking.getName(), defending.getName()), 48.0D);

        ModLogger.debug("Totem at [%d, %d, %d] raid initiated by %s (faction %s)",
            this.xCoord, this.yCoord, this.zCoord, initiator.getCommandSenderName(), attacking.getName());
    }

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }

        if (!this.isRaidActive) {
            this.timeSinceLastRaid++;
            return;
        }

        this.raidTimer++;

        // Process raid battle tick once every 20 ticks (1 second)
        if (this.raidTimer % 20 == 0) {
            processRaidSecond();
        }

        // Periodic defender wave every 200 ticks (10 seconds)
        if (this.raidTimer % 200 == 0) {
            spawnDefenderWave();
        }

        // Timeout check
        if (this.raidTimer >= this.raidDuration) {
            endRaid(false, "Timeout");
        }
    }

    private void processRaidSecond() {
        AxisAlignedBB area = getVillageArea();
        List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, area);

        float attackerStrength = 0;
        float defenderStrength = 0;

        for (EntityLivingBase entity : entities) {
            if (entity.isDead) continue;

            int fId = FACTION_NEUTRAL;
            if (Helper.isVampire(entity)) fId = FACTION_VAMPIRE;
            else if (Helper.isHunter(entity)) fId = FACTION_HUNTER;

            float weight = entity instanceof EntityPlayer ? 3.0F : 1.0F;

            if (fId == this.attackingFactionId) {
                attackerStrength += weight;
                if (entity instanceof IVillageCaptureEntity) {
                    ((IVillageCaptureEntity) entity).attackVillage(this);
                }
            } else if (fId == this.controllingFactionId || (this.controllingFactionId == FACTION_NEUTRAL && entity instanceof EntityIronGolem)) {
                defenderStrength += weight;
                if (entity instanceof IVillageCaptureEntity) {
                    ((IVillageCaptureEntity) entity).defendVillage(this);
                }
            }
        }

        if (attackerStrength == 0) {
            this.noAttackerTimer++;
            if (this.noAttackerTimer >= 15) { // 15 seconds without attackers
                endRaid(false, "No attackers present");
                return;
            }
        } else {
            this.noAttackerTimer = 0;
        }

        if (attackerStrength > defenderStrength) {
            this.raidProgress = Math.min(100, this.raidProgress + 2);
        } else if (defenderStrength > attackerStrength) {
            this.raidProgress = Math.max(0, this.raidProgress - 1);
        }

        // Victory check
        if (this.raidProgress >= 100) {
            endRaid(true, "Capture threshold reached");
        }
    }

    private void spawnDefenderWave() {
        if (this.controllingFactionId == FACTION_HUNTER) {
            EntityHunter hunter = new EntityHunter(this.worldObj);
            hunter.setPosition(this.xCoord + (this.worldObj.rand.nextDouble() - 0.5D) * 10,
                this.yCoord, this.zCoord + (this.worldObj.rand.nextDouble() - 0.5D) * 10);
            this.worldObj.spawnEntityInWorld(hunter);
        } else if (this.controllingFactionId == FACTION_VAMPIRE) {
            EntityVampire vampire = new EntityVampire(this.worldObj);
            vampire.setPosition(this.xCoord + (this.worldObj.rand.nextDouble() - 0.5D) * 10,
                this.yCoord, this.zCoord + (this.worldObj.rand.nextDouble() - 0.5D) * 10);
            this.worldObj.spawnEntityInWorld(vampire);
        }
    }

    private void endRaid(boolean captured, String reason) {
        this.isRaidActive = false;
        IPlayableFaction attacking = getFactionById(this.attackingFactionId);

        if (captured) {
            this.setControllingFactionId(this.attackingFactionId);
            this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D,
                "ambient.weather.thunder", 2.0F, 1.0F);

            notifyNearby(new ChatComponentTranslation("chat.neorism.totem_raid_captured",
                attacking.getName()), 64.0D);

            ModLogger.debug("Totem [%d, %d, %d] successfully captured by %s (%s)",
                this.xCoord, this.yCoord, this.zCoord, attacking.getName(), reason);
        } else {
            this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D,
                "random.successful_hit", 1.5F, 0.9F);

            notifyNearby(new ChatComponentTranslation("chat.neorism.totem_raid_defended"), 48.0D);

            ModLogger.debug("Totem [%d, %d, %d] raid repelled (%s)",
                this.xCoord, this.yCoord, this.zCoord, reason);
        }

        this.raidProgress = 0;
        this.raidTimer = 0;
        this.attackingFactionId = FACTION_NEUTRAL;
        this.timeSinceLastRaid = 0;

        // Disengage nearby village capture entities
        List<IVillageCaptureEntity> captureEntities = this.worldObj.getEntitiesWithinAABB(IVillageCaptureEntity.class, getVillageArea());
        for (IVillageCaptureEntity ce : captureEntities) {
            ce.stopVillageAttackDefense();
        }

        this.markDirty();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    private void notifyNearby(ChatComponentTranslation message, double radius) {
        List<EntityPlayer> players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class,
            AxisAlignedBB.getBoundingBox(
                this.xCoord - radius, this.yCoord - 32, this.zCoord - radius,
                this.xCoord + radius, this.yCoord + 32, this.zCoord + radius
            ));
        for (EntityPlayer p : players) {
            p.addChatMessage(message);
        }
    }

    @Override
    public IPlayableFaction getAttackingFaction() {
        return getFactionById(this.attackingFactionId);
    }

    @Override
    public IPlayableFaction getDefendingFaction() {
        return getFaction();
    }

    @Override
    public int getX() {
        return this.xCoord;
    }

    @Override
    public int getY() {
        return this.yCoord;
    }

    @Override
    public int getZ() {
        return this.zCoord;
    }

    @Override
    public AxisAlignedBB getVillageArea() {
        double r = 32.0D;
        return AxisAlignedBB.getBoundingBox(
            this.xCoord - r, this.yCoord - 16, this.zCoord - r,
            this.xCoord + r, this.yCoord + 24, this.zCoord + r
        );
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.controllingFactionId = tag.getInteger("ControllingFaction");
        this.attackingFactionId = tag.getInteger("AttackingFaction");
        this.isRaidActive = tag.getBoolean("IsRaidActive");
        this.raidProgress = tag.getInteger("RaidProgress");
        this.raidTimer = tag.getInteger("RaidTimer");
        this.timeSinceLastRaid = tag.getLong("TimeSinceLastRaid");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("ControllingFaction", this.controllingFactionId);
        tag.setInteger("AttackingFaction", this.attackingFactionId);
        tag.setBoolean("IsRaidActive", this.isRaidActive);
        tag.setInteger("RaidProgress", this.raidProgress);
        tag.setInteger("RaidTimer", this.raidTimer);
        tag.setLong("TimeSinceLastRaid", this.timeSinceLastRaid);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }
}
