package com.marrybye.neorism.common.potion;

import net.minecraft.entity.SharedMonsterAttributes;

/**
 * Garlic effect - afflicts vampires within pungent garlic zones.
 */
public class PotionGarlic extends PotionVampirismBase {

    public PotionGarlic(int id) {
        super(id, true, 0xFFFFFF, "garlic");
        registerModifier(
            SharedMonsterAttributes.movementSpeed,
            "1b36c4b2-2997-40fa-ba69-158a25bb7e22",
            -0.2D,
            2
        );
        registerModifier(
            SharedMonsterAttributes.attackDamage,
            "2b36c4b2-2997-40fa-ba69-158a25bb7e23",
            -2.0D,
            0
        );
    }
}
