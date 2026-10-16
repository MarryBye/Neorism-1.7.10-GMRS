# Milestone Report: Phase 3 - Blood Subsystem & Vampire Feeding Mechanics

> **Date:** 2026-10-16  
> **Status:** COMPLETED  
> **Phase:** 3.1 - 3.5  

---

## 1. Scope & Objectives
Implement the vampire lifeblood subsystem and feeding mechanics for Neorism in Minecraft Forge 1.7.10:
- Blood statistics engine (`IBloodStats`, `BloodStats`) managing blood level, max capacity, saturation, and hunger exhaustion.
- Complete takeover and locking of vanilla `FoodStats` (locked at 10 units / half bar, rerouting sprint/jump exhaustion to blood exhaustion).
- Starvation penalties: `ModPotions.noBlood` applied at blood level 0, client-side heartbeat sound when blood is < 20%.
- Entity biting and continuous feeding mechanics (`IBiteableEntity`, `ExtendedCreature`, `VampirePlayer` feeding state machine).
- Target validation, reach calculation (3.5 blocks), pinning slowness, and entity infection chance (`Sanguinare Vampiris` for villagers).
- Items: `ItemBloodBottle` (6 subtypes: 0/5 empty to 5/5 full) and `ItemBloodFlask` (10 units capacity) with drinking animations and tooltips.
- Natural vampire healing formula: `getHealModifier()` = `1.0F + (level / 14.0F) * 0.5F`.
- Client HUD: `BloodBarOverlay` rendering 9x9 vampire blood drops replacing vanilla food drumsticks, and `KeyBindings` ("V" key) for biting.

Modern reference sources referenced:
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/api/world/entity/player/vampire/IBloodStats.java`
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/entity/player/vampire/BloodStats.java`
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/api/world/entity/IBiteableEntity.java`
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/entity/ExtendedCreature.java`
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/entity/player/vampire/VampirePlayer.java`
- `reference/vampirism/src/main/java/de/teamlapen/vampirism/client/gui/overlay/BloodBarOverlay.java`
- `reference/vampirism/data/vampirism/data_maps/entity_type/entity_blood.json`

---

## 2. Technical Implementation Details

### 2.1. Blood Statistics Engine (`BloodStats.java`)
- **Capacity Scaling:** Base 20 blood (Levels 0-3), 26 (Levels 4-6), 30 (Levels 7-9), 34 (Levels 10-13), 40 (Level 14).
- **Vanilla Food Interception:** Ticks in `onUpdate()` lock `foodStats.setFoodLevel(10)`. The `foodExhaustionLevel` field is accessed via reflection/SRG (`field_75126_c`), cleared to 0.0F, and added to `bloodExhaustionLevel` (multiplied by `ModAttributes.BLOOD_EXHAUSTION`).
- **Exhaustion Gate:** Consumes 1 saturation (or 1 blood if saturation == 0) per 4.0F exhaustion outside, and per 6.0F exhaustion within vampire biomes (`Helper.isEntityInVampireBiome`).
- **Starvation:** At `bloodLevel <= 0`, triggers `ModPotions.noBlood` every 80 ticks on players with HP > 10.0 (or on Hard difficulty any HP).
- **Healing Formulas:**
  - Fast heal (every 10 ticks): active when saturated, wounded, and `bloodLevel >= maxBlood`. Heals `(sat / 6.0F) * getHealModifier()`.
  - Normal heal (every 80 ticks): active when `bloodLevel >= 18`. Heals `getHealModifier()`.
  - Slow heal (every 300 ticks): active when `bloodLevel > 0`. Heals `0.5F * getHealModifier()`.

### 2.2. Creature Blood & Biting (`IBiteableEntity.java`, `ExtendedCreature.java`)
- Attached to all `EntityLivingBase` instances as `IExtendedEntityProperties` under `"neorism:extended_creature"`.
- Exact blood capacity table ported from modern datamaps:
  - Villager: 15
  - Cow, Mooshroom: 10
  - Horse, Donkey, Mule: 13
  - Sheep, Pig, Wolf: 5
  - Ocelot, Fox: 4
  - Cat: 3, Rabbit: 2
  - Bounding box fallback: `Math.min(15, (int)(volume * 7.0D))`.
- When bitten, drains `maxBlood / 6`. If child, yields 1/3 blood. If blood reaches 0, kills entity with `ModDamageSource.NO_BLOOD`.
- Villagers have a 20% chance to contract `ModPotions.sanguinare`.
- Periodic blood regen: bitten creatures have a 1 in 20 chance every 2 seconds to regain 1 blood.

### 2.3. Continuous Feeding Machine (`VampirePlayer.java`, `BiteRequestPacket.java`)
- Continuous bite loop: on pressing "V", client raytraces living entity within 3.5 blocks and sends `BiteRequestPacket`.
- Server validates reach and biteability, inflicts pinning Slowness (VI on victim, III on player), and drains blood every `FEED_TIMER` (20 ticks = 1 second) with bite sound (`ModSounds.VAMPIRE_BITE`) and drip sound (`ModSounds.BLOOD_DRIP`).
- When key is released or victim dies/leaves range, `endFeeding()` clears state and removes slowness.

### 2.4. Blood Items (`ItemBloodBottle.java`, `ItemBloodFlask.java`)
- **`ItemBloodBottle`**: Subtypes 0 to 5 using textures `blood_bottle_0.png` through `blood_bottle_5.png`. Drinking animation restores blood stats and transitions bottle to empty or lower fill metadata.
- **`ItemBloodFlask`**: Stores up to 10 blood units (1000 mB) with NBT tracking and interactive tooltips.

### 2.5. HUD Overlay & Client Input (`BloodBarOverlay.java`, `KeyBindings.java`)
- Replaces vanilla food rendering in `RenderGameOverlayEvent.Pre` (`ElementType.FOOD`).
- Renders 10 blood drop sprites across 9x9 pixels using `neorism:textures/gui/sprites/blood_bar/` (`background.png`, `quarter.png`, `half.png`, `three_quarter.png`, `full.png`).
- Supports double-layer rendering (up to 40 blood) seamlessly.

---

## 3. Verification & Build Status
- **Build:** `./gradlew assemble` completed with `BUILD SUCCESSFUL in 14s`.
- **Artifact:** Output dev jar `neorism-v0.1.0-master.2+ffbe7479b9-dirty-dev.jar` deployed to `run/client/mods/`.
- **Localization:** Verified Russian and English translation strings for blood bottles, flasks, and bite keybindings.
