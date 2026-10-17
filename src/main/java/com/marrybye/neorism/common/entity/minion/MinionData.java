package com.marrybye.neorism.common.entity.minion;

import com.marrybye.neorism.common.entity.minion.tasks.ModMinionTasks;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Persistent data model for a minion.
 * Minion entities in the world are temporary shells; all stats, inventory, and tasks
 * are preserved here.
 */
public class MinionData {

    private String name = "Minion";
    private float health = 20.0F;
    private float maxHealth = 20.0F;
    private int level = 1;
    private boolean taskLocked = false;
    private int skinType = 0;

    // Stat levels (0 to 5)
    private int statHealth = 0;
    private int statDamage = 0;
    private int statSpeed = 0;
    private int statInventory = 0; // 0: 9 slots, 1: 12 slots, 2: 15 slots

    private final MinionInventory inventory = new MinionInventory(9);
    private IMinionTask.IMinionTaskDesc currentTaskDesc;

    public MinionData() {
        this("Minion");
    }

    public MinionData(String name) {
        this.name = name;
        this.currentTaskDesc = ModMinionTasks.FOLLOW_LORD.activateTask(null, null, this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name != null && !name.trim().isEmpty()) ? name.trim() : "Minion";
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = Math.max(0, Math.min(health, getMaxHealth()));
    }

    public float getMaxHealth() {
        return 20.0F + (statHealth * 4.0F); // up to 40 HP
    }

    public float getAttackDamage() {
        return 4.0F + (statDamage * 1.5F); // up to 11.5 damage
    }

    public double getMovementSpeedModifier() {
        return 0.28D + (statSpeed * 0.02D); // up to 0.38
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public boolean isTaskLocked() {
        return taskLocked;
    }

    public void setTaskLocked(boolean taskLocked) {
        this.taskLocked = taskLocked;
    }

    public int getSkinType() {
        return skinType;
    }

    public void setSkinType(int skinType) {
        this.skinType = Math.max(0, skinType);
    }

    public MinionInventory getInventory() {
        return inventory;
    }

    public IMinionTask.IMinionTaskDesc getCurrentTaskDesc() {
        if (currentTaskDesc == null) {
            currentTaskDesc = ModMinionTasks.FOLLOW_LORD.activateTask(null, null, this);
        }
        return currentTaskDesc;
    }

    public void setCurrentTaskDesc(IMinionTask.IMinionTaskDesc taskDesc) {
        if (this.currentTaskDesc != null && this.currentTaskDesc.getTask() != null) {
            this.currentTaskDesc.getTask().deactivateTask(this.currentTaskDesc);
        }
        this.currentTaskDesc = taskDesc;
    }

    public int getStatHealth() {
        return statHealth;
    }

    public int getStatDamage() {
        return statDamage;
    }

    public int getStatSpeed() {
        return statSpeed;
    }

    public int getStatInventory() {
        return statInventory;
    }

    public int getSpentSkillPoints() {
        return statHealth + statDamage + statSpeed + statInventory;
    }

    public int getRemainingSkillPoints() {
        // 2 skill points per minion level
        int totalPoints = (level - 1) * 2;
        return Math.max(0, totalPoints - getSpentSkillPoints());
    }

    public boolean upgradeStat(String statName) {
        if (getRemainingSkillPoints() <= 0) return false;
        if ("health".equalsIgnoreCase(statName) && statHealth < 5) {
            statHealth++;
            return true;
        } else if ("damage".equalsIgnoreCase(statName) && statDamage < 5) {
            statDamage++;
            return true;
        } else if ("speed".equalsIgnoreCase(statName) && statSpeed < 5) {
            statSpeed++;
            return true;
        } else if ("inventory".equalsIgnoreCase(statName) && statInventory < 2) {
            statInventory++;
            inventory.setAvailableSize(9 + statInventory * 3);
            return true;
        }
        return false;
    }

    public void resetStats() {
        statHealth = 0;
        statDamage = 0;
        statSpeed = 0;
        statInventory = 0;
        inventory.setAvailableSize(9);
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setString("Name", name);
        tag.setFloat("Health", health);
        tag.setInteger("Level", level);
        tag.setBoolean("TaskLocked", taskLocked);
        tag.setInteger("SkinType", skinType);
        tag.setInteger("StatHealth", statHealth);
        tag.setInteger("StatDamage", statDamage);
        tag.setInteger("StatSpeed", statSpeed);
        tag.setInteger("StatInventory", statInventory);

        NBTTagCompound invTag = new NBTTagCompound();
        inventory.writeToNBT(invTag);
        tag.setTag("Inventory", invTag);

        if (currentTaskDesc != null) {
            NBTTagCompound taskTag = new NBTTagCompound();
            currentTaskDesc.writeToNBT(taskTag);
            tag.setTag("Task", taskTag);
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("Name")) this.name = tag.getString("Name");
        if (tag.hasKey("Health")) this.health = tag.getFloat("Health");
        if (tag.hasKey("Level")) this.level = tag.getInteger("Level");
        if (tag.hasKey("TaskLocked")) this.taskLocked = tag.getBoolean("TaskLocked");
        if (tag.hasKey("SkinType")) this.skinType = tag.getInteger("SkinType");
        if (tag.hasKey("StatHealth")) this.statHealth = tag.getInteger("StatHealth");
        if (tag.hasKey("StatDamage")) this.statDamage = tag.getInteger("StatDamage");
        if (tag.hasKey("StatSpeed")) this.statSpeed = tag.getInteger("StatSpeed");
        if (tag.hasKey("StatInventory")) {
            this.statInventory = tag.getInteger("StatInventory");
            this.inventory.setAvailableSize(9 + statInventory * 3);
        }

        if (tag.hasKey("Inventory")) {
            inventory.readFromNBT(tag.getCompoundTag("Inventory"));
        }

        if (tag.hasKey("Task")) {
            NBTTagCompound taskTag = tag.getCompoundTag("Task");
            String taskId = taskTag.getString("Task");
            IMinionTask task = ModMinionTasks.getTask(taskId);
            if (task != null) {
                this.currentTaskDesc = task.activateTask(null, null, this);
                if (this.currentTaskDesc != null) {
                    this.currentTaskDesc.readFromNBT(taskTag);
                }
            }
        }
        if (this.currentTaskDesc == null) {
            this.currentTaskDesc = ModMinionTasks.FOLLOW_LORD.activateTask(null, null, this);
        }
    }
}
