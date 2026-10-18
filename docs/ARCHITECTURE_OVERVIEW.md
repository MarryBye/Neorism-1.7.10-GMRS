# Neorism 1.7.10 - Architectural Analysis of Modern Vampirism

> **Reference Source:** TeamLapen / maxanier *Vampirism: Become a Vampire* (1.20+)  
> **Total Reference Classes:** 1,842 Java files across 4 modular subprojects (`vampirism`, `vampirism-api`, `faction`, `faction-api`).  
> **Goal:** Complete subsystem decomposition and 1:1 mapping to Minecraft Forge 1.7.10 with GTNH enhancements.

---

## 1. High-Level Subsystem Breakdown

Modern Vampirism is divided into two major architectural layers:
1. **The Faction Framework (`de.teamlapen.faction`)**:
   - An extensible, faction-agnostic RPG engine providing faction registration, player attachments, skill tree data structures, action execution pipelines, and lord/minion hierarchies.
2. **The Vampirism Mod (`de.teamlapen.vampirism`)**:
   - Implements specific vampire and hunter factions, blood hunger mechanics, sun exposure calculations, garlic area auras, altars, entities, weapons, armor, and worldgen.

```
┌────────────────────────────────────────────────────────┐
│                   Vampirism Mod Layer                  │
│  (BloodStats, SundamageRegistry, Altars, Entities, GUI)│
├────────────────────────────────────────────────────────┤
│                 Faction Framework Layer                │
│    (IFaction, IAction, ISkill, IMinion, FactionPlayer) │
├────────────────────────────────────────────────────────┤
│                  Minecraft Forge 1.7.10                │
│ (IExtendedEntityProperties, SimpleNetworkWrapper, TESR)│
└────────────────────────────────────────────────────────┘
```

---

## 2. Deep Dive: Core Mechanics & Formulas

### 2.1. Blood & Thirst (`IBloodStats`, `BloodStats.java`)
- **Food Data Override:** Instead of standard hunger, vampires track Blood (0 to 20 base, scaling with level to 40+). Vanilla `FoodData.foodLevel` is locked at 10.
- **Sprint / Action Exhaustion:** Movement exhaustion is intercepted from Forge/Minecraft and added to `bloodExhaustionLevel`.
- **Exhaustion Gate:**
  $$\text{Gate} = \begin{cases} 6.0 & \text{in Vampire Biome} \\ 4.0 & \text{outside Vampire Biome} \end{cases}$$
- **Natural Healing Rate:**
  $$\text{HealMultiplier} = 1.0 + \left(\frac{\text{VampireLevel}}{\text{MaxLevel}}\right) \times 0.5$$
  When injured, saturation is consumed first for rapid healing (10 ticks per half heart); once saturation is 0, normal blood healing consumes blood every 80-300 ticks.
- **Starvation:** At `bloodLevel == 0`, player receives `ModEffects.NO_BLOOD`, suffering severe slowness, weakness, screen pulsing, and progressive damage.

### 2.2. Sun Exposure Engine (`SundamageRegistry.java`, `Helper.java`)
- **Daylight Angle Check:**
  $$\text{Angle} = \frac{\text{SunAngle}}{360^\circ}$$
  $$\text{IsDay} \iff (\text{Angle} > 0.78 \lor \text{Angle} < 0.24)$$
- **Exemptions:**
  - Player is in rain or submerged in water.
  - Player holds an Umbrella in mainhand.
  - Player has active `ModEffects.SUNSCREEN`.
  - Player has active `ModEffects.AURA_OF_DARKNESS`.
  - Player is in the Vampire Forest biome (dark protective canopy).
- **Buffer:** 20 safe ticks before ignition/damage starts.

### 2.3. Leveling Formulas (`VampireLeveling.java`, `HunterLeveling.java`)
#### Vampire Path:
- **Level 1:** Bitten by vampire $\to$ contract `Sanguinare Vampiris` $\to$ wait incubation (or drink pure blood).
- **Levels 2 to 4 (Altar of Inspiration):**
  - Level 2: 40 blood (or 100 passive blood).
  - Level 3: 70 blood (or 175 passive blood).
  - Level 4: 100 blood.
- **Levels 5 to 14 (Altar of Infusion):**
  - Requires specific tiers of Pure Blood (Tiers 1-4), Human Hearts, and Vampire Books.
  - Multiblock Structure Points:
    $$\text{StructurePoints} = 8 + (54 - 8) \times \frac{\lfloor(\text{TargetLevel} - 4) / 2\rfloor}{5}$$

#### Hunter Path:
- **Levels 1 to 4:** Hunter Trainer NPC exchanging Vampire Blood (1, 5, 12 bottles).
- **Levels 5 to 14:** Advanced Hunter Trainer requiring Hunter Intel (items 0 to 9) crafted at Weapon Table tiers 0 to 3 using Iron, Gold, Fangs, and Vampire Books.

### 2.4. Combat & Stake Backstab (`ItemStake.java`)
- **Frontal Attack:** Acts as a standard wooden weapon (3 attack damage).
- **Backstab Condition:**
  $$\vec{V}_{\text{player}} \cdot \vec{V}_{\text{target}} > 0.5$$
  If target is a Vampire below instant-kill health threshold, the stake deals $1000$ true damage, instantly turning the vampire to dust with wood splinter particles and screeching sound.

---

## 3. Subsystem Migration Mapping (Modern -> 1.7.10)

| Subsystem | Modern Implementation (1.20+) | Neorism Forge 1.7.10 Implementation |
| :--- | :--- | :--- |
| **Player Capabilities** | NeoForge `AttachmentType` / Capabilities | `IExtendedEntityProperties` (`neorism:faction_player`, `neorism:vampire_player`, `neorism:hunter_player`) |
| **Network Sync** | Custom Payload records & StreamCodecs | `SimpleNetworkWrapper` (`IMessage`, `IMessageHandler`) |
| **Block Models** | JSON Blockstates (`assets/models/block/`) | `ISimpleBlockRenderingHandler` + `TileEntitySpecialRenderer` (TESR) |
| **Complex 3D Items** | Modern baked models | `IItemRenderer` (equipped, inventory, ground rendering) |
| **Entity AI** | Modern `Brain<E>` / `GoalSelector` | Forge 1.7.10 `EntityAIBase` and `EntityAITasks` |
| **Shaders & Blood Vision** | Post-processing JSON shaders | Custom Angelica shader pipeline (`RenderGameOverlayEvent.Pre`) |
| **Damage System** | `DamageType` datapack registries | Custom `DamageSource` instances with localized death messages |
| **Multiblock Detection** | BlockState pattern matching | Coordinate relative scanner with block/metadata validation |
| **Localization** | JSON files (`en_us.json`) | Forge `.lang` format (`en_US.lang`, `ru_RU.lang`) |

---

## 4. Execution Strategy
All development strictly follows [`docs/BACKPORT_ROADMAP.md`](BACKPORT_ROADMAP.md) and [`AGENTS.md`](../AGENTS.md). Every task:
1. Examines the exact modern code in `reference/vampirism/`.
2. Implements the 1.7.10 equivalent with identical formulas and balance values (using existing original models/textures).
3. Adds categorized diagnostic logging (`ModLogger`).
4. Verifies compilation via `.\gradlew assemble` (never copying jars to `run/client/mods/`).
5. Creates a local Conventional Commit.
