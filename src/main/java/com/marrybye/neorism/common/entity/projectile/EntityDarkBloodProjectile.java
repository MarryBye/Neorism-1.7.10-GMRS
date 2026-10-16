package com.marrybye.neorism.common.entity.projectile;

import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Ranged life-stealing dark blood projectile launched by vampires.
 */
public class EntityDarkBloodProjectile extends EntityThrowable {

    private float customDamage = 8.0F;

    public EntityDarkBloodProjectile(World world) {
        super(world);
    }

    public EntityDarkBloodProjectile(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }

    public EntityDarkBloodProjectile(World world, EntityLivingBase thrower, float damage) {
        super(world, thrower);
        this.customDamage = damage;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (worldObj.isRemote) {
            for (int i = 0; i < 3; i++) {
                worldObj.spawnParticle("reddust", posX, posY, posZ, 0.5D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition mop) {
        if (!worldObj.isRemote) {
            if (mop.entityHit instanceof EntityLivingBase) {
                EntityLivingBase target = (EntityLivingBase) mop.entityHit;
                EntityLivingBase thrower = getThrower();

                float damage = this.customDamage;
                DamageSource source = thrower != null ?
                    DamageSource.causeIndirectMagicDamage(this, thrower) :
                    DamageSource.magic;

                target.attackEntityFrom(source, damage);

                // Life-steal: restore blood and health to vampire caster
                if (thrower instanceof EntityPlayer && Helper.isVampire((EntityPlayer) thrower)) {
                    EntityPlayer caster = (EntityPlayer) thrower;
                    VampirePlayer vp = VampirePlayer.get(caster);
                    if (vp != null) {
                        vp.drinkBlood(3, 1.0F);
                    }
                    caster.heal(3.0F);
                    ModLogger.combat("Dark blood projectile hit %s for %.1f damage (healed caster %s)",
                        target.getCommandSenderName(), damage, caster.getCommandSenderName());
                }

                ModSounds.playAt(worldObj, posX, posY, posZ, ModSounds.BLOOD_PROJECTILE_HIT, 1.0F, 1.0F);
            }
            setDead();
        } else {
            for (int i = 0; i < 15; i++) {
                worldObj.spawnParticle("reddust", posX, posY, posZ,
                    (rand.nextDouble() - 0.5D) * 0.5D, rand.nextDouble() * 0.5D, (rand.nextDouble() - 0.5D) * 0.5D);
            }
        }
    }
}
