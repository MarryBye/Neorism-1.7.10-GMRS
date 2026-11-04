package com.marrybye.neorism.common.entity.vampire;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.ai.EntityAIHurtByTargetFaction;
import com.marrybye.neorism.common.entity.projectile.EntityDarkBloodProjectile;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Vampire Baron miniboss found in ancient crypts and dark spruce groves.
 * Casts dark blood projectiles, scales across levels 0-4, and drops corresponding pure blood tiers.
 */
public class EntityVampireBaron extends EntityVampireBase {

    public static final int MAX_LEVEL = 4;
    private static final int WATCHER_LEVEL = 20;
    private static final int WATCHER_LADY = 21;
    private static final int WATCHER_ENRAGED = 22;

    private int castCooldown = 40;

    public EntityVampireBaron(World world) {
        super(world);
        this.setSize(0.7F, 2.1F);
        this.experienceValue = 35;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(WATCHER_LEVEL, Integer.valueOf(0));
        this.dataWatcher.addObject(WATCHER_LADY, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(WATCHER_ENRAGED, Byte.valueOf((byte) 0));
    }

    @Override
    protected void setupAI() {
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new com.marrybye.neorism.common.entity.ai.EntityAISunFlee(this, 1.25D));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.15D, false));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(5, new EntityAIWander(this, 0.7D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTargetFaction(this, com.marrybye.neorism.common.core.ModFactions.VAMPIRE));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, new IEntitySelector() {
            @Override
            public boolean isEntityApplicable(Entity entity) {
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    int pLevel = Helper.getLevel(player);
                    return (pLevel - 8) / 2.0F - getEntityLevel() <= 0;
                }
                return false;
            }
        }));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVampireBaron.class, 0, true));
    }

    public int getEntityLevel() {
        return MathHelper.clamp_int(this.dataWatcher.getWatchableObjectInt(WATCHER_LEVEL), 0, MAX_LEVEL);
    }

    public void setEntityLevel(int level) {
        int clamped = MathHelper.clamp_int(level, 0, MAX_LEVEL);
        this.dataWatcher.updateObject(WATCHER_LEVEL, Integer.valueOf(clamped));
        this.experienceValue = 20 + 5 * clamped;
        this.updateAttributesForLevel();
    }

    public boolean isLady() {
        return this.dataWatcher.getWatchableObjectByte(WATCHER_LADY) != 0;
    }

    public void setLady(boolean lady) {
        this.dataWatcher.updateObject(WATCHER_LADY, Byte.valueOf((byte) (lady ? 1 : 0)));
    }

    public boolean isEnraged() {
        return this.dataWatcher.getWatchableObjectByte(WATCHER_ENRAGED) != 0;
    }

    public void setEnraged(boolean enraged) {
        this.dataWatcher.updateObject(WATCHER_ENRAGED, Byte.valueOf((byte) (enraged ? 1 : 0)));
    }

    @Override
    public void setAttackTarget(EntityLivingBase target) {
        super.setAttackTarget(target);
        this.setEnraged(target != null);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(150.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.5D);
    }

    public void updateAttributesForLevel() {
        int lvl = getEntityLevel();
        double hp = 120.0D + lvl * 25.0D;
        double dmg = 7.0D + lvl * 2.0D;
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(hp);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(dmg);
        this.setHealth((float) hp);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean flag = super.attackEntityAsMob(target);
        if (flag && target instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) target;
            float tm = 1.0F;
            int mr = 1;
            if (living instanceof EntityPlayer) {
                float pld = (this.getEntityLevel() + 1) - Helper.getLevel((EntityPlayer) living) / 3.0F;
                tm = Math.max(1.0F, pld + 1.0F);
                mr = pld < 1.5F ? 1 : (pld < 3.0F ? 2 : 3);
            }
            if (living instanceof EntityVampireBaron) {
                living.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 40, 2));
            }
            living.addPotionEffect(new PotionEffect(Potion.weakness.id, (int) (200 * tm), this.rand.nextInt(mr)));
            living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, (int) (100 * tm), this.rand.nextInt(mr)));
        }
        return flag;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.worldObj.isRemote && this.isEntityAlive()) {
            EntityLivingBase target = this.getAttackTarget();

            if (target != null && target.isEntityAlive()) {
                double distSq = this.getDistanceSqToEntity(target);

                // Ranged Dark Blood Projectile attack (canonical BaronAIAttackRanged)
                if (--castCooldown <= 0 && distSq > 16.0D && distSq < 400.0D && this.canEntityBeSeen(target)) {
                    castDarkBlood(target);
                    if (this.rand.nextInt(9) == 0) {
                        this.addPotionEffect(new PotionEffect(Potion.invisibility.id, 60, 0));
                    }
                    castCooldown = 50 + this.rand.nextInt(20);
                }
            }
        }
    }

    private void castDarkBlood(EntityLivingBase target) {
        float damage = 6.0F + getEntityLevel() * 1.5F;
        EntityDarkBloodProjectile projectile = new EntityDarkBloodProjectile(this.worldObj, this, damage);
        double dx = target.posX - this.posX;
        double dy = target.posY + (double) target.getEyeHeight() - 1.1D - projectile.posY;
        double dz = target.posZ - this.posZ;
        projectile.setThrowableHeading(dx, dy, dz, 1.4F, 2.0F);

        this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 1.0F, 0.8F);
        this.worldObj.spawnEntityInWorld(projectile);
        this.swingItem();

        ModLogger.combat("Vampire Baron %s (level %d) cast Dark Blood Projectile at %s",
            this.getCommandSenderName(), getEntityLevel(), target.getCommandSenderName());
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        // Pure blood matching baron level (meta 0..4)
        int lvl = getEntityLevel();
        if (recentlyHit) {
            this.entityDropItem(new ItemStack(ModItems.pureBlood, 1, lvl), 0.0F);
        }

        // Fangs
        this.dropItem(ModItems.vampireFang, 2 + this.rand.nextInt(3 + looting));

        if (this.rand.nextInt(2) == 0) {
            this.dropItem(ModItems.bloodInfusedDiamond, 1);
        }

        if (this.rand.nextInt(3) == 0) {
            this.entityDropItem(new ItemStack(ModItems.vampireBook, 1, Math.min(5, 3 + lvl)), 0.0F);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("level", getEntityLevel());
        nbt.setBoolean("lady", isLady());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("level")) {
            setEntityLevel(nbt.getInteger("level"));
        }
        if (nbt.hasKey("lady")) {
            setLady(nbt.getBoolean("lady"));
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        if (!com.marrybye.neorism.common.world.gen.ModWorldGen.isVampireBiome(this.worldObj.getBiomeGenForCoords(x, z))) {
            return false;
        }
        return super.getCanSpawnHere();
    }

    @Override
    protected boolean canDespawn() {
        return !this.hasHome();
    }
}
