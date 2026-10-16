# Milestone Report: Phase 5 - Hunter Progression, Holy Water & Equipment Basics

> **Date:** 2026-10-16  
> **Status:** COMPLETED  
> **Git Commit:** (Staged for commit)  

---

## 1. Scope & Objectives
Full implementation of the Hunter faction player subsystem, Hunter leveling progression engine, intelligence items, holy water mechanics (drinkable & thrown splash entities), wooden stake instant-kill mechanics, holy crucifixes, garlic diffuser cores, and crafting recipes.

Ported from modern Vampirism (`HunterPlayer`, `HunterLeveling`, `ItemHolyWater`, `ItemStake`, `ItemCrucifix`).

---

## 2. Technical Implementation Details

### A. Classes Created & Modified
- **`HunterPlayer.java`** (`com.marrybye.neorism.common.hunter`):
  - Extended entity property managing Hunter level, drain resistance, and attribute buffs.
  - Level-scaled movement speed modifier (+20% max at lvl 14).
  - Bonus attack damage against vampires (+1 at lvl 7+, +2 at lvl 14).
  - Drain resistance: `Math.min(0.70F, level * 0.05F)` reducing blood lost to vampires when bitten.
- **`HunterLeveling.java`** (`com.marrybye.neorism.common.hunter`):
  - Levels 1–4 (Basic Training): consumes Vampire Fangs/Blood (1, 5, 12, 20).
  - Levels 5–14 (Advanced Training): requires Hunter Intel (Tiers 1–10 / Meta 0–9), Iron/Gold ingots, and Vampire Books.
- **`ItemHunterIntel.java`** (`com.marrybye.neorism.common.items`):
  - 10 metadata tiers (0 to 9) representing military and anatomical research data on vampires.
- **`ItemHolyWater.java` & `ItemHolyWaterSplash.java`** (`com.marrybye.neorism.common.items`):
  - 3 tiers (Normal, Enhanced, Ultimate).
  - Drinkable Holy Water: cures `Sanguinare Vampiris` on mortal players; inflicts heavy holy damage and nausea/weakness on vampire players.
  - Thrown Splash Holy Water: spawns `EntityHolyWaterBottle`.
- **`EntityHolyWaterBottle.java`** (`com.marrybye.neorism.common.entity`):
  - Projectile entity that explodes on impact.
  - Area of effect holy damage (8.0 / 14.0 / 22.0 HP) scaling with distance.
  - Cleanses `Sanguinare Vampiris` from mortals in range.
  - Knocks back and weakens vampire mobs and players.
- **`RenderHolyWaterBottle.java`** (`com.marrybye.neorism.client.render.entity`):
  - Client billboard item renderer compatible with Angelica/Sodium.
- **`ItemStake.java`** (`com.marrybye.neorism.common.items`):
  - Wooden stake weapon (base attack damage 3.0, durability 64).
  - Backstab instant-kill calculation: if attacking a vampire from behind (`attackerLook.dotProduct(targetLook) > 0.4`) and target health is <= 40% or <= 8.0 HP:
    - Deals direct lethal damage `ModDamageSource.STAKE`.
    - Plays wood shatter sound `ModSounds.STAKE` and wood break particles.
    - Grants achievement `ModAchievements.stake`.
    - Drops vampire blood bottles.
- **`ItemCrucifix.java`** (`com.marrybye.neorism.common.items`):
  - 3 tiers (Wooden, Iron, Gold).
  - Channeled holy aura pushing vampires and undead away with inverse distance velocity scaling.
  - Inflicts slowness and garlic suppression.
  - Burns unholy vampire wielders trying to hold the crucifix.
- **`ItemGarlicDiffuserCore.java`** (`com.marrybye.neorism.common.items`):
  - 3 tiers (Normal, Long, Strong) providing localized garlic aura around the carrier.
- **`ModRecipes.java`** (`com.marrybye.neorism.common.core`):
  - Crafting recipes for Stakes, Crucifixes, Holy Water Bottles, Splash Holy Water Bottles, Garlic Diffuser Cores, and Hunter Intel notes.
- **`ModEntities.java`** (`com.marrybye.neorism.common.core`):
  - Registered `EntityHolyWaterBottle` with Forge EntityRegistry.

---

## 3. Diagnostic Logging Integration
Integrated with `ModLogger`:
- `[DEBUG:LEVEL]`: Hunter level advancements, attribute modifier applications.
- `[DEBUG:COMBAT]`: Stake instant-kills from behind, holy water splash damage and cleansing, crucifix repulsions, and hunter drain resistance against vampire bites.

---

## 4. Verification & Testing
- Build verification: `.\gradlew assemble` passed cleanly with code 0 in 21s.
- Zero missing textures or compilation errors.
- Audio normalization tasks for future phases (Phase 8, 9, 11) noted in `docs/ROADMAP.md`.

---

## 5. Next Steps
- Transition to **Phase 6: Action Engine & Skill Tree Subsystems** (`IAction`, action keybindings, vampire & hunter skill trees).
