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
        ModEntities.init();
        PacketHandler.init();
    }

    public static void init(FMLInitializationEvent event) {
        ModLogger.debug("Beginning Neorism init registries");
        ModAchievements.init();
        ModRecipes.init();

        // Register event handlers
        com.marrybye.neorism.common.events.PlayerCapabilityEventHandler playerCapHandler =
            new com.marrybye.neorism.common.events.PlayerCapabilityEventHandler();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(playerCapHandler);
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(playerCapHandler);

        com.marrybye.neorism.common.events.VampireCombatEventHandler combatHandler =
            new com.marrybye.neorism.common.events.VampireCombatEventHandler();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(combatHandler);
    }

    public static void postInit(FMLPostInitializationEvent event) {
        ModLogger.debug("Beginning Neorism postInit integrations");
    }
}
