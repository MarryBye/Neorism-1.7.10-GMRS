package com.marrybye.neorism.common.potion;

import com.marrybye.neorism.common.core.ModDamageSource;

import net.minecraft.entity.EntityLivingBase;

/**
 * Bleeding effect - inflicts continuous bleed damage.
 */
public class PotionBleeding extends PotionVampirismBase {

    public PotionBleeding(int id) {
        super(id, true, 0x740000, "bleeding");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int k = 40 >> amplifier;
        return k > 0 ? duration % k == 0 : true;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (!entity.worldObj.isRemote) {
            entity.attackEntityFrom(ModDamageSource.BLEEDING, 1.0F);
        }
    }
}
