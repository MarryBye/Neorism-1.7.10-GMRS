package com.marrybye.neorism.common.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.marrybye.neorism.api.entity.IHunterEntity;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.entity.ExtendedCreature;
import com.marrybye.neorism.common.entity.converted.EntityConvertedVillager;
import com.marrybye.neorism.common.entity.hunter.EntityAdvancedHunter;
import com.marrybye.neorism.common.entity.hunter.EntityHunter;
import com.marrybye.neorism.common.entity.hunter.EntityHunterBase;
import com.marrybye.neorism.common.entity.hunter.EntityHunterTrainer;
import com.marrybye.neorism.common.entity.hunter.EntityTaskmaster;
import com.marrybye.neorism.common.entity.vampire.EntityAdvancedVampire;
import com.marrybye.neorism.common.entity.vampire.EntityVampire;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBase;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.faction.village.FactionVillage;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.villages.ITotem;
import de.teamlapen.faction.api.factions.villages.IVillageFaction;
import de.teamlapen.faction.api.world.ICaptureAttributes;
import de.teamlapen.faction.api.world.entities.IVillageCaptureEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;

/**
 * TileEntity for the Faction Totem multiblock crown.
 * Canonical port of TeamLapen's TotemBlockEntity & VillageEventHandler.
 * Manages 2-phase village capture raids, attacker/defender wave spawning, artificial vampire fog,
 * villager conversion/curing, and VillageNames territory naming.
 */
public class TileEntityTotem extends TileEntity implements ITotem, ICaptureAttributes {

    public static final int FACTION_NEUTRAL = 0;
    public static final int FACTION_VAMPIRE = 1;
    public static final int FACTION_HUNTER = 2;

    // Canonical CAPTURE_PHASE enum from TotemBlockEntity
    public static final int PHASE_1_NEUTRAL = 0;
    public static final int PHASE_1_OPPOSITE = 1;
    public static final int PHASE_2 = 2;

    // Canonical raidPhaseOneDuration = 120 seconds
    public static final int RAID_PHASE_ONE_DURATION = 120;

    public static final Set<TileEntityTotem> ACTIVE_TOTEMS = new HashSet<TileEntityTotem>();

    private int controllingFactionId = FACTION_NEUTRAL;
    private int attackingFactionId = FACTION_NEUTRAL;
    private boolean isRaidActive = false;
    private int capturePhase = PHASE_1_NEUTRAL;
    private int captureTimer = 0; // in seconds
    private int captureAbortTimer = 0; // in seconds
    private int captureDuration = 1200; // in seconds (24000 ticks)
    private float strengthRatio = 0.5F;
    private int raidProgress = 0; // 0..100 (0..80 in Phase 1, 80..100 in Phase 2)
    private long timeSinceLastRaid = 0;
    private boolean pendingInitialPopulate = false;

    // Client beam scale (canonical shouldRenderBeam)
    private long beamRenderCounter = 0;
    private float beamRenderScale = 0.0F;

    private FactionVillage village;

    public TileEntityTotem() {}

    @Override
    public void validate() {
        super.validate();
        ACTIVE_TOTEMS.add(this);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        ACTIVE_TOTEMS.remove(this);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        ACTIVE_TOTEMS.remove(this);
    }

