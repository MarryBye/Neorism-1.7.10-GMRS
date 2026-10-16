package com.marrybye.neorism.common.core;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

/**
 * Custom entity attributes for Neorism backported from modern Vampirism.
 */
public class ModAttributes {

    /**
     * Defines extra sun damage taken by vampires.
     */
    public static final IAttribute SUNDAMAGE = new RangedAttribute(
        "neorism.sundamage", 0.0D, 0.0D, 1000.0D
    ).setDescription("Sun Damage").setShouldWatch(true);

    /**
     * Modifies blood exhaustion burn rate from actions/sprinting.
     */
    public static final IAttribute BLOOD_EXHAUSTION = new RangedAttribute(
        "neorism.blood_exhaustion", 1.0D, 0.0D, 10.0D
    ).setDescription("Blood Exhaustion").setShouldWatch(true);

    /**
     * Modifies the duration of the neonatal weakness effect after resurrection.
     */
    public static final IAttribute NEONATAL_DURATION = new RangedAttribute(
        "neorism.neonatal_duration", 1.0D, 0.0D, Double.MAX_VALUE
    ).setDescription("Neonatal Duration").setShouldWatch(true);

    /**
     * Modifies down-but-not-out (DBNO) resurrection timer.
     */
    public static final IAttribute DBNO_DURATION = new RangedAttribute(
        "neorism.dbno_duration", 1.0D, 0.0D, Double.MAX_VALUE
    ).setDescription("DBNO Duration").setShouldWatch(true);

    /**
     * Bite attack damage dealt during feeding.
     */
    public static final IAttribute BITE_DAMAGE = new RangedAttribute(
        "neorism.bite_damage", 1.0D, 0.0D, 100.0D
    ).setDescription("Bite Damage").setShouldWatch(true);

    /**
     * Speed of blood extraction during biting.
     */
    public static final IAttribute BITE_SPEED = new RangedAttribute(
        "neorism.bite_speed", 1.0D, 0.1D, 10.0D
    ).setDescription("Bite Speed").setShouldWatch(true);
}
