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
        ModFluids.init();
        ModBlocks.init();
        ModItems.init();
        ModEntities.init();
        ModBiomes.init();
        PacketHandler.init();
    }

    public static void init(FMLInitializationEvent event) {
        ModLogger.debug("Beginning Neorism init registries");
        ModAchievements.init();
        ModRecipes.init();
        com.marrybye.neorism.common.world.gen.ModWorldGen.init();
        com.marrybye.neorism.common.action.ActionRegistry.init();
        com.marrybye.neorism.common.skills.SkillRegistry.init();

        // Register event handlers
        com.marrybye.neorism.common.events.PlayerCapabilityEventHandler playerCapHandler =
            new com.marrybye.neorism.common.events.PlayerCapabilityEventHandler();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(playerCapHandler);
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(playerCapHandler);

        com.marrybye.neorism.common.events.VampireCombatEventHandler combatHandler =
            new com.marrybye.neorism.common.events.VampireCombatEventHandler();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(combatHandler);

        com.marrybye.neorism.common.events.WeaponOilEventHandler oilHandler =
            new com.marrybye.neorism.common.events.WeaponOilEventHandler();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(oilHandler);

        com.marrybye.neorism.common.events.VillageFactionHandler villageHandler =
            new com.marrybye.neorism.common.events.VillageFactionHandler();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(villageHandler);
    }

    public static void postInit(FMLPostInitializationEvent event) {
        ModLogger.debug("Beginning Neorism postInit integrations");
        com.marrybye.neorism.common.integration.IntegrationManager.postInit(event);
    }
}
