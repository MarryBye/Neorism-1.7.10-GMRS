# Аудит результатов тестирования клиента и план исправления проблем

> **Дата составления:** 17 октября 2026  
> **Версия Neorism:** 1.7.10-0.1.0-SNAPSHOT  
> **Окружение:** Minecraft 1.7.10 (`10.13.4.1614`), GTNH Stack (Angelica, Hodgepodge, lwjgl3ify, GTNHLib, Et-Futurum-Requiem)  
> **Статус документа:** Исследования завершены, сформированы гипотезы и точный план исправления. Код не изменялся.

---

## Введение

Пользователь провел ручное тестирование клиента игры Neorism 1.7.10 и выявил 6 категорий визуальных, локализационных и функциональных багов.  
Данный документ содержит детальный анализ логов (`run/client/logs/latest.log` и `fml-client-latest.log`), выявление точных первопричин в коде и ресурсах, формулировку рабочих гипотез и пошаговый план исправления для последующей реализации.

---

## Раздел 1. Проблемы локализации и форматирования тултипов

### 1.1. Long Garlic Diffuser Core (в описании отображается `%s`)
- **Симптом:** В тултипе предмета Long Garlic Diffuser Core выводится неформатированная строка с маркерами `%s` (`diffuses garlic in a %s by %s chunk area`).
- **Файл:** `src/main/java/com/marrybye/neorism/common/items/ItemGarlicDiffuserCore.java` (строка 116).
- **Первопричина:**
  В методе `addInformation`:
  ```java
  tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.garlic_diffuser_long"));
  ```
  В языковом файле `en_US.lang` (и `ru_RU.lang`) ключ содержит два аргумента форматирования `%s by %s`. Вызов `translateToLocal` возвращает строку как есть, не подставляя числовые значения.
- **Гипотеза решения:**
  Использовать `StatCollector.translateToLocalFormatted("tooltip.neorism.garlic_diffuser_long", 5, 5)` (или соответствующий радиус чанков для данного тира).

### 1.2. Святая вода: `tooltip.neorism.faction_specifics`, сырые строки фракций и уровней
- **Симптомы:**
  1. В тултипе отображается нелокализованный ключ `tooltip.neorism.faction_specifics`.
  2. Красным цветом выводится сырое слово `hunter` или зеленым `vampire` с маленькой буквы.
  3. Отображается нелокализованный ключ `tooltip.neorism.required_level`.
  4. Отображаются сырые идентификаторы навыков без перевода: `crucifix_wielder`, `double_it`, `master_of_craftsmanship`.
- **Файл:** `src/main/java/com/marrybye/neorism/common/faction/FactionRestriction.java` (строки 106–129).
- **Первопричины:**
  1. Ключи `tooltip.neorism.faction_specifics` и `tooltip.neorism.required_level` отсутствуют в `en_US.lang` и `ru_RU.lang`.
  2. Строка `tooltips.add(" " + color + reqFaction.getName())` берет `reqFaction.getName()`, возвращающий технический идентификатор `"hunter"` или `"vampire"`, вместо локализованного названия фракции.
  3. Проверка локализации навыка:
     ```java
     String skillName = StatCollector.canTranslate("skill.neorism." + reqSkill + ".name") ?
         StatCollector.translateToLocal("skill.neorism." + reqSkill + ".name") : reqSkill;
     ```
     В языковых файлах все навыки зарегистрированы по ключам `skill.neorism.<skill_id>` (например, `skill.neorism.crucifix_wielder=Crucifix Wielder`), **без суффикса `.name`**! Из-за этого `canTranslate` возвращает `false`, и код откатывается к сырому `reqSkill`.
- **Гипотеза решения:**
  1. Добавить в `en_US.lang` и `ru_RU.lang`:
     ```properties
     tooltip.neorism.faction_specifics=Faction Specifics:
     tooltip.neorism.required_level=Required Level: %s
     ```
  2. Локализовать название фракции через `StatCollector.translateToLocal("faction.vampirism." + reqFaction.getName() + ".name")` или `reqFaction.getDisplayName()`.
  3. Исправить поиск названия навыка в `FactionRestriction`: проверять сначала `"skill.neorism." + reqSkill`, а затем `"skill.vampirism." + reqSkill`.

