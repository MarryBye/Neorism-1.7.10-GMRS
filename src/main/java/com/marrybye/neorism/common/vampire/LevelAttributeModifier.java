package com.marrybye.neorism.common.vampire;

import java.util.UUID;

import com.marrybye.neorism.common.core.ModAttributes;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Manages player level-scaled EntityAttribute modifiers.
 * Ported 1:1 from modern Vampirism LevelAttributeModifier.
 */
public class LevelAttributeModifier {

    public static final UUID HEALTH_MOD_UUID = UUID.fromString("6a35d980-9ec8-49f9-b883-2f08a54101e1");
    public static final UUID DAMAGE_MOD_UUID = UUID.fromString("6a35d980-9ec8-49f9-b883-2f08a54101e2");
    public static final UUID SPEED_MOD_UUID = UUID.fromString("6a35d980-9ec8-49f9-b883-2f08a54101e3");
    public static final UUID EXHAUSTION_MOD_UUID = UUID.fromString("6a35d980-9ec8-49f9-b883-2f08a54101e4");

    /**
     * Calculates the modifier value using modern Vampirism's exponential curve formula.
     *
     * @param level   The player's current faction level
     * @param lcap    The maximum level cap for this modifier
     * @param maxMod  The maximum modifier effect at lcap
     * @param type    The exponent (e.g. 0.5 for square root scaling, 1.0 for linear)
     * @return scaled modifier value between 0 and maxMod
     */
    public static double calculateModifierValue(int level, int lcap, double maxMod, double type) {
        if (level <= 0 || lcap <= 0) return 0.0D;
        return Math.pow(Math.min(level, lcap), type) / Math.pow(lcap, type) * maxMod;
    }

    /**
     * Applies a modifier to an attribute on an entity.
     */
    public static void applyModifier(EntityLivingBase entity, IAttribute attribute, UUID modifierId, String name,
                                     int level, int lcap, double maxMod, double type, int operation, boolean evenIntOnly) {
        if (entity == null || attribute == null) return;
        IAttributeInstance instance = entity.getEntityAttribute(attribute);
        if (instance != null) {
            AttributeModifier existing = instance.getModifier(modifierId);
            if (existing != null) {
                instance.removeModifier(existing);
            }
            if (level > 0) {
                double val = calculateModifierValue(level, lcap, maxMod, type);
                if (evenIntOnly) {
                    val = Math.round(val / 2.0D) * 2.0D;
                }
                if (Math.abs(val) > 0.0001D) {
                    AttributeModifier modifier = new AttributeModifier(modifierId, name, val, operation);
                    instance.applyModifier(modifier);
                }
            }
        }
    }

    /**
     * Applies all level modifiers for a vampire player.
     */
    public static void applyVampireModifiers(EntityPlayer player, int level, int maxLevel) {
        if (player == null) return;

        // Max Health boost: up to +20.0 HP (10 hearts) with square root curve, rounded to full hearts (even half-hearts)
        applyModifier(player, SharedMonsterAttributes.maxHealth, HEALTH_MOD_UUID, "Vampire Level Health",
            level, maxLevel, 20.0D, 0.5D, 0, true);

        // Attack damage boost: level 7+ gives +1, level 14 gives +2
        IAttributeInstance dmgInstance = player.getEntityAttribute(SharedMonsterAttributes.attackDamage);
        if (dmgInstance != null) {
            AttributeModifier existing = dmgInstance.getModifier(DAMAGE_MOD_UUID);
            if (existing != null) {
                dmgInstance.removeModifier(existing);
            }
            int bonusDamage = level >= 14 ? 2 : (level >= 7 ? 1 : 0);
            if (bonusDamage > 0) {
                dmgInstance.applyModifier(new AttributeModifier(DAMAGE_MOD_UUID, "Vampire Level Damage", bonusDamage, 0));
            }
        }

        // Movement Speed boost: up to +30% (0.3)
        applyModifier(player, SharedMonsterAttributes.movementSpeed, SPEED_MOD_UUID, "Vampire Level Speed",
            level, maxLevel, 0.3D, 0.5D, 2, false);

        // Blood Exhaustion reduction: up to -50% (-0.5)
        applyModifier(player, ModAttributes.BLOOD_EXHAUSTION, EXHAUSTION_MOD_UUID, "Vampire Level Exhaustion",
            level, maxLevel, -0.5D, 0.5D, 2, false);
    }

    /**
     * Removes all vampire level modifiers from the player.
     */
    public static void removeVampireModifiers(EntityPlayer player) {
        if (player == null) return;
        removeModifier(player, SharedMonsterAttributes.maxHealth, HEALTH_MOD_UUID);
        removeModifier(player, SharedMonsterAttributes.attackDamage, DAMAGE_MOD_UUID);
        removeModifier(player, SharedMonsterAttributes.movementSpeed, SPEED_MOD_UUID);
        removeModifier(player, ModAttributes.BLOOD_EXHAUSTION, EXHAUSTION_MOD_UUID);
    }

    private static void removeModifier(EntityLivingBase entity, IAttribute attribute, UUID id) {
        IAttributeInstance instance = entity.getEntityAttribute(attribute);
        if (instance != null) {
            AttributeModifier mod = instance.getModifier(id);
            if (mod != null) {
                instance.removeModifier(mod);
            }
        }
    }
}
