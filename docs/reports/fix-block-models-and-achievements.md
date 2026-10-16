# Отчёт: Исправление 3D JSON моделей блоков и локализации достижений

> **Дата:** 16 октября 2026 г.  
> **Статус:** Выполнено (Passed)  
> **Коммит:** `65d736c` (`fix(client): register GTNHLib model loader, fix 3D block models and achievement translations`)

---

## 1. Контекст проблемы

1. **Отображение моделей блоков:**
   - Алтари (`altar_infusion`, `altar_inspiration`), пьедесталы (`blood_pedestal`), колонны (`altar_pillar`, `altar_tip`) и контейнеры крови отображались как сплошные кубические блоки 16x16x16.
   - Текстуры на гранях кубов отображались в виде плоских развёрток (UV unwraps).
   - Часть блоков выдавала ошибки об отсутствии текстур (`altar_inspiration`, `blood_container`).
2. **Локализация достижений:**
   - Ачивки присутствовали в игре, однако их названия и описания не отображались на русском/английском языках.

---

## 2. Анализ причин

1. **Интеграция с GTNHLib JSON Model Loader:**
   - В экосистеме GTNH Forge 1.7.10 библиотека `GTNHLib` содержит встроенный бекпортированный загрузчик современных JSON моделей и стейтов блоков (`ModelRegistry`, `ModelISBRH`).
   - Для того чтобы `GTNHLib` начал сканировать `blockstates/` и активировал хук `nhlib$setModeled(true)` на блоках мода, необходимо зарегистрировать modid через `ModelRegistry.registerModid("neorism")` во время `ClientProxy.preInit`. Этот вызов отсутствовал.
   - В 1330 JSON файлах модельного и стейтового дерева сохранялись устаревшие ссылки на `vampirism:` и `factionapi:`, из-за чего GTNHLib не мог разрешить родительские модели и текстуры.
2. **Классы блоков:**
   - Блоки создавались как стандартный `BlockNeorism`, который по умолчанию возвращал `isOpaqueCube() == true`, `renderAsNormalBlock() == true` и `getRenderType() == 0`, вынуждая движок Minecraft отрисовывать обычный куб и накладывать развёртку целиком на каждую грань.
3. **Пути текстур в 1.7.10:**
   - В современных версиях Minecraft текстуры блоков лежат в `textures/block/` (в единственном числе), а в 1.7.10 — в `textures/blocks/` (во множественном числе). Ссылки вида `neorism:block/...` приводили к поиску текстуры в `textures/blocks/block/...`.
4. **Локализация достижений:**
   - Идентификаторы ачивок при вызове `new Achievement(...)` регистрировались с префиксами `vampire.<name>`, в то время как ключи локализации в `.lang` файлах имели канонический вид `achievement.neorism.<name>`.

---

## 3. Выполненные изменения

1. **Регистрация в GTNHLib (`ClientProxy.java`):**
   - В `ClientProxy.preInit` добавлен вызов `ModelRegistry.registerModid(Neorism.MODID)`.
2. **Иерархия классов моделей блоков:**
   - Создан базовый класс `BlockModeled` (`isOpaqueCube() -> false`, `renderAsNormalBlock() -> false`, `getRenderType() -> ModelISBRH.JSON_ISBRH_ID`).
   - Созданы специализированные классы:
     - `BlockBloodPedestal`
     - `BlockAltarInfusion`
     - `BlockAltarInspiration`
     - `BlockAltarPillar`
     - `BlockAltarTip`
     - `BlockBloodContainer` (с поддержкой слоя прозрачности `renderBlockPass = 1`).
   - В `ModBlocks.init()` переведена инициализация на новые классы.
3. **Адаптация ассетов:**
   - Произведена массовая замена пространств имён `vampirism:` и `factionapi:` на `neorism:` во всех 1330 JSON файлах (`blockstates/`, `models/block/`, `models/item/`).
   - Созданы зеркальные подкаталоги `textures/blocks/block/` и `textures/items/item/` для стопроцентной совместимости как со старыми вызовами `registerIcon`, так и с современными JSON путями.
   - Сгенерированы недостающие вспомогательные текстуры `candle.png` и `black_concrete.png`.
   - В `assets/minecraft/textures/blocks/block/` скопированы и слинкованы ванильные текстуры (`obsidian`, `stone`, `iron_block`, `dark_oak_planks` и др.).
4. **Локализация ачивок (`ModAchievements.java`):**
   - Все 37 достижений переведены на канонические идентификаторы (`become_vampire`, `sucking_blood`, `baptism_of_blood`, `max_level_vampire` и др.), полностью соответствующие ключам в `ru_RU.lang` и `en_US.lang`.

---

## 4. Верификация сборки

- `.\gradlew compileJava` — успешно (`BUILD SUCCESSFUL in 16s`).
- `.\gradlew assemble` — успешно (`BUILD SUCCESSFUL in 15s`).
- Изменения зафиксированы в git-коммите `65d736c`.
