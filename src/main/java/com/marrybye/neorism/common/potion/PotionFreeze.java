package com.marrybye.neorism.common.potion;

import net.minecraft.entity.SharedMonsterAttributes;

/**
 * Freeze effect - immobilizes the victim completely.
 */
public class PotionFreeze extends PotionVampirismBase {

    public PotionFreeze(int id) {
        super(id, true, 0xCCFFFF, "freeze");
        registerModifier(
            SharedMonsterAttributes.movementSpeed,
            "6a36c4b2-2997-40fa-ba69-158a25bb7e26",
            -1.0D,
            2 // Complete stop
        );
    }
}
