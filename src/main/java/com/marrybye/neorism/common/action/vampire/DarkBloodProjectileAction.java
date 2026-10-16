package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.entity.projectile.EntityDarkBloodProjectile;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Vampire Dark Blood Projectile spell (launches life-stealing blood orb).
 */
public class DarkBloodProjectileAction extends ActionBase {

    public DarkBloodProjectileAction() {
        super("dark_blood_projectile", 160, 0, false, 3);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            EntityDarkBloodProjectile proj = new EntityDarkBloodProjectile(player.worldObj, player);
            player.worldObj.spawnEntityInWorld(proj);
        }
        return true;
    }
}
