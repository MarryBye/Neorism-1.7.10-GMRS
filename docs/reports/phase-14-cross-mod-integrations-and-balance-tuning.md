# Phase 14 Completion Report: Cross-Mod Integrations & Balance Tuning (GTNH Stack)

**Mod:** Neorism: Become a Vampire (Minecraft 1.7.10)  
**Phase:** 14 — Cross-Mod Integrations & Balance Tuning  
**Date:** October 17, 2026  
**Status:** Completed & Build Verified (`.\gradlew assemble` SUCCESS)

---

## 1. Executive Summary

Phase 14 delivers seamless cross-mod integration across the GT New Horizons (GTNH) and Modern Minecraft backport ecosystem:
- **Et-Futurum-Requiem:** Arrow & quarrel pooling, crossbow support, shield blocking mitigation against stakes and projectile damage, and faction banner patterns.
- **Combat Backport:** 1.9+ weapon attack speeds (Heartseeker, Heartstriker, Hunter Axe) and Sweeping Edge area attacks.
- **Villager Trades Backport:** 1.14+ villager trading integration and dynamic hunter merchant discounts in hunter-controlled villages.
- **VillageNames:** Procedural gothic and vampiric naming for conquered villages, crypts, and faction keeps.
- **Hodgepodge & GTNHLib:** Celestial day/night calculations for precise solar angle tracking and vampire daylight vulnerability.
- **NotEnoughItems (NEI):** Full recipe handlers, GUI overlays, and transfer rects for Alchemical Cauldron, Hunter Research Table, Altar of Infusion, and Altar of Inspiration.

All integrations are implemented as robust, fail-safe soft-dependencies via `IntegrationManager`, ensuring zero crashes if any optional mod is absent.

---

## 2. Integrated Subsystems

### 2.1. Et-Futurum-Requiem (`EtFuturumIntegration.java`)
- **Banners:** Registers Vampire (fang) and Hunter (intel) banner patterns with `ganymedes01.etfuturum.api.BannerPatternHelper`.
- **Crossbow & Arrows:** Seamless detection of Et-Futurum crossbows, tipped arrows, and spectral arrows.
- **Shield Mitigation:** Mitigates 75% of incoming projectile and stake damage when players actively block with an Et-Futurum shield (`EtFuturumIntegration.isBlockingWithShield`).

### 2.2. Combat Backport (`CombatBackportIntegration.java`)
- **Weapon Speeds:** Registered weapon attack speeds directly with `WeaponRegistry.CUSTOM_SPEEDS`:
  - `neorism:heart_seeker`: 1.6F
  - `neorism:heart_striker`: 1.6F
  - `neorism:hunter_axe`: 1.0F
- **Sweeping Attacks:** Registered `CUSTOM_SWEEP_ITEMS` for Heartseeker and Heartstriker, executing wide-area sweep damage and particle effects upon attack.

### 2.3. Villager Trades Backport (`VillagerTradesBackportIntegration.java`)
- **1.14+ Trade GUI:** Supports modern profession workstations and trade structures.
- **Hunter Territory Discounts:** Automatically applies a 15% emerald trade discount when hunter players interact with merchants in hunter-controlled villages.

### 2.4. VillageNames (`VillageNamesIntegration.java`)
- **Gothic Procedural Generation:** Generates canonical gothic and vampiric village, fortress, and crypt names utilizing `astrotibs.villagenames.name.NameGenerator` with fallback procedural gothic matrices (`"Castle of %s"`, `"Blood Keep of %s"`, `"%s Crypt"`).
- Integrates directly into `FactionVillage.java` for persistent named territory generation.

### 2.5. Hodgepodge & GTNHLib (`HodgepodgeIntegration.java`)
- **Celestial Day/Night Synchronization:** Queries celestial angle and surface illumination with Hodgepodge's long day/night cycle calculations.
- Integrated directly into `Helper.isDay(world)`, providing accurate daylight checks across all vampire mechanics (sun damage, bat flight, vampire mob spawning).

### 2.6. NotEnoughItems (NEI) Integration (`com.marrybye.neorism.common.integration.nei`)
- **`NEIConfig`:** Automatically discovered and loaded by NEI via `IConfigureNEI`.
- **`AlchemicalCauldronRecipeHandler`:**
  - Holy Water (Normal, Enhanced, Ultimate, Splash).
  - Purified Garlic.
  - Blood Infused Iron, Gold, and Diamond.
  - Alchemical Weapon Oils (Weakness, Poison, Slowness, Wither).
  - Animated cooking arrow and flame indicators.
- **`HunterTableRecipeHandler`:**
  - Full research progression for levels 5 through 14 (Intel tiers 0-9).
  - Required book, fangs, pure blood, and vampire books.
  - Displays required Hunter Table tier and target level.
- **`AltarInfusionRecipeHandler`:**
  - Vampire levels 5 through 14 infusion rituals.
  - Pure blood tier, human heart counts, and vampire books.
  - Displays required structure points and blood purity.
- **`AltarInspirationRecipeHandler`:**
  - Vampire levels 2 through 4 inspiration rituals with blood requirements (40, 70, 100).
- **Catalysts & Overlays:**
  - Bound catalysts to `ModBlocks.alchemicalCauldron`, `ModBlocks.hunterTable`, `ModBlocks.altarInfusion`, and `ModBlocks.altarInspiration`.
  - Registered GUI transfer rects for `GuiAlchemicalCauldron`, `GuiHunterTable`, and `GuiAltarInfusion`.

---

## 3. Verification & Build Status

- **Compilation:** `BUILD SUCCESSFUL in 8s` via `.\gradlew assemble`.
- **Dependencies:** Added `NotEnoughItems` dev dependency to `dependencies.gradle`.
- **Compatibility:** Clean execution with Angelica, Hodgepodge, GTNHLib, Et-Futurum, VillageNames, and modern Java runtime (Java 25 daemon + Jabel).
