---
name: debug-logging
description: Instructions and patterns for thorough real-time diagnostic logging of combat, AI, leveling, spawns, trading, and rituals with Config toggles.
---

# Diagnostic & Debug Logging System

To ensure immediate detection and resolution of bugs during live gameplay in the test client, Neorism implements extensive, categorized runtime logging.

## 1. Core Principles
1. **Log Everything Observable:** Whenever an action, calculation, state transition, or interaction happens, log it immediately with contextual information (attacker, victim, damage, player coordinates, level, trade details).
2. **Category Isolation:** Log messages must have standardized prefixes (`[DEBUG:COMBAT]`, `[DEBUG:AI]`, `[DEBUG:LEVEL]`, `[DEBUG:SPAWN]`, `[DEBUG:TRADE]`, `[DEBUG:RITUAL]`).
3. **Zero Production Overhead:** All debug calls must be gated behind `Config.debugLogging` (and category flags). In production, players can set `debugLogging = false` in `config/neorism.cfg` to disable all diagnostic output completely.

---

## 2. Using `ModLogger`

Always use the static methods in `com.marrybye.neorism.common.util.ModLogger`:

| Category | Method | When to Log |
| :--- | :--- | :--- |
| **Combat** | `ModLogger.combat(...)` | Attacking an entity, damage calculation (base vs modified), armor reduction, stake instant-kills, sweeping edge, bite blood drain. |
| **AI** | `ModLogger.ai(...)` | Target acquisition, aggro changes, sun-fleeing state changes, boss phase changes, minion command execution. |
| **Leveling** | `ModLogger.leveling(...)` | Gaining vampire/hunter experience, leveling up, unlocking skills, attribute modifier changes (max health, movement speed, attack damage). |
| **Spawn** | `ModLogger.spawn(...)` | Any mob spawn (vampires, hunters, converted mobs, minibosses, bosses), coordinates, biome, reason (natural, structure, ritual, minion call). |
| **Trade** | `ModLogger.trade(...)` | Opening trade window, completing trade, pricing changes with hunter trainers, vampire barons, or converted villagers. |
| **Rituals** | `ModLogger.ritual(...)` | Placing altar blocks, checking altar multiblocks, starting a ritual, consuming catalysts, completing or aborting a ritual. |
| **General** | `ModLogger.debug(...)` | Dimension changes, keybindings pressed, network packet reception. |

---

## 3. Formatting Standards
Always include entity identity, position, and quantitative metrics:
```java
// Example: Combat log
ModLogger.combat(
    "Player %s struck %s with %s. BaseDamage=%.1f, FinalDamage=%.1f, RemainingHealth=%.1f/%.1f",
    player.getCommandSenderName(),
    ModLogger.formatEntity(target),
    heldItem != null ? heldItem.getDisplayName() : "bare hands",
    baseDamage,
    finalDamage,
    target.getHealth(),
    target.getMaxHealth()
);

// Example: Spawning log
ModLogger.spawn(
    "Spawned %s at pos=(%.1f, %.1f, %.1f) in biome '%s'. SpawnReason=%s",
    ModLogger.formatEntity(mob),
    mob.posX, mob.posY, mob.posZ,
    mob.worldObj.getBiomeGenForCoords((int) mob.posX, (int) mob.posZ).biomeName,
    reason
);
```

---

## 4. Configuration Toggle in `neorism.cfg`
In `run/client/config/neorism.cfg`:
```ini
debug {
    B:debugLogging=true
    B:debugCombat=true
    B:debugAI=true
    B:debugLeveling=true
    B:debugSpawning=true
    B:debugTrading=true
    B:debugRituals=true
}
```
Setting `debugLogging=false` disables all debug logs instantly.
