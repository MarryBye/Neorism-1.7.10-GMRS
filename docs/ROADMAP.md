# Neorism 1.7.10 - Master Porting Roadmap (Modern Vampirism -> Forge 1.7.10)

> **Goal:** 100% full, uncompromising backport of modern *Vampirism: Become a Vampire* (1.20+) to Minecraft 1.7.10 in the GT New Horizons (GTNH) ecosystem.  
> **Reference Codebase:** `reference/vampirism/` (1,842 modern source files & data assets).  
> **Progress Tracking:** All tasks are decomposed into checkable milestones `[ ]`. After completing each task, the agent updates this file and generates a corresponding report in `docs/reports/` (per `task-reporting` skill).

---

## Roadmap Architecture & Dependency Order

```
Phase 1: Foundation, Registries, Damage Sources & Network
   ↓
Phase 2: Faction Architecture & Player Capability Storage (IEEP)
   ↓
Phase 3: Blood Subsystem & Feeding Mechanics
   ↓
Phase 4: Vampire Progression, Sun Weakness & Garlic System
   ↓
Phase 5: Hunter Progression, Holy Water & Equipment Basics
   ↓
Phase 6: Action Engine & Skill Tree Subsystems
   ↓
Phase 7: Custom Mod Items, Weapons & Armors
   ↓
Phase 8: Blocks, Tile Entities & Multiblock Altars
   ↓
Phase 9: Entities, Mobs & Custom AI Subsystem
   ↓
Phase 10: Minion Management & Lordship System
   ↓
Phase 11: World Generation, Vampire Biome & Structures
   ↓
Phase 12: Client Experience, GUI, Angelica Shaders & Cross-Mod Integrations
```

---

## Phase 1: Foundation, Core Registries, Damage Sources & Network Pipeline
*Prerequisite for all item, entity, and block logic.*

- [x] **1.1. Core Registries Infrastructure (`ModRegistries`)**
  - [x] Implement `ModItems`, `ModBlocks`, `ModEntities`, `ModPotions`, `ModAttributes` with type-safe registration helpers.
  - [x] Implement `CreativeTabs` for Neorism: `tabVampirism` (main) and `tabVampirismBooks`.
  - [x] Add preInit/init/postInit registration dispatchers in `CommonProxy`.
- [x] **1.2. Custom Damage Sources (`ModDamageSource`)**
  - [x] Implement `VampirismDamageSource`: `sunDamage` (bypasses regular armor, fire-based), `vampireBite`, `holyWater`, `garlic`, `bloodExhaustion`, `stake` (instant-kill condition).
  - [x] Register custom death messages in `.lang` files (`death.attack.sunDamage`, `death.attack.holyWater`, etc.).
- [x] **1.3. Sound Events Registry (`ModSounds`)**
  - [x] Map all sound definitions from `sounds.json` (`neorism:vampire_bite`, `neorism:blood_gargle`, `neorism:heartbeat`, `neorism:bat_flutter`, `neorism:altar_hum`, `neorism:screech`, `neorism:rage`).
  - [x] Implement helper methods: `ModSounds.playAtEntity(entity, soundName, volume, pitch)`.
- [x] **1.4. Network Pipeline (`PacketHandler`)**
  - [x] Implement `PacketHandler` using Forge `SimpleNetworkWrapper` under channel `"neorism"`.
  - [x] Setup packet ID registry with automatic bi-directional packet registration.
- [x] **1.5. Custom Entity Attributes (`ModAttributes`)**
  - [x] Implement `BLOOD_EXHAUSTION` attribute (affects hunger/blood burn rate).
  - [x] Implement `SUNDAMAGE` resistance attribute.
  - [x] Implement `NEONATAL_DURATION` attribute.
  - [x] Implement `BITE_DAMAGE` and `BITE_SPEED` attributes.


---

## Phase 2: Faction Architecture & Player Capability Storage (IEEP)
*The structural spine separating Vampires, Hunters, and Neutrals.*

- [x] **2.1. Faction Model & Interfaces (`com.marrybye.neorism.api.faction`)**
  - [x] Port `IFaction`, `IPlayableFaction`, `IFactionPlayer`.
  - [x] Implement `ModFactions`: `NEUTRAL` (Level 0), `VAMPIRE` (Levels 1-14, Lord 1-5), `HUNTER` (Levels 1-14, Lord 1-5).
  - [x] Faction hostility rules: Vampire vs Hunter (hostile), Vampire vs Neutral (prey/neutral), Hunter vs Vampire (hostile).
