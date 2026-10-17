# Report: Coffin 3D TESR, Quivers/Clips, Alchemical Oils, Spawn Eggs, and Faction Restrictions

> **Date:** 2026-10-17  
> **Author:** Antigravity (pair programming with MarryBye)  
> **Target:** Neorism 1.7.10 (Minecraft Forge 10.13.4.1614 / GTNH Ecosystem)  
> **Commits:** `14ab651`, `cf26fc6`, `d6c5988`

---

## 1. Overview & Objectives

This development pass resolved critical gameplay, rendering, and progression issues identified during playtesting:
1. **Coffin Rendering & Animation:** The coffin was previously an invisible entity/block with no inventory or in-world model due to a broken GTNHLib JSON block model.
2. **Hunter Arsenal Expansion:** Ported modern Vampirism crossbow magazines/clips and alchemical weapon coating oils.
3. **Mob Spawning & Entity IDs:** Fixed missing creative spawn eggs and `/summon` command failures for all 13 Neorism entities.
4. **Faction Equipment Restrictions:** Implemented faction-specific gear locking that damages and debuffs players wearing enemy faction equipment.
5. **Skill Tree Lighting & Unlocking:** Fixed dark GUI rendering in `SkillsScreen` and prerequisite validation for faction skill trees.

---

## 2. Key Implementations

### 2.1. 3D Coffin TESR & Item Renderer
- **Model:** Ported `ModelCoffin.java` faithfully from TeamLapen's `CoffinModel.java` (dual animated opening lids, decorative handles, 256x128 texture mapping).
- **TESR:** Implemented `TileEntityCoffinRenderer.java` handling both head and foot blocks with smooth sine/cosine lid opening transitions when a vampire player rests inside.
- **Item Renderer:** Implemented `ItemCoffinRenderer.java` (`IItemRenderer`) to render the coffin as an isometric 3D model in inventory slots, in first/third-person player hands, and as a dropped entity item.
- **Block Logic:** Set `BlockCoffin` to `renderType = -1` (bypassing the buggy GTNHLib JSON ISBRH parser) and updated multiblock bed bounds.

### 2.2. Hunter Clips & Alchemical Oils
- **`ItemQuarrelClip`:** Holds up to 16 arrows/quarrels (standard and heavy variants). Automatically reloads the player's Tech Crossbow upon empty magazine detection.
- **`ItemOilBottle`:** Ported 8 alchemical weapon oil types with 32 charges per bottle:
  - *Vampire Killer:* Deals 2.5x holy damage against vampires.
  - *Garlic Oil:* Inflicts garlic sickness and prevents vampire healing.
  - *Bleeding Oil:* Causes stacking bleed damage over time.
  - *Fire Oil:* Ignites target for 5 seconds.
  - *Poison, Weakness, Slowness Oils:* Applies corresponding tactical debuffs.
- **`WeaponOilEventHandler`:** Listens to `LivingHurtEvent` to trigger oil procs on weapon strikes and deducts charges dynamically. Displays oil coating status and remaining strikes in item tooltips.

### 2.3. Mob Spawning, Creative Eggs & /summon Fix
- In Forge 1.7.10, `registerModEntity` only creates network IDs, leaving `/summon` and vanilla creative spawn eggs unavailable.
- Updated `ModEntities.java` to invoke `EntityRegistry.registerGlobalEntityID` using `EntityRegistry.findGlobalUniqueEntityId()`, creating native creative spawn eggs with custom egg colors for all 13 mod entities:
  - `neorism:vampire`, `neorism:advanced_vampire`, `neorism:vampire_baron`, `neorism:dracula`, `neorism:blood_bat`
  - `neorism:hunter`, `neorism:advanced_hunter`, `neorism:hunter_trainer`, `neorism:taskmaster`
  - `neorism:converted_villager`, `neorism:converted_cow`, `neorism:converted_sheep`, `neorism:converted_horse`
- Registered entity string aliases in `EntityList.stringToClassMapping` so `/summon vampire`, `/summon neorism:vampire`, and `/summon vampirism:vampire` resolve correctly.

### 2.4. Faction Equipment Restrictions
- **Hunter Armor & Equipment:** Implemented `onArmorTick` searing damage (`causeIndirectHolyWaterDamage`), garlic sickness, and weakness to vampires attempting to equip hunter armor. Added warning chat alerts and tooltip reminders.
- **Vampire Armor & Robes:** Inflicts magic damage, nausea, and confusion upon hunters wearing unholy vampire clothing.
- Integrated `tooltip.neorism.faction_restriction.hunter` and `tooltip.neorism.faction_restriction.vampire`.

### 2.5. Skill Tree GUI & Progression Fixes
- Fixed dark skill node rendering by explicitly disabling OpenGL standard item lighting (`RenderHelper.disableStandardItemLighting()`) and resetting GL blend state.
- Automatically unlocked faction root nodes (`vampire_level_root`, `hunter_level_root`, `vampire_lord_root`, `hunter_lord_root`) upon faction induction.
- Enhanced `SkillSegmentComponent` and `SkillHandler` to cleanly distinguish between prerequisite lock, level lock, points lock, and exclusive branch lock.

---

## 3. Verification & Build Status

- **Compilation:** Clean build with `.\gradlew assemble` (`BUILD SUCCESSFUL in 40s`).
- **Classpath Check:** Verified that all jars are loaded directly from Gradle cache; `run/client/mods/` remains clean of duplicate jars.
- **Documentation:** Updated roadmap checkboxes in `docs/ROADMAP.md` (Sections 7.3, 7.4, 8.4).

---

## 4. Next Priorities

- **Phase 10: Lordship Hierarchy & Minions** (`ILordPlayer`, `LordTitles`, minion task management, `MinionScreen`).
- **Phase 11: World Generation, Vampire Forest Biome & Structures** (`BiomeGenVampireForest`, dark canopy trees, fog effects, crypts and hunter camps).
- **Phase 12: Client HUD Overlays & Angelica Shaders** (Blood bar HUD, sun vignette, Angelica shader compatibility).
- **Phase 13: Villages, Territory Control & Faction War** (Totem capture mechanics, village banners, raid events).
