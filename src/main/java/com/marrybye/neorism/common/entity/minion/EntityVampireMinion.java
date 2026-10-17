package com.marrybye.neorism.common.entity.minion;

import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.util.Helper;
import de.teamlapen.faction.api.factions.IFaction;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Vampire minion entity serving Vampire Lords.
 */
public class EntityVampireMinion extends EntityMinionBase {

    public EntityVampireMinion(World world) {
        super(world);
    }

    @Override
    public IFaction getFaction() {
        return ModFactions.VAMPIRE;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Sunlight damage check
        if (!this.worldObj.isRemote && this.isEntityAlive()) {
            if (Helper.isDay(this.worldObj) && this.worldObj.canBlockSeeTheSky((int) Math.floor(posX), (int) Math.floor(posY), (int) Math.floor(posZ))) {
                if (this.ticksExisted % 40 == 0) {
                    this.attackEntityFrom(DamageSource.onFire, 1.0F);
                    this.setFire(8);
                }
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean flag = super.attackEntityAsMob(target);
        if (flag) {
            // Drink blood / heal on attack
            this.heal(1.0F);
        }
        return flag;
    }

    @Override
    protected Item getDropItem() {
        return ModItems.vampireFang;
    }
}
