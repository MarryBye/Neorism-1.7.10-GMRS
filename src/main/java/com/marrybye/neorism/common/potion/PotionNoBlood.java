package com.marrybye.neorism.common.potion;

import com.marrybye.neorism.common.core.ModAttributes;
import com.marrybye.neorism.common.core.ModDamageSource;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;

/**
 * No Blood effect - afflicts vampires when their blood pool reaches zero.
 * Causes severe weakness, slowness, and progressive heart damage.
 */
public class PotionNoBlood extends PotionVampirismBase {

    public PotionNoBlood(int id) {
        super(id, true, 0x191919, "no_blood");
        registerModifier(
            SharedMonsterAttributes.movementSpeed,
            "4a36c4b2-2997-40fa-ba69-158a25bb7e24",
            -0.4D,
            2
        );
        registerModifier(
            ModAttributes.SUNDAMAGE,
            "5a36c4b2-2997-40fa-ba69-158a25bb7e25",
            0.2D,
            2
        );
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 80 == 0;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (!entity.worldObj.isRemote) {
            entity.attackEntityFrom(ModDamageSource.NO_BLOOD, 1.0F + amplifier);
        }
    }
}
