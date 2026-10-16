# Руководство по адаптации 3D-моделей, текстур и локализации (Modern Vampirism -> Neorism Forge 1.7.10)

Данный документ фиксирует исчерпывающую методологию и архитектурные правила портирования визуальных ресурсов (JSON моделей, стейтов, атласов текстур и файлов перевода) из современных версий *Vampirism* (1.14 - 1.21) в среду Minecraft Forge 1.7.10 с интеграцией `GTNHLib`.

---

## 1. Архитектура загрузки современных JSON моделей в 1.7.10 (`GTNHLib`)

В Minecraft 1.7.10 изначально отсутствовала система JSON-моделей блоков и предметов (были только `ISimpleBlockRenderingHandler`, `TileEntitySpecialRenderer` и рендер через `IIcon`).  
Библиотека **`GTNHLib`** предоставляет бекпортированный парсер и загрузчик современных JSON blockstates и models (`ModelRegistry`, `ModelISBRH`).

### 1.1. Обязательные триггеры инициализации
1. **Корневой `pack.mcmeta` (`pack_format: 4`):**
   - Файл: `src/main/resources/pack.mcmeta`.
   - **Обязателен!** GTNHLib Model Bakery проверяет формат ресурспака. Если файла нет или формат < 4, загрузчик моделей игнорирует ресурсы мода, считая их устаревшими.
2. **Регистрация modid в `ClientProxy.preInit`:**
   ```java
   ModelRegistry.registerModid(Neorism.MODID);
   ```
   Без этого вызова GTNHLib не сканирует папку `assets/neorism/blockstates/` и не ставит флаг `nhlib$setModeled(true)` на блоки мода.
3. **Классы блоков в Java:**
   - Моделируемый блок **обязан** наследоваться от `BlockModeled` (или переопределять `isOpaqueCube() -> false`, `renderAsNormalBlock() -> false`, `getRenderType() -> ModelISBRH.JSON_ISBRH_ID`).
   - Если блок непрозрачный куб (`isOpaqueCube() == true`), ванильный рендерер 1.7.10 перехватит отрисовку и натянет UV-развёртку целиком на каждую грань куба.
4. **Рендеринг блока как предмета (в инвентаре и в руке):**
   - В `ClientProxy.init` для каждого моделируемого блока регистрируется `IItemRenderer`:
     ```java
     Item item = Item.getItemFromBlock(block);
     if (item != null) {
         MinecraftForgeClient.registerItemRenderer(item, ModelISBRH.INSTANCE.get());
     }
     ```

---

## 2. Правила структуры Blockstates JSON

Парсер вариантов в GTNHLib (`MonopartState$StateMatch`) работает по строгим правилам:

### 2.1. Валидный синтаксис ключей вариантов
* **Пустой стейт (Empty properties):**
  - Разрешены **только** два ключа: `""` (пустая строка) или `"normal"`.
  - **КРИТИЧЕСКИ ВАЖНО:** Ключи вида `"inventory"`, `"base"`, `"default"` **ЗАПРЕЩЕНЫ** без знака `=`. Если указать bare word строку, парсер выбрасывает `com.google.gson.JsonParseException: Model variant predicate is missing an equals`, и весь blockstate бракуется, вызывая откат на фиолетово-чёрный куб `MISSING_MODEL`.
* **Стейты со свойствами:**
  - Обязательно содержат знак равенства: `"key=value"` (например: `"type=bone"` или `"facing=north"`).
  - Комбинированные предикаты разделяются запятой: `"facing=north,lit=true"`.

### 2.2. Обязательный fallback-вариант `""`
* В 1.7.10 у большинства блоков карта стейтов по умолчанию пуста (`state.toMap() = {}`).
* При отрисовке предмета в инвентаре свойства также всегда пусты.
* **Правило:** Каждый blockstate с предикатами свойств (`type=...`, `facing=...`) обязан содержать дефолтный вариант `""` или `"normal"`:
  ```json
  {
    "variants": {
      "": { "model": "neorism:block/my_block" },
      "normal": { "model": "neorism:block/my_block" },
      "type=special": { "model": "neorism:block/my_block_special" }
    }
  }
  ```

---

## 3. Правила путей и атласов текстур (Dual Resolution)

