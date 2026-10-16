# Milestone Report: Phase 2 - Faction Architecture & Player Capability Storage (IEEP)

> **Date:** 2026-10-16  
> **Status:** COMPLETED  
> **Phase:** 2.1 - 2.4  

---

## 1. Scope & Objectives
Implement the player faction hierarchy and persistent data storage architecture in Minecraft Forge 1.7.10:
- Faction interfaces and data model (`IFaction`, `IPlayableFaction`, `IFactionPlayer`, `IFactionEntity`).
- Master faction definitions: `NEUTRAL` (Level 0), `VAMPIRE` (Levels 1-14, Lord Ranks 1-5), `HUNTER` (Levels 1-14, Lord Ranks 1-5).
- Faction hostility matrix and join/leave validation.
- Extended player properties via Forge `IExtendedEntityProperties` (`FactionPlayerHandler`).
- Persistence handling across player death and dimension traversal via `PlayerEvent.Clone`.
- Client-server synchronization via `SyncFactionPacket`.
- Faction disguise mechanics and query helper methods (`Helper.appearsAsVampire`, etc.).

Modern reference sources referenced:
- `reference/vampirism/src/main/java/de/teamlapen/faction/api/factions/IFaction.java`
- `reference/vampirism/src/main/java/de/teamlapen/faction/api/factions/IPlayableFaction.java`
- `reference/vampirism/src/main/java/de/teamlapen/faction/api/world/entities/player/IFactionPlayer.java`
- `reference/vampirism/src/main/java/de/teamlapen/faction/common/factions/FactionPlayerHandler.java`
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/core/ModFactions.java`
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/util/Helper.java`
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/entity/player/vampire/properties/VampireDisguise.java`

---

## 2. Technical Implementation Details

### 2.1. Faction Model & Hostility (`com.marrybye.neorism.api.faction`)
- **`IFaction`**: Defines ID, localized display name, chat color code, RGB integer color, max levels, and hostility checks (`isHostileTowards(other)`).
- **`IPlayableFaction`**: Extends `IFaction` with playable flag (`isPlayable()`), neutral flag (`isNeutral()`), and highest reachable level.
- **`ModFactions`**:
  - `NEUTRAL`: Max level 0, non-hostile by default.
  - `VAMPIRE`: Max level 14, Lord level 5, hostile towards `HUNTER`.
  - `HUNTER`: Max level 14, Lord level 5, hostile towards `VAMPIRE`.
- **Entity Interfaces**: `IFactionEntity`, `IVampireEntity`, `IHunterEntity` in `com.marrybye.neorism.api.entity`.

### 2.2. Extended Player Properties (`FactionPlayerHandler.java`)
- Registered under identifier `"neorism:faction_player"` implementing `IExtendedEntityProperties` and `IFactionPlayer`.
- Stores: `currentFaction`, `currentLevel`, `lordLevel`, and `disguised` state.
- Full NBT serialization and deserialization via compound tag `"NeorismFaction"`.
- Method `copyFrom(oldHandler)` for lossless data preservation during player respawn.
- Level bounds checking (`0` to `maxLevel`) and logging via `ModLogger.leveling(...)`.

### 2.3. Persistence & Lifecycle Events (`PlayerCapabilityEventHandler.java`)
- **`EntityConstructing`**: Automatically attaches `FactionPlayerHandler` to all `EntityPlayer` instances.
- **`PlayerEvent.Clone`**: Copies faction, level, lord title, and disguise state from `event.original` to `event.entityPlayer`, preserving progression across deaths and Nether/End teleports.
- **`EntityJoinWorldEvent` & `PlayerLoggedInEvent` & `PlayerRespawnEvent`**: Triggers immediate state synchronization to the client.

### 2.4. Client-Server Synchronization (`SyncFactionPacket.java`)
- Registered in `PacketHandler` on channel `"neorism"` directed to `Side.CLIENT`.
- Carries: entity ID, faction ID string, level integer, lord level integer, and disguise boolean.
- When received on client, locates `EntityPlayer` by entity ID and updates `FactionPlayerHandler.setClientData(...)`.
- Dispatched to the local player and all tracking players within 64 blocks (`PacketHandler.sendToAllAround`).

### 2.5. Utility Helper & Disguise System (`Helper.java`)
- Static query helpers: `isVampire(Entity)`, `isVampire(EntityPlayer)`, `isHunter(...)`, `getFaction(...)`, `getLevel(...)`, `getLordLevel(...)`.
- `appearsAsVampire(EntityPlayer player, Entity viewer)`: Returns false when disguised, preserving stealth in hostile territory.
- Sun calculation: Ported 1:1 celestial angle daylight check (`angle > 0.78F || angle < 0.24F`) and sky exposure logic.
- Item inventory utility: `shrinkItemStack(stack, player)` handling container/bucket returns properly.

---

## 3. Verification & Build Status
- **Build:** `./gradlew assemble` completed with `BUILD SUCCESSFUL in 13s`.
- **Artifact:** Output dev jar `neorism-v0.1.0-master.1+cb0a4ee9b1-dirty-dev.jar` deployed to `run/client/mods/`.
- **Diagnostic Logging:** Verified log points for leveling and disguise under `Config.debugLogging`.
