package com.marrybye.neorism.common.entity.vampire;

import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.projectile.EntityDarkBloodProjectile;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

/**
 * Vampire Mother - Ancient progenitor deity boss encounter.
 */
public class EntityVampireMother extends EntityVampireBase implements IBossDisplayData {

    private int spellCooldown = 0;

    public EntityVampireMother(World world) {
        super(world);
        this.setSize(0.7F, 2.2F);
        this.experienceValue = 90;
        this.setCustomNameTag("Vampire Mother");
        this.setAlwaysRenderNameTag(true);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(450.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.33D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.75D);
    }

    @Override
    public IChatComponent func_145748_c_() {
        return new ChatComponentText(this.getCommandSenderName());
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        BossStatus.setBossStatus(this, true);

        if (!this.worldObj.isRemote && this.isEntityAlive()) {
            if (spellCooldown > 0) {
                spellCooldown--;
            }

            EntityLivingBase target = this.getAttackTarget();
            if (target != null && target.isEntityAlive()) {
                double distSq = this.getDistanceSqToEntity(target);

                if (spellCooldown <= 0 && distSq > 9.0D && distSq < 350.0D && this.canEntityBeSeen(target)) {
                    castBloodWave(target);
                    spellCooldown = 45;
                }
            }
        }
    }

    private void castBloodWave(EntityLivingBase target) {
        for (int i = 0; i < 2; ++i) {
            EntityDarkBloodProjectile proj = new EntityDarkBloodProjectile(this.worldObj, this, 6.5F);
            double dx = target.posX - this.posX;
            double dy = target.posY + (double) target.getEyeHeight() - 1.1D - proj.posY;
            double dz = target.posZ - this.posZ;
            proj.setThrowableHeading(dx, dy, dz, 1.5F, 3.0F);
            this.worldObj.spawnEntityInWorld(proj);
        }
        this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 1.0F, 1.0F);
        this.swingItem();

        ModLogger.combat("Vampire Mother %s cast Blood Wave at %s", this.getCommandSenderName(), target.getCommandSenderName());
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        this.entityDropItem(new ItemStack(ModItems.pureBlood, 2, 4), 0.0F);
        this.dropItem(ModItems.soulOrbVampire, 1);
        this.dropItem(ModItems.bloodInfusedDiamond, 2);
        this.entityDropItem(new ItemStack(ModItems.vampireBook, 1, 7), 0.0F);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }
}
