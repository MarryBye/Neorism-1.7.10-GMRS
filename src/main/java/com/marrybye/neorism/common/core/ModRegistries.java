package com.marrybye.neorism.common.core;

import com.marrybye.neorism.common.achievement.ModAchievements;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Master registries dispatcher for Neorism lifecycle events.
 */
public class ModRegistries {

    public static void preInit(FMLPreInitializationEvent event) {
        ModLogger.debug("Beginning Neorism preInit registries");
        ModPotions.init();
        ModItems.init();
        ModBlocks.init();
        PacketHandler.init();
    }

    public static void init(FMLInitializationEvent event) {
        ModLogger.debug("Beginning Neorism init registries");
        ModAchievements.init();
    }

    public static void postInit(FMLPostInitializationEvent event) {
        ModLogger.debug("Beginning Neorism postInit integrations");
    }
}