- [x] **2.2. Extended Player Properties (`FactionPlayerHandler`)**
  - [x] Implement `FactionPlayerHandler` implementing Forge `IExtendedEntityProperties` registered under `"neorism:faction_player"`.
  - [x] Implement `canJoin(faction)`, `canLeaveFaction()`, `setFaction(faction, level)`.
  - [x] Implement death & dimension cloning handler via `PlayerEvent.Clone` (persistent faction state across deaths).
- [x] **2.3. Client-Server Faction Synchronization**
  - [x] Packet: `SyncFactionPacket` (syncs player faction, level, lord title, and disguise state to tracking clients).
  - [x] Hook into `EntityJoinWorldEvent` and `PlayerLoggedInEvent` for immediate sync.
- [x] **2.4. Faction Disguise Subsystem**
  - [x] Implement `IDisguise` mechanics (Vampire disguise skill, Hunter disguise skill).
  - [x] Visibility check: `Helper.appearsAsVampire(player, viewer)`.
- [x] **2.5. TeamLapen FactionAPI Integration (Vampirism 2.0+ Alignment)**
  - [x] Integrate canonical `de.teamlapen.faction.api` packages (`factions`, `skills`, `actions`, `villages`, `minions`, `refinement`, `services`).
  - [x] Bridge `FactionPlayerHandler` to `IFactionPlayerHandler` and configure `FactionsApi` entry point.
  - [x] Document migration roadmap to author's official architecture (`docs/reports/factionapi-integration-analysis.md`).

---

## Phase 3: Blood Subsystem & Vampire Feeding Mechanics
*The lifeblood of the vampire gameplay loop.*

- [x] **3.1. Blood Stats Engine (`BloodStats`)**
  - [x] Port `IBloodStats` and `BloodStats` attached to `VampirePlayer`:
    - `maxBlood` (base 20, scales with level to 40+), `bloodLevel`, `bloodSaturationLevel`, `bloodExhaustionLevel`.
  - [x] Override vanilla `FoodStats`: lock vanilla food level at 10 (half bar, prevents natural health regen from food), route sprint/jump exhaustion to blood exhaustion.
  - [x] Biome exhaustion gate: 6.0F in Vampire Biome (vampires burn less blood at home), 4.0F outside.
- [x] **3.2. Blood Starvation & Low Blood Penalties**
  - [x] `bloodLevel == 0`: Apply `ModEffects.NO_BLOOD` (screen shake, slowness, weakness, progressive health decay).
  - [x] Heartbeat sound trigger when blood is below 20%.
- [x] **3.3. Entity Biting & Blood Drinking (`DrinkBloodContext`)**
  - [x] Implement `IBiteableEntity` logic for entities (animals, villagers, players).
  - [x] Biting mechanics: keybinding press, raytrace living entity within 2.5 blocks.
  - [x] Feeding duration: continuous drain over time, playing bite and gulp sounds with flying blood particles (`FlyingBloodParticle`).
  - [x] Animal drain: drains blood, leaves entity weakened/exhausted.
  - [x] Human/Villager drain: higher blood saturation; chance of inflicting `Sanguinare Vampiris`.
- [x] **3.4. Blood Containers & Items**
  - [x] `ItemBloodBottle`: Stores blood (1/9, 2/9, ... 9/9 or fluid fractions), drinking animation, restores blood stats.
  - [x] `ItemBloodFlask`: Multi-charge blood container for high-level vampires.
- [x] **3.5. Vampire Natural Healing Formula**
  - [x] Implement `getHealModifier()` = `1.0F + (level / 14.0F) * 0.5F`.
  - [x] Healing ticks consume blood saturation and blood level when wounded.

---

## Phase 4: Vampire Progression, Sun Weakness & Garlic System
*Progression from Fledgling to Master Vampire, balanced by deadly daylight and pungent garlic.*

- [x] **4.1. Vampire Player Subsystem (`VampirePlayer`)**
  - [x] Implement `VampirePlayer` property class managing vampire level (1 to 14), active buffs, and transformation states.
  - [x] Attribute scaling per level: Max Health boost (up to +20 HP), Attack Damage boost, Movement Speed boost.
