package com.marrybye.neorism.common.integration;

import com.marrybye.neorism.common.util.ModLogger;

/**
 * Status helper for NEI integration.
 * Actual handler registration happens solely client-side via {@code NEIConfig} (IConfigureNEI).
 */
public class NEIIntegration {

    public static void init() {
        ModLogger.debug("NEI detected; client plugin is loaded automatically via IConfigureNEI.");
    }
}
