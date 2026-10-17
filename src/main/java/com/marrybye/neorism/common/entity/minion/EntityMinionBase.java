package com.marrybye.neorism.common.entity.minion;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.entity.minion.ai.DefendLordGoal;
import com.marrybye.neorism.common.entity.minion.ai.FollowLordGoal;
import com.marrybye.neorism.common.entity.minion.tasks.StayTask;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.IMinionInventory;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Base minion entity acting as a living shell for MinionData.
 */
public abstract class EntityMinionBase extends EntityCreature implements IMinionEntity {

    public static final int DW_LORD_UUID = 20;
    public static final int DW_SLOT = 21;
    public static final int DW_SKIN = 22;
    public static final int DW_TASK_LOCKED = 23;

    protected MinionData minionData;

    public EntityMinionBase(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
        this.getNavigator().setAvoidsWater(true);
        this.getNavigator().setBreakDoors(true);

        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(3, new FollowLordGoal(this, 1.25D));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.2D, false));
        this.tasks.addTask(5, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new DefendLordGoal(this));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, true));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(DW_LORD_UUID, "");
        this.dataWatcher.addObject(DW_SLOT, 0);
        this.dataWatcher.addObject(DW_SKIN, 0);
        this.dataWatcher.addObject(DW_TASK_LOCKED, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.28D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
    }

    public void bindMinion(UUID lordUUID, int slot, MinionData data) {
        this.minionData = data;
        this.dataWatcher.updateObject(DW_LORD_UUID, lordUUID != null ? lordUUID.toString() : "");
        this.dataWatcher.updateObject(DW_SLOT, slot);
        if (data != null) {
            this.dataWatcher.updateObject(DW_SKIN, data.getSkinType());
            this.dataWatcher.updateObject(DW_TASK_LOCKED, (byte) (data.isTaskLocked() ? 1 : 0));
            this.setCustomNameTag(data.getName());
            this.updateAttributesFromData();
        }
    }

    public void updateAttributesFromData() {
        if (minionData != null) {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(minionData.getMaxHealth());
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(minionData.getMovementSpeedModifier());
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(minionData.getAttackDamage());
            if (this.getHealth() > this.getMaxHealth()) {
                this.setHealth(this.getMaxHealth());
            }
        }
    }

    public MinionData getMinionData() {
        if (minionData == null && !worldObj.isRemote) {
            UUID lordId = getLordUUID();
            if (lordId != null) {
                MinionWorldData wd = MinionWorldData.get(worldObj);
                if (wd != null) {
                    PlayerMinionController c = wd.getController(lordId);
                    if (c != null) {
                        this.minionData = c.getMinionData(getMinionId());
                    }
                }
            }
        }
        return minionData;
    }

    @Override
    public UUID getLordUUID() {
        String uuidStr = this.dataWatcher.getWatchableObjectString(DW_LORD_UUID);
        if (uuidStr != null && !uuidStr.isEmpty()) {
            try {
                return UUID.fromString(uuidStr);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @Override
    public int getMinionId() {
        return this.dataWatcher.getWatchableObjectInt(DW_SLOT);
    }

    public int getSkinType() {
        return this.dataWatcher.getWatchableObjectInt(DW_SKIN);
    }

    @Override
    public boolean isTaskLocked() {
        return this.dataWatcher.getWatchableObjectByte(DW_TASK_LOCKED) == 1;
    }

    @Override
    public ILordPlayer getLord() {
        EntityPlayer p = getLordPlayerEntity();
        if (p != null) {
            return FactionPlayerHandler.get(p);
        }
        return null;
    }

    public EntityPlayer getLordPlayerEntity() {
        UUID id = getLordUUID();
        if (id != null) {
            return worldObj.func_152378_a(id);
        }
        return null;
    }

    public boolean isLord(EntityPlayer player) {
        if (player == null) return false;
        UUID id = getLordUUID();
        return id != null && id.equals(player.getUniqueID());
    }

    @Override
    public IMinionTask.IMinionTaskDesc getCurrentTask() {
        MinionData data = getMinionData();
        return data != null ? data.getCurrentTaskDesc() : null;
    }

    @Override
    public IMinionInventory getInventory() {
        MinionData data = getMinionData();
        return data != null ? data.getInventory() : null;
    }

    @Override
    public EntityLivingBase asEntity() {
        return this;
    }

    @Override
    public void recallMinion() {
        if (!worldObj.isRemote) {
            syncDataToController();
            for (int i = 0; i < 8; i++) {
                worldObj.spawnParticle("smoke", posX + (rand.nextDouble() - 0.5D) * 0.5D, posY + 1.0D, posZ + (rand.nextDouble() - 0.5D) * 0.5D, 0.0D, 0.1D, 0.0D);
            }
            this.setDead();
        }
    }

    public void syncDataToController() {
        if (minionData != null) {
            minionData.setHealth(getHealth());
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (isLord(player)) {
            if (!worldObj.isRemote) {
                syncDataToController();
                player.openGui(Neorism.instance, 100, worldObj, this.getEntityId(), 0, 0);
            }
            return true;
        }
        return super.interact(player);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        return target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (!worldObj.isRemote) {
            MinionData data = getMinionData();
            if (data != null) {
                data.setHealth(0.0F);
                // Drop items from minion inventory
                MinionInventory inv = data.getInventory();
                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (stack != null) {
                        this.entityDropItem(stack, 0.5F);
                        inv.setInventorySlotContents(i, null);
                    }
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        UUID lordId = getLordUUID();
        if (lordId != null) {
            tag.setString("LordUUID", lordId.toString());
        }
        tag.setInteger("MinionSlot", getMinionId());
        tag.setInteger("SkinType", getSkinType());
        tag.setBoolean("TaskLocked", isTaskLocked());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey("LordUUID")) {
            this.dataWatcher.updateObject(DW_LORD_UUID, tag.getString("LordUUID"));
        }
        this.dataWatcher.updateObject(DW_SLOT, tag.getInteger("MinionSlot"));
        this.dataWatcher.updateObject(DW_SKIN, tag.getInteger("SkinType"));
        this.dataWatcher.updateObject(DW_TASK_LOCKED, (byte) (tag.getBoolean("TaskLocked") ? 1 : 0));
        updateAttributesFromData();
    }

    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public abstract IFaction getFaction();
}
