# Milestone Report: Phase 4 — Vampire Progression, Sun Weakness & Garlic System

- **Task Reference:** Phase 4 from [`docs/ROADMAP.md`](../ROADMAP.md)
- **Date:** 2026-10-16
- **Status:** COMPLETED & VERIFIED (`.\gradlew compileJava` & `.\gradlew assemble` pass)

---

## 1. Summary of Ported Mechanics

In this milestone, we implemented the progression from Fledgling to Master Vampire (levels 1 through 14), exponential attribute scaling, the complete daylight calculation and sun damage engine, the garlic aura and area vulnerability system, and the Sanguinare Vampiris infection lifecycle.

### 1.1. Level Progression & Exponential Attribute Scaling
- **Ported Reference:** `de.teamlapen.vampirism.entity.vampire.VampirePlayer`, `de.teamlapen.vampirism.entity.factions.FactionPlayerHandler`, and modern formula definitions.
- **Implemented in Neorism:** [`LevelAttributeModifier.java`](../../src/main/java/com/marrybye/neorism/common/vampire/LevelAttributeModifier.java), [`VampireLeveling.java`](../../src/main/java/com/marrybye/neorism/common/vampire/VampireLeveling.java), [`VampirePlayer.java`](../../src/main/java/com/marrybye/neorism/common/vampire/VampirePlayer.java).
- **Attribute Modifiers:**
  - **Max Health:** Base 20 HP -> up to +20 HP (40 HP / 20 hearts total) at Level 14, calculated via square-root progression rounded to even values:
    $$\Delta HP = 2 \times \operatorname{round}\left(10 \times \sqrt{\frac{\text{level} - 1}{13}} \div 2\right)$$
  - **Attack Damage:** +1.0 damage at Level 7+, +2.0 damage at Level 14.
  - **Movement Speed:** Up to +30% boost at Level 14.
  - **Blood Exhaustion:** Up to -50% reduction in blood drain at Level 14.
  - **Fire Vulnerability:** Vampires take +50% fire damage scaling with level.

### 1.2. Exact Leveling Requirements Engine
- **Levels 2 to 4:**
  - Passive blood requirements: 100 / 175 blood.
  - Altar of Inspiration requirements: 40 / 70 / 100 blood.
- **Levels 5 to 14 (Altar of Infusion):**
  - Pure Blood tier: Level 5-6 (Tier 0), Level 7-8 (Tier 1), Level 9-10 (Tier 2), Level 11-12 (Tier 3), Level 13-14 (Tier 4).
  - Human Hearts: 1 to 5 based on level bracket.
  - Vampire Books: 1 to 3 based on level bracket.
  - Infusion Structure Points requirement:
    $$\text{points}(lvl) = 8 + \operatorname{round}\left(46 \times \frac{\lfloor (lvl - 4)/2 \rfloor}{5}\right)$$

### 1.3. Daylight Calculation & Sun Damage System
- **Ported Reference:** `de.teamlapen.vampirism.entity.vampire.SundamageRegistry`.
- **Implemented in Neorism:** [`SundamageRegistry.java`](../../src/main/java/com/marrybye/neorism/common/vampire/SundamageRegistry.java).
- **Logic:**
  - Day check: `world.getCelestialAngle(1.0F)` outside the night window (angle between 0.24 and 0.78).
  - Exposure check: `world.canBlockSeeTheSky(x, y, z)`.
  - Immunities: Raining at player position, submerged in water, holding an umbrella in main hand, having `ModPotions.sunscreen` active, or standing beneath the dense canopy of the Vampire Forest.
  - Buffer & Damage: 20 safe ticks (`SAFE_SUN_TICKS`), followed by progressive ignition (`player.setFire(3)`) and direct armor-bypassing sun damage (`ModDamageSource.SUN_DAMAGE`) scaled by level:
    $$\text{dmg} = 3.0 + 0.5 \times \text{level}$$
  - Accompanied by vampire screech sound and high-visibility debug logging.

### 1.4. Garlic Aura & Vulnerability Grid
- **Ported Reference:** `de.teamlapen.vampirism.world.GarlicHandler`, `de.teamlapen.vampirism.api.EnumStrength`.
- **Implemented in Neorism:** [`EnumStrength.java`](../../src/main/java/com/marrybye/neorism/common/world/garlic/EnumStrength.java), [`GarlicHandler.java`](../../src/main/java/com/marrybye/neorism/common/world/garlic/GarlicHandler.java), [`ItemGarlic.java`](../../src/main/java/com/marrybye/neorism/common/items/ItemGarlic.java).
- **Logic:**
  - Spatial radius evaluation for garlic beacons and garlic emitters.
  - Strength tiers: `NONE`, `WEAK` (r <= 12), `MEDIUM` (r <= 8), `STRONG` (r <= 4).
  - Effects on vampires: cancels active feeding, applies `ModPotions.garlic` debuff, inflicts damage in strong garlic areas, and damages vampires if they attempt to hold garlic items in hand or eat them.

### 1.5. Sanguinare Vampiris Infection Lifecycle
- **Implemented in Neorism:** [`VampireCombatEventHandler.java`](../../src/main/java/com/marrybye/neorism/common/events/VampireCombatEventHandler.java), [`VampirePlayer.java`](../../src/main/java/com/marrybye/neorism/common/vampire/VampirePlayer.java).
- **Logic:**
  - 15% chance to transmit infection upon vampire attacks on mortal players.
  - Incubation tracked via `ModPotions.sanguinare` duration.
  - On duration expiry: triggers `onSanguinareFinished()`, setting faction to Vampire Level 1, granting the `becomeVampire` achievement, and playing screech audio.

---

## 2. Diagnostic Logging & Verification

All leveling, combat, and damage triggers are wired to `ModLogger`:
- `ModLogger.leveling(...)`: Logs level changes, attribute modifier updates, and Sanguinare infection completions.
- `ModLogger.combat(...)`: Logs sun damage ticks, garlic damage ticks, garlic item ingestion, and fire damage vulnerability amplification.

Build validation passed:
- `.\gradlew compileJava`: 0 errors.
- `.\gradlew assemble`: 0 errors.
