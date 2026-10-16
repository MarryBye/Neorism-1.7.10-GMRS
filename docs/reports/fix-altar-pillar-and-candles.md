# Отчёт: Исправление стейтов Altar Pillar, текстур свечей Altar of Inspiration и фиксация задачи глобальной нормализации

> **Дата:** 16 октября 2026 г.  
> **Статус:** Выполнено (Passed)  
> **Коммит:** `fix(render): fix altar_pillar blockstate fallback and candle texture paths`

---

## 1. Контекст проблемы

После успешного включения парсера современных моделей `pack.mcmeta` (`pack_format: 4`) и исправления локализации предметов/достижений:
1. `Altar Pillar` (`altar_pillar`):
   - Отрисовывался как фиолетово-чёрный куб 16x16x16, хотя его хитбокс при наведении корректно сужался (`0.125F`..`0.875F`).
2. `Altar of Inspiration` (`altar_inspiration`):
   - 3D модель отрисовывалась полностью корректно, однако группа свечей на одном из углов алтаря отображалась без текстур (фиолетово-чёрный).

---

## 2. Анализ причин

1. **Причина Altar Pillar:**
   - В файле `assets/neorism/blockstates/altar_pillar.json` варианты были объявлены только через свойство типа: `"type=bone"`, `"type=gold"`, `"type=iron"`, `"type=none"`, `"type=stone"`.
   - В классе `BlockAltarPillar.java` (наследующем `BlockModeled`) свойства стейта не регистрировались, и карта свойств блока в Forge 1.7.10 пуста (`state.toMap() = {}`).
   - Загрузчик `GTNHLib` (`MonopartState.selectModel`) при пустой карте свойств ищет вариант с пустым ключом `""` либо `"normal"`. При их отсутствии он возвращает `MISSING_MODEL`, который рендерится как куб с текстурой ошибки.
2. **Причина свечей Altar of Inspiration:**
   - В модели `altar_inspiration.json` свечи и фитили используют текстуры `"7": "block/candle"` и `"8": "block/black_concrete"`.
   - В `GTNHLib` хук `MixinTextureMap` при загрузке бейс-проверенных текстур из JSON-моделей запрашивает ресурс по пути:
     `textures/%s.png` -> `assets/minecraft/textures/block/candle.png` и `assets/minecraft/textures/block/black_concrete.png`.
   - Папка в `assets/minecraft/textures` называлась `blocks` (во множественном числе 1.7.10) и `blocks/block`, а папка в единственном числе `assets/minecraft/textures/block/` отсутствовала. Из-за этого движок не находил `candle.png` и `black_concrete.png`.

---

## 3. Выполненные изменения

1. **`altar_pillar.json`:**
   - Добавлены варианты `""`, `"normal"` и `"inventory"`, указывающие на базовую модель `"neorism:block/altar_pillar"`.
2. **`altar_pillar_filled.json`:**
   - Исправлен путь текстуры с `neorism:altar_pillar_stone` на `neorism:block/altar_pillar_stone`.
3. **Текстуры ванильного домена (`assets/minecraft/textures/block/`):**
   - Создана директория `assets/minecraft/textures/block/` (в единственном числе) и скопированы все ванильные текстуры (`candle.png`, `black_concrete.png`, `stone.png`, `obsidian.png`, `iron_block.png`, древесина и др.).
   - В `assets/neorism/textures/block/` добавлены `candle.png` и `black_concrete.png`.
4. **Фиксация задачи глобальной нормализации моделей и текстур:**
   - В дорожную карту `docs/ROADMAP.md` в Фазу 8 добавлен пункт **8.0. Global Model & Texture System Normalization (Forge 1.7.10 / GTNHLib)** для систематической проверки всех оставшихся моделей и стейтов мода.

---

## 4. Верификация сборки

- Команда: `.\gradlew assemble`
- Результат: `BUILD SUCCESSFUL in 25s`
- Ошибки компиляции/ресурсов: Отсутствуют.
