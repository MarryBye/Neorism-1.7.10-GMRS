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
        if (this.rand.nextBoolean()) {
            this.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.potion.Potion.resistance.id, 1000000, 0));
        }
        ModLogger.spawn("Spawned Advanced Vampire %s at (%.1f, %.1f, %.1f)",
            this.getCommandSenderName(), this.posX, this.posY, this.posZ);
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        boolean flag = super.attackEntityFrom(source, amount);
        if (flag && !this.worldObj.isRemote) {
            // Canonical AdvancedVampireEntity.hurtServer: 1-in-4 chance when hit by a Player to cast Sunscreen (150 ticks, amp 2)
            if (source != null && source.getEntity() instanceof net.minecraft.entity.player.EntityPlayer && this.rand.nextInt(4) == 0) {
                this.addPotionEffect(new net.minecraft.potion.PotionEffect(com.marrybye.neorism.common.core.ModPotions.sunscreen.id, 150, 2));
                ModLogger.combat("Advanced Vampire %s activated Sunscreen after player attack!", this.getCommandSenderName());
            }
            // Canonical leader behavior: rally nearby basic vampires to defend their Advanced Vampire leader
            if (source != null && source.getEntity() instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase) source.getEntity();
                net.minecraft.util.AxisAlignedBB box = this.boundingBox.expand(16.0D, 8.0D, 16.0D);
                java.util.List<EntityVampire> followers = this.worldObj.getEntitiesWithinAABB(EntityVampire.class, box);
                int count = 0;
                for (EntityVampire v : followers) {
                    if (v.getAttackTarget() == null && count < 4) {
                        v.setAttackTarget(attacker);
                        count++;
                    }
                }
            }
        }
        return flag;
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
