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

    // --- World Generation ---
    public static final String CATEGORY_WORLD = "world";
    public static int biomeVampireForestId = 115;
    public static int dimensionVelmorraId = -25;
    public static boolean generateVampireForest = true;
    public static boolean generateVampireCrypt = true;
    public static boolean generateHunterCamp = true;
    public static boolean generateHunterTower = true;
    public static boolean generateAltarRuins = true;
    public static boolean generateDraculaCastle = true;
    public static boolean generateVelmorraPortal = true;
    public static boolean generateVampireHut = true;
    public static boolean generateVampireDungeon = true;
    public static boolean generateMotherTree = true;

    // --- Garlic Diffuser Balance ---
    public static int garlicDiffuserStartupTime = 100;
    public static int garlicDiffuserNormalDist = 0;
    public static int garlicDiffuserEnhancedDist = 1;
    public static int garlicDiffuserWeakDist = 2;

    public static void synchronizeConfiguration(File configFile) {
        configuration = new Configuration(configFile);
        configuration.load();

        // World settings
        biomeVampireForestId = configuration.getInt(
            "biomeVampireForestId",
            CATEGORY_WORLD,
            115,
            40,
            255,
            "Biome ID for the Vampire Forest"
        );
        dimensionVelmorraId = configuration.getInt(
            "dimensionVelmorraId",
            CATEGORY_WORLD,
            -25,
            -1000,
            1000,
            "Dimension ID for Velmorra (Dracula's Realm)"
        );
        generateVampireForest = configuration.getBoolean(
            "generateVampireForest",
            CATEGORY_WORLD,
            true,
            "Enable generation of Vampire Forest biome in the overworld"
        );
        generateVampireCrypt = configuration.getBoolean(
            "generateVampireCrypt",
            CATEGORY_WORLD,
            true,
            "Enable generation of subterranean Vampire Crypt dungeons"
        );
        generateHunterCamp = configuration.getBoolean(
            "generateHunterCamp",
            CATEGORY_WORLD,
            true,
            "Enable generation of surface Hunter Camps and Outposts"
        );
        generateHunterTower = configuration.getBoolean(
            "generateHunterTower",
            CATEGORY_WORLD,
            true,
            "Enable generation of fortified Hunter Watchtowers"
        );
        generateAltarRuins = configuration.getBoolean(
            "generateAltarRuins",
            CATEGORY_WORLD,
            true,
            "Enable generation of ancient Vampire Altar Ruins"
        );
        generateDraculaCastle = configuration.getBoolean(
            "generateDraculaCastle",
            CATEGORY_WORLD,
            true,
            "Enable generation of Dracula Castle boss arenas in Vampire Forests"
        );
        generateVelmorraPortal = configuration.getBoolean(
            "generateVelmorraPortal",
            CATEGORY_WORLD,
            true,
            "Enable generation of Velmorra Portal Ruins in Vampire Forests"
        );
        generateVampireHut = configuration.getBoolean(
            "generateVampireHut",
            CATEGORY_WORLD,
            true,
            "Enable generation of Vampire Huts in Vampire Forests"
        );
        generateVampireDungeon = configuration.getBoolean(
            "generateVampireDungeon",
            CATEGORY_WORLD,
            true,
            "Enable generation of subterranean Vampire Dungeons"
        );
        generateMotherTree = configuration.getBoolean(
            "generateMotherTree",
            CATEGORY_WORLD,
            true,
            "Enable generation of rare Mother Trees in Vampire Forests"
        );

        garlicDiffuserStartupTime = configuration.getInt(
            "garlicDiffuserStartupTime",
            CATEGORY_WORLD,
            100,
            1,
            24000,
            "Time in seconds for a Garlic Diffuser to boot up (canonical 100s)"
        );
        garlicDiffuserNormalDist = configuration.getInt(
            "garlicDiffuserNormalDist",
            CATEGORY_WORLD,
            0,
            0,
            5,
            "Chunk distance radius around a Normal Garlic Diffuser (0 = 1x1 chunk)"
        );
        garlicDiffuserEnhancedDist = configuration.getInt(
            "garlicDiffuserEnhancedDist",
            CATEGORY_WORLD,
            1,
            0,
            5,
            "Chunk distance radius around a Strong Garlic Diffuser (1 = 3x3 chunks)"
        );
        garlicDiffuserWeakDist = configuration.getInt(
            "garlicDiffuserWeakDist",
            CATEGORY_WORLD,
            2,
            0,
            5,
            "Chunk distance radius around a Long Garlic Diffuser (2 = 5x5 chunks)"
        );

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
