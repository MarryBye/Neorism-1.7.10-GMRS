# Milestone Report: Phase 9 - Entities, Mobs & Custom AI Subsystem

> **Date:** 2026-10-16  
> **Status:** COMPLETED  
> **Git Commit:** (Staged for commit)  

---

## 1. Scope & Objectives
Full implementation of Neorism's living mob ecosystem: custom goal-oriented AI tasks, faction base hierarchy, vampires, hunters, converted creatures, legendary boss encounters (Lord Dracula, Vampire Mother), and Angelica-compatible client renderers.

Ported from modern Vampirism (`VampireEntity`, `HunterEntity`, `EntityAISunFlee`, `EntityAIBiteLiving`, `EntityAICrossbowAttack`, `EntityDracula`, `ConvertedCreatures`).

---

## 2. Technical Implementation Details

### A. Entity Base Hierarchy & Faction Integration
- **`EntityVampireBase.java`** (`com.marrybye.neorism.common.entity.vampire`):
  - Extends `EntityCreature`, implements `IVampireEntity`, `IFactionEntity`, `IMob`.
  - Belongs to `ModFactions.VAMPIRE`.
  - Sunlight damage calculation: burns when exposed to daytime direct sky light (`worldObj.canBlockSeeTheSky`) unless in water, protected by a Fog Diffuser (`TileEntityFogDiffuser.isEntityProtected`), or possessing sunscreen protection.
  - Thirst for blood (`wantsBlood()`): triggers when wounded or low on blood reserve.
  - Overrides `getCanSpawnHere()` to require darkness / shadow (light <= 7).
- **`EntityHunterBase.java`** (`com.marrybye.neorism.common.entity.hunter`):
  - Extends `EntityCreature`, implements `IHunterEntity`, `IFactionEntity`.
  - Belongs to `ModFactions.HUNTER`.
  - Targets vampire entities and vampire players on sight.

### B. Custom AI Tasks (Forge 1.7.10 `EntityAIBase`)
- **`EntityAISunFlee.java`** (`com.marrybye.neorism.common.entity.ai`):
  - Actively scans a 17x7x17 radius for shade (solid roof, canopy, cave, or water) as soon as daylight strikes the vampire.
  - Disengages normal wandering and sprints towards shelter at 1.25x speed.
- **`EntityAIBiteLiving.java`**:
  - Allows vampires to latch onto living creatures with blood reserves (`IBiteableEntity` or `ExtendedCreature`).
  - Drains blood, heals the vampire (+1.5x blood drained), triggers blood particles (`reddust`), and plays bite sounds.
- **`EntityAICrossbowAttack.java`**:
  - Marksman AI: maintains 8-18 block tactical distance, aims and fires `EntityQuarrel` crossbow bolts.
  - If a vampire closes in (< 6 blocks), throws an `EntityHolyWaterBottle` to knock back and weaken the vampire.
- **`EntityAIAttackFaction.java`**:
  - Dynamically acquires targets from opposing factions (Hunters vs Vampires) and identifies prey when blood is desired.

### C. Vampire Entities & Minibosses
- **`EntityVampire.java`**:
  - Basic melee vampire roaming at night with 12+ skin textures.
  - Attacks can transmit `Sanguinare Vampiris` to neutral players and villagers. Drops vampire fangs and blood bottles.
- **`EntityAdvancedVampire.java`**:
  - Elite vampire armed with `ItemHeartStriker` and crown.
  - Tactical flash step / teleportation behind targets (> 6 blocks away).
- **`EntityVampireBaron.java` & `EntityVampireBaroness.java`**:
  - Ancient crypt minibosses (150 HP).
  - Casts `EntityDarkBloodProjectile` and summons blood bat swarms when below 50% HP.
  - Drops Tier 3-4 `ItemPureBlood`, `blood_infused_diamond`, and ancient grimoires.
- **`EntityBloodBat.java`**:
  - Hostile flying creature that swoops down from ceilings, bites victims, restores its own health, and retreats upward.

### D. Hunter Entities & NPCs
- **`EntityHunter.java`**:
  - Patrol hunters armed with stakes and hunter axes; multiple skin textures.
- **`EntityAdvancedHunter.java`**:
  - Crossbow marksmen utilizing `EntityAICrossbowAttack` and throwing holy water grenades.
- **`EntityHunterTrainer.java`**:
  - Village/camp NPC trainer. Allows hunter players to level up from Level 1 to 14 by trading fangs, pure blood, and intel. Inducts neutral players with stake + garlic.
- **`EntityTaskmaster.java`**:
  - Bounty officer exchanging vampire fangs for Hunter Intel and emeralds.

### E. Converted Mobs (Thralls)
- **`EntityConvertedCow.java`**, **`EntityConvertedSheep.java`**, **`EntityConvertedHorse.java`**, **`EntityConvertedVillager.java`**:
  - Thrall creatures allied to the vampire faction.
  - Converted Villager permits peaceful feeding for vampire players (sneak + right-click) and offers dark trades.

### F. Legendary Boss Encounters
- **`EntityDracula.java`**:
  - 3-Phase Boss Encounter with boss health bar (`IBossDisplayData`).
    - *Phase 1 (100% - 66% HP):* Melee combat, 40% parry defense, tactical teleportation.
    - *Phase 2 (66% - 33% HP):* Blood bat swarm summons, rapid triple Dark Blood projectile barrages.
    - *Phase 3 (33% - 0% HP):* Enraged Blood Rage (+100% attack damage, lifesteal, lightning strikes).
  - Drops: Sovereign Blood, Tier 9 Ancient Grimoire, Ultimate Heart Striker, and Pure Blood.
- **`EntityVampireMother.java`**:
  - Ancient progenitor deity boss (450 HP) with blood waves and soul orb drops.

### G. Angelica-Compatible Client Renderers
- Created and registered renderers in `ClientProxy.java`:
  - `RenderVampire`, `RenderHunter`, `RenderAdvancedVampire`, `RenderAdvancedHunter`, `RenderVampireBaron`, `RenderBloodBat`, `RenderDracula`, `RenderVampireMother`.
  - Converted creature renderers (`RenderConvertedCow`, `RenderConvertedSheep`, `RenderConvertedHorse`, `RenderConvertedVillager`) rendering the red glowing eye overlay pass (`textures/entity/overlay/converted_*.png`) via `shouldRenderPass`.

---

## 3. Verification & Build
- Clean build via `.\gradlew assemble` with zero errors (`BUILD SUCCESSFUL in 15s`).
- All entities registered in `ModEntities.java` with corresponding spawn eggs.
- Full localizations added to `en_US.lang` and `ru_RU.lang`.
