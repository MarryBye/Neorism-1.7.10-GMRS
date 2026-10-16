# Milestone Report: Phase 6 - Action Engine & Skill Tree Subsystems

> **Date:** 2026-10-16  
> **Status:** COMPLETED  
> **Git Commit:** (Staged for commit)  

---

## 1. Scope & Objectives
Full implementation of the Action Engine and Skill Tree progression systems for both Vampire and Hunter factions. This includes active action handlers, cooldown and duration management, network synchronization packets, client keybinding triggers, a comprehensive set of 12 active/passive abilities, projectile entities, and combat integration hooks.

Ported from modern Vampirism (`ActionHandler`, `SkillHandler`, `VampireActions`, `HunterActions`, `DarkBloodProjectile`).

---

## 2. Technical Implementation Details

### A. Core Action Framework
- **`IAction.java`** (`com.marrybye.neorism.api.action`):
  - Defines action contract: unique ID, cooldown ticks, active duration ticks, blood cost, toggleability, garlic check, activation validation, lifecycle callbacks (`onActivated`, `onDeactivated`, `onUpdate`).
- **`ActionBase.java`** (`com.marrybye.neorism.common.action`):
  - Standard base implementation with garlic suppression checks and duration logic.
- **`ActionHandler.java`** (`com.marrybye.neorism.common.action`):
  - Extended entity properties (`neorism:action_handler`) on `EntityPlayer`.
  - Manages active action timers and cooldown timers on both client and server.
  - Automatically consumes vampire blood when triggering blood-cost actions.
  - Syncs cooldowns and active states to client via `SyncActionsPacket`.
- **`ActionRegistry.java`** (`com.marrybye.neorism.common.action`):
  - Central registry indexing all action definitions by string identifier.

### B. Skill Progression System
- **`SkillHandler.java`** (`com.marrybye.neorism.common.skills`):
  - Extended entity properties (`neorism:skill_handler`) on `EntityPlayer`.
  - Tracks available skill points (1 skill point awarded per faction level-up).
  - Tracks unlocked skill nodes and primary/secondary action bindings.
  - Persistent save/load via NBT (`NeorismSkills` compound tag).
  - Full synchronization to client with `SyncSkillsPacket`.

### C. Vampire Abilities Ported
- **`NightVisionAction`** (`night_vision`):
  - Toggles vampire dark vision without green potion tint.
- **`VampireRegenAction`** (`regen`):
  - Instant high-tier regeneration burst consuming blood saturation.
- **`VampireRageAction`** (`vampire_rage`):
  - Grants Strength II and Speed II while increasing blood exhaustion.
- **`BatFormAction`** (`bat`):
  - Toggles flight mode, disables fall damage, increases sun vulnerability.
- **`TeleportAction`** (`teleport`):
  - Instant blink 16 blocks forward through air and transparent blocks with ender sound and dark particles.
- **`FreezeAction`** (`freeze`):
  - Area of effect freezing cold (Slowness IV & Mining Fatigue III for 8 seconds within 8 blocks) targeting non-vampires.
- **`DarkBloodProjectileAction`** (`dark_blood_projectile`):
  - Fires an `EntityDarkBloodProjectile` dealing 8.0 damage and restoring health to the caster.
- **`SunscreenAction`** (`sunscreen`):
  - Grants temporary Sunscreen potion effect protecting against sunlight burn.
- **`InvisibilityAction`** (`vampire_invisibility`):
  - Complete stealth camouflage with dark smoke particles.
- **`SummonBatsAction`** (`summon_bats`):
  - Spawns a protective swarm of blood bats around the player.

### D. Hunter Abilities & Passive Perks Ported
- **`HunterAwarenessAction`** (`awareness_hunter`):
  - Scans a 24-block radius for vampires and undead through solid blocks, alerting the hunter in chat and spawning locator particles.
- **`PotionResistanceAction`** (`potion_resistance_hunter`):
  - Instant cleansing of harmful debuffs (Poison, Wither, Weakness, Slowness, Blindness, Bleeding) + 4 HP divine heal.
- **`CrossbowTechnique`** (`crossbow_technique`):
  - Passive +25% projectile damage against hostile targets in `VampireCombatEventHandler`.
- **`StakeMastery`** (`stake_mastery`):
  - Increases stake instant-kill backstab threshold to 60% HP / 14 HP (from 40% / 8 HP) and doubles frontal strike bonus to 6.0 HP in `ItemStake`.
- **`MasterBrewer` & `WeaponCrafting`**:
  - Progression skill nodes for hunter alchemy and weapon workbench recipes.

### E. Projectiles & Client Rendering
- **`EntityDarkBloodProjectile.java`** (`com.marrybye.neorism.common.entity.projectile`):
  - Custom magic projectile with particle trail and life-steal mechanics.
- **`RenderDarkBloodProjectile.java`** (`com.marrybye.neorism.client.render.entity`):
  - Angelica / Sodium compatible billboard renderer.
- **`KeyBindings.java`** (`com.marrybye.neorism.client.input`):
  - Registered `keyAction1` (Key R) and `keyAction2` (Key Z) dispatching `ActionKeyPacket`.

### F. Network Architecture
- `ActionKeyPacket`: client -> server key press dispatcher.
- `SyncActionsPacket`: server -> client cooldown and active state synchronizer.
- `UnlockSkillPacket`: client -> server skill unlock request.
- `SyncSkillsPacket`: server -> client skill tree state synchronizer.

---

## 3. Verification & Validation
- **Compilation:** `.\gradlew assemble` passed cleanly with 0 errors.
- **Localization:** All action keybindings and status messages registered in `en_US.lang` and `ru_RU.lang`.
- **Logging:** All activations, cooldown events, and combat modifiers logged via `ModLogger` (`[DEBUG:COMBAT]` / `[DEBUG:LEVEL]`).
