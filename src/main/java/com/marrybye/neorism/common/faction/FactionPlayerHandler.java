package com.marrybye.neorism.common.faction;

import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.api.faction.IFactionPlayer;
import com.marrybye.neorism.api.faction.IPlayableFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.network.packets.AppearancePacket;
import com.marrybye.neorism.common.network.packets.SyncFactionPacket;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Extended entity property managing player faction membership, levels, lord ranks, and disguise state.
 * Implements both Neorism and TeamLapen FactionAPI interfaces.
 */
public class FactionPlayerHandler implements IExtendedEntityProperties, IFactionPlayer,
        de.teamlapen.faction.api.factions.IFactionPlayerHandler,
        de.teamlapen.faction.api.factions.IFactionPlayer,
        de.teamlapen.faction.api.factions.lord.ILordPlayer {

    public static final String PROPERTY_ID = "neorism:faction_player";

    private final EntityPlayer player;
    private IPlayableFaction currentFaction = ModFactions.NEUTRAL;
    private int currentLevel = 0;
    private int lordLevel = 0;
    private boolean disguised = false;
    private de.teamlapen.faction.api.factions.IPlayableFaction.TitleGender titleGender =
        de.teamlapen.faction.api.factions.IPlayableFaction.TitleGender.UNKNOWN;

    // 3 Refinement slots: 0 = Amulet, 1 = Ring, 2 = Obi Belt
    private final net.minecraft.item.ItemStack[] refinements = new net.minecraft.item.ItemStack[3];

    // Vampire Appearance Customization
    private int fangType = 0; // 0..6
    private int eyeType = 0;  // 0..18
    private boolean glowingEyes = false;
    private boolean wings = false;
    private int wingsType = 0; // 0 = normal, 1 = castle contest, 2 = dev

    public FactionPlayerHandler(EntityPlayer player) {
        this.player = player;
    }

    public static FactionPlayerHandler get(EntityPlayer player) {
        if (player == null) return null;
        return (FactionPlayerHandler) player.getExtendedProperties(PROPERTY_ID);
    }

    public static void register(EntityPlayer player) {
        if (player.getExtendedProperties(PROPERTY_ID) == null) {
            player.registerExtendedProperties(PROPERTY_ID, new FactionPlayerHandler(player));
        }
    }

    @Override
    public void init(Entity entity, World world) {
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("faction", currentFaction != null ? currentFaction.getId() : "neutral");
        tag.setInteger("level", currentLevel);
        tag.setInteger("lordLevel", lordLevel);
        tag.setBoolean("disguised", disguised);
        tag.setString("titleGender", titleGender.getName());

        // Refinements
        for (int i = 0; i < 3; i++) {
            if (refinements[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                refinements[i].writeToNBT(itemTag);
                tag.setTag("refinement_" + i, itemTag);
            }
        }

        // Appearance
        tag.setInteger("fangType", fangType);
        tag.setInteger("eyeType", eyeType);
        tag.setBoolean("glowingEyes", glowingEyes);
        tag.setBoolean("wings", wings);
        tag.setInteger("wingsType", wingsType);

        compound.setTag("NeorismFaction", tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey("NeorismFaction")) {
            NBTTagCompound tag = compound.getCompoundTag("NeorismFaction");
            this.currentFaction = ModFactions.getFaction(tag.getString("faction"));
            this.currentLevel = tag.getInteger("level");
            this.lordLevel = tag.getInteger("lordLevel");
            this.disguised = tag.getBoolean("disguised");
            if (tag.hasKey("titleGender")) {
                this.titleGender = de.teamlapen.faction.api.factions.IPlayableFaction.TitleGender.byName(tag.getString("titleGender"));
            }

            for (int i = 0; i < 3; i++) {
                if (tag.hasKey("refinement_" + i)) {
                    this.refinements[i] = net.minecraft.item.ItemStack.loadItemStackFromNBT(tag.getCompoundTag("refinement_" + i));
                } else {
                    this.refinements[i] = null;
                }
            }

            this.fangType = tag.getInteger("fangType");
            this.eyeType = tag.getInteger("eyeType");
            this.glowingEyes = tag.getBoolean("glowingEyes");
            this.wings = tag.getBoolean("wings");
            this.wingsType = tag.getInteger("wingsType");
        }
    }

    public void copyFrom(FactionPlayerHandler oldHandler) {
        if (oldHandler != null) {
            this.currentFaction = oldHandler.currentFaction;
            this.currentLevel = oldHandler.currentLevel;
            this.lordLevel = oldHandler.lordLevel;
            this.disguised = oldHandler.disguised;
            this.titleGender = oldHandler.titleGender;
            for (int i = 0; i < 3; i++) {
                this.refinements[i] = oldHandler.refinements[i] != null ? oldHandler.refinements[i].copy() : null;
            }
            this.fangType = oldHandler.fangType;
            this.eyeType = oldHandler.eyeType;
            this.glowingEyes = oldHandler.glowingEyes;
            this.wings = oldHandler.wings;
            this.wingsType = oldHandler.wingsType;
        }
    }

    public net.minecraft.item.ItemStack getRefinement(int slot) {
        if (slot >= 0 && slot < 3) return refinements[slot];
        return null;
    }

    public void setRefinement(int slot, net.minecraft.item.ItemStack stack) {
        if (slot >= 0 && slot < 3) {
            refinements[slot] = stack;
        }
    }

    public int getFangType() { return fangType; }
    public void setFangType(int fangType) { this.fangType = Math.max(0, Math.min(6, fangType)); }

    public int getEyeType() { return eyeType; }
    public void setEyeType(int eyeType) { this.eyeType = Math.max(0, Math.min(18, eyeType)); }

    public boolean hasGlowingEyes() { return glowingEyes; }
    public void setGlowingEyes(boolean glowingEyes) { this.glowingEyes = glowingEyes; }

    public boolean hasWings() { return wings; }
    public void setWings(boolean wings) { this.wings = wings; }

    public int getWingsType() { return wingsType; }
    public void setWingsType(int wingsType) { this.wingsType = Math.max(0, Math.min(2, wingsType)); }

    public IPlayableFaction getFaction() {
        return currentFaction;
    }

    public IPlayableFaction getCurrentFaction() {
        return currentFaction;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    @Override
    public de.teamlapen.faction.api.factions.IFactionPlayer getFactionPlayer() {
        return this;
    }

    public int getLordLevel() {
        return lordLevel;
    }

    public boolean isDisguised() {
        return disguised;
    }

    public boolean isVampire() {
        return ModFactions.VAMPIRE.equals(currentFaction);
    }

    public boolean isHunter() {
        return ModFactions.HUNTER.equals(currentFaction);
    }

    public boolean isNeutral() {
        return currentFaction == null || currentFaction.isNeutral();
    }

    public boolean isInFaction(IFaction faction) {
        if (faction == null) return false;
        return currentFaction != null && currentFaction.equals(faction);
    }

    public boolean isInFaction(de.teamlapen.faction.api.factions.IFaction faction) {
        if (faction == null) return false;
        return currentFaction != null && currentFaction.getId().equalsIgnoreCase(faction.getName());
    }

    @Override
    public boolean canJoin(de.teamlapen.faction.api.factions.IPlayableFaction faction) {
        if (faction instanceof IPlayableFaction) {
            return canJoin((IPlayableFaction) faction);
        }
        return isNeutral();
    }

    public boolean canJoin(IPlayableFaction faction) {
        if (faction == null || faction.isNeutral()) return true;
        // Players can only join a faction if currently neutral
        return currentFaction.isNeutral();
    }

    public boolean canLeaveFaction() {
        return !currentFaction.isNeutral();
    }

    @Override
    public void setFaction(de.teamlapen.faction.api.factions.IPlayableFaction faction, int level) {
        if (faction instanceof IPlayableFaction) {
            setFaction((IPlayableFaction) faction, level);
        } else {
            setFaction(ModFactions.NEUTRAL, level);
        }
    }

    public void setFaction(IPlayableFaction faction, int level) {
        int oldLevel = this.currentLevel;
        IPlayableFaction oldFaction = this.currentFaction;
        this.currentFaction = faction != null ? faction : ModFactions.NEUTRAL;
        this.currentLevel = level;

        com.marrybye.neorism.common.skills.SkillHandler sh =
            com.marrybye.neorism.common.skills.SkillHandler.get(player);
        if (oldFaction != null && !oldFaction.equals(this.currentFaction) && sh != null) {
            sh.resetSkills();
            sh.setPrimaryAction("");
            sh.setSecondaryAction("");
            sh.setSkillPoints(this.currentFaction.isNeutral() ? 0 : this.currentLevel);
        } else if (this.currentLevel > oldLevel && sh != null) {
            sh.addSkillPoints(this.currentLevel - oldLevel);
        }

        if (this.currentFaction.isNeutral()) {
            this.lordLevel = 0;
            this.disguised = false;
        }
        ModLogger.leveling("Player %s faction changed to %s (level %d)",
            player.getCommandSenderName(), this.currentFaction.getId(), this.currentLevel);
        com.marrybye.neorism.common.vampire.VampirePlayer vp =
            com.marrybye.neorism.common.vampire.VampirePlayer.get(player);
        if (vp != null && isVampire()) {
            vp.onLevelChanged(this.currentLevel);
        } else if (!isVampire()) {
            com.marrybye.neorism.common.vampire.LevelAttributeModifier.removeVampireModifiers(player);
        }
        com.marrybye.neorism.common.hunter.HunterPlayer hp =
            com.marrybye.neorism.common.hunter.HunterPlayer.get(player);
        if (hp != null && isHunter()) {
            hp.onLevelChanged(this.currentLevel);
        } else if (!isHunter()) {
            com.marrybye.neorism.common.hunter.HunterPlayer.removeHunterModifiers(player);
        }
        sync();
    }

    public void setLevel(int level) {
        int max = currentFaction.getMaxLevel();
        int oldLevel = this.currentLevel;
        this.currentLevel = Math.max(0, Math.min(level, max));
        if (this.currentLevel > oldLevel) {
            com.marrybye.neorism.common.skills.SkillHandler sh =
                com.marrybye.neorism.common.skills.SkillHandler.get(player);
            if (sh != null) {
                sh.addSkillPoints(this.currentLevel - oldLevel);
            }
        }
        ModLogger.leveling("Player %s level updated to %d (faction: %s)",
            player.getCommandSenderName(), this.currentLevel, this.currentFaction.getId());
        com.marrybye.neorism.common.vampire.VampirePlayer vp =
            com.marrybye.neorism.common.vampire.VampirePlayer.get(player);
        if (vp != null && isVampire()) {
            vp.onLevelChanged(this.currentLevel);
        }
        com.marrybye.neorism.common.hunter.HunterPlayer hp =
            com.marrybye.neorism.common.hunter.HunterPlayer.get(player);
        if (hp != null && isHunter()) {
            hp.onLevelChanged(this.currentLevel);
        }
        sync();
    }

    public void setLordLevel(int lordLevel) {
        int max = getMaxLordLevel();
        this.lordLevel = Math.max(0, Math.min(lordLevel, max));
        com.marrybye.neorism.common.util.ScoreboardUtil.updateScore(player, com.marrybye.neorism.common.util.ScoreboardUtil.CRITERIA_LORD, this.lordLevel);
        player.refreshDisplayName();
        ModLogger.leveling("Player %s lord rank updated to %d (faction: %s)",
            player.getCommandSenderName(), this.lordLevel, this.currentFaction.getId());
        sync();
    }

    public void setDisguised(boolean disguised) {
        this.disguised = disguised;
        ModLogger.debug("Player %s disguise state: %b", player.getCommandSenderName(), disguised);
        player.refreshDisplayName();
        sync();
    }

    public void setClientData(IPlayableFaction faction, int level, int lordLevel, boolean disguised) {
        setClientData(faction, level, lordLevel, disguised, (byte) 0);
    }

    public void setClientData(IPlayableFaction faction, int level, int lordLevel, boolean disguised, byte genderOrdinal) {
        this.currentFaction = faction;
        this.currentLevel = level;
        this.lordLevel = lordLevel;
        this.disguised = disguised;
        de.teamlapen.faction.api.factions.IPlayableFaction.TitleGender[] genders =
            de.teamlapen.faction.api.factions.IPlayableFaction.TitleGender.values();
        if (genderOrdinal >= 0 && genderOrdinal < genders.length) {
            this.titleGender = genders[genderOrdinal];
        }
        player.refreshDisplayName();
    }

    public void sync() {
        if (!player.worldObj.isRemote && player instanceof EntityPlayerMP) {
            SyncFactionPacket packet = new SyncFactionPacket(
                player.getEntityId(),
                currentFaction.getId(),
                currentLevel,
                lordLevel,
                disguised,
                (byte) this.titleGender.ordinal()
            );
            // Send to the player
            PacketHandler.sendTo(packet, (EntityPlayerMP) player);
            // Send to tracking players nearby
            PacketHandler.sendToAllAround(packet, new NetworkRegistry.TargetPoint(
                player.dimension, player.posX, player.posY, player.posZ, 64.0D
            ));
            AppearancePacket appPacket = new AppearancePacket(
                player.getEntityId(),
                fangType,
                eyeType,
                glowingEyes,
                wings,
                wingsType
            );
            PacketHandler.sendTo(appPacket, (EntityPlayerMP) player);
            PacketHandler.sendToAllAround(appPacket, new NetworkRegistry.TargetPoint(
                player.dimension, player.posX, player.posY, player.posZ, 64.0D
            ));
        }
    }

    @Override
    public de.teamlapen.faction.api.factions.IPlayableFaction.TitleGender titleGender() {
        return this.titleGender;
    }

    @Override
    public boolean setTitleGender(de.teamlapen.faction.api.factions.IPlayableFaction.TitleGender gender) {
        this.titleGender = gender != null ? gender : de.teamlapen.faction.api.factions.IPlayableFaction.TitleGender.UNKNOWN;
        player.refreshDisplayName();
        sync();
        return true;
    }

    @Override
    public boolean setTitleGender(boolean female) {
        return setTitleGender(female ? de.teamlapen.faction.api.factions.IPlayableFaction.TitleGender.FEMALE
                                     : de.teamlapen.faction.api.factions.IPlayableFaction.TitleGender.MALE);
    }

    @Override
    public int getMaxLordLevel() {
        return currentFaction != null ? currentFaction.getMaxLordLevel() : 0;
    }

    @Override
    public int getMaxMinions() {
        return getLordLevel() * 2;
    }

    @Override
    public net.minecraft.util.IChatComponent getLordTitle() {
        if (getLordLevel() <= 0 || currentFaction == null) return null;
        if (isVampire()) {
            return com.marrybye.neorism.common.util.LordTitles.VAMPIRE.getLordTitle(getLordLevel(), titleGender());
        } else if (isHunter()) {
            return com.marrybye.neorism.common.util.LordTitles.HUNTER.getLordTitle(getLordLevel(), titleGender());
        }
        return null;
    }

    @Override
    public net.minecraft.util.IChatComponent getLordTitleShort() {
        if (getLordLevel() <= 0 || currentFaction == null) return null;
        if (isVampire()) {
            return com.marrybye.neorism.common.util.LordTitles.VAMPIRE.getShort(getLordLevel(), titleGender());
        } else if (isHunter()) {
            return com.marrybye.neorism.common.util.LordTitles.HUNTER.getShort(getLordLevel(), titleGender());
        }
        return null;
    }

    public com.marrybye.neorism.common.entity.minion.PlayerMinionController getMinionController() {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) return null;
        com.marrybye.neorism.common.entity.minion.MinionWorldData wd =
            com.marrybye.neorism.common.entity.minion.MinionWorldData.get(player.worldObj);
        if (wd != null) {
            com.marrybye.neorism.common.entity.minion.PlayerMinionController c =
                wd.getOrCreateController(player.getUniqueID());
            c.setMaxMinions(getMaxMinions());
            return c;
        }
        return null;
    }

    @Override
    public void updateMinionAttributes(boolean increasedStats) {
        com.marrybye.neorism.common.entity.minion.PlayerMinionController c = getMinionController();
        if (c != null) {
            c.setMaxMinions(getMaxMinions());
        }
    }

    @Override
    public void resetLordTasks() {
        // Reset unique lord progression tasks
    }

    @Override
    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public EntityLivingBase asEntity() {
        return player;
    }

    @Override
    public int getLevel() {
        return currentLevel;
    }

    @Override
    public int getMaxLevel() {
        return currentFaction.getMaxLevel();
    }

    @Override
    public void leaveFaction() {
        setFaction(ModFactions.NEUTRAL, 0);
    }

    @Override
    public String getLevelDisplay() {
        return StatCollector.translateToLocal(currentFaction.getUnlocalizedName()) + " " + currentLevel;
    }
}
