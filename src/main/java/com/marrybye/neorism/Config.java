package com.marrybye.neorism;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static Configuration configuration;

    // --- Debug & Diagnostic Logging ---
    public static final String CATEGORY_DEBUG = "debug";
    public static boolean debugLogging = true;
    public static boolean debugCombat = true;
    public static boolean debugAI = true;
    public static boolean debugLeveling = true;
    public static boolean debugSpawning = true;
    public static boolean debugTrading = true;
    public static boolean debugRituals = true;

    public static void synchronizeConfiguration(File configFile) {
        configuration = new Configuration(configFile);
        configuration.load();

        // Debug settings
        debugLogging = configuration.getBoolean(
            "debugLogging",
            CATEGORY_DEBUG,
            true,
            "Master switch for detailed diagnostic logging during development. Set false in production."
        );
        debugCombat = configuration.getBoolean(
            "debugCombat",
            CATEGORY_DEBUG,
            true,
            "Log combat events: weapon hits, damage formulas, knockback, armor reductions."
        );
        debugAI = configuration.getBoolean(
            "debugAI",
            CATEGORY_DEBUG,
            true,
            "Log entity AI events: target acquisition, pathfinding, behavior switching."
        );
        debugLeveling = configuration.getBoolean(
            "debugLeveling",
            CATEGORY_DEBUG,
            true,
            "Log leveling events: level changes, skill unlocks, stat modifications."
        );
        debugSpawning = configuration.getBoolean(
            "debugSpawning",
            CATEGORY_DEBUG,
            true,
            "Log entity spawning: vampires, hunters, minions, minibosses, bosses."
        );
        debugTrading = configuration.getBoolean(
            "debugTrading",
            CATEGORY_DEBUG,
            true,
            "Log trading events with hunter trainers, vampire barons, and converted villagers."
        );
        debugRituals = configuration.getBoolean(
            "debugRituals",
            CATEGORY_DEBUG,
            true,
            "Log altar and ritual state machine transitions, recipes, and errors."
        );

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