### 1.3. Суффиксы `.name` в названиях предметов (`Heavy Quarrel Clip.name`, `Quarell Clip.name`, `item.neorism.vampire_cloak.name`)
- **Симптомы:** При наведении на обоймы болтов и плащи отображается сырой суффикс `.name` или полный идентификатор.
- **Файлы:**
  - `src/main/java/com/marrybye/neorism/common/items/ItemQuarrelClip.java` (строки 64–67).
  - `src/main/java/com/marrybye/neorism/common/items/ItemVampireCloak.java` (строки 69–76).
- **Первопричины:**
  1. `ItemQuarrelClip` наследует ванильный `net.minecraft.item.Item`, а не `ItemNeorism`. Ванильный `getUnlocalizedNameInefficiently(ItemStack)` вызывает `translateToLocal(s + ".name")`, мгновенно переводя строку в `"Heavy Quarrel Clip"`. Сторонние интерфейсы или подсказки затем повторно обращаются к полученному имени с суффиксом `.name`, получая непереведенную строку `"Heavy Quarrel Clip.name"`.
  2. В `ItemVampireCloak.getItemStackDisplayName` формируется ключ `item.neorism.vampire_cloak.<color>.name` с точкой:
     ```java
     String key = "item.neorism.vampire_cloak." + COLORS[meta] + ".name";
     ```
     Однако в `en_US.lang` и `ru_RU.lang` плащи объявлены через подчеркивание: `item.neorism.vampire_cloak_<color>.name`. Поиск терпит неудачу, и вызывается `super.getItemStackDisplayName()`, возвращающий `item.neorism.vampire_cloak.name`.
- **Гипотеза решения:**
  1. В `ItemQuarrelClip`: унаследовать `ItemNeorism` и переопределить `getItemStackDisplayName(ItemStack stack)` с прямой локализацией ключа.
  2. В `ItemVampireCloak`: изменить формирование ключа на `"item.neorism.vampire_cloak_" + COLORS[meta] + ".name"`.

---

## Раздел 2. Отсутствующие текстуры блоков и артефакты рендера

### 2.1. Dark Spruce Log (черно-фиолетовая текстура)
- **Симптом:** При установке или в руках Dark Spruce Log не имеет текстуры.
- **Файл:** `src/main/java/com/marrybye/neorism/common/blocks/BlockDarkSpruceLog.java`.
- **Первопричина:**
  Класс наследует `BlockRotatedPillar` и переопределяет `getSideIcon(int side)` и `getTopIcon(int side)`. В Forge 1.7.10 при обфускации/деобфускации эти методы не всегда корректно связываются с ванильными методами `func_150163_b` / `func_150161_d`. Поле `this.blockIcon` остается `null`.
- **Гипотеза решения:**
  Явно переопределить стандартный метод `getIcon(int side, int meta)`:
  ```java
  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int side, int meta) {
      int axis = meta & 12;
      if (axis == 0 && (side == 0 || side == 1)) return topIcon != null ? topIcon : blockIcon;
      if (axis == 4 && (side == 4 || side == 5)) return topIcon != null ? topIcon : blockIcon;
      if (axis == 8 && (side == 2 || side == 3)) return topIcon != null ? topIcon : blockIcon;
      return sideIcon != null ? sideIcon : blockIcon;
  }
  ```
  И в `registerBlockIcons` назначить `this.blockIcon = sideIcon;`.

### 2.2. Cursed Grass (отсутствующая текстура)
- **Симптом:** Блок проклятой травы отображает отсутствующую текстуру на некоторых сторонах.
- **Файл:** `src/main/java/com/marrybye/neorism/common/blocks/BlockCursedGrass.java` (строка 41).
- **Первопричина:**
  Конструктор вызывает `super("cursed_grass", Material.grass);`. `BlockNeorism` регистрирует текстуру по умолчанию `neorism:cursed_grass`. На диске в `textures/blocks/` файла `cursed_grass.png` нет (есть только `cursed_grass_top.png` и `cursed_grass_side.png`).
