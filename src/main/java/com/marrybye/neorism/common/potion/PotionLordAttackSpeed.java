package com.marrybye.neorism.common.potion;

/**
 * Lord Attack Speed effect. Canonical port of Vampirism ModEffects.LORD_ATTACK_SPEED (+5% attack speed per level).
 * Minecraft 1.7.10 has no attack speed attribute (no attack cooldown), so this effect has no attribute modifier
 * and only exists so the canonical lord action has a visible, synced effect.
 */
public class PotionLordAttackSpeed extends PotionVampirismBase {

    public PotionLordAttackSpeed(int id) {
        super(id, false, 0xFFFFFF, "lord_attack_speed");
    }
}
