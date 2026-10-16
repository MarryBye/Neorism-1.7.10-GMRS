package com.marrybye.neorism.common.world.garlic;

/**
 * Strength tiers for garlic emitters and holy water.
 * Ported from modern Vampirism EnumStrength.
 */
public enum EnumStrength {
    NONE("none", 0),
    WEAK("weak", 1),
    MEDIUM("medium", 2),
    STRONG("strong", 3);

    private final String name;
    private final int strength;

    EnumStrength(String name, int strength) {
        this.name = name;
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }

    public String getName() {
        return name;
    }

    public boolean isStrongerThan(EnumStrength compare) {
        return compare == null || this.strength > compare.strength;
    }
}
