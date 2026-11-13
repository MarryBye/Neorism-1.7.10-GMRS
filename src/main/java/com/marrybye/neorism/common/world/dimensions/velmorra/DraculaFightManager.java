package com.marrybye.neorism.common.world.dimensions.velmorra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.achievement.ModAchievements;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.entity.vampire.EntityDracula;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;

/**
 * Manages the canonical Lord Dracula boss encounter in Velmorra.
 * Controls ritual chalices, Dracula summoning, phase tracking, return portal opening,
 * and the 10-minute realm collapse timer upon victory.
 * Ported 1:1 from TeamLapen's DraculaFightData.java.
 */
public class DraculaFightManager extends WorldSavedData {

    public static final String DATA_NAME = "Neorism_DraculaFight";
    public static final int COLLAPSE_DURATION = 10 * 60 * 20; // 12000 ticks = 10 minutes

    public enum State {
        NONE,
        FIGHTING,
        DEAD
    }

    private static class CollapseWarning {
        final int tick;
        final String translationKey;

        CollapseWarning(int tick, String translationKey) {
            this.tick = tick;
            this.translationKey = translationKey;
        }
    }

    private static final CollapseWarning[] WARNINGS = new CollapseWarning[] {
        new CollapseWarning(1, "message.vampirism.velmorra.crumblin"),
        new CollapseWarning(COLLAPSE_DURATION - 5 * 60 * 20, "message.vampirism.velmorra.collapse.5min"),
        new CollapseWarning(COLLAPSE_DURATION - 2 * 60 * 20, "message.vampirism.velmorra.collapse.2min"),
        new CollapseWarning(COLLAPSE_DURATION - 60 * 20, "message.vampirism.velmorra.crumblin2"),
        new CollapseWarning(COLLAPSE_DURATION - 30 * 20, "message.vampirism.velmorra.collapse.30s")
    };

    private World world;
    private State state = State.NONE;
    private int deathTicks = 0;
    private int warningsSent = 0;
    private int rumbleCooldown = 0;
    private UUID draculaId = null;
    private final Set<ChunkCoordinates> chalicePositions = new HashSet<ChunkCoordinates>();

    public DraculaFightManager(String name) {
        super(name);
    }

    public DraculaFightManager() {
        super(DATA_NAME);
    }

    public static DraculaFightManager get(World world) {
        if (world == null || world.isRemote) return null;
        DraculaFightManager data = (DraculaFightManager) world.perWorldStorage.loadData(DraculaFightManager.class, DATA_NAME);
        if (data == null) {
            data = new DraculaFightManager(DATA_NAME);
            world.perWorldStorage.setData(DATA_NAME, data);
        }
        data.world = world;
        return data;
    }

    public State getState() {
        return state;
    }

    public void registerChalice(int x, int y, int z) {
        chalicePositions.add(new ChunkCoordinates(x, y, z));
        markDirty();
    }

    public void onChaliceFilled(int x, int y, int z) {
        registerChalice(x, y, z);
        if (state != State.NONE) return;

        // Count filled chalices
        int filled = 0;
        for (ChunkCoordinates pos : chalicePositions) {
            if (world.getBlock(pos.posX, pos.posY, pos.posZ) == ModBlocks.chalice &&
                world.getBlockMetadata(pos.posX, pos.posY, pos.posZ) == 1) {
                filled++;
            }
        }

        // If not all registered, scan nearby arena (around throne hall)
        if (filled < 4) {
            for (int dx = -12; dx <= 12; dx++) {
                for (int dy = 0; dy <= 6; dy++) {
                    for (int dz = 10; dz <= 26; dz++) {
                        if (world.getBlock(dx, 65 + dy, dz) == ModBlocks.chalice) {
                            chalicePositions.add(new ChunkCoordinates(dx, 65 + dy, dz));
                            if (world.getBlockMetadata(dx, 65 + dy, dz) == 1) {
                                filled++;
                            }
                        }
                    }
                }
            }
        }

        if (filled >= 4) {
            startFight();
        }
    }

    public boolean debugStartFight() {
        if (state != State.NONE) return false;
        startFight();
        return true;
    }

    public void awakenDracula(EntityDracula existing) {
        if (state != State.NONE) return;
        activateFight(existing);
    }

