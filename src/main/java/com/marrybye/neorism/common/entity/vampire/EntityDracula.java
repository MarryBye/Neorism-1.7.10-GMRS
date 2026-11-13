package com.marrybye.neorism.common.entity.vampire;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.entity.projectile.EntityDarkBloodProjectile;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.world.dimensions.velmorra.DraculaFightManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Lord Dracula - Ancient Vampire Lord and 3-Phase Legendary Boss Encounter in Velmorra.
 * Canonical port of TeamLapen's Dracula boss fight:
 * - Phase 1: 50 HP * scale, arrow reflection, vampire minion adds, 40% damage resistance.
 * - Phase 2: 100 HP * scale, blood bat swarms, dark blood barrages, 50% projectile resistance.
 * - Phase 3: 200 HP * scale, blood siphon channel, mist form upon taking heavy burst damage, 150% projectile vulnerability.
 * - Transitions: 100 ticks of invulnerability, health regeneration, and concussive shockwave knockback.
 */
public class EntityDracula extends EntityVampireBase implements IBossDisplayData {

    private static final int DATA_PHASE = 21;
    private static final int DATA_DORMANT = 22;

    private int phase = 1;
    private boolean dormant = false;
    private float fightScale = 1.0F;
    private int phaseCooldown = 0;

    // Transformation state
    private boolean isTransforming = false;
    private int transformTicks = 0;
    private int targetPhase = 1;

    // Mist form (Phase 3)
    private boolean mistForm = false;
    private int mistTicks = 0;
    private int mistCooldown = 0;

    // Damage tracking for burst threshold
    private static class DamageRecord {
        final long time;
        final float amount;
        DamageRecord(long time, float amount) {
            this.time = time;
            this.amount = amount;
        }
    }
    private final List<DamageRecord> recentDamage = new ArrayList<DamageRecord>();

    public EntityDracula(World world) {
        super(world);
        this.setSize(0.8F, 2.4F);
        this.experienceValue = 1000;
        this.setCustomNameTag("Lord Dracula");
        this.setAlwaysRenderNameTag(true);
        this.setIgnoringSundamage(true);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(DATA_PHASE, Byte.valueOf((byte) 1));
        this.dataWatcher.addObject(DATA_DORMANT, Byte.valueOf((byte) 0));
    }

    public boolean isDormant() {
        return this.worldObj != null && this.worldObj.isRemote
            ? this.dataWatcher.getWatchableObjectByte(DATA_DORMANT) != 0
            : this.dormant;
    }

    public void setDormant(boolean dormant) {
        this.dormant = dormant;
        this.dataWatcher.updateObject(DATA_DORMANT, Byte.valueOf((byte) (dormant ? 1 : 0)));
        if (dormant) {
            this.setAttackTarget(null);
            this.motionX = 0.0D;
            this.motionZ = 0.0D;
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
    }

    public void startFight(int playerCount) {
        this.setDormant(false);
        this.fightScale = 1.0F + 0.5F * (playerCount - 1);
        this.phase = 1;
        this.dataWatcher.updateObject(DATA_PHASE, Byte.valueOf((byte) 1));
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0D * this.fightScale);
        this.setHealth(this.getMaxHealth());
        ModLogger.combat("Dracula started fight scaled for %d players (scale: %.1f, HP: %.0f)",
            playerCount, this.fightScale, this.getMaxHealth());
    }

    @SideOnly(Side.CLIENT)
    private void updateClientBossStatus() {
        BossStatus.setBossStatus(this, true);
    }

