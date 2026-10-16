package com.marrybye.neorism.common.entity.hunter;

import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.ai.EntityAICrossbowAttack;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Advanced Hunter marksman armed with mechanical crossbows and holy water.
 */
public class EntityAdvancedHunter extends EntityHunterBase {

    public EntityAdvancedHunter(World world) {
        super(world);
        this.setSize(0.6F, 1.85F);
        this.experienceValue = 16;
    }

    @Override
    protected void setupAI() {
        super.setupAI();
        // Add crossbow attack task
        this.tasks.addTask(1, new EntityAICrossbowAttack(this, 1.0D, 18.0F));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30D);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setCurrentItemOrArmor(0, new ItemStack(ModItems.crossbow, 1, 0));
        this.setCurrentItemOrArmor(4, new ItemStack(ModItems.hunterHat));
        this.setCurrentItemOrArmor(3, new ItemStack(ModItems.hunterCoatChest));
        this.setCurrentItemOrArmor(2, new ItemStack(ModItems.hunterCoatLegs));
        this.setCurrentItemOrArmor(1, new ItemStack(ModItems.hunterCoatFeet));

        ModLogger.spawn("Spawned Advanced Hunter marksman %s at (%.1f, %.1f, %.1f)",
            this.getCommandSenderName(), this.posX, this.posY, this.posZ);
        return data;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        this.entityDropItem(new ItemStack(ModItems.quarrel, 4 + this.rand.nextInt(8 + looting)), 0.0F);

        if (this.rand.nextInt(3) == 0) {
            this.dropItem(ModItems.holyWaterBottle, 1);
        }

        if (this.rand.nextInt(5) == 0) {
            this.dropItem(ModItems.crossbow, 1);
        }
    }
}
