package com.marrybye.neorism.common.potion;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;

/**
 * Base class for all Neorism custom potion effects in 1.7.10.
 */
public class PotionVampirismBase extends Potion {

    public PotionVampirismBase(int id, boolean isBadEffect, int liquidColor, String unlocalizedName) {
        super(id, isBadEffect, liquidColor);
        setPotionName("potion." + unlocalizedName);
    }

    /**
     * Helper to attach attribute modifiers using 1.7.10 SRG method func_111184_a.
     */
    public PotionVampirismBase registerModifier(IAttribute attribute, String uuid, double amount, int operation) {
        func_111184_a(attribute, uuid, amount, operation);
        return this;
    }
}