    @Override
    public IChatComponent func_145748_c_() {
        String title = this.getCommandSenderName();
        if (isTransforming) {
            title += " (Awakening...)";
        } else if (mistForm) {
            title += " (Mist Form)";
        } else if (getPhase() == 3) {
            title += " (Blood Rage)";
        }
        return new ChatComponentText(title);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Update Boss Health Bar on client only once awakened
        if (this.worldObj.isRemote) {
            if (!isDormant()) {
                updateClientBossStatus();
            }
        }

        if (!this.worldObj.isRemote && this.isEntityAlive()) {
            if (this.dormant) {
                this.motionX = 0.0D;
                this.motionZ = 0.0D;
                this.setAttackTarget(null);
                EntityPlayer nearest = this.worldObj.getClosestPlayerToEntity(this, 16.0D);
                if (nearest != null) {
                    this.getLookHelper().setLookPositionWithEntity(nearest, 30.0F, 30.0F);
                }
                return;
            }
            if (isTransforming) {
                tickTransformation();
                return;
            }

            if (mistForm) {
                tickMistForm();
            }

            if (mistCooldown > 0) {
                mistCooldown--;
            }

            if (phaseCooldown > 0) {
                phaseCooldown--;
            }

            cleanRecentDamage();

            EntityLivingBase target = this.getAttackTarget();
            if (target != null && target.isEntityAlive()) {
                double distSq = this.getDistanceSqToEntity(target);

                if (phase == 1) {
                    // Periodic add summons in Phase 1
                    if (this.ticksExisted % 200 == 0 && this.rand.nextInt(3) == 0) {
                        summonVampireMinion();
                    }
                } else if (phase == 2) {
                    // Phase 2: Barrage of dark blood projectiles, Bat Swarm Dash & Backstab
                    if (phaseCooldown <= 0 && this.canEntityBeSeen(target)) {
                        if (distSq > 36.0D && this.rand.nextInt(3) == 0) {
                            backstabTeleport(target);
                            phaseCooldown = 40;
                        } else if (distSq < 100.0D && this.rand.nextInt(3) == 0) {
                            batSwarmDash(target);
                            phaseCooldown = 50;
                        } else if (distSq > 9.0D && distSq < 400.0D) {
                            fireBloodBarrage(target);
                            phaseCooldown = 35;
                        }
                    }
                } else if (phase == 3) {
                    // Phase 3: Blood Rage particles, Multi-target Blood Siphon & Ring Projectiles
                    if (this.ticksExisted % 10 == 0) {
                        this.worldObj.spawnParticle("flame",
                            this.posX + (this.rand.nextDouble() - 0.5D) * 1.5D,
                            this.posY + this.rand.nextDouble() * 2.0D,
                            this.posZ + (this.rand.nextDouble() - 0.5D) * 1.5D, 0, 0.05, 0);
                    }

                    // Passive regeneration in Phase 3 if injured
                    if (this.ticksExisted % 40 == 0 && this.getHealth() < this.getMaxHealth() * 0.8F) {
                        this.heal(3.0F);
                    }

                    if (phaseCooldown <= 0) {
                        if (this.rand.nextInt(3) == 0) {
                            fireRadialBloodRing();
                            phaseCooldown = 60;
                        } else if (distSq < 144.0D) {
                            channelBloodSiphon(target);
                            phaseCooldown = 45;
                        }
                    }
                }
            }
        }
    }

    private void tickTransformation() {
        transformTicks--;

        float percentage = (100 - transformTicks) / 100.0F;
        this.setHealth(Math.max(1.0F, this.getMaxHealth() * percentage));

        // Dark crimson ascension particles
        for (int i = 0; i < 3; i++) {
            double px = this.posX + (this.rand.nextDouble() - 0.5D) * 1.2D;
            double py = this.posY + this.rand.nextDouble() * 2.2D;
            double pz = this.posZ + (this.rand.nextDouble() - 0.5D) * 1.2D;
            this.worldObj.spawnParticle("reddust", px, py, pz, 0.5D, 0.0D, 0.0D);
            this.worldObj.spawnParticle("smoke", px, py, pz, 0.0D, 0.05D, 0.0D);
        }

        if (transformTicks <= 0) {
            finishTransformation();
        }
    }

    private void startTransformation(int nextPhase) {
        this.isTransforming = true;
        this.transformTicks = 100;
        this.targetPhase = nextPhase;

        double nextHp = (nextPhase == 2 ? 100.0D : 200.0D) * this.fightScale;
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(nextHp);

        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, ModSounds.DRACULA_TRANSFORM, 2.0F, 1.0F);

        // Broadcast transformation message to all players in dimension
        for (Object pObj : this.worldObj.playerEntities) {
            if (pObj instanceof EntityPlayer) {
                ((EntityPlayer) pObj).addChatMessage(new ChatComponentTranslation("message.vampirism.dracula.transformation"));
            }
        }