- **Гипотеза решения:**
  Вызывать `super("cursed_grass", "cursed_grass_side", Material.grass);` и явно инициализировать `this.blockIcon = iconSide;` в `registerBlockIcons`.

### 2.3. Faction Totem Top (черно-фиолетовый куб)
- **Симптом:** Верхний блок тотема фракции отображается как черно-фиолетовый блок без текстуры.
- **Файлы:**
  - `src/main/java/com/marrybye/neorism/common/blocks/BlockTotemTop.java`
  - `src/main/java/com/marrybye/neorism/ClientProxy.java` (строка 143).
- **Первопричина:**
  `BlockTotemTop` наследует `BlockModeled` и добавлен в массив `modeledBlocks`. GTNHLib `ModelISBRH` пытается загрузить `neorism:blockstates/totem_top.json`. Данный файл на диске **отсутствует** (есть только `totem_top_vampirism_hunter.json` и `totem_top_vampirism_vampire.json`).
- **Гипотеза решения:**
  Создать файл `assets/neorism/blockstates/totem_top.json`, связывающий варианты метаданных с соответствующими моделями тотема, либо перевести блок на рендеринг через TESR (`TileEntityTotemRenderer`), убрав его из `modeledBlocks`.

### 2.4. Vampire Coffin (разительное отличие мира от инвентаря, черный бархат/дверцы)
- **Симптомы:**
  1. В инвентаре гроб выглядит идеально (темный дуб, красный бархат, открытые дверцы).
  2. В мире при установке красный бархат выглядит как отсутствующая текстура, дверцы черные, дерево светлое.
  3. По боковой грани дверцы текстура неестественно растянута.
- **Файлы:**
  - `src/main/java/com/marrybye/neorism/common/blocks/BlockCoffin.java`
  - `src/main/java/com/marrybye/neorism/client/render/tileentity/TileEntityCoffinRenderer.java`
  - `src/main/java/com/marrybye/neorism/client/model/ModelCoffin.java`
  - `src/main/resources/assets/neorism/models/block/coffin.json`
- **Первопричина:**
  - **Конфликт двух разных визуальных систем:**
    - В инвентаре отображается современная JSON-модель `models/block/coffin.json` / `coffin_red.json`, использующая современные текстуры блоков: `textures/blocks/coffin_base.png` (темный дуб с металлическими петлями) и `textures/blocks/coffin/coffin_inner_red.png` (красный бархат).
    - В мире блок рендерится через `TileEntityCoffinRenderer` и устаревший Java-класс `ModelCoffin`. TESR привязывает текстуру сущности `textures/entity/coffin/coffin_base.png`. В этой текстуре вообще нет красного бархата (внутри светлое незаполненное дерево), а петли нарисованы черными полосами.
  - **Растянутая текстура дверцы:** В `models/block/coffin.json` (строки 99–100) развертка торцов дверцы задана как `"uv": [0, 8, 8, 8.5]`, что растягивает полоску толщиной в 0.5 пикселя на 24 единицы длины.
- **Гипотеза решения:**
  1. Привести в соответствие текстуру `textures/entity/coffin/coffin_base.png`: добавить в нее текстуру красного бархата и темного дуба из `textures/blocks/coffin_base.png`, либо в `TileEntityCoffinRenderer` выполнять двухпроходный рендер (деревянная основа + цветная бархатная вкладка в зависимости от цвета гроба).
  2. В `models/block/coffin.json` скорректировать UV-координаты для граней `up` и `down` дверцы гроба.

---

## Раздел 3. Отсутствующие модели (Hunter Research Table и Hunter Tent)