- [x] **4.2. Vampire Leveling Engine (`VampireLeveling`)**
  - [x] Port exact leveling requirements from `VampireLeveling.java`:
    - Level 1: Infection via `Sanguinare Vampiris` bite.
    - Levels 2-4: Passive blood consumption (100 / 175 blood) OR Altar of Inspiration (40 / 70 / 100 blood).
    - Levels 5-14: Altar of Infusion multiblock rituals (Pure Blood tiers 1-4, Human Hearts, Vampire Books, Structure Points `8 + (54-8)*((lvl-4)/2)/5`).
- [x] **4.3. Sun Damage & Exposure Calculation (`SundamageRegistry`)**
  - [x] Implement exact sun check: `Helper.isDay(world, pos)` (sun angle > 0.78 or < 0.24) + sky visibility check (`canBlockSeeTheSky`).
  - [x] Exceptions: Rain, water immersion, Vampire Forest biome canopy, Umbrella item in hand, `ModEffects.SUNSCREEN`.
  - [x] Safe tick buffer: 20 safe ticks before ignition/damage starts.
  - [x] Damage ticks: fire damage + direct sun damage based on vampire level and sun resistance skills.
- [x] **4.4. Sunscreen & Buff Effects**
  - [x] Implement `ModEffects.SUNSCREEN` (duration-based daylight protection potion).
  - [x] Implement `ModEffects.AURA_OF_DARKNESS` (vampire biome / Dracula protection aura).
- [x] **4.5. Garlic Aura System**
  - [x] Implement garlic grid cache per chunk (`GarlicHandler`).
  - [x] Garlic strength tiers: `WEAK`, `MEDIUM`, `STRONG`, `EXTREME`.
  - [x] Debuffs inside garlic zone: weakness, slowness, inability to use vampire skills, continuous damage on contact with garlic blocks/items.
- [x] **4.6. Infection Mechanic (`Sanguinare Vampiris`)**
  - [x] Inflicted by vampire mob/player bites.
  - [x] Incubation period (1-2 in-game days). Can be cured before expiry using Holy Water / Milk.
  - [x] Transformation sequence on completion: screen pulse, sound, sets faction to Vampire Level 1.

---

## Phase 5: Hunter Progression, Holy Water & Equipment Basics
*The mortal retaliators: skill, technology, alchemy, and divine grace.*

- [x] **5.1. Hunter Player Subsystem (`HunterPlayer`)**
  - [x] Implement `HunterPlayer` property class managing hunter level (1 to 14) and training state.
  - [x] Attribute scaling: attack damage increase, movement speed increase, resistance to vampire drain.
- [x] **5.2. Hunter Leveling Engine (`HunterLeveling`)**
  - [x] Levels 1-4: Basic Hunter Training using Vampire Blood & Fangs (1, 5, 12, 20 fangs/blood).
  - [x] Levels 5-14: Advanced Hunter Training with Hunter Trainer NPC + Weapon Table tiers (Hunter Intel items 0-9, Iron ingots, Gold ingots, Vampire Books).
- [x] **5.3. Hunter Intel Items**
  - [x] Implement items `ItemHunterIntel` with 10 tiers (Intel 0 through 9).
  - [x] Crafting/research logic on crafting grid.
- [x] **5.4. Holy Water Mechanics**
  - [x] `ItemHolyWater`: Normal, Enhanced, Ultimate tiers with drinking & cure effects.
  - [x] Splash Holy Water entity (`EntityHolyWaterBottle` / `ItemHolyWaterSplash`): explodes on impact, damages vampires, cures Sanguinare infection, repels vampire mobs.
- [x] **5.5. Wooden Stake Mechanics (`ItemStake`)**
  - [x] Backstab instant-kill calculation: if attacking a vampire from behind (dot product of look vectors > 0.4) and target health is below threshold, instant kill with wood-shatter sound and critical particles.
- [x] **5.6. Garlic Diffuser & Crucifix Items**
  - [x] Portable garlic diffuser core item (`ItemGarlicDiffuserCore`) emitting localized garlic aura in 3 tiers.
  - [x] Crucifix (`ItemCrucifix`): channeled holy repulsion of undead and vampire mobs in 3 material tiers (Wooden, Iron, Gold).

---

## Phase 6: Action Engine & Skill Tree Subsystems
*Active abilities, keybindings, cooldowns, and branching progression.*

