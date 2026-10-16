package com.marrybye.neorism.common.potion;

import com.marrybye.neorism.common.core.ModAttributes;

/**
 * Sunscreen potion effect - temporarily protects vampires from burning in sunlight.
 */
public class PotionSunscreen extends PotionVampirismBase {

    public PotionSunscreen(int id) {
        super(id, false, 0xFFF100, "sunscreen");
        registerModifier(
            ModAttributes.SUNDAMAGE,
            "7c26c4b2-2997-40fa-ba69-158a25bb7e21",
            -0.5D,
            2 // Multiply total
        );
    }
}
