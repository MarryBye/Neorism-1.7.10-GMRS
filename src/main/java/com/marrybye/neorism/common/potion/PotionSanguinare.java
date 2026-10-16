package com.marrybye.neorism.common.potion;

import net.minecraft.entity.SharedMonsterAttributes;

/**
 * Sanguinare Vampiris - the vampirism infection effect.
 * Slowly weakens the victim and transforms them into a vampire upon duration expiry.
 */
public class PotionSanguinare extends PotionVampirismBase {

    public PotionSanguinare(int id) {
        super(id, false, 0x6A0888, "sanguinare");
        registerModifier(
            SharedMonsterAttributes.movementSpeed,
            "3c36c4b2-2997-40fa-ba69-158a25bb7e20",
            -0.15D,
            2 // Operation: Multiply
        );
        registerModifier(
            SharedMonsterAttributes.attackDamage,
            "f057c3e8-f7aa-42b7-a3e9-74d75f284e36",
            -4.0D,
            0 // Operation: Add
        );
    }
}
