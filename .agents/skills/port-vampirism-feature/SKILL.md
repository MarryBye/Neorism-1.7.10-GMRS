---
name: port-vampirism-feature
description: Guide and procedure for porting mechanics, items, and features from modern Vampirism versions to Neorism Minecraft 1.7.10.
---

# Porting Modern Vampirism Features to Neorism (Minecraft 1.7.10)

> **Критическое правило:**  
> Всегда следовать скиллу `canonical-backport` (`.agents/skills/canonical-backport/SKILL.md`).  
> **НЕ создавать новый код и НЕ писать по представлению.** Всегда находить исходный код в `reference/vampirism/` и точно адаптировать его под 1.7.10.

Use this skill whenever tasked with implementing or porting a feature from modern Vampirism (1.12 - 1.21) to 1.7.10.

## 1. Feature Investigation Workflow
1. Check the modern implementation in TeamLapen/Vampirism (`reference/vampirism/`):
   - Identify data classes, logic handlers, network packets, and GUI/client visual components.
2. Determine dependencies on modern Minecraft APIs:
   - Tags, Registries, Capabilities, Particle types, SoundEvents, BlockPos/Direction, Math libraries.
3. Map modern concepts to Forge 1.7.10 equivalents:
   - `BlockPos` -> `int x, int y, int z`
   - `Direction` -> `ForgeDirection` or integer metadata
   - `CompoundTag` -> `NBTTagCompound`
   - Modern Capability -> `IExtendedEntityProperties`
   - Packets -> `IMessage` / `IMessageHandler` via `SimpleNetworkWrapper`

## 2. Implementation Steps
1. **Model / Data:** Define state and properties, copying original constants and fields.
2. **Registration:** Register items/blocks in `com.marrybye.neorism.common.*`.
3. **Network Synchronization:** If state affects client HUD or actions, write packet pair.
4. **Logic & Events:** Hook into Forge event bus (`MinecraftForge.EVENT_BUS` or `FMLCommonHandler.bus()`).
5. **Client Presentation:** Add textures, icons, localized `.lang` strings, and renderers.
6. **Compile & Test:** Run `./gradlew classes` to verify clean compilation.

## 3. 3D Models, Textures & Localization (GTNHLib / 1.7.10)
Consult `docs/MODEL_TEXTURE_PORTING_GUIDE.md` for full details:
1. **pack.mcmeta format 4:** Mandatory at root of resources for GTNHLib model bakery.
2. **Blockstates variants:** Only `""` or `"normal"` for empty states; all property variants MUST have `=`. Bare words like `"inventory"` are forbidden.
3. **Dual Texture Path:** Every block texture must exist in both `textures/block/<name>.png` and `textures/blocks/<name>.png`.
4. **Vanilla Mirroring:** All vanilla textures referenced by modern models must exist in `assets/minecraft/textures/block/`.
5. **ItemRenderer:** Modeled blocks must extend `BlockModeled` and register `ModelISBRH.INSTANCE.get()` as `IItemRenderer` in `ClientProxy.init`.
6. **Localization (.lang):**
   - Items: `item.neorism.<name>.name` (override `getItemStackDisplayName` to prevent vanilla `.name` suffix bug).
   - Blocks: `tile.neorism.<name>.name`.
   - Tabs: `itemGroup.neorism.<tab>` (override `getTranslatedTabLabel` with fallbacks).
