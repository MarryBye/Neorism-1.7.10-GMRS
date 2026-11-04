package com.marrybye.neorism.common.potion;

import net.minecraft.entity.SharedMonsterAttributes;

/**
 * Lord Speed effect. Canonical port of Vampirism ModEffects.LORD_SPEED:
 * +7% movement speed (ADD_MULTIPLIED_TOTAL) per amplifier level.
 */
public class PotionLordSpeed extends PotionVampirismBase {

    public PotionLordSpeed(int id) {
        super(id, false, 0xFFFFFF, "lord_speed");
        registerModifier(
            SharedMonsterAttributes.movementSpeed,
            "e3f7c2a4-55d1-4d0b-9a4e-7b1c1f0d2a01",
            0.07D,
            2
        );
    }
}
