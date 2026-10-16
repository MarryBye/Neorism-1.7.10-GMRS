package com.marrybye.neorism.common.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

/**
 * Custom damage sources for Neorism backported from modern Vampirism.
 */
public class ModDamageSource extends DamageSource {

    public static final DamageSource SUN_DAMAGE = new ModDamageSource("neorism.sun_damage")
        .setDamageBypassesArmor()
        .setFireDamage();

    public static final DamageSource VAMPIRE_ON_FIRE = new ModDamageSource("neorism.vampire_on_fire")
        .setFireDamage();

    public static final DamageSource VAMPIRE_IN_FIRE = new ModDamageSource("neorism.vampire_in_fire")
        .setFireDamage();

    public static final DamageSource HOLY_WATER = new ModDamageSource("neorism.holy_water")
        .setDamageBypassesArmor()
        .setMagicDamage();

    public static final DamageSource NO_BLOOD = new ModDamageSource("neorism.no_blood")
        .setDamageBypassesArmor()
        .setDamageIsAbsolute();

    public static final DamageSource BLEEDING = new ModDamageSource("neorism.bleeding")
        .setDamageBypassesArmor();

    public static final DamageSource RITUAL_KNIFE = new ModDamageSource("neorism.ritual_knife")
        .setDamageBypassesArmor();

    public static final DamageSource MOTHER = new ModDamageSource("neorism.mother")
        .setDamageBypassesArmor()
        .setMagicDamage();

    public static final DamageSource STAKE = new ModDamageSource("neorism.stake")
        .setDamageBypassesArmor();

    public ModDamageSource(String damageType) {
        super(damageType);
    }

    public static DamageSource causeBiteDamage(EntityLivingBase attacker) {
        return new EntityDamageSource("neorism.bite", attacker);
    }

    public static DamageSource causeIndirectHolyWaterDamage(Entity projectile, Entity thrower) {
        return new EntityDamageSourceIndirect("neorism.holy_water", projectile, thrower)
            .setDamageBypassesArmor()
            .setMagicDamage();
    }
}
