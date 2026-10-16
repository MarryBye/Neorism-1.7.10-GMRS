package com.marrybye.neorism.common.entity.vampire;

import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Advanced vampire elite mob equipped with vampiric weaponry and teleportation dash.
 */
public class EntityAdvancedVampire extends EntityVampireBase {

    private int teleportCooldown = 0;

    public EntityAdvancedVampire(World world) {
        super(world);
        this.setSize(0.6F, 1.85F);
        this.experienceValue = 15;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(45.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.32D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.5D);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setCurrentItemOrArmor(0, new ItemStack(ModItems.heartStriker, 1, 0));
        if (this.rand.nextBoolean()) {
            this.setCurrentItemOrArmor(4, new ItemStack(ModItems.vampireCrown));
        }
        ModLogger.spawn("Spawned Advanced Vampire %s at (%.1f, %.1f, %.1f)",
            this.getCommandSenderName(), this.posX, this.posY, this.posZ);
        return data;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (teleportCooldown > 0) {
            teleportCooldown--;
        }

        // Tactical teleportation behind target if distanced
        if (!this.worldObj.isRemote && this.getAttackTarget() != null && teleportCooldown <= 0) {
            EntityLivingBase target = this.getAttackTarget();
            double distSq = this.getDistanceSqToEntity(target);
            if (distSq > 36.0D && distSq < 256.0D && this.canEntityBeSeen(target)) {
                teleportBehind(target);
                teleportCooldown = 100; // 5 second cooldown
            }
        }
    }

    private void teleportBehind(EntityLivingBase target) {
        double angle = Math.toRadians(target.rotationYaw);
        // Position ~2 blocks behind target
        double behindX = target.posX + Math.sin(angle) * 2.0D;
        double behindZ = target.posZ - Math.cos(angle) * 2.0D;
        double behindY = target.posY;

        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.endermen.portal", 1.0F, 1.0F);
        this.setPositionAndUpdate(behindX, behindY, behindZ);
        this.worldObj.playSoundEffect(behindX, behindY, behindZ, "mob.endermen.portal", 1.0F, 1.0F);

        ModLogger.ai("Advanced Vampire %s teleported behind target %s!",
            this.getCommandSenderName(), target.getCommandSenderName());
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        // High tier loot
        int fangs = 1 + this.rand.nextInt(3 + looting);
        for (int i = 0; i < fangs; ++i) {
            this.dropItem(ModItems.vampireFang, 1);
        }

        if (this.rand.nextInt(2) == 0) {
            this.entityDropItem(new ItemStack(ModItems.pureBlood, 1, 0), 0.0F);
        }

        if (this.rand.nextInt(4) == 0) {
            this.dropItem(ModItems.bloodInfusedIron, 1);
        }
    }
}
