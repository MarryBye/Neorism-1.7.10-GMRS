# Neorism 1.7.10 - Project Technical Context & Architecture

## 1. Project Background & Motivation

The original 1.7.10 version of *Vampirism* was an early, simplistic release that lacked the extensive faction systems, skill trees, rich entity ecosystem, minions, advanced rituals, and polished mechanics introduced in modern Minecraft versions (1.12.2, 1.16.5, 1.18.2, 1.20+, and 1.21).

**Neorism** is created to bring the full, modern, and mature Vampirism experience to **Minecraft 1.7.10**, seamlessly integrating into the modern 1.7.10 **GT New Horizons (GTNH)** modded ecosystem.

---

## 2. Technical Stack & GTNH Environment

### Build System & Toolchain
- **Gradle Version:** Gradle 9.7.1 with `gtnhsettingsconvention` and `gtnhconvention` (2.0.33+).
- **Gradle JVM Daemon:** Java 25 (Eclipse Temurin 25 / Adoptium).
- **Language Level & Jabel:** Modern Java syntax (switch expressions, lambdas, type inference, records) compiled to Java 8 bytecode via `jabel`.
- **Target Minecraft / Forge:** Minecraft 1.7.10 with Forge 10.13.4.1614.
- **Modern Runtime Support:** Runs in development with LWJGL3ify, modern JVM optimizations, Hodgepodge, and UniMixins.

### 2.1. Integrated GTNH Ecosystem Dependencies
Neorism is configured to integrate with and support the latest versions (and anticipated future releases) of the following core GTNH mods:
1. **Et-Futurum-Requiem (`ganymedes01.etfuturum:Et-Futurum-Requiem:2.6.64-GTNH:dev`)**:
   Provides backported modern vanilla features (armors, crossbows, banners, sound types, block models). Neorism hunter crossbows and item behaviors hook into Et Futurum's item architectures.
2. **GTNHLib (`com.github.GTNewHorizons:GTNHLib:0.11.54:dev`)**:
   Core GTNH utility and math library, utilized for high-performance particle systems, color management, and rendering routines.
3. **Hodgepodge (`com.github.GTNewHorizons:Hodgepodge:2.7.216:dev`)**:
   Deep performance fixes, celestial cycle / sun calculation synchronization, and compatibility with modern JVMs (Java 17-25).
4. **Angelica (`com.github.GTNewHorizons:Angelica:2.2.33:dev`)**:
   Modern rendering overhaul (Sodium / Iris / OptiFine backport). Custom GUI shaders, vampire blood vision, and ritual TESR models must comply with modern OpenGL shader pipelines and avoid deprecated immediate mode calls (`glBegin`/`glEnd`).
5. **VillageNames (`com.github.GTNewHorizons:VillageNames:4.5.19-GTNH:dev`)**:
   Integration with village structures and naming. Used for vampire village takeovers, hunter outpost names, and villager lore.
6. **lwjgl3ify (`com.github.GTNewHorizons:lwjgl3ify:3.0.39:dev`)**:
   Provides modern input handling (GLFW, raw mouse input, high-refresh rate displays) and enables running on Java 17-25.
7. **UniMixins (`io.github.legacymoddingmc:unimixins:0.3.1:dev`)**:
   SpongePowered Mixin implementation for 1.7.10 bytecode manipulation for entity AI, day/night burning calculations, and HUD hooking.
8. **Combat Backport (`com.marrybye.combatbackport:combatbackport:1.0.0:dev`)**:
   Modern 1.9+ combat backport (attack cooldowns, weapon speed, shields, Sweeping Edge). Neorism vampire weapons, stakes, and combat abilities are designed to integrate with Combat Backport's speed heuristics and shielding mechanics.
9. **Villager Trades Backport (`com.marrybye.villagertradesbackport:villagertradesbackport:1.0.0:dev`)**:
   Modern 1.14+ villager trading system (levels, workstations, trade rebalance). Neorism vampire village control, villager biting effects, and hunter priests/trainers hook into modern villager trades and profession data.

All dependencies are resolved via Maven (`GTNH Nexus` and `mavenLocal`) in `dependencies.gradle` using `devOnlyNonPublishable`, ensuring clean decoupling from runtime distribution.

---

## 3. Subsystem Migration Mapping (Modern Vampirism -> Neorism 1.7.10)

### 3.1. Player Capabilities & State Storage
* **Modern Vampirism:** Uses Forge/NeoForge Capabilities attached to `Player` (`IVampirePlayer`, `IHunterPlayer`, `IFactionPlayer`).
* **Neorism (1.7.10):**
  - Implement `IExtendedEntityProperties` (IEEP):
    - `VampirePlayerProperty` (`neorism:vampire_player`)
    - `HunterPlayerProperty` (`neorism:hunter_player`)
    - `FactionPlayerProperty` (`neorism:faction_player`)
  - Hook into `EntityConstructing` event to attach properties.
  - Hook into `EntityJoinWorldEvent` and `PlayerEvent.Clone` (for death/respawn and dimension change persistence) to restore and re-sync data.
  - Fast helper accessors: `VampirePlayer.get(entityPlayer)`.

### 3.2. Networking & Client-Server Sync
* **Modern Vampirism:** Custom network channels with records / friendly byte buffers.
* **Neorism (1.7.10):**
  - `SimpleNetworkWrapper` registered in `CommonProxy` under channel name `"neorism"`.
  - Packets implement `IMessage` and `IMessageHandler<REQ, REPLY>`.
  - Dedicated packets for:
    - `SyncVampireDataPacket`: Syncs blood level, max blood, vampire level, active skills.
    - `SyncHunterDataPacket`: Syncs hunter level, skills.
    - `ActionKeyPacket`: Handles hotkey presses (bite, skill trigger, bat mode toggle).
    - `RitualUpdatePacket`: Syncs altar animation, particles, and progress.

### 3.3. Asset & Visual Conversions
* **Textures:** Modern PNG textures (16x16, 32x32, 64x64, GUI sprites) can directly be loaded by Minecraft 1.7.10 under `assets/neorism/textures/...`.
* **Models:**
  - Modern JSON models (`models/item/...`, `models/block/...`) were introduced in 1.8.
  - In 1.7.10:
    - Standard blocks use sprite-based icon textures via `IIcon` / `IIconRegister`.
    - Custom 3D blocks (Altars, Coffin, Alchemical Cauldron) use `TileEntitySpecialRenderer` (TESR) or `ISimpleBlockRenderingHandler` (ISBRH) with Wavefront `.obj` or Java-based `ModelBase` models.
    - Custom handheld items (Crossbows, Blood bottles, Weapons) can utilize `IItemRenderer`.
* **GUI / HUD:**
  - Blood bar HUD overlay hooked into `RenderGameOverlayEvent.Pre` / `Post`.
  - Skill menu, Altar GUI, Alchemical Cauldron GUI extending `GuiContainer`.

---

## 4. Licensing Integrity & Community Compliance

1. **Authorship Recognition:**
   - Always retain references to `maxanier` and `TeamLapen`.
   - Never remove or obscure original license notices in `LICENSE` and `licenses/`.
2. **Support Isolation:**
   - Any question, issue, crash, or bug must be addressed to the Neorism issue tracker, NEVER to Vampirism or maxanier.
3. **Non-Commercial:**
   - Absolutely no paywalls, paid early-access, or commercial distribution of assets.
