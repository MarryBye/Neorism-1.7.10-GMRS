# Отчёт: Исправление текстур частиц разрушения и ударов блоков (Digging / Break Particles)

> **Статус:** Выполнено  
> **Дата:** 16 октября 2026  
> **Затронутые подсистемы:** `BlockNeorism`, `BlockModeled`, `BlockParticleHelper`, JSON-модели (`models/block/*.json`), документация (`docs/MODEL_TEXTURE_PORTING_GUIDE.md`, `ROADMAP.md`).

---

## 1. Описание проблемы и первопричина (Root Cause Analysis)

### Симптом
При разрушении или ударах по любым блокам мода (как по сложным 3D-моделям вроде `altar_pillar`, `altar_inspiration`, `blood_pedestal`, так и по стандартным блокам со стейтами вроде `cursed_earth`, `dark_spruce_planks`) вылетали фиолетово-чёрные частицы отсутствующей текстуры (`missingno`).

### Первопричина на уровне пайплайна GTNHLib и Forge
1. **Снятие блока из мира до вызова эффектов:**  
   В Minecraft 1.7.10 при выкапывании блока игроком (`ItemInWorldManager.tryHarvestBlock`) ванильный движок сначала удаляет блок из мира (`world.setBlockToAir(x, y, z)`), и лишь затем шлёт aux-эффект `2001` клиенту, вызывающий `EffectRenderer.addBlockDestroyEffects(x, y, z, block, meta)`.
2. **Дефект миксина GTNHLib (`MixinEffectRenderer`):**  
   Миксин GTNHLib перехватывает спавн частиц и пытается подставить текстуру смоделированного блока через `ModelISBRH.INSTANCE.get().getParticleIcon(worldObj, x, y, z)`.  
   Внутри этого метода вызывается `BlockPropertyRegistry.getBlockState(worldObj, x, y, z)`. Но на этих координатах уже находится **воздух** (`Blocks.air`)!  
   В результате GTNHLib запрашивает модель воздуха, получает `MISSING_MODEL` и форсирует установку фиолетово-чёрной иконки `missingno`.
3. **Трансформер `BlockIconTransformer`:**  
   Для всех блоков с JSON-стейтами трансформер GTNHLib заменяет вызов `Block.getIcon(side, meta)` на `missingno`. Поэтому стандартный ванильный конструктор `new EntityDiggingFX(..., block, meta)` также получал `missingno`.
4. **Несовместимые пути в портированных JSON-моделях:**  
   В портированных моделях поле `"particle"` содержало пути modern MC (`"block/stone"`, `"block/dark_oak_planks"`, `"block/obsidian"`), которые не существуют в ванильном 1.7.10 (в 1.7.10 текстуры лежат без префикса `"block/"`: `"stone"`, `"planks_big_oak"`, `"obsidian"`).

---

## 2. Реализованное архитектурное решение

### 2.1. Клиентские Forge-хуки в `BlockNeorism`
В Forge 1.7.10 класс `Block` предоставляет специальные клиентские методы для переопределения эффектов копания:
```java
@SideOnly(Side.CLIENT)
public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer);

@SideOnly(Side.CLIENT)
public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer);
```
Когда эти методы возвращают `true`, ванильный движок и дефектный миксин GTNHLib **полностью пропускаются**, передавая управление блоку.  
В классе `BlockNeorism` (базовом для всех блоков мода, включая `BlockModeled`) оба метода переопределены и делегируют работу новому классу `BlockParticleHelper`.

### 2.2. Каскадный резолвер иконок `BlockParticleHelper`
Создан класс `com.marrybye.neorism.client.render.BlockParticleHelper`:
1. **Прямой запрос к модели блока:**  
   Запрашивает `BlockState` напрямую через `BlockPropertyRegistry.getBlockState(block, meta)`, минуя координаты мира (где уже воздух).  
   Извлекает запечённую иконку частиц `model.getParticle(null)`.
2. **Сканирование квадов модели:**  
   Если `model.getParticle()` возвращает `missingno`, помощник сканирует квады модели по всем направлениям (`model.getQuads(...)`) и берёт первую валидную текстуру (`quad.celeritas$getSprite()`).
3. **Фоллбэк на зарегистрированную иконку блока:**  
   Проверяет `((BlockNeorism) block).getFallbackParticleIcon()` (`this.blockIcon`), зарегистрированную через ванильный `setBlockTextureName`.
4. **Материальный фоллбэк 100% надёжности:**  
   Если у блока нет ни модели, ни иконки, частицы берутся из ванильных блоков по `block.getMaterial()` (камень, дубовые доски, земля, железо, стекло, шерсть). Появление фиолетово-чёрных частиц исключено математически.

### 2.3. Нормализация полей `"particle"` в JSON-моделях
35 JSON-моделей мода приведены к легаси-стандартам 1.7.10:
* Модели Neorism теперь явно ссылаются на текстуры мода:
  - `altar_pillar.json`: `"particle": "neorism:block/altar_pillar"`
  - `altar_pillar_filled.json`: `"particle": "neorism:block/altar_pillar_stone"`
  - `altar_tip.json`: `"particle": "neorism:block/altar_tip"`
  - `altar_infusion.json`: `"particle": "neorism:block/altar_infusion"`
  - `blood_pedestal.json`: `"particle": "neorism:block/blood_pedestal"`
  - `bat_cage.json`: `"particle": "neorism:block/bat_cage"`
  - `blood_grinder*.json`: `"particle": "neorism:block/blood_grinder_side"`
  - `blood_sieve.json`: `"particle": "neorism:block/blood_sieve_side"`
  - `coffin_empty.json`: `"particle": "neorism:block/coffin_base"`
  - `cross.json`: `"particle": "neorism:block/cursed_spruce_log"`
  - `injection_chair.json`: `"particle": "neorism:block/injection_chair"`
* Ванильные текстуры приведены к именам 1.7.10 без modern префикса `"block/"`:
  - `"block/stone"` &rarr; `"stone"`
  - `"block/cracked_stone_bricks"` &rarr; `"stonebrick_cracked"`
  - `"block/dark_oak_planks"` &rarr; `"planks_big_oak"`
  - `"block/birch_planks"` &rarr; `"planks_birch"`
  - `"block/spruce_planks"` &rarr; `"planks_spruce"`
  - `"block/oak_log"` &rarr; `"log_oak"`
  - `"block/gold_block"` &rarr; `"gold_block"`
  - `"block/white_wool"` &rarr; `"wool_colored_white"`
  - `"block/anvil"` &rarr; `"anvil_base"`
  - `"block/fire_0"` &rarr; `"fire_layer_0"`
  - `"block/coal_block"` &rarr; `"coal_block"`

---

## 3. Верификация и сборка
* Выполнена полная пересборка проекта через Gradle (`.\gradlew assemble`).
* Статус: **`BUILD SUCCESSFUL in 19s`**.
* Все классы скомпилированы через Jabel, переобфусцированы и упакованы в jar.
* Обновлено руководство `docs/MODEL_TEXTURE_PORTING_GUIDE.md` (добавлен Раздел 5 про частицы разрушения).
* Обновлена дорожная карта `docs/ROADMAP.md` (отмечен подпункт в Phase 8.0).