    /**
     * Canonical VillageEventHandler.onVillageAreaChanged:
     * Vampire-controlled villages (or active Vampire raids) project artificial fog over the village area,
     * protecting vampires from sun damage during the day.
     */
    public static boolean isEntityProtectedByVampireVillage(net.minecraft.entity.Entity entity) {
        if (entity == null || entity.worldObj == null) return false;
        for (TileEntityTotem totem : ACTIVE_TOTEMS) {
            if (totem != null && totem.worldObj == entity.worldObj && !totem.isInvalid()) {
                if (totem.controllingFactionId == FACTION_VAMPIRE
                    || (totem.isRaidActive && totem.attackingFactionId == FACTION_VAMPIRE)) {
                    AxisAlignedBB area = totem.getVillageArea();
                    if (area.isVecInside(net.minecraft.util.Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public IVillageFaction getVillage() {
        if (this.village == null && this.worldObj != null) {
            this.village = new FactionVillage(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            this.village.setControllingFaction(getFaction());
        }
        return this.village;
    }

    public String getVillageName() {
        getVillage();
        return this.village != null ? this.village.getVillageName() : "Village";
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

    public int getCapturePhase() {
        return capturePhase;
    }

    /**
     * Canonical TotemBlockEntity.shouldRenderBeam():
     * Smoothly scales the beacon beam from 0.0 to 1.0 when a raid is active.
     */
    public float shouldRenderBeam() {
        if (!isComplete() || !this.isRaidActive || this.attackingFactionId == FACTION_NEUTRAL) {
            this.beamRenderScale = 0.0F;
            return 0.0F;
        }
        long gameTime = this.worldObj != null ? this.worldObj.getTotalWorldTime() : 0L;
        int elapsed = (int) (gameTime - this.beamRenderCounter);
        this.beamRenderCounter = gameTime;
        if (elapsed > 1) {
            this.beamRenderScale -= (float) elapsed / 40.0F;
            if (this.beamRenderScale < 0.0F) {
                this.beamRenderScale = 0.0F;
            }
        }
        this.beamRenderScale += 0.025F;
        if (this.beamRenderScale > 1.0F) {
            this.beamRenderScale = 1.0F;
        }
        return this.beamRenderScale;
    }

    /**
     * Canonical TotemBlockEntity.canPlayerRemoveBlock:
     * Prevents breaking enemy totems or totems currently being captured.
     */
    public boolean canPlayerRemoveBlock(EntityPlayer player) {
        if (player == null) return false;
        if (player.capabilities.isCreativeMode) return true;
        if (!player.isEntityAlive()) return false;

        int playerFactionId = FACTION_NEUTRAL;
        if (Helper.isVampire(player)) playerFactionId = FACTION_VAMPIRE;
        else if (Helper.isHunter(player)) playerFactionId = FACTION_HUNTER;

        if (playerFactionId == this.controllingFactionId) {
            if (!this.isRaidActive) {
                return true;
            } else {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.totem_raid_in_progress",
                    getFactionById(this.attackingFactionId).getName(), this.raidProgress));
                return false;
            }
        } else if (this.isRaidActive && playerFactionId == this.attackingFactionId) {
            return this.controllingFactionId == FACTION_NEUTRAL;
        } else {
            return !this.isRaidActive && this.controllingFactionId == FACTION_NEUTRAL;
        }
    }

    public boolean isComplete() {
        return this.worldObj != null
            && this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) == ModBlocks.totemTop
            && this.worldObj.getBlock(this.xCoord, this.yCoord - 1, this.zCoord) == ModBlocks.totem;
    }

    /**
     * Called by VillageTotemCreationHandler when a new village totem is generated.
     */
    public void initializeGeneratedVillage(Random rand) {
        this.pendingInitialPopulate = true;
    }

    public boolean onRightClick(EntityPlayer player) {
        if (this.worldObj.isRemote) {
            return true;
        }

        if (!isComplete()) {
            return false;
        }

        int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (meta != this.controllingFactionId && (meta == FACTION_VAMPIRE || meta == FACTION_HUNTER)) {
            this.controllingFactionId = meta;
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
            this.startRaid(playerFactionId, player);
            return true;
        }

        IPlayableFaction current = getFaction();
        player.addChatMessage(new ChatComponentTranslation("chat.neorism.totem_status",
            current.getName(), this.controllingFactionId == FACTION_NEUTRAL ? "Neutral" : getVillageName()));

        return true;
    }

    /**
     * Canonical TotemBlockEntity.initiateCapture:
     * Starts Phase 1 (PHASE_1_NEUTRAL or PHASE_1_OPPOSITE), calculates strength ratio,
     * and wakes/arms villagers if Vampires attack a Hunter/Neutral village.
     */
    public void startRaid(int attackingFaction, EntityPlayer initiator) {
        this.isRaidActive = true;
        this.attackingFactionId = attackingFaction;
        this.captureTimer = 0;
        this.captureAbortTimer = 0;
        this.captureDuration = 1200; // 1200 seconds = 24000 ticks
        this.strengthRatio = 0.5F;
        this.raidProgress = 0;
        this.timeSinceLastRaid = 0;
        this.capturePhase = (this.controllingFactionId == FACTION_NEUTRAL) ? PHASE_1_NEUTRAL : PHASE_1_OPPOSITE;

        IPlayableFaction attacking = getFactionById(attackingFaction);
        IPlayableFaction defending = getFaction();

        this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D,
            "random.anvil_land", 1.5F, 0.8F);

        notifyNearby(new ChatComponentTranslation("chat.neorism.totem_raid_started",
            attacking.getName(), defending.getName()), 64.0D);

        // Immediately spawn initial wave of attackers and defenders so the battle starts right away
        spawnCaptureEntity(this.attackingFactionId);
        if (this.controllingFactionId != FACTION_NEUTRAL) {
            spawnCaptureEntity(this.controllingFactionId);
        }

        // Canonical makeAggressive: when Vampires attack a Neutral or Hunter village, 1 in 3 villagers mobilize as defenders
        if (this.attackingFactionId == FACTION_VAMPIRE
            && (this.controllingFactionId == FACTION_HUNTER || this.controllingFactionId == FACTION_NEUTRAL)) {
            spawnMilitiaDefenders();
        }

        this.markDirty();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);

        ModLogger.combat("Totem at [%d, %d, %d] (%s) raid initiated by %s (faction %s, phase %d)",
            this.xCoord, this.yCoord, this.zCoord, getVillageName(),
            initiator != null ? initiator.getCommandSenderName() : "RandomEvent",
            attacking.getName(), this.capturePhase);
    }

    @Override
    public void updateEntity() {
        if (this.worldObj == null) {
            return;
        }

        // Sync controllingFactionId with block metadata if placed/generated with metadata
        int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (this.controllingFactionId == FACTION_NEUTRAL && (meta == FACTION_VAMPIRE || meta == FACTION_HUNTER) && !this.isRaidActive) {
            this.controllingFactionId = meta;
        }

        // Client-side particle aura (canonical TotemBlockEntity.clientTick)
        if (this.worldObj.isRemote) {
            if (this.worldObj.getTotalWorldTime() % 10 == 7 && this.controllingFactionId != FACTION_NEUTRAL) {
                String particle = this.controllingFactionId == FACTION_VAMPIRE ? "reddust" : "Crit";
                for (int i = 0; i < 3; i++) {
                    double px = this.xCoord + 0.5D + (this.worldObj.rand.nextDouble() - 0.5D) * 0.8D;
                    double py = this.yCoord + 0.3D + this.worldObj.rand.nextDouble() * 0.7D;
                    double pz = this.zCoord + 0.5D + (this.worldObj.rand.nextDouble() - 0.5D) * 0.8D;
                    if (this.controllingFactionId == FACTION_VAMPIRE) {
                        this.worldObj.spawnParticle("reddust", px, py, pz, 0.8D, 0.05D, 0.1D);
                    } else {
                        this.worldObj.spawnParticle("magicCrit", px, py, pz, 0.0D, 0.05D, 0.0D);
                    }
                }
            }
            return;
        }

        if (!isComplete()) {
            if (this.isRaidActive) {
                breakCapture();
            }
            return;
        }

        if (this.pendingInitialPopulate && this.worldObj.getTotalWorldTime() % 20 == 0) {
            this.pendingInitialPopulate = false;
            populateInitialFactionVillage();
        }

        long time = this.worldObj.getTotalWorldTime();

        if (!this.isRaidActive) {
            this.timeSinceLastRaid++;
            if (time % 20 == 7) {
                serverTickSecondNonCapture((int) (time / 20));
            }
            return;
        }

        if (time % 20 == 0) {
            serverTickSecondCapture((int) (time / 20));
        }
    }

    /**
     * Populates a newly generated Vampire or Hunter village with converted villagers, taskmaster, trainer, and guards.
     */
    @SuppressWarnings("unchecked")
    private void populateInitialFactionVillage() {
        if (this.controllingFactionId == FACTION_NEUTRAL) return;

        AxisAlignedBB area = getVillageArea();
        List<EntityVillager> villagers = this.worldObj.getEntitiesWithinAABB(EntityVillager.class, area);

        if (this.controllingFactionId == FACTION_VAMPIRE) {
            // Canonical Vampire village: convert existing villagers to EntityConvertedVillager
            for (EntityVillager v : new ArrayList<EntityVillager>(villagers)) {
                if (!(v instanceof EntityConvertedVillager) && !v.isDead) {
                    replaceWithConvertedVillager(v);
                }
            }
            for (int i = 0; i < 3; i++) {
                spawnCaptureEntity(FACTION_VAMPIRE);
            }
        } else if (this.controllingFactionId == FACTION_HUNTER) {
            // Canonical Hunter village: give villagers poisonous blood (garlic protection)
            for (EntityVillager v : villagers) {
                ExtendedCreature ext = ExtendedCreature.get(v);
                if (ext != null) {
                    ext.setPoisonousBlood(ExtendedCreature.POISONOUS_BLOOD_DOSE_DURATION);
                }
            }
            for (int i = 0; i < 3; i++) {
                spawnCaptureEntity(FACTION_HUNTER);
            }
        }

        tickNonCaptureVillageSpawns();
    }

    /**
     * Canonical port of TotemBlockEntity.serverTickSecondNonCapture:
     * Every 16 seconds, maintains village population (ConvertedVillager in Vampire villages, garlic-protected Villager in Hunter villages),
     * spawns Taskmaster, Hunter Trainer, and up to 6 faction guards, and checks for random raids.
     */
    @SuppressWarnings("unchecked")
    private void serverTickSecondNonCapture(int timeInSeconds) {
        if (this.controllingFactionId != FACTION_NEUTRAL && timeInSeconds % 16 == 0) {
            tickNonCaptureVillageSpawns();
        }

        // Canonical random raid check: after 12000 ticks (10 minutes) since last raid on non-Peaceful
        if (this.timeSinceLastRaid > 12000
            && this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL
            && this.worldObj.rand.nextFloat() < 0.0005F) {
            if (this.worldObj.getClosestPlayer(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, 64.0D) != null) {
                int raiderFaction = (this.controllingFactionId == FACTION_VAMPIRE)
                    ? FACTION_HUNTER
                    : (this.controllingFactionId == FACTION_HUNTER ? FACTION_VAMPIRE : (this.worldObj.rand.nextBoolean() ? FACTION_VAMPIRE : FACTION_HUNTER));
                startRaid(raiderFaction, null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void tickNonCaptureVillageSpawns() {
        if (this.worldObj.getClosestPlayer(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, 80.0D) == null) {
            return;
        }

        AxisAlignedBB area = getVillageArea();
        List<EntityVillager> villagers = this.worldObj.getEntitiesWithinAABB(EntityVillager.class, area);

        // Canonical VillageEventHandler.onSpawnVillager: maintain up to 8 villagers
        if (villagers.size() < 8) {
            if (this.controllingFactionId == FACTION_VAMPIRE) {
                EntityVillager newV = this.worldObj.rand.nextBoolean()
                    ? new EntityConvertedVillager(this.worldObj)
                    : new EntityVillager(this.worldObj);
                spawnEntityInVillage(newV);
            } else if (this.controllingFactionId == FACTION_HUNTER) {
                EntityVillager newV = new EntityVillager(this.worldObj);
                if (spawnEntityInVillage(newV)) {
                    ExtendedCreature ext = ExtendedCreature.get(newV);
                    if (ext != null) {
                        ext.setPoisonousBlood(ExtendedCreature.POISONOUS_BLOOD_DOSE_DURATION);
                    }
                }
            }
        }

        // Convert any infected villagers in Vampire villages or ensure at least half are converted
        if (this.controllingFactionId == FACTION_VAMPIRE) {
            int convertedCount = 0;
            for (EntityVillager v : villagers) {
                if (v instanceof EntityConvertedVillager) convertedCount++;
            }
            if (convertedCount == 0 && !villagers.isEmpty()) {
                replaceWithConvertedVillager(villagers.get(0));
            }
        }

        // Taskmaster spawn
        List<EntityTaskmaster> taskmasters = this.worldObj.getEntitiesWithinAABB(EntityTaskmaster.class, area);
        if (taskmasters.isEmpty()) {
            EntityTaskmaster tm = new EntityTaskmaster(this.worldObj);
            tm.onSpawnWithEgg(null);
            tm.setHomeArea(this.xCoord, this.yCoord, this.zCoord, 24);
            tm.func_110163_bv();
            spawnEntityInVillage(tm);
        }

        // Hunter Trainer & Guards
        if (this.controllingFactionId == FACTION_HUNTER) {
            List<EntityHunterTrainer> trainers = this.worldObj.getEntitiesWithinAABB(EntityHunterTrainer.class, area);
            if (trainers.isEmpty()) {
                EntityHunterTrainer trainer = new EntityHunterTrainer(this.worldObj);
                trainer.onSpawnWithEgg(null);
                trainer.setHomeArea(this.xCoord, this.yCoord, this.zCoord, 24);
                trainer.func_110163_bv();
                spawnEntityInVillage(trainer);
            }

            List<EntityHunterBase> guards = this.worldObj.getEntitiesWithinAABB(EntityHunterBase.class, area);
            if (guards.size() < 6) {
                spawnCaptureEntity(FACTION_HUNTER);
            }
        } else if (this.controllingFactionId == FACTION_VAMPIRE) {
            List<EntityVampireBase> guards = this.worldObj.getEntitiesWithinAABB(EntityVampireBase.class, area);
            if (guards.size() < 6) {
                spawnCaptureEntity(FACTION_VAMPIRE);
            }
        }
    }

    /**
     * Canonical port of TotemBlockEntity.serverTickSecondCapture:
     * Executes the 2-phase village capture state machine once per second (every 20 ticks).
     */
    @SuppressWarnings("unchecked")
    private void serverTickSecondCapture(int timeInSeconds) {
        AxisAlignedBB area = getVillageArea();
        List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, area);

        int currentAttacker = 0;
        int currentDefender = 0;
        float attackerStrength = 0.0F;
        float defenderStrength = 0.0F;

        for (EntityLivingBase entity : entities) {
            if (!entity.isEntityAlive()) continue;
            if (entity instanceof EntityTaskmaster || entity instanceof EntityHunterTrainer) continue;

            int fId = getEntityFactionId(entity);
            if (fId == FACTION_NEUTRAL) continue;

            if (fId == this.attackingFactionId) {
                currentAttacker++;
                attackerStrength += getEntityCaptureStrength(entity);
                if (entity instanceof IVillageCaptureEntity) {
                    ((IVillageCaptureEntity) entity).attackVillage(this);
                }
            } else if (fId == this.controllingFactionId) {
                currentDefender++;
                defenderStrength += getEntityCaptureStrength(entity);
                if (entity instanceof IVillageCaptureEntity) {
                    ((IVillageCaptureEntity) entity).defendVillage(this);
                }
            }
        }

        if (currentAttacker == 0) {
            this.captureAbortTimer++;
        } else {
            this.captureAbortTimer = 0;
        }

        this.captureTimer++;
        this.captureDuration--;

        if (this.captureDuration <= 0 || this.captureAbortTimer > 12) {
            abortCapture();
            return;
        }

        if (this.capturePhase == PHASE_1_NEUTRAL) {
            if (this.captureTimer >= RAID_PHASE_ONE_DURATION) {
                this.captureTimer = 1;
                this.capturePhase = PHASE_2;
                this.raidProgress = 80;
            } else {
                this.raidProgress = Math.min(80, (int) ((float) this.captureTimer / (float) RAID_PHASE_ONE_DURATION * 80.0F));
                if (this.captureTimer % 2 == 0 && attackerStrength < 5.0F) {
                    spawnCaptureEntity(this.attackingFactionId);
                }
            }
        } else if (this.capturePhase == PHASE_1_OPPOSITE) {
            if (this.captureTimer >= RAID_PHASE_ONE_DURATION) {
                this.captureTimer = 1;
                this.capturePhase = PHASE_2;
                this.raidProgress = 80;
                notifyNearby(new ChatComponentTranslation("chat.neorism.totem_raid_in_progress",
                    getFactionById(this.attackingFactionId).getName(), 80), 64.0D);
            } else {
                this.raidProgress = Math.min(80, (int) ((float) this.captureTimer / (float) RAID_PHASE_ONE_DURATION * 80.0F));
                if (this.captureTimer % 2 == 0) {
                    float max = attackerStrength + defenderStrength;
                    if (max <= 0.0F || attackerStrength / max <= this.strengthRatio) {
                        spawnCaptureEntity(this.attackingFactionId);
                    } else if (defenderStrength / max <= 1.0F - this.strengthRatio) {
                        spawnCaptureEntity(this.controllingFactionId);
                    }
                }
            }
        } else if (this.capturePhase == PHASE_2) {
            if (currentDefender == 0) {
                this.raidProgress = Math.min(100, 80 + this.captureTimer * 5);
                if (this.captureTimer > 4) {
                    completeCapture(true, false);
                    return;
                }
            } else if (currentAttacker == 0) {
                if (this.captureTimer > 4) {
                    abortCapture();
                    return;
                }
            } else {
                this.captureTimer = 1;
                this.raidProgress = 80;
            }
        }

        this.markDirty();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    private int getEntityFactionId(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) entity;
            if (p.capabilities.isCreativeMode) return FACTION_NEUTRAL;
            if (Helper.isVampire(p)) return FACTION_VAMPIRE;
            if (Helper.isHunter(p)) return FACTION_HUNTER;
            return FACTION_NEUTRAL;
        }
        if (entity instanceof IVampireEntity) return FACTION_VAMPIRE;
        if (entity instanceof IHunterEntity) return FACTION_HUNTER;
        if (entity instanceof EntityIronGolem && this.controllingFactionId != FACTION_VAMPIRE) {
            return this.controllingFactionId == FACTION_NEUTRAL ? FACTION_HUNTER : this.controllingFactionId;
        }
        return FACTION_NEUTRAL;
    }

    /**
     * Canonical TotemBlockEntity.getStrength:
     * Players contribute relative faction level (0.5..1.5), ConvertedVillagers contribute 0.5, mobs contribute 1.0 (or 1.5 for Advanced).
     */
    private float getEntityCaptureStrength(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            FactionPlayerHandler fph = FactionPlayerHandler.get((EntityPlayer) entity);
            if (fph != null && fph.getCurrentLevel() > 0) {
                return Math.max(0.5F, (float) fph.getCurrentLevel() / 14.0F * 1.5F);
            }
            return 1.0F;
        }
        if (entity instanceof EntityConvertedVillager) {
            return 0.5F;
        }
        if (entity instanceof EntityAdvancedVampire || entity instanceof EntityAdvancedHunter) {
            return 1.5F;
        }
        return 1.0F;
    }

    /**
     * Canonical VillageEventHandler.onSpawnCaptureEntity:
     * Weighted 10:2 basic to advanced faction raiders/defenders.
     */
    private void spawnCaptureEntity(int factionId) {
        EntityCreature entity = null;
        boolean advanced = this.worldObj.rand.nextInt(12) < 2; // 2 in 12 = 10:2 weight

        if (factionId == FACTION_HUNTER) {
            entity = advanced ? new EntityAdvancedHunter(this.worldObj) : new EntityHunter(this.worldObj);
        } else if (factionId == FACTION_VAMPIRE) {
            entity = advanced ? new EntityAdvancedVampire(this.worldObj) : new EntityVampire(this.worldObj);
        }

        if (entity != null) {
            entity.onSpawnWithEgg(null);
            entity.setHomeArea(this.xCoord, this.yCoord, this.zCoord, 32);
            if (spawnEntityInVillage(entity) && entity instanceof IVillageCaptureEntity) {
                IVillageCaptureEntity ce = (IVillageCaptureEntity) entity;
                if (factionId == this.controllingFactionId) {
                    ce.defendVillage(this);
                } else {
                    ce.attackVillage(this);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void spawnMilitiaDefenders() {
        List<EntityVillager> villagers = this.worldObj.getEntitiesWithinAABB(EntityVillager.class, getVillageArea());
        int spawned = 0;
        for (EntityVillager v : villagers) {
            if (v.getGrowingAge() >= 0 && !(v instanceof EntityConvertedVillager) && this.worldObj.rand.nextInt(3) == 0) {
                EntityHunter militia = new EntityHunter(this.worldObj);
                militia.setLocationAndAngles(v.posX, v.posY, v.posZ, v.rotationYaw, v.rotationPitch);
                militia.onSpawnWithEgg(null);
                militia.setCurrentItemOrArmor(0, new net.minecraft.item.ItemStack(com.marrybye.neorism.common.core.ModItems.pitchfork));
                militia.setHomeArea(this.xCoord, this.yCoord, this.zCoord, 32);
                militia.defendVillage(this);
                this.worldObj.spawnEntityInWorld(militia);
                if (++spawned >= 3) break;
            }
        }
    }

    private boolean spawnEntityInVillage(EntityCreature entity) {
        for (int attempt = 0; attempt < 25; attempt++) {
            double rx = this.xCoord + 0.5D + (this.worldObj.rand.nextDouble() - 0.5D) * 36.0D;
            double rz = this.zCoord + 0.5D + (this.worldObj.rand.nextDouble() - 0.5D) * 36.0D;
            int bx = MathHelper.floor_double(rx);
            int bz = MathHelper.floor_double(rz);
            int by = this.worldObj.getTopSolidOrLiquidBlock(bx, bz);
            if (by <= 0 || Math.abs(by - this.yCoord) > 18) {
                by = this.yCoord;
            }
            Block ground = this.worldObj.getBlock(bx, by - 1, bz);
            if (ground != null && ground.getMaterial().isSolid() && this.worldObj.isAirBlock(bx, by, bz) && this.worldObj.isAirBlock(bx, by + 1, bz)) {
                entity.setLocationAndAngles(bx + 0.5D, by, bz + 0.5D, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
                this.worldObj.spawnEntityInWorld(entity);
                return true;
            }
        }
        entity.setLocationAndAngles(
            this.xCoord + 0.5D + (this.worldObj.rand.nextDouble() - 0.5D) * 6.0D,
            this.yCoord,
            this.zCoord + 0.5D + (this.worldObj.rand.nextDouble() - 0.5D) * 6.0D,
            this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
        this.worldObj.spawnEntityInWorld(entity);
        return true;
    }

    private void abortCapture() {
        this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D,
            "random.successful_hit", 1.5F, 0.9F);
        notifyNearby(new ChatComponentTranslation("chat.neorism.totem_raid_defended"), 64.0D);
        ModLogger.combat("Totem [%d, %d, %d] (%s) raid repelled", this.xCoord, this.yCoord, this.zCoord, getVillageName());
        breakCapture();
    }

    @SuppressWarnings("unchecked")
    public void breakCapture() {
        this.isRaidActive = false;
        this.attackingFactionId = FACTION_NEUTRAL;
        this.raidProgress = 0;
        this.captureTimer = 0;
        this.captureAbortTimer = 0;
        this.timeSinceLastRaid = 0;

        List<IVillageCaptureEntity> captureEntities = this.worldObj.getEntitiesWithinAABB(IVillageCaptureEntity.class, getVillageArea());
        for (IVillageCaptureEntity ce : captureEntities) {
            ce.stopVillageAttackDefense();
        }

        this.markDirty();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    /**
     * Canonical TotemBlockEntity.completeCapture & VillageEventHandler.onUpdateCreaturesOnCapture:
     * Flips controlling faction, converts/cures villagers, spawns trainer/taskmaster, and announces victory.
     */
    @SuppressWarnings("unchecked")
    private void completeCapture(boolean notifyPlayer, boolean fullConvert) {
        int newFactionId = this.attackingFactionId;
        IPlayableFaction attacking = getFactionById(newFactionId);

        AxisAlignedBB area = getVillageArea();
        List<EntityVillager> villagers = this.worldObj.getEntitiesWithinAABB(EntityVillager.class, area);

        if (newFactionId == FACTION_VAMPIRE) {
            // Canonical Vampire capture: infect or convert villagers, remove Hunter trainers
            for (EntityVillager v : new ArrayList<EntityVillager>(villagers)) {
                if (v instanceof EntityConvertedVillager || v.isDead) continue;
                ExtendedCreature ext = ExtendedCreature.get(v);
                if (ext != null) {
                    ext.setPoisonousBlood(0);
                }
                if (fullConvert || this.worldObj.rand.nextBoolean()) {
                    replaceWithConvertedVillager(v);
                } else {
                    v.addPotionEffect(new PotionEffect(ModPotions.sanguinare.id, 240, 0));
                }
            }
            List<EntityHunterTrainer> trainers = this.worldObj.getEntitiesWithinAABB(EntityHunterTrainer.class, area);
            for (EntityHunterTrainer t : trainers) {
                t.setDead();
            }
        } else if (newFactionId == FACTION_HUNTER) {
            // Canonical Hunter capture: cure converted villagers, apply garlic blood protection, spawn Hunter Trainer
            for (EntityVillager v : new ArrayList<EntityVillager>(villagers)) {
                if (v.isDead) continue;
                if (v.isPotionActive(ModPotions.sanguinare)) {
                    v.removePotionEffect(ModPotions.sanguinare.id);
                }
                if (v instanceof EntityConvertedVillager) {
                    EntityVillager cured = new EntityVillager(this.worldObj, v.getProfession());
                    cured.copyLocationAndAnglesFrom(v);
                    v.setDead();
                    this.worldObj.spawnEntityInWorld(cured);
                    v = cured;
                }
                ExtendedCreature ext = ExtendedCreature.get(v);
                if (ext != null) {
                    ext.setPoisonousBlood(ExtendedCreature.POISONOUS_BLOOD_DOSE_DURATION);
                }
            }
        }

        this.setControllingFactionId(newFactionId);
        breakCapture();

        if (notifyPlayer) {
            this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D,
                "ambient.weather.thunder", 2.0F, 1.0F);
            notifyNearby(new ChatComponentTranslation("chat.neorism.totem_raid_captured", attacking.getName()), 64.0D);
        }

        tickNonCaptureVillageSpawns();

        ModLogger.combat("Totem [%d, %d, %d] (%s) captured by %s",
            this.xCoord, this.yCoord, this.zCoord, getVillageName(), attacking.getName());
    }

    private void replaceWithConvertedVillager(EntityVillager villager) {
        EntityConvertedVillager converted = new EntityConvertedVillager(this.worldObj);
        converted.setProfession(villager.getProfession());
        converted.copyLocationAndAnglesFrom(villager);
        villager.setDead();
        this.worldObj.spawnEntityInWorld(converted);
    }

    @SuppressWarnings("unchecked")
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
        double r = 40.0D;
        return AxisAlignedBB.getBoundingBox(
            this.xCoord - r, this.yCoord - 16, this.zCoord - r,
            this.xCoord + r, this.yCoord + 24, this.zCoord + r
        );
    }

    @Override
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.controllingFactionId = tag.getInteger("ControllingFaction");
        this.attackingFactionId = tag.getInteger("AttackingFaction");
        this.isRaidActive = tag.getBoolean("IsRaidActive");
        this.capturePhase = tag.getInteger("CapturePhase");
        this.captureTimer = tag.getInteger("CaptureTimer");
        this.captureAbortTimer = tag.getInteger("CaptureAbortTimer");
        this.captureDuration = tag.hasKey("CaptureDuration") ? tag.getInteger("CaptureDuration") : 1200;
        this.raidProgress = tag.getInteger("RaidProgress");
        this.timeSinceLastRaid = tag.getLong("TimeSinceLastRaid");
        this.pendingInitialPopulate = tag.getBoolean("PendingInitialPopulate");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("ControllingFaction", this.controllingFactionId);
        tag.setInteger("AttackingFaction", this.attackingFactionId);
        tag.setBoolean("IsRaidActive", this.isRaidActive);
        tag.setInteger("CapturePhase", this.capturePhase);
        tag.setInteger("CaptureTimer", this.captureTimer);
        tag.setInteger("CaptureAbortTimer", this.captureAbortTimer);
        tag.setInteger("CaptureDuration", this.captureDuration);
        tag.setInteger("RaidProgress", this.raidProgress);
        tag.setLong("TimeSinceLastRaid", this.timeSinceLastRaid);
        tag.setBoolean("PendingInitialPopulate", this.pendingInitialPopulate);
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
        if (this.worldObj != null) {
            this.worldObj.func_147479_m(this.xCoord, this.yCoord, this.zCoord);
        }
    }
}
