package com.marrybye.neorism.common.entity.vampire;

import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.projectile.EntityDarkBloodProjectile;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Vampire Baron miniboss found in ancient crypts and dark spruce groves.
 * Casts dark blood projectiles, summons blood bat swarms, and drops high tier pure blood.
 */
public class EntityVampireBaron extends EntityVampireBase {

    private int castCooldown = 40;
    private boolean summonedBats = false;

    public EntityVampireBaron(World world) {
        super(world);
        this.setSize(0.7F, 2.1F);
        this.experienceValue = 35;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(150.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.5D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.worldObj.isRemote && this.isEntityAlive()) {
            EntityLivingBase target = this.getAttackTarget();

            if (target != null && target.isEntityAlive()) {
                double distSq = this.getDistanceSqToEntity(target);

                // Ranged Dark Blood Projectile attack
                if (--castCooldown <= 0 && distSq > 16.0D && distSq < 400.0D && this.canEntityBeSeen(target)) {
                    castDarkBlood(target);
                    castCooldown = 50 + this.rand.nextInt(20);
                }

                // Minion summoning below 50% HP
                if (!summonedBats && this.getHealth() < this.getMaxHealth() * 0.5F) {
                    summonBats();
                    summonedBats = true;
                }
            }
        }
    }

    private void castDarkBlood(EntityLivingBase target) {
        EntityDarkBloodProjectile projectile = new EntityDarkBloodProjectile(this.worldObj, this, 6.0F);
        double dx = target.posX - this.posX;
        double dy = target.posY + (double) target.getEyeHeight() - 1.1D - projectile.posY;
        double dz = target.posZ - this.posZ;
        projectile.setThrowableHeading(dx, dy, dz, 1.4F, 2.0F);

        this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 1.0F, 0.8F);
        this.worldObj.spawnEntityInWorld(projectile);
        this.swingItem();

        ModLogger.combat("Vampire Baron %s cast Dark Blood Projectile at %s",
            this.getCommandSenderName(), target.getCommandSenderName());
    }

    private void summonBats() {
        ModLogger.combat("Vampire Baron %s enraged and summoned a swarm of Blood Bats!", this.getCommandSenderName());
        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.bat.takeoff", 2.0F, 0.7F);

        for (int i = 0; i < 3; ++i) {
            EntityBloodBat bat = new EntityBloodBat(this.worldObj);
            bat.setPosition(this.posX + (this.rand.nextDouble() - 0.5D) * 2.0D,
                            this.posY + 1.5D,
                            this.posZ + (this.rand.nextDouble() - 0.5D) * 2.0D);
            this.worldObj.spawnEntityInWorld(bat);
        }
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        // Drop Pure Blood (Tier 3 or 4)
        int tier = 3 + (this.rand.nextBoolean() ? 1 : 0);
        this.entityDropItem(new ItemStack(ModItems.pureBlood, 1 + this.rand.nextInt(2), tier), 0.0F);

        // Fangs and infusing gems
        this.dropItem(ModItems.vampireFang, 2 + this.rand.nextInt(3));

        if (this.rand.nextInt(2) == 0) {
            this.dropItem(ModItems.bloodInfusedDiamond, 1);
        }

        if (this.rand.nextInt(3) == 0) {
            this.entityDropItem(new ItemStack(ModItems.vampireBook, 1, 4 + this.rand.nextInt(3)), 0.0F);
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }
}
