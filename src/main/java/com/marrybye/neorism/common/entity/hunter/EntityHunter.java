package com.marrybye.neorism.common.entity.hunter;

import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Basic vampire hunter patrol mob armed with stakes and hunter axes.
 */
public class EntityHunter extends EntityHunterBase {

    private static final int DATA_SKIN = 20;

    public EntityHunter(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(DATA_SKIN, (byte) (this.rand.nextInt(9) + 1));
    }

    public int getSkinType() {
        return this.dataWatcher.getWatchableObjectByte(DATA_SKIN);
    }

    public void setSkinType(int skin) {
        this.dataWatcher.updateObject(DATA_SKIN, (byte) skin);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setSkinType(this.rand.nextInt(9) + 1);

        // Equip melee weapon: Stake or Hunter Axe
        if (this.rand.nextBoolean()) {
            this.setCurrentItemOrArmor(0, new ItemStack(ModItems.hunterAxe, 1, 0));
        } else {
            this.setCurrentItemOrArmor(0, new ItemStack(ModItems.stake));
        }

        // Equip hunter coat hat or chest
        if (this.rand.nextBoolean()) {
            this.setCurrentItemOrArmor(4, new ItemStack(ModItems.hunterHat));
        }
        if (this.rand.nextBoolean()) {
            this.setCurrentItemOrArmor(3, new ItemStack(ModItems.hunterCoatChest));
        }

        ModLogger.spawn("Spawned Hunter patrol %s (skin=%d) at (%.1f, %.1f, %.1f)",
            this.getCommandSenderName(), this.getSkinType(), this.posX, this.posY, this.posZ);
        return data;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        if (this.rand.nextInt(2) == 0) {
            this.dropItem(ModItems.stake, 1 + this.rand.nextInt(2));
        }

        if (this.rand.nextInt(3) == 0) {
            this.entityDropItem(new ItemStack(ModItems.quarrel, 2 + this.rand.nextInt(4)), 0.0F);
        }

        if (this.rand.nextInt(4) == 0) {
            this.dropItem(ModItems.hunterCoatHead, 1);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setByte("SkinType", (byte) this.getSkinType());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey("SkinType")) {
            this.setSkinType(tag.getByte("SkinType"));
        }
    }
}