### 3.1. Hunter Research Table (100% прозрачный блок)
- **Симптом:** Исследовательский стол охотника полностью невидим в мире.
- **Файлы:**
  - `src/main/java/com/marrybye/neorism/common/blocks/BlockHunterTable.java`
  - `src/main/resources/assets/neorism/blockstates/hunter_table.json`
- **Первопричина:**
  Файл `hunter_table.json` содержит 228 строк синтаксиса modern Minecraft `"multipart": [...]` с условиями `when: { "facing": "north" }`. GTNHLib `ModelISBRH` в версии 1.7.10 **не поддерживает синтаксис multipart** — он парсит исключительно секцию `"variants": { ... }`. Из-за отсутствия вариантов GTNHLib не находит геометрию и не отрисовывает ни одного полигона.
- **Гипотеза решения:**
  Преобразовать `hunter_table.json` в стандартный формат вариантов:
  ```json
  {
    "variants": {
      "": { "model": "neorism:block/hunter_table/hunter_table" },
      "facing=north": { "model": "neorism:block/hunter_table/hunter_table" },
      "facing=south": { "model": "neorism:block/hunter_table/hunter_table", "y": 180 },
      "facing=west": { "model": "neorism:block/hunter_table/hunter_table", "y": 270 },
      "facing=east": { "model": "neorism:block/hunter_table/hunter_table", "y": 90 }
    }
  }
  ```

### 3.2. Hunter Tent (100% прозрачный блок)
- **Симптом:** Палатка охотника полностью прозрачна при установке.
- **Файлы:**
  - `src/main/java/com/marrybye/neorism/common/blocks/BlockTent.java`
  - `src/main/resources/assets/neorism/blockstates/tent.json`
- **Первопричина:**
  `BlockTent` имеет флаги `isOpaqueCube() = false` и `renderAsNormalBlock() = false`, но метод `getRenderType()` возвращает 0 (стандартный куб). Блок не зарегистрирован как modeled блок в `ClientProxy`, у него нет зарегистрированного TESR. Кроме того, файл `tent.json` также использует 382 строки неподдерживаемого в 1.7.10 синтаксиса `"multipart": [...]`.
- **Гипотеза решения:**
  1. Сделать `BlockTent` наследником `BlockModeled` и зарегистрировать его в `ClientProxy.modeledBlocks`.
  2. Преобразовать `blockstates/tent.json` в формат `"variants"`.

---

## Раздел 4. Некорректные текстуры NPC (Вампиры и Охотники)

- **Симптомы:**
  - Модель мобов человеческая, но текстура наложена совершенно неправильно (эффект сбитой развертки, отсутствие лица, текстура брони на голове, отсутствие половины тела).
  - В логах `latest.log` зафиксированы ошибки:
    - `FileNotFoundException: neorism:textures/entity/hunter/hunter5.png`
    - `FileNotFoundException: neorism:textures/entity/hunter/hunter6.png`
    - `FileNotFoundException: neorism:textures/entity/hunter/hunter7.png`
    - `FileNotFoundException: neorism:textures/entity/hunter/hunter8.png`
    - `FileNotFoundException: neorism:textures/entity/vampire/vampire7.png`
- **Файлы:**
  - `src/main/java/com/marrybye/neorism/client/render/entity/RenderHunter.java`
  - `src/main/java/com/marrybye/neorism/client/render/entity/RenderVampire.java`
- **Первопричины:**
  1. **Несовместимость формата скинов 64x64 и 64x32:**
     - В Minecraft 1.7.10 ванильный класс `ModelBiped` жестко закодирован под старый формат текстур игрока **64x32** (`textureWidth = 64; textureHeight = 32`).
     - Все текстуры охотников и вампиров на диске взяты из современного мода и имеют формат **64x64** (с раздельными текстурами левых/правых конечностей и вторыми слоями брони/одежды).
     - При натягивании текстуры 64x64 на развертку 64x32 вертикальная координата V делится пополам: текстура головы берется из верхней четверти, лицо сдвигается под шлем, туловище попадает на ноги, а нижняя половина текстуры полностью теряется.
  2. **Несоответствие имен файлов на диске:**
     - Для охотников на диске лежат файлы `hunter5_slim.png`, `hunter6_slim.png`, `hunter7_slim.png`, `hunter8_slim.png`, а код запрашивает `hunter5.png`–`hunter8.png`.
     - Для вампиров на диске отсутствуют файлы `vampire2.png` и `vampire7.png`.
