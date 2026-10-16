package de.teamlapen.faction.api;

import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import de.teamlapen.faction.api.services.IFactionServices;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Global entry point for the FactionAPI framework (Backported from TeamLapen / Vampirism 2.0+).
 */
public class FactionsApi {

    private static IFactionServices services;

    public static void registerServices(IFactionServices serviceImpl) {
        services = serviceImpl;
    }

    public static IFactionServices services() {
        return services;
    }

    public static IFactionPlayerHandler factionPlayerHandler(EntityPlayer player) {
        if (player == null) return null;
        return (IFactionPlayerHandler) player.getExtendedProperties("neorism:faction_player");
    }
}
