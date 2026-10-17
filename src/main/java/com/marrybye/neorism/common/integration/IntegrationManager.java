package com.marrybye.neorism.common.integration;

import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

/**
 * Master dispatcher for cross-mod integrations in the GT New Horizons ecosystem.
 */
public class IntegrationManager {

    private static boolean etFuturumLoaded = false;
    private static boolean combatBackportLoaded = false;
    private static boolean villagerTradesLoaded = false;
    private static boolean villageNamesLoaded = false;
    private static boolean hodgepodgeLoaded = false;
    private static boolean neiLoaded = false;

    public static void postInit(FMLPostInitializationEvent event) {
        ModLogger.debug("Initializing Neorism cross-mod integrations...");

        etFuturumLoaded = Loader.isModLoaded("etfuturum");
        combatBackportLoaded = Loader.isModLoaded("combatbackport");
        villagerTradesLoaded = Loader.isModLoaded("villagertradesbackport");
        villageNamesLoaded = Loader.isModLoaded("villagenames");
        hodgepodgeLoaded = Loader.isModLoaded("hodgepodge");
        neiLoaded = Loader.isModLoaded("NotEnoughItems");

        if (etFuturumLoaded) {
            EtFuturumIntegration.init();
        }

        if (combatBackportLoaded) {
            CombatBackportIntegration.init();
        }

        if (villagerTradesLoaded) {
            VillagerTradesBackportIntegration.init();
        }

        if (villageNamesLoaded) {
            VillageNamesIntegration.init();
        }

        if (hodgepodgeLoaded) {
            HodgepodgeIntegration.init();
        }

        if (neiLoaded) {
            NEIIntegration.init();
        }

        ModLogger.debug("Cross-mod integrations complete: [EtFuturum: %b, CombatBackport: %b, VillagerTrades: %b, VillageNames: %b, Hodgepodge: %b, NEI: %b]",
            etFuturumLoaded, combatBackportLoaded, villagerTradesLoaded, villageNamesLoaded, hodgepodgeLoaded, neiLoaded);
    }

    public static boolean isEtFuturumLoaded() {
        return etFuturumLoaded;
    }

    public static boolean isCombatBackportLoaded() {
        return combatBackportLoaded;
    }

    public static boolean isVillagerTradesLoaded() {
        return villagerTradesLoaded;
    }

    public static boolean isVillageNamesLoaded() {
        return villageNamesLoaded;
    }

    public static boolean isHodgepodgeLoaded() {
        return hodgepodgeLoaded;
    }

    public static boolean isNeiLoaded() {
        return neiLoaded;
    }
}
