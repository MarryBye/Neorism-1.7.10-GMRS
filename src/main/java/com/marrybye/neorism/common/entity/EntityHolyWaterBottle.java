package com.marrybye.neorism.common.entity;

import java.util.List;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Thrown splash bottle of holy water.
 * Explodes on impact, dealing holy damage to vampires and curing Sanguinare Vampiris on mortals.
 */
public class EntityHolyWaterBottle extends EntityThrowable {

    private int tier = 0;

    public EntityHolyWaterBottle(World world) {
        super(world);
    }

    public EntityHolyWaterBottle(World world, EntityLivingBase thrower, int tier) {
        super(world, thrower);
        this.tier = tier;
    }

    public EntityHolyWaterBottle(World world, double x, double y, double z, int tier) {
        super(world, x, y, z);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("tier", tier);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.tier = tag.getInteger("tier");
    }

    @Override
    protected void onImpact(MovingObjectPosition mop) {
        if (!worldObj.isRemote) {
            float radius = tier == 2 ? 7.0F : (tier == 1 ? 5.5F : 4.0F);
            float baseDamage = tier == 2 ? 22.0F : (tier == 1 ? 14.0F : 8.0F);

            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                posX - radius, posY - radius, posZ - radius,
                posX + radius, posY + radius, posZ + radius
            );
            List entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

            for (Object obj : entities) {
                if (obj instanceof EntityLivingBase) {
                    EntityLivingBase target = (EntityLivingBase) obj;
                    double dist = target.getDistance(posX, posY, posZ);
                    if (dist <= radius) {
                        float factor = (float) (1.0D - (dist / radius));

                        boolean isVampire = Helper.isVampire(target) ||
                            (target instanceof IVampireEntity) ||
                            target.getClass().getName().toLowerCase().contains("vampire");

                        if (isVampire) {
                            float finalDmg = Math.max(1.0F, baseDamage * factor);
                            target.attackEntityFrom(ModDamageSource.causeIndirectHolyWaterDamage(this, getThrower()), finalDmg);
                            target.addPotionEffect(new PotionEffect(Potion.weakness.id, (int) (200 * factor), 1));
                            target.addPotionEffect(new PotionEffect(Potion.blindness.id, (int) (100 * factor), 0));

                            // Repel/knockback vampire
                            double dx = target.posX - posX;
                            double dz = target.posZ - posZ;
                            double d = Math.sqrt(dx * dx + dz * dz);
                            if (d > 0.001D) {
                                target.addVelocity((dx / d) * 0.4D, 0.2D, (dz / d) * 0.4D);
                            }

                            ModLogger.combat("Holy water splash tier %d struck vampire %s for %.1f holy damage",
                                tier, target.getCommandSenderName(), finalDmg);
                        } else {
                            // Cure Sanguinare on mortals
                            if (target.isPotionActive(ModPotions.sanguinare)) {
                                target.removePotionEffect(ModPotions.sanguinare.id);
                                ModLogger.combat("Holy water splash tier %d cleansed Sanguinare Vampiris from %s",
                                    tier, target.getCommandSenderName());
                            }
                            // Half damage to generic undead
                            if (target.isEntityUndead()) {
                                target.attackEntityFrom(ModDamageSource.causeIndirectHolyWaterDamage(this, getThrower()), baseDamage * 0.5F * factor);
                            }
                        }
                    }
                }
            }

            // Canonical Cursed Earth purification in splash radius
            int minX = (int) Math.floor(posX - radius);
            int maxX = (int) Math.ceil(posX + radius);
            int minY = (int) Math.floor(posY - 2.0D);
            int maxY = (int) Math.ceil(posY + 2.0D);
            int minZ = (int) Math.floor(posZ - radius);
            int maxZ = (int) Math.ceil(posZ + radius);
            for (int bx = minX; bx <= maxX; bx++) {
                for (int by = minY; by <= maxY; by++) {
                    for (int bz = minZ; bz <= maxZ; bz++) {
                        if (worldObj.getBlock(bx, by, bz) == com.marrybye.neorism.common.core.ModBlocks.cursedEarth) {
                            worldObj.setBlock(bx, by, bz, net.minecraft.init.Blocks.dirt, 0, 3);
                        }
                    }
                }
            }

            worldObj.playSoundEffect(posX, posY, posZ, "game.potion.smash", 1.0F, 0.9F + rand.nextFloat() * 0.2F);
            ModSounds.playAt(worldObj, posX, posY, posZ, ModSounds.SIZZLE, 0.8F, 1.2F);
            setDead();
        } else {
            // Client visual splash effects
            for (int i = 0; i < 40; i++) {
                double speedX = (rand.nextDouble() - 0.5D) * 0.6D;
                double speedY = rand.nextDouble() * 0.5D;
                double speedZ = (rand.nextDouble() - 0.5D) * 0.6D;
                worldObj.spawnParticle("splash", posX, posY, posZ, speedX, speedY, speedZ);
                worldObj.spawnParticle("magicCrit", posX, posY, posZ, speedX * 0.5D, speedY * 0.5D, speedZ * 0.5D);
            }
        }
    }
}
