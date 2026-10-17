package com.marrybye.neorism.common.integration;

import com.marrybye.neorism.common.util.ModLogger;
import astrotibs.villagenames.name.NameGenerator;

import java.util.Random;

/**
 * Integration with VillageNames mod.
 * Supports:
 * - Procedural gothic and vampiric name generation for captured villages and crypts.
 */
public class VillageNamesIntegration {

    private static final String[] GOTHIC_PREFIXES = {
        "Castle of ", "Blood Keep of ", "Shadows of ", "Crypt of ", "Fortress of ", "Barony of "
    };

    private static final String[] VAMPIRIC_FALLBACKS = {
        "Barovia", "Ravenloft", "Sylvania", "Karasburg", "Drachenfels", "Mortheim", "Karnstein", "Mirkwood", "Shadowglen", "Vampyria", "Blutstein"
    };

    public static void init() {
        ModLogger.debug("VillageNames integration active: Procedural gothic naming enabled");
    }

    /**
     * Generates a procedural gothic village or structure name.
     */
    public static String generateGothicName(Random random) {
        if (random == null) random = new Random();
        try {
            String[] generated = NameGenerator.newRandomName("vampire", random);
            if (generated != null && generated.length > 0 && generated[0] != null && !generated[0].isEmpty()) {
                return generated[0];
            }
        } catch (Throwable ignored) {
        }

        // Fallback procedural generator
        String prefix = GOTHIC_PREFIXES[random.nextInt(GOTHIC_PREFIXES.length)];
        String base = VAMPIRIC_FALLBACKS[random.nextInt(VAMPIRIC_FALLBACKS.length)];
        return prefix + base;
    }

    /**
     * Generates a gothic crypt name.
     */
    public static String generateCryptName(Random random) {
        if (random == null) random = new Random();
        String base = VAMPIRIC_FALLBACKS[random.nextInt(VAMPIRIC_FALLBACKS.length)];
        return base + " Crypt";
    }
}
