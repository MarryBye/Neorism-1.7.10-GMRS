package com.marrybye.neorism;

import com.marrybye.neorism.common.achievement.ModAchievements;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        Neorism.LOG.info("Initializing {} version {}", Neorism.NAME, Tags.VERSION);
        Neorism.LOG.info("An unofficial backport of Vampirism to Minecraft 1.7.10");
        Neorism.LOG.info("Original authors: maxanier, TeamLapen | Backport authors: MarryBye + Gemini AI");
    }

    public void init(FMLInitializationEvent event) {
        // Register achievements
        ModAchievements.init();
        // Register handlers, recipes, network packets
    }

    public void postInit(FMLPostInitializationEvent event) {
        // Cross-mod integrations
    }

    public void serverStarting(FMLServerStartingEvent event) {
        // Register server commands
    }
}
