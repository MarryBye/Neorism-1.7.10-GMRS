# Neorism 1.7.10 - Backport Roadmap & Milestones

This document tracks the progress of porting all modern Vampirism mechanics to Minecraft 1.7.10.

---

## 📋 Status Overview

- [x] **Phase 0: Project Setup & Legal Foundation**
  - [x] GTNH Gradle 9 / Java 25 workspace configuration
  - [x] Package setup: `com.marrybye.neorism`
  - [x] Project metadata, mod name, mod ID in `gradle.properties` & `mcmod.info`
  - [x] Full original license acquisition (LGPL-3.0, Asset License, Narrative License)
  - [x] Disclaimers, author credits (`maxanier`, `TeamLapen`, `MarryBye + Gemini AI`)
  - [x] Agent rules (`AGENTS.md`) and project technical context (`.agents/PROJECT_CONTEXT.md`)
  - [x] Skills folder structure (`.agents/skills/`)

- [ ] **Phase 1: Core Foundation & Registries**
  - [ ] Creative Tabs (`NeorismCreativeTab` for Items, Blocks, Weapons)
  - [ ] Central Registry Architecture (`ModItems`, `ModBlocks`, `ModEntities`, `ModPotions`)
  - [ ] SimpleNetworkWrapper networking pipeline setup
  - [ ] Base Configuration file (`NeorismConfig`)

- [ ] **Phase 2: Faction Framework & Player Capabilities**
  - [ ] `Faction` enum (`NONE`, `VAMPIRE`, `HUNTER`)
  - [ ] Extended Player Properties (`IExtendedEntityProperties`):
    - `VampirePlayerProperty`: Blood level, max blood, vampire level (1-14), skills, bite cooldown
    - `HunterPlayerProperty`: Hunter level (1-14), skills, training status
    - `FactionPlayerProperty`: Active faction, faction reputation
  - [ ] Packet synchronization on login, dimension change, and respawn
  - [ ] Client HUD Overlays: Blood bar, vampire level, action selector

- [ ] **Phase 3: Vampire Core Mechanics**
  - [ ] Sanguinare Vampiris infection (bite by vampire mob, potion/curse)
  - [ ] Blood Feeding mechanic:
    - Biting living mobs (Cows, Sheep, Pigs, Horses)
    - Biting Villagers (drain blood, blood depletion tracking)
    - Blood bottles and blood containers
  - [ ] Sun Exposure & Damage calculations:
    - Daylight burning / weakness
    - Sunscreen / protection skills & umbrellas
  - [ ] Night Vision & Vampire Buffs (speed, jump, strength, night vision)
  - [ ] Garlic weakness & repellent effects
  - [ ] Coffin mechanics (sleeping through the day)

- [ ] **Phase 4: Altars & Vampire Progression (Level 1-14)**
  - [ ] Altar of Inspiration (multiblock structure, recipes, ritual logic)
  - [ ] Altar of Infusion (advanced leveling requirements: human hearts, pure blood, soul orbs)
  - [ ] Blood Container & Blood Sieve
  - [ ] Vampire Level 1 through 14 milestone unlocks

- [ ] **Phase 5: Hunter Core Mechanics & Progression**
  - [ ] Hunter Training: Hunter Trainer NPC and training contracts
  - [ ] Hunter Weapons & Equipment:
    - Crossbows & custom bolts (normal, holy water, poison, fire)
    - Stakes, Holy Water Splash / Grenades
    - Garlic Diffuser & Garlic bread
    - Hunter Armor sets (Leather, Iron, Enhanced, Ultimate)
  - [ ] Hunter Weapon Table & Alchemical Cauldron
  - [ ] Hunter Level 1 through 14 milestone unlocks

- [ ] **Phase 6: Skill Trees & Active Abilities**
  - [ ] Vampire Skill Tree (Bat Form, Teleport/Mist, Blood Vision, Freeze, Rage)
  - [ ] Hunter Skill Tree (Enhanced Senses, Quick Reload, Garlic Aura, Resistance)
  - [ ] Action Keybind Manager and radial / hotkey activation system

- [ ] **Phase 7: Entities, Minions & Bosses**
  - [ ] Vampire Mobs: Basic Vampire, Advanced Vampire
  - [ ] Vampire Baron & Vampire Baroness (forest bosses)
  - [ ] Hunter Mobs: Basic Hunter, Hunter Trainer, Expert Hunter
  - [ ] Minion System: Taming, commanding, equipping, and task delegation
  - [ ] Ghost / Soul entities

- [ ] **Phase 8: World Generation & Structures**
  - [ ] Vampire Forest Biome (cursed earth, blood orchids, dark trees)
  - [ ] Vampire Village generation & Village faction control
  - [ ] Hunter Tent Camps & Outposts
  - [ ] Vampire Crypts & Dungeons

- [ ] **Phase 9: Audio, Visual Polish & Cross-Mod Integration**
  - [ ] Sound events registration
  - [ ] Visual particle effects (blood splashes, ritual circles, mist)
  - [ ] Integration with GTNH / NEI (NotEnoughItems recipes for altars & tables)
  - [ ] Integration with Thaumcraft / Blood Magic (if applicable)
