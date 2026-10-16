package com.marrybye.neorism.common.entity.vampire;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.entity.ai.EntityAIAttackFaction;
import com.marrybye.neorism.common.entity.ai.EntityAIBiteLiving;
import com.marrybye.neorism.common.entity.ai.EntityAISunFlee;
import com.marrybye.neorism.common.tileentity.TileEntityFogDiffuser;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Base class for all vampire mobs in Neorism.
 * Implements sunlight burning, dark vision, blood thirst, and faction hostility towards hunters.
 */
public abstract class EntityVampireBase extends EntityCreature implements IVampireEntity, IMob {

    private boolean ignoringSundamage = false;
    private int bloodLevel = 20;
    private int maxBloodLevel = 20;

    public EntityVampireBase(World world) {
        super(world);
        this.experienceValue = 5;
        this.setupAI();
    }

    protected void setupAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAISunFlee(this, 1.25D));
        this.tasks.addTask(2, new EntityAIBiteLiving(this, 1.15D));
        this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.1D, false));
        this.tasks.addTask(4, new EntityAIWander(this, 0.9D));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAIAttackFaction(this, ModFactions.VAMPIRE, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.28D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(24.0D);
    }

    @Override
    public IFaction getFaction() {
        return ModFactions.VAMPIRE;
    }

    @Override
    public EntityLivingBase asEntity() {
        return this;
    }

    @Override
    public boolean isGettingSundamage() {
        if (ignoringSundamage || this.worldObj.isRemote) return false;
        if (!this.worldObj.isDaytime()) return false;
        if (TileEntityFogDiffuser.isEntityProtected(this)) return false;
        if (this.isInWater()) return false;

        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY + 0.5D);
        int z = MathHelper.floor_double(this.posZ);

        return this.worldObj.canBlockSeeTheSky(x, y, z);
    }

    @Override
    public boolean isIgnoringSundamage() {
        return ignoringSundamage;
    }

    public void setIgnoringSundamage(boolean ignoring) {
        this.ignoringSundamage = ignoring;
    }

    @Override
    public boolean wantsBlood() {
        return this.getHealth() < this.getMaxHealth() || this.bloodLevel < (this.maxBloodLevel / 2);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.worldObj.isRemote && this.isEntityAlive()) {
            if (this.isGettingSundamage()) {
                this.setFire(8);
                this.attackEntityFrom(ModDamageSource.SUN_DAMAGE, 2.0F);
                if (this.ticksExisted % 40 == 0) {
                    ModLogger.combat("Vampire entity %s taking sundamage at (%.1f, %.1f, %.1f)",
                        this.getCommandSenderName(), this.posX, this.posY, this.posZ);
                }
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        boolean flag = target.attackEntityFrom(net.minecraft.util.DamageSource.causeMobDamage(this), damage);
        if (flag) {
            this.swingItem();
        }
        return flag;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setBoolean("IgnoringSundamage", this.ignoringSundamage);
        tag.setInteger("BloodLevel", this.bloodLevel);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.ignoringSundamage = tag.getBoolean("IgnoringSundamage");
        if (tag.hasKey("BloodLevel")) {
            this.bloodLevel = tag.getInteger("BloodLevel");
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        return isValidLightLevel() && super.getCanSpawnHere();
    }

    protected boolean isValidLightLevel() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);

        if (this.worldObj.getSavedLightValue(net.minecraft.world.EnumSkyBlock.Sky, x, y, z) > this.rand.nextInt(32)) {
            return false;
        }
        int light = this.worldObj.getBlockLightValue(x, y, z);
        return light <= 7;
    }
}