- [x] **6.1. Action Core Framework (`ActionHandler`)**
  - [x] `IAction` interface: cooldown, duration, validation, onActivated, onDeactivated, isUnlocked.
  - [x] Network packet: `ActionKeyPacket` (handles client hotkey triggers: active skill 1, active skill 2, bat mode, bite).
  - [x] Sync packet: `SyncActionsPacket` (syncs cooldowns and active timers to client).
- [x] **6.2. Vampire Skill Tree Implementation (`VampireSkills`)**
  - [x] `NightVision`: Night vision toggle without vanilla potion green tint.
  - [x] `VampireRegeneration`: Active health burst consuming large blood saturation.
  - [x] `VampireRage`: Damage boost, increased speed, increased blood consumption.
  - [x] `BatForm`: Shrinks player bounding box to 0.6x0.6, enables creative-like flight, restricts inventory, burns rapidly in sun.
  - [x] `Teleport`: Blink forwards up to 16 blocks through air/glass.
  - [x] `Freeze`: Area freeze spell immobilizing target entities.
  - [x] `DarkBloodProjectile`: Ranged life-stealing blood orb.
  - [x] `SunscreenSkill`: Temporary passive sunlight resistance.
  - [x] `Invisibility`: Complete stealth from mobs and players.
  - [x] `SummonBats`: Spawns swarm of blood bats attacking the target.
- [x] **6.3. Hunter Skill Tree Implementation (`HunterSkills`)**
  - [x] `HunterAwareness`: Highlights nearby vampires through blocks.
  - [x] `CrossbowTechnique`: Faster reload, higher quarrel velocity.
  - [x] `StakeMastery`: Increases instant-kill threshold and frontal stake damage.
  - [x] `PotionResistance`: Immunity to negative potion effects from vampires.
  - [x] `MasterBrewer`: Unlocks advanced cauldron infusion recipes.
  - [x] `WeaponCrafting`: Unlocks advanced weapon table blueprints.
- [x] **6.4. Skill Points & Tree GUI Engine**
  - [x] Skill points awarded per level-up.
  - [x] Skill unlocking validation (parent skills required, mutually exclusive branches).
  - [x] Persistent NBT save/load in player properties.
- [x] **6.5. FactionAPI Skill Tree & Radial Action Rose Migration (TeamLapen Alignment)**
  - [x] Connect canonical `de.teamlapen.faction.api` interfaces (`ISkill`, `ISkillSegment`, `ISkillTree`, `IAction`, `FactionsApi`).
  - [x] Migrate skill definitions to `ISkillSegment` DAG with `(x, y)` coordinates, parent prerequisites, and exclusive choices.
  - [x] Build pannable/scrollable `SkillsScreen` canvas with node connector lines matching TeamLapen's `SkillsScreen`.
  - [x] Implement `SelectActionRadialScreen` (Action Rose / Radial Menu on hold `[R]`): circular ability selector with cooldown sweeps.
  - [x] Implement `FactionMenuScreen` (Faction Dashboard on `[P]`): 3D player model, faction level, skills button, task overview.

---

## Phase 7: Custom Mod Items, Weapons & Armors
*Full equipment arsenal ported with exact 1.7.10 models, icons, and attributes.*

- [x] **7.1. Basic & Crafting Materials**
  - [x] Vampire Fang, Pure Blood (Tiers 1-5), Human Heart, Weak/Strong Vampire Blood, Garlic, Garlic Bulb.
  - [x] Vampire Orchid, Cursed Wood stick, Blood Infused Ingot.
- [x] **7.2. Hunter Weapons**
  - [x] `HunterAxe`: Bonus damage against vampires, disables blocking.
  - [x] `Heartseeker` (Iron, Diamond, Netherite equivalent): Vampire slaying sword, bonus damage against unholy creatures.
  - [x] `Heartstriker`: Life-stealing blade.
  - [x] Wooden & Reinforced Stakes.
- [x] **7.3. Hunter Crossbow Arsenal, Clips & Weapon Oils**
  - [x] `BasicCrossbow`, `DoubleCrossbow` (dual shot), `EnhancedCrossbow`, `TechCrossbow` (rapid-fire semi-automatic magazine).
  - [x] `QuarrelClip` magazine (16 ammo capacity, standard & heavy variants) with automatic inventory reload.
  - [x] `ItemOilBottle` Alchemical Weapon Oils (8 types, 32 charges per bottle, on-hit combat procs and tooltips).
  - [x] Custom Projectiles: Arrows, Quarrels, Heavy Quarrels.
