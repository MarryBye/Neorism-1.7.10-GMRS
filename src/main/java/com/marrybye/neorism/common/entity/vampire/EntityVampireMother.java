package com.marrybye.neorism.common.entity.vampire;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.projectile.EntityDarkBloodProjectile;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
        this.setIgnoringSundamage(true);
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
        if (this.worldObj.isRemote) {
            updateClientBossStatus();
        }

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

    @SideOnly(Side.CLIENT)
    private void updateClientBossStatus() {
        BossStatus.setBossStatus(this, true);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        // Canonical loot_table/blocks/mother.json
        if (ModItems.motherCore != null) {
            this.dropItem(ModItems.motherCore, 1);
        }
        if (ModBlocks.ancientTrophy != null) {
            this.entityDropItem(new ItemStack(ModBlocks.ancientTrophy, 1), 0.0F);
        }
        if (ModItems.soulOrbVampire != null) {
            this.dropItem(ModItems.soulOrbVampire, 1);
        }
        int bonusRolls = 1 + this.rand.nextInt(4);
        for (int i = 0; i < bonusRolls; i++) {
            if (this.rand.nextBoolean() && ModItems.pureBlood != null) {
                this.entityDropItem(new ItemStack(ModItems.pureBlood, 1, 4), 0.0F);
            } else if (ModItems.vampireBloodBottle != null) {
                this.dropItem(ModItems.vampireBloodBottle, 1);
            }
        }
    }

    @Override
    protected String getLivingSound() {
        return com.marrybye.neorism.common.core.ModSounds.MOTHER_AMBIENT;
    }

    @Override
    protected String getDeathSound() {
        return com.marrybye.neorism.common.core.ModSounds.MOTHER_DEATH;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }
}
