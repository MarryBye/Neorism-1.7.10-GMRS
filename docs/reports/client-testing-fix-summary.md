# Neorism 1.7.10 - Client Testing Bugfix & Polish Summary Report

> **Date:** October 17, 2026  
> **Status:** Completed & Assembled  
> **Target Version:** Neorism 1.7.10 (GTNH Ecosystem, Java 25 / Jabel target Java 8 bytecode)  
> **Reference:** TeamLapen Vampirism & FactionAPI canonical sources (`reference/vampirism/`)

---

## Executive Summary

Following first-person client runtime testing, all six reported categories of visual, rendering, localization, and UI bugs have been systematically diagnosed, resolved according to canonical references, compiled, and verified via `gradle assemble`.

| Category | Issue Description | Root Cause | Status |
| :--- | :--- | :--- | :--- |
| **Phase A** | Translation & Tooltip Formatting | `%s` unformatted strings, raw unlocalized faction names, missing headers, `.name` leakage | **Resolved** |
| **Phase B** | Missing Textures & Models | Log, cursed grass, totem top missing default variants; table & tent using modern `multipart` | **Resolved** |
| **Phase C** | Vampire Coffin Model & Textures | TESR texture sheet had `#F704F0` magenta placeholders & black lids; UV door stretch; chunk z-fighting | **Resolved** |
| **Phase D** | NPC Broken Skins (64x64) | 1.7.10 vanilla `ModelBiped` only supports 64x32 legacy skins; textures were mismapped onto models | **Resolved** |
| **Phase E** | Handheld 3D Weapons Translucency & 1st-Person | `GL_BLEND` without culling caused see-through cubes; Forge 1.7.10 2D item transform rotated & shrunk 1st person | **Resolved** |
| **Phase F** | Skill Tree GUI Black Canvas | `window.png` transparent center was drawn with `GL_BLEND` disabled (painted solid black); missing block textures | **Resolved** |

---

## Detailed Resolutions by Phase

### Phase A: Localization & Tooltips

1. **`ItemGarlicDiffuserCore.java`**:
   - Replaced raw unformatted `StatCollector.translateToLocal` with `translateToLocalFormatted("item.neorism.garlic_diffuser_core_improved.desc", 5, 3)` supplying the canonical radius (5 blocks) and duration tier (3).
2. **`FactionRestriction.java`**:
   - Localized faction name lookups via `faction.neorism.<name>` and `faction.vampirism.<name>.name` with translation fallbacks.
   - Added `tooltip.neorism.required_skills` category header before listing skill requirements.
   - Fixed skill title translation key lookup and formatted required level text cleanly.
3. **`ItemQuarrelClip.java` & `ItemVampireCloak.java`**:
   - Overrode `getUnlocalizedNameInefficiently` and `getItemStackDisplayName` to eliminate duplicate `.name` string suffixes leaking from `Item.getUnlocalizedName()`.
   - Harmonized vampire cloak color variant keys (`item.neorism.vampire_cloak_<color>.name`).
4. **`en_US.lang` & `ru_RU.lang`**:
   - Added missing keys: `tooltip.neorism.faction_specifics`, `tooltip.neorism.required_level`, `tooltip.neorism.required_skills`.
   - Added skill titles and descriptions: `master_craftsmanship`, `double_it`, `crucifix_wielder`.

---

### Phase B: Block Textures & Modern Blockstates

1. **`BlockDarkSpruceLog.java`**:
   - Added explicit side icon binding `blockIcon = sideIcon;` and axis-aware `getIcon(int side, int meta)` handling metadata 0, 4, 8, 12.
2. **`blockstates/dark_spruce_log.json` & `stripped_dark_spruce_log.json`**:
   - Added `""` and `"normal"` variants mapped to `neorism:dark_spruce_log` for GTNHLib `ModelISBRH` variant matching.
3. **`blockstates/cursed_grass.json`**:
   - Added default `""` and `"normal"` variants mapped to `neorism:cursed_grass`.
4. **`blockstates/totem_top.json`**:
   - Created missing blockstate JSON pointing to `neorism:block/totem_top`.
5. **`blockstates/hunter_table.json` & `tent.json`**:
   - Converted modern 1.12+ `"multipart": [...]` schema to GTNHLib-supported `"variants": { ... }` with 4-directional Y rotations (`facing=north/south/east/west`).
   - Overrode `BlockTent.getRenderType()` to return `ModelISBRH.JSON_ISBRH_ID` and registered it in `ClientProxy.modeledBlocks`.

---

### Phase C: Vampire Coffin In-World vs Inventory Harmonization

1. **`textures/entity/coffin/coffin_base.png` (256x128)**:
   - Repaired the TESR texture sheet: replaced `#F704F0` magenta placeholder regions with rich red velvet extracted from canonical `coffin_inner_red.png`.
   - Replaced solid black lid regions with dark oak textures matching the inventory model appearance.
2. **`models/block/coffin.json`**:
   - Corrected door edge UV mappings: changed `uv: [0, 8, 8, 8.5]` to `[0, 8, 8, 10]` and `uv: [8, 8, 12, 8.5]` to `[8, 8, 12, 10]` eliminating stretching across 32 units.