        ModLogger.combat("Lord Dracula begins transformation into Phase %d!", nextPhase);
    }

    private void finishTransformation() {
        this.isTransforming = false;
        this.phase = this.targetPhase;
        this.dataWatcher.updateObject(DATA_PHASE, Byte.valueOf((byte) this.phase));
        this.setHealth(this.getMaxHealth());

        // Concussive shockwave explosion
        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, ModSounds.DRACULA_TRANSFORM_END, 2.0F, 1.0F);
        this.worldObj.spawnParticle("hugeexplosion", this.posX, this.posY + 1.0D, this.posZ, 0, 0, 0);

        // Knockback nearby entities
        List<?> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(10.0D, 4.0D, 10.0D));
        for (Object obj : entities) {
            if (obj != this && obj instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) obj;
                double dx = living.posX - this.posX;
                double dz = living.posZ - this.posZ;
                living.knockBack(this, 1.8F, -dx, -dz);
            }
        }

        if (this.phase == 2) {
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(15.0D);
            // Spawn bat swarms
            for (int i = 0; i < 4; ++i) {
                EntityBloodBat bat = new EntityBloodBat(this.worldObj);
                bat.setPosition(this.posX + (this.rand.nextDouble() - 0.5D) * 3.0D,
                                this.posY + 2.0D,
                                this.posZ + (this.rand.nextDouble() - 0.5D) * 3.0D);
                this.worldObj.spawnEntityInWorld(bat);
            }
        } else if (this.phase == 3) {
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(22.0D);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.42D);
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.enderdragon.growl", 2.0F, 0.8F);
        }

        ModLogger.combat("Lord Dracula emerged in Phase %d with %.0f HP!", this.phase, this.getMaxHealth());
    }

    private void tickMistForm() {
        mistTicks--;

        for (int i = 0; i < 4; ++i) {
            this.worldObj.spawnParticle("largesmoke",
                this.posX + (this.rand.nextDouble() - 0.5D) * 1.5D,
                this.posY + this.rand.nextDouble() * 2.2D,
                this.posZ + (this.rand.nextDouble() - 0.5D) * 1.5D, 0, 0.05, 0);
        }

        if (mistTicks <= 0) {
            mistForm = false;
            ModLogger.combat("Lord Dracula exited Mist Form.");
        }
    }

    private void enterMistForm() {
        this.mistForm = true;
        this.mistTicks = 100; // 5 seconds
        this.mistCooldown = 600; // 30 seconds cooldown
        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.bat.takeoff", 2.0F, 0.5F);
        ModLogger.combat("Lord Dracula entered Mist Form to avoid burst damage!");
    }

    private void summonVampireMinion() {
        EntityVampire minion = new EntityVampire(this.worldObj);
        minion.setPosition(this.posX + (this.rand.nextDouble() - 0.5D) * 4.0D,
                           this.posY + 0.5D,
                           this.posZ + (this.rand.nextDouble() - 0.5D) * 4.0D);
        minion.onSpawnWithEgg(null);
        this.worldObj.spawnEntityInWorld(minion);
        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.zombie.infect", 1.5F, 0.8F);
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

    private void backstabTeleport(EntityLivingBase target) {
        Vec3 look = target.getLookVec();
        double behindX = target.posX - look.xCoord * 1.5D;
        double behindY = target.posY;
        double behindZ = target.posZ - look.zCoord * 1.5D;

        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, ModSounds.DRACULA_TELEPORT, 2.0F, 1.0F);
        this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 1.0D, this.posZ, 0, 0, 0);

        this.setPositionAndUpdate(behindX, behindY, behindZ);
        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, ModSounds.DRACULA_TELEPORT, 2.0F, 1.0F);
        this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 1.0D, this.posZ, 0, 0, 0);

        this.faceEntity(target, 360.0F, 360.0F);
        this.attackEntityAsMob(target);
        this.swingItem();
        ModLogger.combat("Lord Dracula performed Backstab on %s!", target.getCommandSenderName());
    }

    private void batSwarmDash(EntityLivingBase target) {
        double dx = target.posX - this.posX;
        double dz = target.posZ - this.posZ;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 0.1D) return;

        double dirX = dx / dist;
        double dirZ = dz / dist;
        double destX = target.posX + dirX * 3.5D;
        double destY = target.posY;
        double destZ = target.posZ + dirZ * 3.5D;

        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, ModSounds.BAT_SWARM, 2.0F, 1.0F);

        // Particle trail & damage along the dash line
        int steps = (int) Math.ceil(dist + 3.5D) * 2;
        for (int i = 0; i <= steps; i++) {
            double px = this.posX + dirX * (dist + 3.5D) * ((double) i / steps);
            double py = this.posY + 1.0D;
            double pz = this.posZ + dirZ * (dist + 3.5D) * ((double) i / steps);
            this.worldObj.spawnParticle("largesmoke", px, py, pz, 0, 0.05, 0);
        }

        // Damage and blind entities along path
        List<?> victims = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
                this.boundingBox.addCoord(dx + dirX * 3.5D, 0.0D, dz + dirZ * 3.5D).expand(1.5D, 1.5D, 1.5D));
        for (Object obj : victims) {
            if (obj != this && obj instanceof EntityLivingBase) {
                EntityLivingBase victim = (EntityLivingBase) obj;
                if (!Helper.isVampire(victim)) {
                    victim.attackEntityFrom(DamageSource.causeMobDamage(this), 8.0F);
                    victim.addPotionEffect(new PotionEffect(Potion.blindness.id, 60, 0));
                }
            }
        }

        this.setPositionAndUpdate(destX, destY, destZ);
        this.faceEntity(target, 360.0F, 360.0F);
        this.swingItem();
        ModLogger.combat("Lord Dracula dashed through %s with bat swarm!", target.getCommandSenderName());
    }

    private void fireRadialBloodRing() {
        int count = 8;
        for (int i = 0; i < count; i++) {
            double angle = (2.0D * Math.PI * i) / count;
            double dirX = Math.cos(angle);
            double dirZ = Math.sin(angle);

            EntityDarkBloodProjectile projectile = new EntityDarkBloodProjectile(this.worldObj, this, 7.5F);
            projectile.setThrowableHeading(dirX, 0.1D, dirZ, 1.2F, 1.0F);
            this.worldObj.spawnEntityInWorld(projectile);
        }
        this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 1.5F, 0.9F);
        this.swingItem();
        ModLogger.combat("Lord Dracula unleashed Radial Blood Ring!");
    }

    private void channelBloodSiphon(EntityLivingBase target) {
        // Drains blood from living non-vampires within 12 blocks, heals Dracula
        List<?> victims = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(12.0D, 4.0D, 12.0D));
        float totalDrained = 0.0F;

        for (Object obj : victims) {
            if (obj != this && obj instanceof EntityLivingBase) {
                EntityLivingBase victim = (EntityLivingBase) obj;
                if (Helper.isVampire(victim)) continue;
                if (victim instanceof EntityPlayer) {
                    EntityPlayer p = (EntityPlayer) victim;
                    if (p.capabilities.isCreativeMode) continue;
                }
                if (victim.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this), 3.0F)) {
                    totalDrained += 3.0F;
                    // Visual siphon particles towards Dracula
                    for (int i = 0; i < 4; i++) {
                        double px = victim.posX + (this.rand.nextDouble() - 0.5D) * 0.8D;
                        double py = victim.posY + this.rand.nextDouble() * 1.5D;
                        double pz = victim.posZ + (this.rand.nextDouble() - 0.5D) * 0.8D;
                        double vx = (this.posX - victim.posX) * 0.1D;
                        double vy = (this.posY + 1.2D - victim.posY) * 0.1D;
                        double vz = (this.posZ - victim.posZ) * 0.1D;
                        this.worldObj.spawnParticle("reddust", px, py, pz, vx, vy, vz);
                    }
                }
            }
        }

        if (totalDrained > 0.0F) {
            this.heal(totalDrained * 1.5F);
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, ModSounds.DRACULA_SIPHON, 2.0F, 1.0F);
            ModLogger.combat("Lord Dracula siphoned %.1f blood and healed %.1f HP!", totalDrained, totalDrained * 1.5F);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        if (this.dormant) return false;
        return super.attackEntityAsMob(target);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.dormant && !this.worldObj.isRemote) {
            if (source.getEntity() instanceof EntityPlayer) {
                if (this.worldObj.provider.dimensionId == Config.dimensionVelmorraId) {
                    DraculaFightManager mgr = DraculaFightManager.get(this.worldObj);
                    if (mgr != null && mgr.getState() == DraculaFightManager.State.NONE) {
                        mgr.awakenDracula(this);
                    } else {
                        this.startFight(Math.max(1, this.worldObj.playerEntities.size()));
                    }
                } else {
                    this.startFight(1);
                }
            } else {
                return false;
            }
        }
        // Complete immunity during transformation and mist form
        if (this.isTransforming || this.mistForm) {
            return false;
        }

        // Phase 1 Arrow Reflection: deflects 180 degrees and deals 0 damage
        if (getPhase() == 1 && source.isProjectile()) {
            Entity projectile = source.getSourceOfDamage();
            if (projectile != null) {
                projectile.motionX = -projectile.motionX;
                projectile.motionY = -projectile.motionY;
                projectile.motionZ = -projectile.motionZ;
                projectile.rotationYaw += 180.0F;
            }
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.break", 1.0F, 1.8F);
            return false;
        }

        // Projectile scaling per phase
        if (source.isProjectile()) {
            if (getPhase() == 2) {
                amount *= 0.5F; // 50% projectile damage in Phase 2
            } else if (getPhase() == 3) {
                amount *= 1.5F; // 150% projectile damage in Phase 3
            }
        }

        // Phase 1 passive resistance
        if (getPhase() == 1 && !source.isUnblockable()) {
            amount *= 0.6F;
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.break", 0.8F, 1.5F);
        }

        // Phase 3 Mist Form trigger on heavy burst damage
        if (getPhase() == 3 && mistCooldown <= 0) {
            recentDamage.add(new DamageRecord(this.worldObj.getTotalWorldTime(), amount));
            float sumDamage = 0;
            for (DamageRecord r : recentDamage) {
                sumDamage += r.amount;
            }
            if (sumDamage >= this.getMaxHealth() * 0.20F) {
                enterMistForm();
                return false;
            }
        }

        // Intercept lethal blow for Phase 1 & 2 transitions
        if (this.getHealth() - amount <= 0.0F) {
            if (getPhase() == 1) {
                startTransformation(2);
                this.setHealth(1.0F);
                return true;
            } else if (getPhase() == 2) {
                startTransformation(3);
                this.setHealth(1.0F);
                return true;
            }
        }

        return super.attackEntityFrom(source, amount);
    }

    private void cleanRecentDamage() {
        long curTime = this.worldObj.getTotalWorldTime();
        Iterator<DamageRecord> iter = recentDamage.iterator();
        while (iter.hasNext()) {
            DamageRecord r = iter.next();
            if (curTime - r.time > 100) {
                iter.remove();
            }
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!this.worldObj.isRemote && this.worldObj.provider.dimensionId == Config.dimensionVelmorraId) {
            DraculaFightManager mgr = DraculaFightManager.get(this.worldObj);
            if (mgr != null) {
                mgr.onDraculaKilled(this);
            }
        }
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        // Canonical Dracula drop: 15 to 48 Sovereign Blood
        int count = 15 + this.rand.nextInt(34);
        if (looting > 0) {
            count += this.rand.nextInt(looting * 4);
        }
        this.dropItem(ModItems.sovereignBlood, count);

        ModLogger.combat("Lord Dracula defeated! Dropped %d Sovereign Blood.", count);
    }

    public int getPhase() {
        return this.worldObj != null && this.worldObj.isRemote
            ? this.dataWatcher.getWatchableObjectByte(DATA_PHASE)
            : this.phase;
    }

    /**
     * Debug helper to switch Dracula directly to phase 1, 2, or 3 with canonical stats.
     */
    public void debugSetPhase(int target) {
        int clamped = Math.max(1, Math.min(3, target));
        this.isTransforming = false;
        this.transformTicks = 0;
        this.mistForm = false;
        this.mistTicks = 0;
        this.phaseCooldown = 0;
        this.phase = clamped;
        this.targetPhase = clamped;
        this.dataWatcher.updateObject(DATA_PHASE, Byte.valueOf((byte) clamped));

        double hp;
        double dmg;
        double speed;
        if (clamped == 1) {
            hp = 50.0D * this.fightScale;
            dmg = 10.0D;
            speed = 0.35D;
        } else if (clamped == 2) {
            hp = 100.0D * this.fightScale;
            dmg = 15.0D;
            speed = 0.35D;
        } else {
            hp = 200.0D * this.fightScale;
            dmg = 22.0D;
            speed = 0.42D;
        }
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(hp);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(dmg);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(speed);
        this.setHealth(this.getMaxHealth());
        ModLogger.combat("Debug: Lord Dracula switched to Phase %d (HP: %.0f)", clamped, this.getMaxHealth());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("Phase", this.phase);
        tag.setBoolean("Dormant", this.dormant);
        tag.setFloat("FightScale", this.fightScale);
        tag.setBoolean("IsTransforming", this.isTransforming);
        tag.setInteger("TransformTicks", this.transformTicks);
        tag.setInteger("TargetPhase", this.targetPhase);
        tag.setBoolean("MistForm", this.mistForm);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey("Phase")) {
            this.phase = Math.max(1, Math.min(3, tag.getInteger("Phase")));
            this.dataWatcher.updateObject(DATA_PHASE, Byte.valueOf((byte) this.phase));
        }
        if (tag.hasKey("Dormant")) {
            this.setDormant(tag.getBoolean("Dormant"));
        }
        if (tag.hasKey("FightScale")) {
            this.fightScale = tag.getFloat("FightScale");
        }
        this.isTransforming = tag.getBoolean("IsTransforming");
        this.transformTicks = tag.getInteger("TransformTicks");
        this.targetPhase = tag.getInteger("TargetPhase");
        this.mistForm = tag.getBoolean("MistForm");
    }

    @Override
    protected String getLivingSound() {
        return ModSounds.DRACULA_AMBIENT;
    }

    @Override
    protected String getHurtSound() {
        return ModSounds.DRACULA_HURT;
    }

    @Override
    protected String getDeathSound() {
        return ModSounds.DRACULA_DEATH;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }
}

