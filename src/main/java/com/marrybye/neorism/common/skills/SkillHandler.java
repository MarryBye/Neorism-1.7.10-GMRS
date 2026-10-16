package com.marrybye.neorism.common.skills;

import java.util.HashSet;
import java.util.Set;

import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.network.packets.SyncSkillsPacket;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants;

/**
 * Extended entity property managing player skill points, unlocked skill nodes, and action bindings.
 */
public class SkillHandler implements IExtendedEntityProperties {

    public static final String PROPERTY_ID = "neorism:skill_handler";

    private final EntityPlayer player;
    private int skillPoints = 0;
    private final Set<String> unlockedSkills = new HashSet<String>();
    private String primaryAction = "";
    private String secondaryAction = "";

    public SkillHandler(EntityPlayer player) {
        this.player = player;
    }

    public static SkillHandler get(EntityPlayer player) {
        if (player == null) return null;
        return (SkillHandler) player.getExtendedProperties(PROPERTY_ID);
    }

    public static void register(EntityPlayer player) {
        if (player != null && player.getExtendedProperties(PROPERTY_ID) == null) {
            player.registerExtendedProperties(PROPERTY_ID, new SkillHandler(player));
        }
    }

    @Override
    public void init(Entity entity, World world) {
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("skillPoints", skillPoints);
        tag.setString("primaryAction", primaryAction != null ? primaryAction : "");
        tag.setString("secondaryAction", secondaryAction != null ? secondaryAction : "");

        NBTTagList list = new NBTTagList();
        for (String skillId : unlockedSkills) {
            list.appendTag(new NBTTagString(skillId));
        }
        tag.setTag("unlockedSkills", list);
        compound.setTag("NeorismSkills", tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey("NeorismSkills")) {
            NBTTagCompound tag = compound.getCompoundTag("NeorismSkills");
            this.skillPoints = tag.getInteger("skillPoints");
            this.primaryAction = tag.getString("primaryAction");
            this.secondaryAction = tag.getString("secondaryAction");

            this.unlockedSkills.clear();
            NBTTagList list = tag.getTagList("unlockedSkills", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.tagCount(); i++) {
                this.unlockedSkills.add(list.getStringTagAt(i));
            }
        }
    }

    public void copyFrom(SkillHandler old) {
        if (old != null) {
            this.skillPoints = old.skillPoints;
            this.primaryAction = old.primaryAction;
            this.secondaryAction = old.secondaryAction;
            this.unlockedSkills.clear();
            this.unlockedSkills.addAll(old.unlockedSkills);
        }
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void setSkillPoints(int points) {
        this.skillPoints = Math.max(0, points);
        sync();
    }

    public void addSkillPoints(int points) {
        if (points > 0) {
            this.skillPoints += points;
            sync();
        }
    }

    public boolean isSkillUnlocked(String skillId) {
        return unlockedSkills.contains(skillId);
    }

    public boolean isActionUnlocked(String actionId) {
        // By default, base vampire actions become available as level increases or through skills
        if ("night_vision".equals(actionId)) return true;
        if ("regen".equals(actionId)) return Helper.getLevel(player) >= 2 || isSkillUnlocked("vampire_regen");
        if ("bat".equals(actionId)) return isSkillUnlocked("bat_form") || Helper.getLevel(player) >= 6;
        if ("vampire_rage".equals(actionId)) return isSkillUnlocked("vampire_rage") || Helper.getLevel(player) >= 4;
        if ("teleport".equals(actionId)) return isSkillUnlocked("teleport") || Helper.getLevel(player) >= 8;
        if ("freeze".equals(actionId)) return isSkillUnlocked("freeze") || Helper.getLevel(player) >= 10;
        if ("dark_blood_projectile".equals(actionId)) return isSkillUnlocked("dark_blood_projectile") || Helper.getLevel(player) >= 5;
        if ("sunscreen".equals(actionId)) return isSkillUnlocked("sunscreen") || Helper.getLevel(player) >= 7;
        if ("vampire_invisibility".equals(actionId)) return isSkillUnlocked("invisibility") || Helper.getLevel(player) >= 9;
        if ("summon_bats".equals(actionId)) return isSkillUnlocked("summon_bats") || Helper.getLevel(player) >= 11;
        if ("awareness_hunter".equals(actionId)) return Helper.isHunter(player) && (isSkillUnlocked("hunter_awareness") || Helper.getLevel(player) >= 2);
        if ("potion_resistance_hunter".equals(actionId)) return Helper.isHunter(player) && (isSkillUnlocked("potion_resistance") || Helper.getLevel(player) >= 4);

        return unlockedSkills.contains(actionId);
    }

    public boolean unlockSkill(String skillId) {
        if (skillPoints <= 0 || unlockedSkills.contains(skillId)) return false;

        unlockedSkills.add(skillId);
        skillPoints--;
        ModLogger.leveling("Player %s unlocked skill '%s' (remaining points: %d)",
            player.getCommandSenderName(), skillId, skillPoints);
        sync();
        return true;
    }

    public void resetSkills() {
        int refund = unlockedSkills.size();
        unlockedSkills.clear();
        skillPoints += refund;
        ModLogger.leveling("Player %s reset skills (refunded %d points, total: %d)",
            player.getCommandSenderName(), refund, skillPoints);
        sync();
    }

    public String getPrimaryAction() {
        if (primaryAction == null || primaryAction.isEmpty()) {
            return Helper.isVampire(player) ? "vampire_rage" : (Helper.isHunter(player) ? "awareness_hunter" : "");
        }
        return primaryAction;
    }

    public void setPrimaryAction(String action) {
        this.primaryAction = action != null ? action : "";
        sync();
    }

    public String getSecondaryAction() {
        if (secondaryAction == null || secondaryAction.isEmpty()) {
            return Helper.isVampire(player) ? "bat" : (Helper.isHunter(player) ? "potion_resistance_hunter" : "");
        }
        return secondaryAction;
    }

    public void setSecondaryAction(String action) {
        this.secondaryAction = action != null ? action : "";
        sync();
    }

    public Set<String> getUnlockedSkills() {
        return unlockedSkills;
    }

    public void setClientData(int points, String primary, String secondary, Set<String> skills) {
        this.skillPoints = points;
        this.primaryAction = primary;
        this.secondaryAction = secondary;
        this.unlockedSkills.clear();
        if (skills != null) {
            this.unlockedSkills.addAll(skills);
        }
    }

    public void sync() {
        if (!player.worldObj.isRemote && player instanceof EntityPlayerMP) {
            SyncSkillsPacket packet = new SyncSkillsPacket(
                skillPoints,
                getPrimaryAction(),
                getSecondaryAction(),
                unlockedSkills
            );
            PacketHandler.sendTo(packet, (EntityPlayerMP) player);
        }
    }
}
