package com.marrybye.neorism;

import com.marrybye.neorism.common.core.ModRegistries;
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

        ModRegistries.preInit(event);
    }

    public void init(FMLInitializationEvent event) {
        ModRegistries.init(event);
    }

    public void postInit(FMLPostInitializationEvent event) {
        ModRegistries.postInit(event);
    }

    public void serverStarting(FMLServerStartingEvent event) {
        // Register server commands
    }
}
