# Milestone Report: Phase 1 - Foundation, Core Registries, Damage Sources & Network Pipeline

> **Date:** 2026-10-16  
> **Status:** COMPLETED  
> **Phase:** 1.1 - 1.5  

---

## 1. Scope & Objectives
Implement the foundational infrastructure for Neorism in Minecraft Forge 1.7.10:
- Centralized mod lifecycle dispatchers (`ModRegistries`).
- Creative Tabs for main equipment and vampire lore books (`ModCreativeTabs`).
- Custom entity attributes ported from modern Vampirism (`ModAttributes`).
- Custom damage sources with death messages and bypass rules (`ModDamageSource`).
- Sound events mapping and audio dispatchers (`ModSounds`).
- Network pipeline via Forge SimpleNetworkWrapper (`PacketHandler`).
- Custom potion effects with safe dynamic potion array expansion (`ModPotions`, `PotionVampirismBase`, etc.).
- Base item/block registration foundation (`ModItems`, `ModBlocks`).

Modern reference sources referenced:
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/core/ModAttributes.java`
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/attachments/ModDamageSources.java`
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/core/ModSounds.java`
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/core/ModEffects.java`

---

## 2. Technical Implementation Details

### 2.1. Core Registries & Lifecycle (`ModRegistries.java`)
- Coordinated initialization across `preInit`, `init`, and `postInit` hooked into `CommonProxy`.
- Creative tabs: `neorism.main` (icon dynamically shows `ModItems.vampireFang` when initialized) and `neorism.books`.

### 2.2. Custom Entity Attributes (`ModAttributes.java`)
Implemented via Forge 1.7.10 `RangedAttribute` with `setShouldWatch(true)` for client syncing:
- `SUNDAMAGE`: Extra sun damage taken by vampires (base 0.0, max 1000.0).
- `BLOOD_EXHAUSTION`: Multiplier for blood hunger burn rate (base 1.0, max 10.0).
- `NEONATAL_DURATION`: Multiplier for debuff length following resurrection.
- `DBNO_DURATION`: Multiplier for down-but-not-out bleedout timer.
- `BITE_DAMAGE` & `BITE_SPEED`: Modifiers for blood extraction rate and damage during feeding.

### 2.3. Custom Damage Sources (`ModDamageSource.java`)
- `SUN_DAMAGE`: Bypasses armor, marked as fire damage.
- `VAMPIRE_ON_FIRE` & `VAMPIRE_IN_FIRE`: Fire-based damage sources.
- `HOLY_WATER`: Bypasses armor, marked as magic damage.
- `NO_BLOOD`: Absolute damage bypassing all armor when vampires starve.
- `BLEEDING` & `RITUAL_KNIFE`: Direct armor-bypassing bleed damage.
- `STAKE`: Instant-kill damage source for hunters.
- `causeBiteDamage(attacker)` & `causeIndirectHolyWaterDamage(proj, thrower)`.

### 2.4. Sound Events (`ModSounds.java`)
- Mapped all primary sound keys to namespace `neorism:` (`entity.vampire_scream`, `entity.vampire_bite`, `ambient.blood_drip`, `block.coffin_lid`, `entity.bat_swarm`, `fx.stake`, `fx.teleport_away`, `fx.freeze`, `fx.rage`, `fx.heartbeat`).
- Utility dispatchers: `playAtEntity(entity, sound, vol, pitch)` and `playSound(world, x, y, z, sound, vol, pitch)`.

### 2.5. Network Pipeline (`PacketHandler.java`)
- Backed by `SimpleNetworkWrapper` under channel `"neorism"`.
- Utility methods: `sendTo(player)`, `sendToAll()`, `sendToAllAround(point)`, `sendToDimension(dim)`, `sendToServer()`.

### 2.6. Custom Potions Engine (`ModPotions.java`, `PotionVampirismBase.java`)
- Implemented automatic reflection-based expansion of `Potion.potionTypes` to 256 slots to avoid vanilla 32-slot limits.
- Created potion classes:
  - `PotionSanguinare`: Inflicts slowness (-15%) and weakness (-4 attack damage) while incubating vampirism.
  - `PotionSunscreen`: -50% sun damage reduction attribute modifier.
  - `PotionGarlic`: Slowness (-20%) and weakness (-2 damage) within garlic zones.
  - `PotionNoBlood`: Periodic damage ticks and severe slowness (-40%).
  - `PotionFreeze`: 100% movement speed reduction.
  - `PotionBleeding`: Periodic bleed damage.

---

## 3. Diagnostic Logging Integration
- `ModLogger` calls inserted into registries (`preInit`, `init`, `postInit`).
- Registration of potions, blocks, items, and network channel logged with exact IDs.

---

## 4. Verification & Testing
- Gradle build test (`.\gradlew assemble`): **BUILD SUCCESSFUL** in 13s.
- Synced build jar: `run/client/mods/neorism-v0.1.0-master+941c705f79-dirty-dev.jar`.

---

## 5. Next Steps
Proceeding to **Phase 2: Faction Architecture & Player Capability Storage (IEEP)**:
- Implement `IFaction` and `ModFactions` (`NEUTRAL`, `VAMPIRE`, `HUNTER`).
- Implement `FactionPlayerHandler` via Forge `IExtendedEntityProperties`.
- Add `SyncFactionPacket` and event hooks for persistence across respawns.