- [x] **7.4. Armor & Wearables**
  - [x] Vampire Cloak & Vampire Robes (with custom Cape/Cloak renderer).
  - [x] Vampire Crown & Vampire Lord Armor.
  - [x] Hunter Hat, Hunter Coat, Hunter Trousers, Hunter Boots (with movement speed attribute bonuses).
  - [x] Umbrella (handheld sun shade preventing daylight burns).
  - [x] Faction equipment restrictions: debuffs and sears players wearing enemy faction armors.
- [x] **7.5. Vampire Books & Lore**
  - [x] Vampire Book items (Tiers 1 to 9).
  - [x] Guidebook integration.


---

## Phase 8: Blocks, Tile Entities & Multiblock Altars
*Custom 3D TESR models, animations, and ritual state machines.*

- [x] **8.0. Global Model & Texture System Normalization (Forge 1.7.10 / GTNHLib)**
  - [x] Create root `pack.mcmeta` (`pack_format: 4`) in `src/main/resources` enabling GTNHLib modern model bakery parser.
  - [x] Configure fallback variants (`""`, `"normal"`) for modeled blocks lacking property variants (e.g. `altar_pillar.json`).
  - [x] Mirror vanilla fallback textures (candles, concrete, stone, wood, metals) into `assets/minecraft/textures/block/` and `neorism/textures/block/`.
  - [x] Global audit & normalization across all blocks and models:
    - Zero missing textures across all 377 declared model textures.
    - Zero missing parent models across all JSON models (`blood_container`, `totem_top`, `totem_base`).
    - Every texture referenced in models exists in both singular `textures/block/` and legacy `textures/blocks/`.
    - Every 3D modeled block registers `ModelISBRH` as `IItemRenderer` for inventory / hand rendering.
    - [x] Fix block break & hit particle textures: implement `BlockParticleHelper` and Forge `addDestroyEffects`/`addHitEffects` in `BlockNeorism` to resolve model/quad textures directly and bypass GTNHLib's air-block destruction query bug.
    - Full step-by-step methodology documented in `docs/MODEL_TEXTURE_PORTING_GUIDE.md` and `.agents/skills/port-vampirism-feature/SKILL.md`.
- [x] **8.1. Altar of Inspiration**
  - [x] Block, TileEntity, and multiblock blood basin.
  - [x] TESR fluid rendering (smooth blood level rising/falling).
  - [x] Right-click leveling interaction for levels 2-4.
- [x] **8.2. Altar of Infusion (Complex Multiblock)**
  - [x] Center Altar block (`BlockAltarInfusion`).
  - [x] Pillars (`BlockAltarPillar`) and Tips (`BlockAltarTip` gold/diamond/nether).
  - [x] Pedestals (`BlockBloodPedestal`) for Pure Blood and Catalyst items.
  - [x] Multiblock pattern recognition algorithm (calculates `StructurePoints` based on distance, height, and materials).
  - [x] Ritual state machine: starting, catalyst consumption, lightning/particle effects, level-up completion, ritual failure.
- [x] **8.3. Blood Container & Pedestals**
  - [x] `TileEntityBloodContainer`: Stores up to 16,000 mB of blood. Fluid capability / 1.7.10 FluidHandler.
  - [x] Visual blood fluid level rendering inside glass tank.
- [x] **8.4. Coffin System**
  - [x] `BlockCoffin` (2-block multiblock, head & foot, `renderType = -1`).
  - [x] `TileEntityCoffinRenderer` & `ModelCoffin`: faithful 3D model with dual opening animated lids.
  - [x] `ItemCoffinRenderer`: 3D item rendering in inventory, in-hand, and as dropped entity.
  - [x] Allows vampire players to sleep through the sunny day and wake at dusk.
  - [x] Sets vampire spawn point and heals vampire on rest.
- [x] **8.5. Environmental & Utility Blocks**
  - [x] Fog Diffuser (generates dark fog field preventing sun damage).
  - [x] Garlic Diffuser (cleanses area from vampires).
  - [x] Faction Totem (Village capture center).
  - [x] Cursed Earth (spreads dark foliage, spawns vampires at night).
  - [x] Dark Spruce Wood, Planks, Leaves, Sapling.
