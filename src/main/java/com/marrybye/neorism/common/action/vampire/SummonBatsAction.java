package com.marrybye.neorism.common.action.vampire;

import java.util.List;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;

/**
 * Vampire Summon Bats spell.
 * Canonical port of TeamLapen's SummonBatVampireAction:
 * Spawns an evasive swarm of bats that distract and blind nearby non-vampires.
 */
public class SummonBatsAction extends ActionBase {

    public SummonBatsAction() {
        super("summon_bats", 1200, 0, false, 7);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            // Spawn distracting bats
            for (int i = 0; i < 8; i++) {
                EntityBat bat = new EntityBat(player.worldObj);
                bat.setPosition(
                    player.posX + (player.worldObj.rand.nextDouble() - 0.5D) * 3.0D,
                    player.posY + 0.5D + player.worldObj.rand.nextDouble() * 1.5D,
                    player.posZ + (player.worldObj.rand.nextDouble() - 0.5D) * 3.0D
                );
                bat.setIsBatHanging(false);
                player.worldObj.spawnEntityInWorld(bat);
            }

            // Blind nearby non-vampires
            double r = 10.0D;
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                player.posX - r, player.posY - r, player.posZ - r,
                player.posX + r, player.posY + r, player.posZ + r
            );
            List list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

            for (Object obj : list) {
                if (obj instanceof EntityLivingBase && obj != player) {
                    EntityLivingBase living = (EntityLivingBase) obj;
                    boolean isVampire = Helper.isVampire(living) || (living instanceof IVampireEntity);
                    if (!isVampire) {
                        living.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
                    }
                }
            }
        }

        ModSounds.playAtEntity(player, ModSounds.BAT_SWARM, 1.2F, 1.0F);
        return true;
    }
}
