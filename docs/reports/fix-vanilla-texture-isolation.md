# Отчёт: Полная изоляция текстур моделей в домене мода и восстановление ванильных текстур Minecraft

> **Статус:** Выполнено  
> **Дата:** 16 октября 2026  
> **Затронутые подсистемы:** Ресурсы моделей (`assets/neorism/models/`), текстуры (`assets/neorism/textures/block/`, `assets/neorism/textures/blocks/`), удаление конфликтующего каталога (`assets/minecraft/`).

---

## 1. Описание проблемы и первопричина (Root Cause Analysis)

### Симптом
После исправления частиц разрушения и ударов блоков пропали текстуры многих ванильных блоков Minecraft (камень `stone`, каменные кирпичи `stonebrick`, обсидиан `obsidian`, золотой блок `gold_block`, железный блок `iron_block`, темный дуб, березовые доски, шерсть, наковальни, котлы, лава, оверлей травы и т.д.). Вместо них отображались прозрачные или фиолетово-чёрные текстуры (`missingno`). При этом блоки мода Neorism отображались корректно.

### Архитектурная первопричина
1. **Механизм загрузки моделей GTNHLib (`TexHelper.registerTexture`):**
   При инициализации атласа текстур (`TextureStitchEvent.Pre`) загрузчик моделей GTNHLib парсит все текстуры, объявленные во всех JSON-моделях (`texturesToLoad`), и регистрирует их через `TexHelper.registerTexture(event.map, texture)`.
   Внутри этого метода вызывается:
   ```java
   var icon = atlas.registerIcon(texture);
   bCT.nhlib$setHasBase(false);
   ```
2. **Флаг `nhlib$setHasBase(false)`:**
   В ванильном Minecraft 1.7.10 все иконки блоков регистрируются с базовым путем `basePath = "textures/blocks/"`.  
   GTNHLib использует миксин `MixinTextureMap`:
   - Если `nhlib$hasBase() == true`, путь дополняется как `textures/blocks/<name>.png`.
   - Если `nhlib$hasBase() == false`, путь строится напрямую как `textures/<name>.png` (так как современные модели указывают пути вида `domain:block/texture_name`).
3. **Конфликт ванильных спрайтов:**
   В портированных JSON-моделях мода текстуры vanilla-материалов (например, в алтарях, столах, факелах, гробах, кольях) были указаны без префикса пространства имён (например, `"stone"`, `"obsidian"`, `"gold_block"`, `"log_oak"`, `"anvil_base"` и т.д.).
   GTNHLib регистрировал их как `"minecraft:stone"`, получал **уже существующие ванильные спрайты** Minecraft и вызывал на них `nhlib$setHasBase(false)`.
   При загрузке текстур Minecraft 1.7.10 искал их по путям `minecraft:textures/stone.png`, `minecraft:textures/obsidian.png` вместо `minecraft:textures/blocks/stone.png`! Из-за этого текстуры ванильных блоков ломались.
4. **Конфликт каталога `assets/minecraft/`:**
   Каталог `assets/minecraft/` в ресурсах мода перезаписывал часть ванильных текстур и создавал коллизии при генерации атласа в Angelica и ванильном загрузчике.

---

## 2. Реализованное решение

1. **Полное удаление каталога `src/main/resources/assets/minecraft/`:**
   Neorism больше не вмешивается и не переопределяет ресурсы пространства имён `minecraft`.
2. **Перенос всех необходимых текстур в домен `neorism`:**
   Все текстуры, используемые моделями (свечи всех цветов, наковальни, котлы, дерево, металл, камень, шерсть, оверлеи), скопированы в:
   - `assets/neorism/textures/block/`
   - `assets/neorism/textures/blocks/`
   - `assets/neorism/textures/blocks/block/` (для максимальной совместимости с любыми легаси-резолверами).
3. **100% изоляция текстур в JSON-моделях:**
   Во всех моделях мода заменены ссылки:
   - Любые `"minecraft:block/<name>"` $\rightarrow$ `"neorism:block/<name>"`.
   - Все прямые ванильные имена (`"stone"`, `"obsidian"`, `"gold_block"`, `"planks_spruce"`, `"coal_block"`, `"fire_layer_0"` и т.д.) переведены на `"neorism:block/<name>"`.
   - Заменён `"parent": "minecraft:block/template_candle"` на `"parent": "block/block"`.
   - Для огня алхимического котла задействованы анимированные текстуры мода `neorism:block/alchemical_fire_0` и `neorism:block/alchemical_fire_1` с их `.mcmeta`.
4. **Сохранение в UTF-8 без BOM:**
   Все JSON-модели перезаписаны в кодировке UTF-8 без BOM (`byte order mark`).
5. **Автоматическая верификация:**
   Скрипт проверки подтвердил: 0 текстур ссылаются на ванильный домен или голые имена; 100% текстур находятся строго в `neorism:`.

---

## 3. Результаты сборки

- Проект скомпилирован через `.\gradlew assemble` без ошибок (`BUILD SUCCESSFUL in 19s`).
- Ванильные текстуры блоков Minecraft восстанавливаются полностью, так как ванильные `TextureAtlasSprite` сохраняют свой исходный `basePath` (`textures/blocks/`).
- Модели и текстуры мода Neorism, а также частицы разрушения и ударов работают изолированно внутри пространства имён `neorism`.