- **Гипотеза решения:**
  1. Создать класс `ModelBiped64.java` (наследник `ModelBiped` с `textureWidth = 64; textureHeight = 64;` и современной разверткой UV для рук, ног, туловища и оверлеев).
  2. Использовать `ModelBiped64` в конструкторах `RenderHunter` и `RenderVampire`.
  3. В массивах `SKINS` обоих рендереров указать только реально существующие на диске текстуры скинов.

---

## Раздел 5. Оружие и предметы: прозрачность и вид от 1-го лица

- **Симптомы:**
  1. Предметы в руках (кресты, арбалеты, мечи) просвечивают насквозь (задние грани видны сквозь передние), хотя в инвентаре выглядят корректно.
  2. При виде от 1-го лица оружие удерживается неправильно: оно смещено слишком вправо и уменьшено.
- **Файл:** `src/main/java/com/marrybye/neorism/client/render/item/JsonModelItemRenderer.java`.
- **Первопричины:**
  1. **Просвечивание (translucency):**
     В строках 130–135 включен `GL11.glEnable(GL11.GL_BLEND)` без включения отсечения задних граней (`GL11.glEnable(GL11.GL_CULL_FACE)`) и без корректной записи в буфер глубины:
     ```java
     GL11.glEnable(GL11.GL_BLEND);
     GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
     ```
     Для непрозрачных 3D-моделей кубов блендинг смешивает грани, находящиеся внутри и сзади модели, создавая эффект "стеклянного" каркаса.
  2. **Смещение от 1-го лица:**
     В ветке `case EQUIPPED_FIRST_PERSON:` код выполняет:
     ```java
     GL11.glTranslatef(0.5F, 0.5F, 0.5F);
     if (model.firstPerson != null) {
         model.firstPerson.apply();
     } else {
         GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
         GL11.glScalef(0.8F, 0.8F, 0.8F);
     }
     GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
     ```
     В 1.7.10 метод ванильного `ItemRenderer.renderItemInFirstPerson` перед вызовом `IItemRenderer` уже применяет свои трансформации руки игрока. Смещение `(0.5, 0.5, 0.5)` сдвигает модель на полблока вправо и вперед, а параметры из тега modern JSON `firstperson_righthand` рассчитаны на совершенно другую систему координат (1.8+).
- **Гипотеза решения:**
  1. Для solid-моделей убрать безусловный `GL_BLEND`, использовать `GL_ALPHA_TEST` и включить `GL_CULL_FACE` + `glDepthMask(true)`.
  2. Откалибровать матрицу трансформаций `EQUIPPED_FIRST_PERSON` под стандарт удержания предметов в 1.7.10 (приведение масштаба ~1.2–1.5, центрирование рукояти в руке).

---

## Раздел 6. Ошибка UI: Черное окно дерева навыков

- **Симптомы:** При открытии меню навыков кнопки снизу активны и реагируют на курсор, но холст (окно с навыками и деревом) абсолютно черный.
- **Файлы:**
  - `src/main/java/com/marrybye/neorism/client/gui/skills/SkillsScreen.java`
  - `src/main/java/com/marrybye/neorism/client/gui/skills/SkillsTabComponent.java`
  - `src/main/java/com/marrybye/neorism/common/skills/SkillTreeLayout.java`
  - `src/main/java/com/marrybye/neorism/common/skills/SkillRegistry.java`
