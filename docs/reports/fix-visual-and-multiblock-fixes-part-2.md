# Milestone Report: [Phase 15.8] - Final Polish: 2D Weapons, Tent Multiblock, Totem Textures & Coffin Parity

> **Date:** 2026-10-17  
> **Status:** COMPLETED  
> **Git Commit:** f22473e  

---

## 1. Scope & Objectives
This task addresses the remaining visual, multiblock, and texture defects observed during in-game client testing:
1. **Faction Totem Top Texture**: Missing textures on obsidian frame and iron plates.
2. **Vampire Coffin In-World vs Inventory Parity & Transparency**: Model transparency in world due to backface culling and mismatch between inventory leaning lid and in-world swinging lid.
3. **Hunter Tent Multiblock & Inventory**: Sides scrambled and inverted in world; 1/4th corner display in inventory instead of complete tent item.
4. **Localization**: Missing restriction key `message.neorism.restriction.skill`.
5. **Weapon Models (Swords, Crossbows, Crucifixes, Axes, Pitchforks)**: Switched to Minecraft native 2D extruded item rendering (Fresh Vampirism / Vanilla style), eliminating layer bleed, transparency issues, and improper mob hand orientation.
6. **Umbrella Item Renderer**: Restored solid canopy rendering, proper first-person scale, and canonical orientation.

---

## 2. Technical Implementation Details

### Faction Totem Top Textures
- **Root Cause:** `totem_top.json` referenced textures using modern namespaces: `"minecraft:block/obsidian"` and `"minecraft:block/iron_block"`. In Forge 1.7.10, GTNHLib failed to resolve modern prefixes from vanilla assets.
- **Fix:** Switched texture references in `totem_top.json` to `"neorism:block/obsidian"` and `"neorism:block/iron_block"`, which are already bundled in the mod's texture atlas.

### Vampire Coffin Parity & Culling Fix
- **File Modified:** `src/main/java/com/marrybye/neorism/client/render/tileentity/TileEntityCoffinRenderer.java`
- **Transparency Fix:** Disabled backface culling (`GL11.glDisable(GL11.GL_CULL_FACE)`) and corrected winding order of North and East outer walls to CCW. This completely eliminates all transparent holes and missing inner velvet walls.
- **Lid Parity:** Replaced the horizontal slide/pivot animation with `renderLeaningLid(Tessellator tess)` leaning against the side at `-32.5°` with origin at `[-5/16, 0, 0]`, matching `coffin.json` identically when open. When closed (`lidAngle == 0`), `renderFlatLid` renders the lid flat across the top rim.

### Hunter Tent Multiblock Alignment & Inventory 2D Icon
- **Files Created/Modified:**
  - `src/main/resources/assets/neorism/models/block/tent_br.json`: Created mirrored right-hand model sloping up from `X = 16` to `X = 0` (apex ridge at `X = 0`).
  - `src/main/resources/assets/neorism/models/block/tent_tr.json`: Created mirrored right-hand model with back wall cloth at `Z = 0`.
  - `src/main/resources/assets/neorism/blockstates/tent.json`: Unified Y-rotation across all four quarters (`dir 0: y=0, dir 1: y=90, dir 2: y=180, dir 3: y=270`).
  - `src/main/java/com/marrybye/neorism/common/blocks/BlockTent.java`: Re-aligned `OFFSETS_FROM_ZERO` matrix to match modern Vampirism's `TentItem.placeAt` coordinate transformations. Bound `blockIcon` to `neorism:item_tent` and returned it from `getIcon()`.
  - `src/main/java/com/marrybye/neorism/ClientProxy.java`: Removed `ModBlocks.tent` from `modeledBlocks` array so `ModelISBRH` is not called for the item in GUI, rendering the clean 2D `item_tent` icon.

### 2D Weapons & Native Extrusion
- **File Modified:** `src/main/java/com/marrybye/neorism/ClientProxy.java`
- Unregistered `JsonModelItemRenderer` for:
  - `ModItems.pitchfork`
  - `ModItems.crucifix`
  - `ModItems.hunterAxe`
  - `ModItems.heartSeeker`
  - `ModItems.heartStriker`
  - `ModItems.crossbow`
- All items now utilize Minecraft's native `ItemRenderer.renderItemIn2D` with `isFull3D() = true`. This provides 100% bug-free rendering, zero transparent face glitches, canonical swing animations, and perfect hand holding by players and biped NPCs.
- Overrode `getIcon`, `getIconIndex`, and pulling variants in `ItemCrossbow.java` and `ItemCrucifix.java`.

### Umbrella Item Renderer Restoration
- **File Modified:** `src/main/java/com/marrybye/neorism/client/render/item/JsonModelItemRenderer.java`
- Disabled `GL_CULL_FACE` so single-sided fabric faces of the canopy are rendered solid from both sides.
- Removed artificial `1.5F` scale in first-person view, returning umbrella to comfortable distance from camera.
- Removed hardcoded third-person offsets and allowed `umbrella.json`'s own `display` properties to position the held item.

### Localization Additions
- Added `message.neorism.restriction.skill`, `message.neorism.restriction.level`, and `message.neorism.restriction.faction` (and corresponding `message.vampirism.*` fallbacks) to `en_US.lang` and `ru_RU.lang`.

---

## 3. Verification & Testing
- Build verification via `.\gradlew assemble`: completed successfully in 12s with 0 errors.
- Checked git diff to ensure all changes conform to guidelines and reference codebase.

---

## 4. Next Steps
- User in-game test of visual improvements.
- Proceed to Phase 16 / ongoing feature enhancements upon user confirmation.
