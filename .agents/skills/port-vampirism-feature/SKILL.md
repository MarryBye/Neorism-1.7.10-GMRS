---
name: port-vampirism-feature
description: Guide and procedure for porting mechanics, items, and features from modern Vampirism versions to Neorism Minecraft 1.7.10.
---

# Porting Modern Vampirism Features to Neorism (Minecraft 1.7.10)

Use this skill whenever tasked with implementing or porting a feature from modern Vampirism (1.12 - 1.21) to 1.7.10.

## 1. Feature Investigation Workflow
1. Check the modern implementation in TeamLapen/Vampirism:
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
1. **Model / Data:** Define state and properties.
2. **Registration:** Register items/blocks in `com.marrybye.neorism.common.*`.
3. **Network Synchronization:** If state affects client HUD or actions, write packet pair.
4. **Logic & Events:** Hook into Forge event bus (`MinecraftForge.EVENT_BUS` or `FMLCommonHandler.bus()`).
5. **Client Presentation:** Add textures, icons, localized `.lang` strings, and renderers.
6. **Compile & Test:** Run `./gradlew classes` to verify clean compilation.