- [x] **8.6. Remains & Block Sound Adaptation**
  - [x] Adapt modern sound events in `sounds.json` (`entity.remains.*`) with bundled 1.7.10 .ogg assets or valid vanilla sound fallbacks to eliminate startup warnings.

---

## Phase 9: Entities, Mobs & Custom AI Subsystem
*Rich ecosystem of creatures with goal-oriented AI.*

- [x] **9.1. Entity Base Hierarchy & Faction Integration**
  - [x] `EntityVampireBase` (immune to dark, burns in sun, drinks blood, target hunters/villagers).
  - [x] `EntityHunterBase` (attacks vampires on sight, uses crossbows and stakes).
- [x] **9.2. Custom AI Tasks (Forge 1.7.10 `EntityAIBase`)**
  - [x] `EntityAISunFlee`: Scans for shadows, caves, or water when the sun rises; runs away from daylight.
  - [x] `EntityAIBiteLiving`: Vampire attacks victim, latches on, drains blood, restores health.
  - [x] `EntityAICrossbowAttack`: Hunter keeps distance, reloads crossbow, fires quarrels.
- [x] **9.3. Vampire Entities**
  - [x] `EntityVampire` (Basic melee vampire).
  - [x] `EntityAdvancedVampire` (Faster, uses abilities, carries weapons).
  - [x] `EntityVampireBaron` & `EntityVampireBaroness` (Minibosses found in crypts, drops Pure Blood).
  - [x] `EntityBloodBat` (Hostile blood-feeding flying bats).
- [x] **9.4. Hunter Entities**
  - [x] `EntityHunter` (Basic patrol hunter with sword/stake).
  - [x] `EntityAdvancedHunter` (Crossbow marksman, uses holy water grenades).
  - [x] `EntityHunterTrainer` (Friendly NPC trainer in camps/villages for leveling).
  - [x] `EntityTaskmaster` (Gives faction quests/bounties).
- [x] **9.5. Converted Mobs (Bitten Creatures)**
  - [x] `EntityConvertedCow`, `EntityConvertedSheep`, `EntityConvertedHorse`, `EntityConvertedVillager` (Red glowing eyes, immune to bleeding, hostile/allied to vampires).
- [x] **9.6. Boss Encounters**
  - [x] `EntityDracula` (Multi-phase boss: Phase 1 melee/teleport, Phase 2 mist form & bat swarm, Phase 3 blood rage).
  - [x] `EntityVampireMother` (Ancient deity boss).
  - [x] Custom Boss Bars, sound themes, and unique drops.
- [x] **9.7. Mob & Boss Audio Asset Adaptation**
  - [x] Adapt sound events for Dracula, Ghosts, and Remains Defenders (`sounds.json`) with bundled .ogg assets or vanilla 1.7.10 sound mappings (replacing modern vex/allay/amethyst sound references).

---

## Phase 10: Minion Management & Lordship System
*Rule as a Vampire Count or Hunter General with loyal follower NPCs.*
*Original Sources:*
- Faction Lord API: `reference/vampirism/src/main/java/de/teamlapen/faction/api/factions/lord/ILordPlayer.java`
- Titles Provider: `reference/vampirism/src/main/java/de/teamlapen/faction/api/factions/lord/LordTitles.java`
- Minion Entities: `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/entity/minion/VampireMinionEntity.java`, `HunterMinionEntity.java`
- Minion Tasks Engine: `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/entity/minion/MinionTasks.java`
- Client Screens: `reference/vampirism/src/main/java/de/teamlapen/faction/client/gui/screens/MinionScreen.java`, `MinionStatsScreen.java`

- [x] **10.1. Lordship Hierarchy & Titles**
  - [x] Lord Levels 1 to 5 (`ILordPlayer`, `ILordTitleProvider`).
  - [x] Titles: Baron/Baroness, Viscount/Viscountess, Count/Countess, Duke/Duchess, Prince/Princess (`LordTitles.java`).
  - [x] Nametag prefix display and chat integration (`ScoreboardUtil.java`).
- [x] **10.2. Minion Entities (`VampireMinionEntity`, `HunterMinionEntity`)**
  - [x] Summoned or hired by Lords (`IMinionEntity`).
  - [x] Persistent ownership, stats, custom names, and equipment inventory (`MinionEntry.java`, `MinionContainer.java`).
