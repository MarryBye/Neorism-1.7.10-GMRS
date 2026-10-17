package com.marrybye.neorism.common.entity.hunter;

import com.marrybye.neorism.api.entity.IHunterEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.entity.ai.EntityAIAttackFaction;
import com.marrybye.neorism.common.util.ModLogger;

import de.teamlapen.faction.api.world.ICaptureAttributes;
import de.teamlapen.faction.api.world.entities.IVillageCaptureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Base class for all vampire hunter NPCs and patrol mobs.
 * Allied with human hunters, attacks vampires on sight, participates in village capture raids.
 */
public abstract class EntityHunterBase extends EntityCreature implements IHunterEntity, IVillageCaptureEntity {

    private ICaptureAttributes captureInfo;
    private boolean isAttackingVillage;
    private boolean isDefendingVillage;

    public EntityHunterBase(World world) {
        super(world);
        this.experienceValue = 6;
        this.setupAI();
    }

    protected void setupAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.15D, false));
        this.tasks.addTask(2, new EntityAIWander(this, 0.9D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAIAttackFaction(this, ModFactions.HUNTER, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.28D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.5D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(26.0D);
    }

    @Override
    public IFaction getFaction() {
        return ModFactions.HUNTER;
    }

    @Override
    public EntityLivingBase asEntity() {
        return this;
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        boolean flag = target.attackEntityFrom(net.minecraft.util.DamageSource.causeMobDamage(this), damage);
        if (flag) {
            this.swingItem();
            ModLogger.combat("Hunter %s attacked %s for %.1f damage",
                this.getCommandSenderName(), target.getCommandSenderName(), damage);
        }
        return flag;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    public boolean tryCureSanguinare(EntityPlayer player) {
        if (!this.worldObj.isRemote && player != null && player.isPotionActive(com.marrybye.neorism.common.core.ModPotions.sanguinare)) {
            player.removePotionEffect(com.marrybye.neorism.common.core.ModPotions.sanguinare.id);
            player.addChatMessage(new net.minecraft.util.ChatComponentTranslation("dialogue.vampirism.hunter.cured_sanguinare"));
            return true;
        }
        return false;
    }

    @Override
    public void attackVillage(ICaptureAttributes totem) {
        this.captureInfo = totem;
        this.isAttackingVillage = true;
        this.isDefendingVillage = false;
    }

    @Override
    public void defendVillage(ICaptureAttributes totem) {
        this.captureInfo = totem;
        this.isDefendingVillage = true;
        this.isAttackingVillage = false;
    }

    @Override
    public ICaptureAttributes getCaptureInfo() {
        return this.captureInfo;
    }

    @Override
    public AxisAlignedBB getTargetVillageArea() {
        return this.captureInfo != null ? this.captureInfo.getVillageArea() : null;
    }

    @Override
    public boolean isAttackingVillage() {
        return this.isAttackingVillage;
    }

    @Override
    public boolean isDefendingVillage() {
        return this.isDefendingVillage;
    }

    @Override
    public void stopVillageAttackDefense() {
        this.captureInfo = null;
        this.isAttackingVillage = false;
        this.isDefendingVillage = false;
    }
}