- **Первопричины (критическое открытие):**
  1. **Координаты узлов никогда не назначались сегментам:**
     В классе `SkillTreeLayout.layout(tree)` алгоритм вычисляет координаты и записывает их в локальную мапу `placements`. Однако метод `segment.setPosition(p.x, p.y)` **не вызывается ни в одном месте кодовой базы**! Все сегменты навыков имеют координаты по умолчанию `x = 0, y = 0`. Все 25 узлов отрисовываются строго в одной точке друг на друге.
  2. **Несуществующая текстура фона холста:**
     В `SkillsTabComponent.drawTiledBackground` выполняется:
     ```java
     ResourceLocation bg = tree.getBackground(); // возвращает minecraft:textures/blocks/dirt.png
     mc.getTextureManager().bindTexture(bg);
     GL11.glColor4f(0.25f, 0.25f, 0.25f, 1.0f);
     ```
     В Minecraft 1.7.10 текстуры блоков не существуют в виде отдельных файлов — они сшиты в общий атлас `textures/atlas/blocks.png`. Попытка загрузить `minecraft:textures/blocks/dirt.png` приводит к ошибке загрузки текстуры. В сочетании с цветом `0.25, 0.25, 0.25` отрисовывается глухой черный прямоугольник на весь холст.
  3. **Отсутствующие иконки навыков:**
     В логе зафиксировано отсутствие `textures/skills/vampire_rage.png` и `vampire_invisibility.png`, из-за чего сбивается состояние текстурного юнита OpenGL.
  4. **Scissor test:**
     Расчет `sy = mc.displayHeight - (guiTop + viewY + viewH) * scale;` в GTNH при использовании Angelica/lwjgl3ify может некорректно отсекать холст при определенных настройках GUI Scale.
- **Гипотеза решения:**
  1. В `SkillRegistry` после вызова `SkillTreeLayout.layout(tree)` добавить обход `layout.getPlacements()` и применить `entry.getKey().setPosition(p.x, p.y)`.
  2. В `SkillsTabComponent.drawTiledBackground` привязать стандартную ванильную текстуру фона меню: `new ResourceLocation("textures/gui/options_background.png")`.
  3. Добавить недостающие иконки навыков либо безопасный fallback на иконку по умолчанию.

---

## План последующей реализации (Action Plan)

После согласования и возобновления работы исправления будут внесены по следующим этапам:

1. **Этап A: Локализация и тултипы**
   - Добавить недостающие ключи в `en_US.lang` и `ru_RU.lang`.
   - Исправить форматирование радиуса в `ItemGarlicDiffuserCore`.
   - Исправить отображение фракции и навыков в `FactionRestriction`.
   - Исправить отображение имен в `ItemQuarrelClip` и `ItemVampireCloak`.

2. **Этап B: Текстуры и модели блоков**
   - Переопределить `getIcon` в `BlockDarkSpruceLog`.
   - Исправить имя текстуры по умолчанию в `BlockCursedGrass`.
   - Создать блокстейт `totem_top.json`.
   - Преобразовать multipart-блокстейты `hunter_table.json` и `tent.json` в формат `variants`.
   - Подключить `BlockTent` к `BlockModeled`.

3. **Этап C: Гроб вампира (Vampire Coffin)**
   - Синхронизировать текстуры: перенести текстуру бархата и дуба в текстуру TESR гроба.
   - Скорректировать развертку дверцы в `coffin.json`.

4. **Этап D: Текстуры и модели NPC (Охотники и Вампиры)**
   - Создать класс `ModelBiped64` для поддержки скинов 64x64.
   - Обновить `RenderHunter` и `RenderVampire`, настроить массивы скинов под существующие файлы.

5. **Этап E: Рендеринг предметов от 1-го лица и прозрачность**
   - В `JsonModelItemRenderer` настроить `GL_CULL_FACE`, `GL_DEPTH_TEST` и отключить паразитный `GL_BLEND` для solid-моделей.
   - Скорректировать матрицу вида от 1-го лица.

6. **Этап F: UI меню навыков**
   - Назначить координаты `setPosition` для узлов дерева навыков.
   - Заменить текстуру фона холста на `textures/gui/options_background.png`.
   - Добавить fallback для иконок навыков и проверить `glScissor`.

7. **Проверка компиляции:** `.\gradlew assemble` на каждом этапе.