- [x] **10.3. Minion Tasks & Orders (`MinionTasks.java`)**
  - [x] `TaskFollow`: Protects and follows the Lord.
  - [x] `TaskDefendArea`: Guards a designated radius around a beacon/coffin.
  - [x] `TaskCollectBlood`: Collects blood from surrounding wildlife and stores in containers.
  - [x] `TaskGatherLoot`: Collects drops from slain hunters/vampires.
- [x] **10.4. Minion Management GUI**
  - [x] Client GUI for inspecting minion health, stats, inventory, and issuing remote commands (`MinionScreen.java`, `MinionStatsScreen.java`).

---

## Phase 11: World Generation, Vampire Biome & Structures
*Atmospheric exploration across spooky biomes, crypts, and camps.*
*Original Sources:*
- Biomes: `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/biomes/VampirismBiomes.java`
- Vegetation & Foliage: `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/features/`
- Crypt Structure: `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/structures/crypt/CryptStructure.java`
- Hunter Camp & Outpost: `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/structures/huntercamp/HunterCampStructure.java`, `hunteroutpost/`
- Vampire Altar Ruins: `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/structures/vampirealtar/VampireAltarStructure.java`
- Dracula Castle: `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/structures/draculacastle/DraculaCastleStructure.java`

- [x] **11.1. Vampire Forest Biome**
  - [x] Biome registration (`BiomeGenVampireForest` / `VampirismBiomes.java`).
  - [x] Dark purple sky fog color (`0x1b002c`), blood-red water color (`0x4a0000`), dead grass color.
  - [x] Dense dark spruce trees with Spanish moss / cursed roots (`ModFeatures.java`).
  - [x] Vampire mob spawn weights enabled during both day and night (under tree canopy).
- [x] **11.2. Underground Vampire Crypt Structure (`CryptStructure.java`)**
  - [x] Procedural multiblock underground crypt rooms.
  - [x] Coffin chambers, Vampire Barons spawners, rare loot chests (Vampire Books, Pure Blood).
- [x] **11.3. Hunter Camps & Outposts (`HunterCampStructure.java`, `HunterOutpostStructure.java`)**
  - [x] Surface tents, watchtowers, training dummies, Hunter Trainers, weapon benches.
- [x] **11.4. Altar Ruins in Overworld (`VampireAltarStructure.java`)**
  - [x] Ancient abandoned Altar of Inspiration structures in deep forests.
- [x] **11.5. Dracula Castle Boss Arena (`DraculaCastleStructure.java`)**
  - [x] Gothic castle structure in Vampire Forest with throne room, bat roosts, and boss summon altar.
- [x] **11.6. Dimension & Ambient Audio Normalization**
  - [x] Adapt dimension ambient sound events (`ambient.velmorra_rumble`) with 1.7.10 sound loops.

---

## Phase 12: Client Experience, GUI, Angelica Shaders & Integrations
*Polished visuals, modern HUD overlays, shader compatibility, and modpack integrations.*
*Original Sources:*
- Faction Dashboard: `reference/vampirism/src/main/java/de/teamlapen/faction/client/gui/screens/FactionMenuScreen.java`
- Skills Screen: `reference/vampirism/src/main/java/de/teamlapen/faction/client/gui/screens/skills/SkillsScreen.java`, `SkillSegmentComponent.java`, `SkillSegmentConnection.java`
- Action Radial: `reference/vampirism/src/main/java/de/teamlapen/faction/client/gui/screens/SelectActionRadialScreen.java`, `DualSwitchingRadialMenu.java`
- HUD Overlays: `reference/vampirism/src/main/java/de/teamlapen/vampirism/client/gui/overlay/`
- Blood Vision: `reference/vampirism/src/main/java/de/teamlapen/vampirism/common/world/entity/player/vampire/vision/BloodVision.java`

- [x] **12.1. In-Game HUD Overlays**
  - [x] Blood Bar overlay (`BloodBarOverlay.java`): replaces vanilla hunger bar when Vampire, with blood drop icons, saturation halo, thirst warnings.
  - [x] Sun exposure warning vignette (`SunOverlay.java`, `SunBlindOverlay.java`).
  - [x] Rage effect vignette (`RageOverlay.java`) and Bat flight overlay (`BatOverlay.java`).
  - [x] Active action cooldown badges on hotbar (`ActionCooldownOverlay.java`).
