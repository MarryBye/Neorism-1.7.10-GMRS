package com.marrybye.neorism.common.integration;

import com.marrybye.neorism.common.integration.nei.NEIConfig;
import com.marrybye.neorism.common.util.ModLogger;

/**
 * Dispatcher for NEI integration.
 */
public class NEIIntegration {

    public static void init() {
        try {
            new NEIConfig().loadConfig();
        } catch (Throwable t) {
            ModLogger.error("Failed to initialize NEI integration: %s", t.getMessage());
        }
    }
}