    public void startFight() {
        if (state != State.NONE) return;
        AxisAlignedBB castleBB = AxisAlignedBB.getBoundingBox(-45, 50, -45, 45, 110, 45);
        List<?> existingList = world.getEntitiesWithinAABB(EntityDracula.class, castleBB);
        EntityDracula existing = null;
        for (Object o : existingList) {
            if (o instanceof EntityDracula && ((EntityDracula) o).isEntityAlive()) {
                existing = (EntityDracula) o;
                break;
            }
        }
        activateFight(existing);
    }

    private void activateFight(EntityDracula dracula) {
        state = State.FIGHTING;
        deathTicks = 0;
        warningsSent = 0;

        // Reset chalices to empty (meta 0)
        for (ChunkCoordinates pos : chalicePositions) {
            if (world.getBlock(pos.posX, pos.posY, pos.posZ) == ModBlocks.chalice) {
                world.setBlockMetadataWithNotify(pos.posX, pos.posY, pos.posZ, 0, 3);
            }
        }

        int playerCount = Math.max(1, world.playerEntities.size());

        if (dracula == null) {
            // Spawn Dracula in front of the throne
            dracula = new EntityDracula(world);
            dracula.setLocationAndAngles(0.5D, 67.0D, 21.5D, 180.0F, 0.0F);
            dracula.onSpawnWithEgg(null);
            dracula.startFight(playerCount);
            world.spawnEntityInWorld(dracula);
        } else {
            dracula.startFight(playerCount);
        }
        this.draculaId = dracula.getPersistentID();

        // Theatrical summoning effects
        world.addWeatherEffect(new EntityLightningBolt(world, dracula.posX, dracula.posY + 1.0D, dracula.posZ));
        world.playSoundEffect(dracula.posX, dracula.posY, dracula.posZ, ModSounds.VAMPIRE_SCREAM, 2.0F, 0.6F);

        broadcastMessage("message.vampirism.dracula.summoned");
        ModLogger.combat("Dracula Fight started with %d players!", playerCount);
        markDirty();
    }

    public void onDraculaKilled(EntityDracula dracula) {
        if (state == State.DEAD) return;
        state = State.DEAD;
        deathTicks = 0;
        warningsSent = 0;
        draculaId = null;

        // 1. Open return portal on the grand avenue in front of the gatehouse
        ChunkProviderVelmorra.placeReturnPortal(world, 0, 65, -31);

        // 2. Broadcast victory and portal opened messages
        broadcastMessage("message.vampirism.dracula.defeated");
        broadcastMessage("message.vampirism.velmorra.portal_opened");

        // 3. Award all players in dimension
        for (Object pObj : world.playerEntities) {
            if (pObj instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) pObj;
                player.addExperience(1000);
                if (ModAchievements.defeatDracula != null) {
                    player.triggerAchievement(ModAchievements.defeatDracula);
                }
            }
        }

