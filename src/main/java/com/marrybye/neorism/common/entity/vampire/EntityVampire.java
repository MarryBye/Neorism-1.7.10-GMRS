package com.marrybye.neorism.common.entity.vampire;

import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Basic vampire mob roaming dark biomes and night landscapes.
 * Fast, attacks hunters and mortals, carries multiple visual appearances.
 */
public class EntityVampire extends EntityVampireBase {

    private static final int DATA_SKIN = 20;

    public EntityVampire(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        // Random skin index between 1 and 17 (excluding missing ones)
        this.dataWatcher.addObject(DATA_SKIN, (byte) (this.rand.nextInt(12) + 1));
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
        this.setSkinType(this.rand.nextInt(12) + 1);
        ModLogger.spawn("Spawned Vampire %s (skin=%d) at (%.1f, %.1f, %.1f)",
            this.getCommandSenderName(), this.getSkinType(), this.posX, this.posY, this.posZ);
        return data;
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean flag = super.attackEntityAsMob(target);
        if (flag) {
            // Chance to infect victim with Sanguinare Vampiris
            if (target instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) target;
                FactionPlayerHandler fph = FactionPlayerHandler.get(player);
                if (fph != null && fph.isNeutral() && !player.isPotionActive(ModPotions.sanguinare)) {
                    if (this.rand.nextInt(5) == 0) {
                        player.addPotionEffect(new PotionEffect(ModPotions.sanguinare.id, 24000, 0));
                        ModSounds.playAtEntity(player, ModSounds.HEARTBEAT, 1.0F, 1.0F);
                        ModLogger.combat("Vampire infected player %s with Sanguinare Vampiris", player.getCommandSenderName());
                    }
                }
            } else if (target instanceof EntityVillager) {
                com.marrybye.neorism.common.entity.ExtendedCreature ext =
                    com.marrybye.neorism.common.entity.ExtendedCreature.get(target);
                if (ext != null && ext.canBeInfected(this)) {
                    ext.tryInfect(this);
                }
            }
        }
        return flag;
    }

    @Override
    protected Item getDropItem() {
        return ModItems.vampireFang;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        int fangs = this.rand.nextInt(2 + looting);
        for (int i = 0; i < fangs; ++i) {
            this.dropItem(ModItems.vampireFang, 1);
        }

        if (this.rand.nextInt(3) == 0) {
            this.dropItem(ModItems.vampireBloodBottle, 1);
        }

        if (this.rand.nextInt(4) == 0) {
            this.dropItem(Items.bone, 1 + this.rand.nextInt(2));
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

    @Override
    protected String getLivingSound() {
        return ModSounds.VAMPIRE_SCREAM;
    }

    @Override
    public int getTalkInterval() {
        return 2400;
    }
}