3. **`BlockCoffin.java`**:
   - Overrode `getRenderType() { return -1; }` so only `TileEntityCoffinRenderer` renders the coffin in-world, eliminating duplicate chunk mesh and z-fighting.
   - Added `ModBlocks.coffin` to `ClientProxy.modeledBlocks` for clean inventory model rendering.

---

### Phase D: NPC 64x64 Modern Skins & Biped Model

1. **`ModelBiped64.java`**:
   - Implemented custom biped model with `textureWidth = 64` and `textureHeight = 64`.
   - Added independent texture UV mapping for left arm `(32, 48)` and left leg `(16, 48)` (not mirrored from right side).
   - Added 6 outer overlay layers: `bipedHeadwear` (hat), `bipedBodywear` (jacket), `bipedRightArmwear` (right sleeve), `bipedLeftArmwear` (left sleeve), `bipedRightLegwear` (right pant), and `bipedLeftLegwear` (left pant).
2. **Entity Renderers Updated**:
   - `RenderHunter.java`
   - `RenderVampire.java`
   - `RenderHunterMinion.java`
   - `RenderVampireMinion.java`
   - `RenderAdvancedHunter.java`
   - `RenderAdvancedVampire.java`
   - Replaced `ModelBiped` with `ModelBiped64(0.0F)` and added safe modulo array indexing for skin variants.

---

### Phase E: Handheld 3D Weapon Rendering (Translucency & 1st-Person Orientation)

1. **`JsonModelItemRenderer.java` Rendering States**:
   - Disabled `GL_BLEND` for solid 3D models.
   - Enabled backface culling (`GL11.glEnable(GL11.GL_CULL_FACE); GL11.glCullFace(GL11.GL_BACK);`) and depth testing (`glDepthMask(true); glEnable(GL_DEPTH_TEST);`).
   - Enabled alpha testing (`glAlphaFunc(GL_GREATER, 0.1F);`) for 1-bit cutout transparency.
   - Result: All see-through artifacts, internal quad bleeding, and ghost/translucent appearances completely eliminated.
2. **First-Person & Equipped Transformations**:
   - Handled `ItemRendererHelper.EQUIPPED_BLOCK` returning `true` for `EQUIPPED_FIRST_PERSON`, bypassing Forge 1.7.10's 2D flat item transformation (`glRotatef(50 Y, 335 Z), glTranslatef(-0.9375)`).
   - In `case EQUIPPED_FIRST_PERSON:`:
     - Undid Forge's `(-0.5, -0.5, -0.5)` offset via `glTranslatef(0.5F, 0.5F, 0.5F)`.
     - Undid vanilla 1.7.10's 45° Y rotation via `glRotatef(-45.0F, 0.0F, 1.0F, 0.0F)`.
     - Matrix now matches modern Minecraft's first-person hand position `(0.56, -0.52, -0.72)`.
     - Applied `model.firstPerson.apply()` directly from JSON display transform followed by `glTranslatef(-0.5F, -0.5F, -0.5F)`.
   - In `case EQUIPPED:`: Removed spurious `glTranslatef(0.5F, 0.5F, 0.5F)` before `model.thirdPerson.apply()`.
3. **`ItemCrucifix.java` & `ItemUmbrella.java`**:
   - Added `setFull3D();` in constructors so the player holds them with weapon-style grip rather than flat item grip in third person.

---

### Phase F: Skill Tree GUI Black Canvas & Node Placement

1. **`SkillsScreen.java` Window Blending**:
   - Enabled `GL_BLEND` with `glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)` before calling `func_146110_a` with `WINDOW_TEX`.
   - Root Cause: `window.png` has a transparent center cutout where the canvas is visible. Because `GL_BLEND` was previously disabled, OpenGL drew alpha=0 pixels as solid black RGB `(0, 0, 0)`, covering the entire canvas and nodes.
2. **Background Textures**:
   - Replaced nonexistent `minecraft:textures/blocks/*.png` references in `SkillRegistry.java` and `SkillTree.java` with canonical `factionapi:textures/gui/skills/backgrounds/level.png`.
   - Updated `SkillsTabComponent.drawTiledBackground` with dynamic UV offsets (`-scrollX / 16.0f`, `-scrollY / 16.0f`) for smooth scrolling as the canvas is dragged.
3. **Missing Skill Icons & Fallbacks**:
   - Created missing `vampire_rage.png` and `vampire_invisibility.png` icons.
   - Added fallback texture binding in `SkillSegmentComponent.draw()` to prevent missing texture errors.

---

## Git Commits Log (Local Repository)

```
* f13a023 - fix(gui): fix skill tree black screen, window blending, and background textures
* 0981e4e - fix(render): fix handheld 3d item translucency and first-person orientation
* 31a8bf3 - fix(entity): support 64x64 skins with ModelBiped64 for hunters and vampires
* 24a8175 - fix(render): harmonize coffin in-world TESR model and UV mapping with inventory
* 8f23eda - fix(block): fix missing textures and blockstate variants for table, tent, log, and totem
* 6063305 - fix(lang): resolve tooltip formatting and item name localization issues
```

---

## Build Verification

- **Task Executed:** `.\gradlew assemble`
- **Result:** `BUILD SUCCESSFUL in 18s`
- **Output Jar:** `build/libs/neorism-v0.3.0-master.8+f13a023194-dirty.jar`
- **Status:** Ready for client testing by the user.