        ModLogger.combat("Lord Dracula defeated! Return portal opened. Realm collapse countdown begun.");
        markDirty();
    }

    public void tick() {
        if (world == null || world.isRemote) return;

        if (state == State.DEAD) {
            deathTicks++;

            // Canonical VelmorraCollapseHandler ambient rumble scaling with eased collapse progress
            if (--rumbleCooldown <= 0) {
                float progress = Math.min(1.0F, Math.max(0.0F, (float) deathTicks / (float) COLLAPSE_DURATION));
                float p = progress * progress;
                rumbleCooldown = 300 - (int) (200 * p) + world.rand.nextInt(100);
                float volume = 0.5F + 1.5F * p;
                float pitch = 0.5F + world.rand.nextFloat() * 0.3F;
                for (Object pObj : world.playerEntities) {
                    if (pObj instanceof EntityPlayer) {
                        world.playSoundAtEntity((EntityPlayer) pObj, ModSounds.VELMORRA_RUMBLE, volume, pitch);
                    }
                }
            }

            // Check staged warnings
            while (warningsSent < WARNINGS.length && deathTicks >= WARNINGS[warningsSent].tick) {
                broadcastMessage(WARNINGS[warningsSent].translationKey);
                warningsSent++;
                markDirty();
            }

            // Final 10 seconds countdown
            int remaining = COLLAPSE_DURATION - deathTicks;
            if (remaining > 0 && remaining <= 10 * 20 && remaining % 20 == 0) {
                broadcastMessage("message.vampirism.velmorra.collapse.countdown", String.valueOf(remaining / 20));
            }

            // Time's up - collapse and evict all players
            if (deathTicks >= COLLAPSE_DURATION) {
                collapseAndEvict();
            }
        } else if (state == State.FIGHTING) {
            // Verify Dracula is still alive
            if (world.getTotalWorldTime() % 40 == 0) {
                AxisAlignedBB castleBB = AxisAlignedBB.getBoundingBox(-45, 50, -45, 45, 110, 45);
                List<?> draculas = world.getEntitiesWithinAABB(EntityDracula.class, castleBB);
                if (draculas.isEmpty()) {
                    // Dracula disappeared or died without triggering death callback
                    ModLogger.warn("Dracula fight was active but no Dracula found in castle arena.");
                }
            }
        }
    }

    private void broadcastMessage(String translationKey, Object... args) {
        if (world == null) return;
        for (Object pObj : world.playerEntities) {
            if (pObj instanceof EntityPlayer) {
                ((EntityPlayer) pObj).addChatMessage(new ChatComponentTranslation(translationKey, args));
            }
        }
    }

    private void collapseAndEvict() {
        ModLogger.info("Velmorra dimension collapsed. Evicting players to Overworld.");
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return;

        WorldServer overworld = server.worldServerForDimension(0);
        if (overworld == null) return;

        List<EntityPlayerMP> players = new ArrayList<EntityPlayerMP>();
        for (Object pObj : world.playerEntities) {
            if (pObj instanceof EntityPlayerMP) {
                players.add((EntityPlayerMP) pObj);
            }
        }

        for (EntityPlayerMP player : players) {
            double tx, ty, tz;
            if (player.getEntityData().hasKey("VelmorraReturnX")) {
                int rx = player.getEntityData().getInteger("VelmorraReturnX");
                int ry = player.getEntityData().getInteger("VelmorraReturnY");
                int rz = player.getEntityData().getInteger("VelmorraReturnZ");
                tx = rx + 0.5D;
                ty = ry;
                tz = rz + 1.5D;
            } else {
                int sx = overworld.getSpawnPoint().posX;
                int sy = overworld.getTopSolidOrLiquidBlock(sx, overworld.getSpawnPoint().posZ);
                int sz = overworld.getSpawnPoint().posZ;
                tx = sx + 0.5D;
                ty = sy + 1.0D;
                tz = sz + 0.5D;
            }
            server.getConfigurationManager().transferPlayerToDimension(player, 0,
                new TeleporterVelmorra(overworld, tx, ty, tz));
        }

        // Reset fight state
        state = State.NONE;
        deathTicks = 0;
        warningsSent = 0;
        draculaId = null;
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        String stateStr = nbt.getString("State");
        try {
            state = State.valueOf(stateStr);
        } catch (Exception e) {
            state = State.NONE;
        }
        deathTicks = nbt.getInteger("DeathTicks");
        warningsSent = nbt.getInteger("WarningsSent");

        if (nbt.hasKey("DraculaIdMost") && nbt.hasKey("DraculaIdLeast")) {
            draculaId = new UUID(nbt.getLong("DraculaIdMost"), nbt.getLong("DraculaIdLeast"));
        } else {
            draculaId = null;
        }

        chalicePositions.clear();
        NBTTagList list = nbt.getTagList("Chalices", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound cTag = list.getCompoundTagAt(i);
            chalicePositions.add(new ChunkCoordinates(cTag.getInteger("X"), cTag.getInteger("Y"), cTag.getInteger("Z")));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("State", state.name());
        nbt.setInteger("DeathTicks", deathTicks);
        nbt.setInteger("WarningsSent", warningsSent);

        if (draculaId != null) {
            nbt.setLong("DraculaIdMost", draculaId.getMostSignificantBits());
            nbt.setLong("DraculaIdLeast", draculaId.getLeastSignificantBits());
        }

        NBTTagList list = new NBTTagList();
        for (ChunkCoordinates pos : chalicePositions) {
            NBTTagCompound cTag = new NBTTagCompound();
            cTag.setInteger("X", pos.posX);
            cTag.setInteger("Y", pos.posY);
            cTag.setInteger("Z", pos.posZ);
            list.appendTag(cTag);
        }
        nbt.setTag("Chalices", list);
    }
}
