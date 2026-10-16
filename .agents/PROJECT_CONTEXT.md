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
