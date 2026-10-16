package com.marrybye.neorism.common.entity.vampire;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.entity.ExtendedCreature;
import com.marrybye.neorism.common.tileentity.TileEntityFogDiffuser;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Hostile flying blood bat that swoops from the shadows, bites victims, and restores health.
 */
public class EntityBloodBat extends EntityAmbientCreature implements IVampireEntity, IMob {

    private ChunkCoordinates spawnPosition;
    private EntityLivingBase attackTarget;
    private int biteCooldown = 0;

    public EntityBloodBat(World world) {
        super(world);
        this.setSize(0.5F, 0.9F);
        this.experienceValue = 4;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(12.0D);
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
        if (this.worldObj.isRemote || !this.worldObj.isDaytime()) return false;
        if (TileEntityFogDiffuser.isEntityProtected(this)) return false;
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);
        return this.worldObj.canBlockSeeTheSky(x, y, z);
    }

    @Override
    public boolean isIgnoringSundamage() {
        return false;
    }

    @Override
    public boolean wantsBlood() {
        return this.getHealth() < this.getMaxHealth();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // Bat wing flapping animation
        this.motionY *= 0.6000000238418579D;

        if (biteCooldown > 0) {
            biteCooldown--;
        }

        if (!this.worldObj.isRemote) {
            if (isGettingSundamage()) {
                this.setFire(4);
                this.attackEntityFrom(ModDamageSource.SUN_DAMAGE, 1.5F);
            }

            // Target acquisition if none
            if (attackTarget == null || !attackTarget.isEntityAlive() || attackTarget.isDead) {
                findNewTarget();
            }

            if (attackTarget != null) {
                // Swoop towards target
                double dx = attackTarget.posX - this.posX;
                double dy = attackTarget.posY + (double) (attackTarget.height / 2.0F) - this.posY;
                double dz = attackTarget.posZ - this.posZ;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

                if (dist > 0.5D) {
                    this.motionX += (dx / dist * 0.4D - this.motionX) * 0.15D;
                    this.motionY += (dy / dist * 0.4D - this.motionY) * 0.15D;
                    this.motionZ += (dz / dist * 0.4D - this.motionZ) * 0.15D;
                }

                // Attack on contact
                if (dist < 1.4D && biteCooldown <= 0) {
                    biteCooldown = 20;
                    boolean hit = attackTarget.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
                    if (hit) {
                        this.heal(2.0F);
                        ModSounds.playAtEntity(this, ModSounds.BITE, 0.8F, 1.2F);
                        ModLogger.combat("Blood Bat %s bit %s (healed +2.0 HP)",
                            this.getCommandSenderName(), attackTarget.getCommandSenderName());

                        // Bounce upwards after strike
                        this.motionY = 0.35D;
                    }
                }
            } else {
                // Random fluttering flight
                if (this.spawnPosition != null && (!this.worldObj.isAirBlock(this.spawnPosition.posX, this.spawnPosition.posY, this.spawnPosition.posZ) || this.spawnPosition.posY < 1)) {
                    this.spawnPosition = null;
                }

                if (this.spawnPosition == null || this.rand.nextInt(30) == 0 || this.spawnPosition.getDistanceSquared((int) this.posX, (int) this.posY, (int) this.posZ) < 4.0F) {
                    this.spawnPosition = new ChunkCoordinates(
                        (int) this.posX + this.rand.nextInt(7) - this.rand.nextInt(7),
                        (int) this.posY + this.rand.nextInt(6) - 2,
                        (int) this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7)
                    );
                }

                double dx = (double) this.spawnPosition.posX + 0.5D - this.posX;
                double dy = (double) this.spawnPosition.posY + 0.1D - this.posY;
                double dz = (double) this.spawnPosition.posZ + 0.5D - this.posZ;
                this.motionX += (Math.signum(dx) * 0.5D - this.motionX) * 0.1D;
                this.motionY += (Math.signum(dy) * 0.7D - this.motionY) * 0.1D;
                this.motionZ += (Math.signum(dz) * 0.5D - this.motionZ) * 0.1D;
            }
        }
    }

    private void findNewTarget() {
        // Search 12 block radius for prey or hunters
        for (Object obj : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(12.0D, 6.0D, 12.0D))) {
            EntityLivingBase living = (EntityLivingBase) obj;
            if (living == this || !living.isEntityAlive()) continue;

            if (living instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) living;
                if (!player.capabilities.isCreativeMode && !Helper.isVampire(player)) {
                    this.attackTarget = player;
                    return;
                }
            } else if (Helper.isHunter(living)) {
                this.attackTarget = living;
                return;
            } else {
                ExtendedCreature ext = ExtendedCreature.get(living);
                if (ext != null && ext.canBeBitten(this)) {
                    this.attackTarget = living;
                    return;
                }
            }
        }
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        if (this.rand.nextInt(2) == 0) {
            this.dropItem(ModItems.vampireFang, 1);
        }
    }

    @Override
    protected String getLivingSound() {
        return "mob.bat.idle";
    }

    @Override
    protected String getHurtSound() {
        return "mob.bat.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.bat.death";
    }
}
