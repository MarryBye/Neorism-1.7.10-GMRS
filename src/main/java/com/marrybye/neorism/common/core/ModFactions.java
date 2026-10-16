package com.marrybye.neorism.common.core;

import java.util.HashMap;
import java.util.Map;

import com.marrybye.neorism.api.faction.IPlayableFaction;
import com.marrybye.neorism.common.faction.PlayableFaction;

/**
 * Registry of core playable factions in Neorism.
 */
public class ModFactions {

    private static final Map<String, IPlayableFaction> FACTIONS_BY_ID = new HashMap<>();

    public static final IPlayableFaction NEUTRAL = register(
        new PlayableFaction("neutral", "faction.neorism.neutral", 0xAAAAAA, "\u00a77", 0, 0, true)
    );

    public static final IPlayableFaction VAMPIRE = register(
        new PlayableFaction("vampire", "faction.neorism.vampire", 0x800080, "\u00a75", 14, 5, false)
    );

    public static final IPlayableFaction HUNTER = register(
        new PlayableFaction("hunter", "faction.neorism.hunter", 0x0055FF, "\u00a79", 14, 5, false)
    );

    public static IPlayableFaction register(IPlayableFaction faction) {
        FACTIONS_BY_ID.put(faction.getId().toLowerCase(), faction);
        return faction;
    }

    public static IPlayableFaction getFaction(String id) {
        if (id == null) return NEUTRAL;
        IPlayableFaction f = FACTIONS_BY_ID.get(id.toLowerCase());
        return f != null ? f : NEUTRAL;
    }

    public static Map<String, IPlayableFaction> getAllFactions() {
        return FACTIONS_BY_ID;
    }
}
