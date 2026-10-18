package com.marrybye.neorism.common.entity.vampire;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.projectile.EntityDarkBloodProjectile;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

/**
 * Lord Dracula - Ancient Vampire Lord and 3-Phase Legendary Boss Encounter.
 */
public class EntityDracula extends EntityVampireBase implements IBossDisplayData {

    private static final int DATA_PHASE = 21;

    private int phase = 1;
    private int phaseCooldown = 0;
    private boolean spawnedPhase2Bats = false;

    public EntityDracula(World world) {
        super(world);
        this.setSize(0.8F, 2.4F);
        this.experienceValue = 100;
        this.setCustomNameTag("Lord Dracula");
        this.setAlwaysRenderNameTag(true);
        this.setIgnoringSundamage(true);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(DATA_PHASE, Byte.valueOf((byte) 1));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(500.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(12.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.8D);
    }

    @Override
    public IChatComponent func_145748_c_() {
        return new ChatComponentText(this.getCommandSenderName() + (getPhase() == 3 ? " (Blood Rage)" : ""));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Update Boss Health Bar for players
        BossStatus.setBossStatus(this, true);

        if (!this.worldObj.isRemote && this.isEntityAlive()) {
            float hpRatio = this.getHealth() / this.getMaxHealth();

            // Phase Transitions
            if (hpRatio <= 0.33F && phase < 3) {
                transitionToPhase(3);
            } else if (hpRatio <= 0.66F && phase < 2) {
                transitionToPhase(2);
            }

            if (phaseCooldown > 0) {
                phaseCooldown--;
            }

            EntityLivingBase target = this.getAttackTarget();
            if (target != null && target.isEntityAlive()) {
                double distSq = this.getDistanceSqToEntity(target);

                if (phase == 2) {
                    // Phase 2: Barrage of dark blood projectiles
                    if (phaseCooldown <= 0 && distSq > 9.0D && distSq < 400.0D && this.canEntityBeSeen(target)) {
                        fireBloodBarrage(target);
                        phaseCooldown = 35;
                    }
                } else if (phase == 3) {
                    // Phase 3: Enraged particles and periodic lightning strikes
                    if (this.ticksExisted % 10 == 0) {
                        this.worldObj.spawnParticle("flame", this.posX + (this.rand.nextDouble() - 0.5D) * 1.5D,
                            this.posY + this.rand.nextDouble() * 2.0D,
                            this.posZ + (this.rand.nextDouble() - 0.5D) * 1.5D, 0, 0.05, 0);
                    }

                    if (phaseCooldown <= 0 && this.rand.nextInt(30) == 0) {
                        // Cosmetic strike on target
                        this.worldObj.addWeatherEffect(new EntityLightningBolt(this.worldObj, target.posX, target.posY, target.posZ));
                        phaseCooldown = 40;
                    }
                }
            }
        }
    }

    private void transitionToPhase(int newPhase) {
        this.phase = newPhase;
        this.dataWatcher.updateObject(DATA_PHASE, Byte.valueOf((byte) newPhase));
        ModLogger.combat("Lord Dracula transitioned to Phase %d!", newPhase);

        if (newPhase == 2 && !spawnedPhase2Bats) {
            spawnedPhase2Bats = true;
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.bat.takeoff", 2.0F, 0.5F);
            for (int i = 0; i < 4; ++i) {
                EntityBloodBat bat = new EntityBloodBat(this.worldObj);
                bat.setPosition(this.posX + (this.rand.nextDouble() - 0.5D) * 3.0D,
                                this.posY + 2.0D,
                                this.posZ + (this.rand.nextDouble() - 0.5D) * 3.0D);
                this.worldObj.spawnEntityInWorld(bat);
            }
        } else if (newPhase == 3) {
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(22.0D);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.42D);
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.enderdragon.growl", 2.0F, 0.8F);
        }
    }

    private void fireBloodBarrage(EntityLivingBase target) {
        for (int i = 0; i < 3; ++i) {
            EntityDarkBloodProjectile projectile = new EntityDarkBloodProjectile(this.worldObj, this, 7.0F);
            double dx = target.posX - this.posX;
            double dy = target.posY + (double) target.getEyeHeight() - 1.1D - projectile.posY;
            double dz = target.posZ - this.posZ;
            projectile.setThrowableHeading(dx, dy, dz, 1.6F, 4.0F);
            this.worldObj.spawnEntityInWorld(projectile);
        }
        this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 1.0F, 1.2F);
        this.swingItem();
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean flag = super.attackEntityAsMob(target);
        if (flag) {
            // Lifesteal
            float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
            this.heal(damage * (getPhase() == 3 ? 0.5F : 0.25F));
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.eat", 1.0F, 0.7F);
        }
        return flag;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        // Phase 1 parry mechanic: takes 40% reduced damage
        if (getPhase() == 1 && !source.isUnblockable()) {
            amount *= 0.6F;
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.break", 0.8F, 1.5F);
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        // Guaranteed Sovereign Blood
        this.dropItem(ModItems.sovereignBlood, 1);

        // Ancient Vampire Grimoire (Tier 9)
        this.entityDropItem(new ItemStack(ModItems.vampireBook, 1, 8), 0.0F);

        // Ultimate Heart Striker sword
        this.entityDropItem(new ItemStack(ModItems.heartStriker, 1, 2), 0.0F);

        // Pure blood and fangs
        this.entityDropItem(new ItemStack(ModItems.pureBlood, 3, 4), 0.0F);
        this.dropItem(ModItems.vampireFang, 6 + this.rand.nextInt(6));

        ModLogger.combat("Lord Dracula defeated! Dropped Sovereign Blood, Tier 9 Book, and Ultimate Heart Striker.");
    }

    public int getPhase() {
        return this.worldObj != null && this.worldObj.isRemote
            ? this.dataWatcher.getWatchableObjectByte(DATA_PHASE)
            : this.phase;
    }

    @Override
    public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("Phase", this.phase);
        tag.setBoolean("SpawnedPhase2Bats", this.spawnedPhase2Bats);
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey("Phase")) {
            this.phase = Math.max(1, Math.min(3, tag.getInteger("Phase")));
            this.dataWatcher.updateObject(DATA_PHASE, Byte.valueOf((byte) this.phase));
        }
        this.spawnedPhase2Bats = tag.getBoolean("SpawnedPhase2Bats");
    }

    @Override
    protected String getLivingSound() {
        return com.marrybye.neorism.common.core.ModSounds.DRACULA_AMBIENT;
    }

    @Override
    protected String getHurtSound() {
        return com.marrybye.neorism.common.core.ModSounds.DRACULA_HURT;
    }

    @Override
    protected String getDeathSound() {
        return com.marrybye.neorism.common.core.ModSounds.DRACULA_DEATH;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }
}
