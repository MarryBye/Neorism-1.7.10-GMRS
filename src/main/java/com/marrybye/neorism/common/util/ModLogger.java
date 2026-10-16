package com.marrybye.neorism.common.util;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.Neorism;

import net.minecraft.entity.Entity;

/**
 * Diagnostic logger for Neorism development.
 * Allows on-the-fly tracing of combat, AI, leveling, spawns, trades, and rituals.
 * Gated by Config.debugLogging so it has zero overhead when disabled in production.
 */
public class ModLogger {

    public static void debug(String format, Object... args) {
        if (Config.debugLogging) {
            Neorism.LOG.info("[DEBUG] " + String.format(format, args));
        }
    }

    public static void warn(String format, Object... args) {
        Neorism.LOG.warn("[WARN] " + String.format(format, args));
    }

    public static void error(String format, Object... args) {
        Neorism.LOG.error("[ERROR] " + String.format(format, args));
    }

    public static void combat(String format, Object... args) {
        if (Config.debugLogging && Config.debugCombat) {
            Neorism.LOG.info("[DEBUG:COMBAT] " + String.format(format, args));
        }
    }

    public static void ai(String format, Object... args) {
        if (Config.debugLogging && Config.debugAI) {
            Neorism.LOG.info("[DEBUG:AI] " + String.format(format, args));
        }
    }

    public static void leveling(String format, Object... args) {
        if (Config.debugLogging && Config.debugLeveling) {
            Neorism.LOG.info("[DEBUG:LEVEL] " + String.format(format, args));
        }
    }

    public static void spawn(String format, Object... args) {
        if (Config.debugLogging && Config.debugSpawning) {
            Neorism.LOG.info("[DEBUG:SPAWN] " + String.format(format, args));
        }
    }

    public static void trade(String format, Object... args) {
        if (Config.debugLogging && Config.debugTrading) {
            Neorism.LOG.info("[DEBUG:TRADE] " + String.format(format, args));
        }
    }

    public static void ritual(String format, Object... args) {
        if (Config.debugLogging && Config.debugRituals) {
            Neorism.LOG.info("[DEBUG:RITUAL] " + String.format(format, args));
        }
    }

    public static void rituals(String format, Object... args) {
        ritual(format, args);
    }

    public static String formatEntity(Entity entity) {
        if (entity == null) return "null";
        return String.format("%s[id=%d, pos=(%.1f, %.1f, %.1f)]",
            entity.getClass().getSimpleName(),
            entity.getEntityId(),
            entity.posX, entity.posY, entity.posZ
        );
    }
}
