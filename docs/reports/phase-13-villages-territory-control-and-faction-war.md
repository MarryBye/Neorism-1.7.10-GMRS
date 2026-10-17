# Phase 13 Completion Report: Villages, Territory Control & Faction War

**Mod:** Neorism: Become a Vampire (Minecraft 1.7.10)  
**Phase:** 13 — Villages, Territory Control & Faction War  
**Reference Codebase:** `reference/vampirism/src/main/java/de/teamlapen/faction/common/factions/FactionVillage.java`, `TotemBaseBlock.java`, `TotemTopBlock.java`, `TotemBlockEntity.java`, `TotemRenderer.java`  
**Date:** October 17, 2026  
**Status:** Completed & Build Verified (`.\gradlew assemble` SUCCESS)

---

## 1. Executive Summary

Phase 13 implements the territory conquest and faction war subsystem, allowing Vampire and Hunter players to wage siege raids on villages, capture territory, raise faction totems with towering beacon beams, and defend their settlements. All mechanics strictly adhere to TeamLapen's canonical `FactionVillage`, `TotemBlockEntity`, and `IVillageCaptureEntity` architectures adapted for Minecraft 1.7.10.

---

## 2. Ported Architectural Components

### 2.1. Village Territory Subsystem (`FactionVillage.java`)
- Tracks village radius, bounding boxes, controlling faction (`Neutral`, `Vampire`, `Hunter`), and active raid state.
- Manages persistent NBT serialization and faction flips upon completed sieges.

### 2.2. Faction Totem Multiblock (`BlockTotem`, `BlockTotemTop`, `TileEntityTotem`)
- **`BlockTotem` (`totem_base`)**:
  - Lower stone pedestal (`Material.rock`, hardness 5.0F).
  - Handles multiblock break cascading (breaking base destroys top) and interaction forwarding.
- **`BlockTotemTop` (`totem_top`)**:
  - Upper multiblock crown with glowing faction core.
  - Subtypes / metadata: 0 = Neutral, 1 = Vampire, 2 = Hunter.
  - Houses `TileEntityTotem`.
- **`TileEntityTotem`**:
  - Implements canonical `ITotem` and `ICaptureAttributes`.
  - State machine supporting Neutral, Vampire, and Hunter control.
  - Initiates village siege raids when right-clicked by an opposing faction player.
  - Manages real-time capture calculation: attacker strength vs defender strength within a 32-block radius.
  - Periodically summons defender reinforcement waves during active sieges.
  - Victory triggers village ownership transfer, lightning sound, and local broadcasts.
- **`TileEntityTotemRenderer`**:
  - TESR rendering a faction beacon beam extending into the sky (Crimson Red for Vampires, Azure Blue for Hunters).
  - Pulsates dynamically during active raids reflecting real-time capture progression.
  - 100% compatible with Angelica, Sodium, and GTNHLib pipelines.

### 2.3. Village Raid & Capture Mechanics (`IVillageCaptureEntity`)
- `IVillageCaptureEntity` interface implemented by `EntityVampireBase` and `EntityHunterBase`.
- Entities dynamically switch goals to attack or defend the village totem during sieges.

### 2.4. Faction Villagers & Territory Effects (`VillageFactionHandler`)
- **Vampire Villages:**
  - Vampire players can peacefully feed on villagers (`Shift` + right-click with empty hand) without alarming Iron Golems.
  - Iron Golems are pacified towards vampire players inside vampire villages.
  - Hostile zombie spawns convert into Vampire patrols.
- **Hunter Villages:**
  - Hunter players receive friendly welcomes from villagers.
  - Natural spawns convert into Hunter patrols.

---

## 3. Verification & Build Status

- **Compilation:** `BUILD SUCCESSFUL in 7s` via `.\gradlew assemble`.
- **Blocks Registered:** `totem_base`, `totem_top`.
- **Tile Entities Registered:** `neorism:totem`.
- **Event Bus:** `VillageFactionHandler` registered in `ModRegistries.init()`.
- **Localization:** Full Russian (`ru_RU`) and English (`en_US`) string coverage.