### 3.1. Несовпадение путей 1.7.10 и Modern MC
* В 1.7.10 ванильный `TextureMap` использует базовые пути `textures/blocks` и `textures/items` (во множественном числе).
* В modern MC (1.8+) атлас унифицирован, а текстуры лежат в `textures/block/` и `textures/item/` (в единственном числе).
* В GTNHLib хук `MixinTextureMap.nhlib$unbaseTextures` для текстур из JSON-моделей загружает:
  `textures/%s.png`, где `%s` — путь из модели (например, `"block/candle"` &rarr; `textures/block/candle.png`).
* Но легаси-код вызова `registerIcon("neorism:candle")` ищет текстуру в `textures/blocks/candle.png`.

### 3.2. Золотое правило атласа
**Любая текстура блока мода должна существовать одновременно в двух каталогах:**
1. `src/main/resources/assets/neorism/textures/block/<name>.png` (для JSON моделей).
2. `src/main/resources/assets/neorism/textures/blocks/<name>.png` (для ванильного `registerIcon`).

### 3.3. Изоляция от домена `minecraft` (СТРОЖАЙШЕЕ ПРАВИЛО)
* **НИКОГДА не создавать каталог `assets/minecraft/` в ресурсах мода!**
* **НИКОГДА не указывать в JSON-моделях ссылки на домен `minecraft:` или голые ванильные имена (`"stone"`, `"obsidian"`, `"gold_block"`, `"candle"` и т.д.)!**
* **Почему это критично:** GTNHLib при сканировании JSON-моделей регистрирует все встреченные текстуры через `TexHelper.registerTexture`. Если текстура без префикса или с префиксом `minecraft:`, GTNHLib перехватывает уже зарегистрированный ванильный `TextureAtlasSprite` и выполняет:
  ```java
  bCT.nhlib$setHasBase(false);
  ```
  После этого ванильный Minecraft ищет текстуру по пути `minecraft:textures/<name>.png` вместо `minecraft:textures/blocks/<name>.png`. Так как таких файлов в ванильном клиенте нет, **ванильные блоки Minecraft теряют текстуры и превращаются в `missingno` (черно-фиолетовый или прозрачный куб)**!
* **Правило:** Все текстуры (включая свечи, наковальни, котлы, металл, камень, шерсть), используемые в моделях Neorism, **ОБЯЗАНЫ** находиться в пространстве имён `neorism:` (`assets/neorism/textures/block/<name>.png` и `assets/neorism/textures/blocks/<name>.png`), а в JSON-моделях указываться строго как:
  ```json
  "textures": {
    "stone": "neorism:block/stone",
    "candle": "neorism:block/candle"
  }
  ```

---

## 4. Локализация (.lang) в Forge 1.7.10

### 4.1. Предметы (`ItemNeorism`)
* В конструкторе: `setUnlocalizedName("neorism." + name)`.
* Ванильный класс `Item` автоматически добавляет префикс `item.`, получая unlocalizedName: `item.neorism.<name>`.
* В `ru_RU.lang` и `en_US.lang` ключи должны задаваться как:
  ```properties
  item.neorism.<name>.name=Локализованное Имя
  item.neorism.<name>=Локализованное Имя
  ```
* Для предотвращения бага ванильного 1.7.10 (`"ПереведённоеИмя.name"`) в `ItemNeorism` переопределены `getItemStackDisplayName` и `getUnlocalizedNameInefficiently`.

### 4.2. Блоки (`BlockNeorism`, `BlockModeled`)
* В конструкторе: `setBlockName("neorism." + name)`.
* UnlocalizedName формируется как: `tile.neorism.<name>`.
* В `.lang` файлах регистрируются ключи:
  ```properties
  tile.neorism.<name>.name=Локализованное Имя
  tile.neorism:<name>.name=Локализованное Имя
  tile.<name>.name=Локализованное Имя
  ```

### 4.3. Вкладки креатива (`CreativeTabs`)
* При объявлении: `new CreativeTabs("neorism.main")`.
* Ванильный метод `getTranslatedTabLabel()` ищет ключ: `itemGroup.<label>` &rarr; `itemGroup.neorism.main`.
* В `.lang` файлах регистрируются:
  ```properties
  itemGroup.neorism.main=Вампиризм
  itemGroup.neorism.books=Вампирские книги
  ```
* В `ModCreativeTabs.java` обязательно переопределяется `getTranslatedTabLabel()` с каскадным поиском фоллбэков (`itemGroup.neorism.main` &rarr; `itemGroup.neorism` &rarr; `itemGroup.vampirism`).