- [x] **12.2. Interactive GUIs (FactionAPI Port)**
  - [x] `FactionMenuScreen` on `[P]`: Main player dashboard with overview, skills, tasks, and refinement slots.
  - [x] `SkillsScreen`: Smooth zooming and dragging canvas showing skill nodes, lines, and tooltips.
  - [x] `SelectActionRadialScreen`: Radial pop-up selector for triggering skills via hotkey `[R]`.
  - [x] `AltarInfusionScreen`, `HunterTableScreen`, `AlchemicalCauldronScreen`.
- [x] **12.3. Angelica Shader Compatibility & Post-Processing**
  - [x] `BloodVision`: Highlights living warm-blooded creatures through blocks in red/gold glow without breaking Angelica/Sodium pipelines.
  - [x] Night Vision tone mapping (no bright green wash-out).
  - [x] Fog Diffuser volumetric fog rendering.

---

## Phase 13: Villages, Territory Control & Faction War
*Conquer villages, raise faction banners, and defend territory against enemy raids.*
*Original Sources:*
- Village Faction API: `reference/vampirism/src/main/java/de/teamlapen/faction/api/factions/village/IFactionVillage.java`
- Village Logic: `reference/vampirism/src/main/java/de/teamlapen/faction/common/factions/FactionVillage.java`
- Totem Block & Entity: `reference/vampirism/src/main/java/de/teamlapen/faction/common/world/blocks/TotemBaseBlock.java`, `TotemTopBlock.java`, `TotemBlockEntity.java`
- Totem Renderer: `reference/vampirism/src/main/java/de/teamlapen/faction/client/renderer/TotemRenderer.java`
- Village Banners: `reference/vampirism/src/main/java/de/teamlapen/faction/api/factions/village/VillageBanner.java`
- Capture Entity: `reference/vampirism/src/main/java/de/teamlapen/faction/api/world/entities/IVillageCaptureEntity.java`

- [x] **13.1. Village Faction Territory Subsystem (`FactionVillage.java`)**
  - [x] Track village boundaries, controlling faction, and capture status.
  - [x] Village faction state machine: Neutral, Vampire Controlled, Hunter Controlled.
- [x] **13.2. Faction Totem Multiblock (`TotemBlockEntity.java`)**
  - [x] Central totem block structure (`TotemBase` + `TotemTop`).
  - [x] Right-click interaction to view defense level, garrison health, and initiate capture.
- [x] **13.3. Village Raid & Capture Mechanics (`IVillageCaptureEntity.java`)**
  - [x] Attacking players trigger village siege raid.
  - [x] Capture progress bar: players must hold territory near the totem and defeat defenders.
  - [x] Capture completion flips village control, spawns faction-specific guards, and replaces banners (`VillageBanner.java`).
- [x] **13.4. Faction Villagers & Trades**
  - [x] Villagers in vampire-controlled villages provide blood peacefully or become thralls.
  - [x] Hunter-controlled villages spawn additional Hunter patrols and discount trades.

---

## Phase 14: Cross-Mod Integrations & Balance Tuning (GTNH Stack)
*Seamless integration with modern backports and GT New Horizons core mods.*

- [x] **14.1. Et-Futurum-Requiem Integration**
  - [x] Shared crossbow mechanics, arrow/quarrel pooling, shield blocking against stakes.
  - [x] Modern banner patterns for Vampire and Hunter factions.
- [x] **14.2. Combat Backport Integration**
  - [x] Attack cooldowns and attack speed attributes on Hunter Axes and Heartseekers.
  - [x] Sweeping Edge enchantment support for faction blades.
- [x] **14.3. Villager Trades Backport Integration**
  - [x] Modern 1.14+ trading GUI and workstation AI for Hunter Trainers and converted villagers.
- [x] **14.4. VillageNames Integration**
  - [x] Procedural gothic/vampiric name generation for captured villages and crypts.
- [x] **14.5. Hodgepodge & GTNHLib Integration**
  - [x] Celestial angle time sync with Hodgepodge day/night calculations.
  - [x] Matrix transformations and rendering math via GTNHLib.
- [x] **14.6. NEI (Not Enough Items) Integration**
  - [x] Recipe handlers for Altar of Inspiration, Altar of Infusion, Weapon Table, and Cauldron.
