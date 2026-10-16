package com.marrybye.neorism.common.action;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.marrybye.neorism.api.action.IAction;
import com.marrybye.neorism.common.action.hunter.HunterAwarenessAction;
import com.marrybye.neorism.common.action.hunter.PotionResistanceAction;
import com.marrybye.neorism.common.action.vampire.*;
import com.marrybye.neorism.common.util.ModLogger;

/**
 * Registry of all playable actions and abilities in Neorism.
 */
public class ActionRegistry {

    private static final Map<String, IAction> ACTIONS = new HashMap<String, IAction>();

    public static void init() {
        register(new NightVisionAction());
        register(new VampireRegenAction());
        register(new VampireRageAction());
        register(new BatFormAction());
        register(new TeleportAction());
        register(new FreezeAction());
        register(new DarkBloodProjectileAction());
        register(new SunscreenAction());
        register(new InvisibilityAction());
        register(new SummonBatsAction());

        register(new HunterAwarenessAction());
        register(new PotionResistanceAction());

        ModLogger.debug("Registered %d core actions in ActionRegistry", ACTIONS.size());
    }

    public static void register(IAction action) {
        if (action != null) {
            ACTIONS.put(action.getId(), action);
        }
    }

    public static IAction getAction(String id) {
        return ACTIONS.get(id);
    }

    public static Collection<IAction> getAllActions() {
        return Collections.unmodifiableCollection(ACTIONS.values());
    }
}