---

---

## 5. Частицы разрушения и ударов блоков (Digging / Break Particles)

### 5.1. Причина бага ванильного рендерера и GTNHLib
* В Minecraft 1.7.10 при разрушении блока (`ItemInWorldManager.tryHarvestBlock`) блок удаляется из мира (`world.setBlockToAir`) **до** вызова эффектов разрушения (`EffectRenderer.addBlockDestroyEffects`).
* Миксин GTNHLib (`MixinEffectRenderer.nhlib$fixBreakIcon`) пытается разрешить текстуру частиц через `ModelISBRH.getParticleIcon(world, x, y, z)`, обращаясь к `world.getBlock(x, y, z)`. Так как блок уже стал воздухом (`Blocks.air`), GTNHLib возвращает `MISSING_MODEL` &rarr; `missingno` (чёрно-фиолетовая сетка).
* Кроме того, `BlockIconTransformer` в GTNHLib принудительно заменяет `Block.getIcon(side, meta)` на `missingno` для всех блоков со стейтами.
* В портированных моделях поле `"particle"` часто ссылалось на пути modern MC (`"block/stone"`, `"block/dark_oak_planks"`), не существующие в ванильном 1.7.10.

### 5.2. Архитектурное решение в Neorism
1. **Кастомные Forge-хуки в `BlockNeorism`:**
   В базовом классе `BlockNeorism` (от которого наследуются все блоки, включая `BlockModeled`) переопределены клиентские методы Forge:
   - `@SideOnly(Side.CLIENT) public boolean addDestroyEffects(World, int, int, int, int, EffectRenderer)`
   - `@SideOnly(Side.CLIENT) public boolean addHitEffects(World, MovingObjectPosition, EffectRenderer)`
   Возврат `true` полностью отключает ванильный код и дефектный миксин GTNHLib.
2. **Многоуровневый резолвер `BlockParticleHelper`:**
   - Шаг 1: Берёт `BlockState` напрямую из `(Block, meta)` без обращения к блоку в мире.
   - Шаг 2: Извлекает `BakedModel.getParticle(null)`.
   - Шаг 3: Если иконка невалидна/`missingno`, сканирует квады модели (`model.getQuads(...)`) и берёт первую валидную текстуру (`quad.celeritas$getSprite()`).
   - Шаг 4: Проверяет зарегистрированный `this.blockIcon` блока.
   - Шаг 5: Фоллбэк на базовые ванильные текстуры по `Material` (камень, дерево, земля, железо, шерсть). Блок **ни при каких условиях** не вызовет фиолетово-чёрных частиц.
3. **Нормализация поля `"particle"` в JSON-моделях:**
   - Для блоков мода указывать `"particle": "neorism:block/<texture_name>"`.
   - Для ванильных текстур указывать легаси 1.7.10 имена (`"stone"`, `"planks_spruce"`, `"log_oak"`), а не modern пути `"block/..."`.

---

## 6. Чек-лист портирования новой модели/блока

Перед добавлением нового моделируемого блока выполнить:
- [ ] Проверить, что в `models/block/<name>.json` все ссылки используют namespace `neorism:` вместо `vampirism:`.
- [ ] Убедиться, что поле `"particle"` задано корректно (`neorism:block/...` или ванильное имя 1.7.10 без префикса `"block/"`).
- [ ] Убедиться, что все используемые текстуры лежат в обоих каталогах: `textures/block/` и `textures/blocks/`.
- [ ] Если модель использует ванильные текстуры, убедиться, что они присутствуют в `assets/minecraft/textures/block/`.
- [ ] В `blockstates/<name>.json` обязательно предусмотреть вариант `""` (или `"normal"`), не использовать bare-words без `=`.
- [ ] Класс блока унаследовать от `BlockModeled` (или `BlockNeorism`).
- [ ] В `ClientProxy.init` зарегистрировать `MinecraftForgeClient.registerItemRenderer(item, ModelISBRH.INSTANCE.get())`.
- [ ] Добавить ключи локализации в `ru_RU.lang` и `en_US.lang` (`tile.neorism.<name>.name=...`).
- [ ] Собрать через `.\gradlew assemble` и проверить логи на отсутствие `JsonParseException` и `FileNotFoundException`.
