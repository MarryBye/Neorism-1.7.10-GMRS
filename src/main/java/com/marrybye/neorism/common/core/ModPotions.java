package com.marrybye.neorism.common.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.marrybye.neorism.common.potion.*;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.potion.Potion;

/**
 * Registry and management of custom Potion effects in Neorism.
 */
public class ModPotions {

    public static Potion sanguinare;
    public static Potion sunscreen;
    public static Potion garlic;
    public static Potion noBlood;
    public static Potion freeze;
    public static Potion bleeding;

    public static void init() {
        ensurePotionArrayCapacity(256);

        sanguinare = new PotionSanguinare(getNextFreePotionId());
        sunscreen = new PotionSunscreen(getNextFreePotionId());
        garlic = new PotionGarlic(getNextFreePotionId());
        noBlood = new PotionNoBlood(getNextFreePotionId());
        freeze = new PotionFreeze(getNextFreePotionId());
        bleeding = new PotionBleeding(getNextFreePotionId());

        ModLogger.debug("Registered Neorism potion effects: Sanguinare(id=%d), Sunscreen(id=%d), Garlic(id=%d), NoBlood(id=%d), Freeze(id=%d), Bleeding(id=%d)",
            sanguinare.id, sunscreen.id, garlic.id, noBlood.id, freeze.id, bleeding.id);
    }

    private static int getNextFreePotionId() {
        for (int i = 32; i < Potion.potionTypes.length; i++) {
            if (Potion.potionTypes[i] == null) {
                return i;
            }
        }
        throw new IllegalStateException("No free potion IDs available in Potion.potionTypes array!");
    }

    private static void ensurePotionArrayCapacity(int targetSize) {
        if (Potion.potionTypes.length < targetSize) {
            try {
                Field potionTypesField = null;
                for (Field field : Potion.class.getDeclaredFields()) {
                    if (field.getType().isArray() && field.getType().getComponentType().equals(Potion.class)) {
                        potionTypesField = field;
                        break;
                    }
                }
                if (potionTypesField != null) {
                    potionTypesField.setAccessible(true);
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(potionTypesField, potionTypesField.getModifiers() & ~Modifier.FINAL);

                    Potion[] newPotionTypes = new Potion[targetSize];
                    System.arraycopy(Potion.potionTypes, 0, newPotionTypes, 0, Potion.potionTypes.length);
                    potionTypesField.set(null, newPotionTypes);
                    ModLogger.debug("Expanded Potion.potionTypes array from %d to %d", Potion.potionTypes.length, targetSize);
                }
            } catch (Exception e) {
                ModLogger.debug("Note: Potion array expansion bypassed: %s", e.getMessage());
            }
        }
    }
}
