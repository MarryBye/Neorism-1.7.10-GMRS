# Отчёт: Глобальная нормализация 3D моделей, атласов текстур и методологии портирования

> **Дата:** 16 октября 2026 г.  
> **Статус:** Выполнено (Passed)  
> **Веха RoadMap:** Фаза 8, Пункт 8.0  
> **Коммит:** `feat(render): global normalization of 3d models, textures and porting methodology`

---

## 1. Контекст и цели задачи

По результатам успешного тестирования Алтарей и Колонны Алтаря поставлена цель:
1. Зафиксировать исчерпывающую пошаговую инструкцию и архитектурные правила портирования 3D-моделей, текстур и локализации под Forge 1.7.10 и GTNHLib во избежание рецидивов ошибок в будущем.
2. Провести тотальный аудит и адаптацию всех существующих моделей, текстур и стейтов мода (1431 JSON файл в `assets/neorism/`).
3. Исключить любые недостающие текстуры, битые связи родителей моделей (`parent`) и невалидные синтаксические конструкции стейтов.

---

## 2. Результаты полного аудита ассетов

1. **Анализ пространств имён (`namespace`):**
   - Проверено 1431 JSON-файл в `assets/neorism/`.
   - В моделях и стейтах устаревшие пространства `vampirism:` и `factionapi:` устранены на 100%, везде используется канонический `neorism:`.
2. **Аудит текстур по всем 3D-моделям мода:**
   - Извлечено **377 уникальных ссылок на текстуры** из блоков `"textures": { ... }` всех моделей мода.
   - Первичный аудит выявил 58 неразрешённых текстур (алиасы стадий чеснока, cursed grass, фонари, а также современные ванильные текстуры свечей, камня, шерсти, наковален и котлов).
   - По итогам адаптации: **0 недостающих текстур** (100% покрытие).
3. **Аудит иерархии родителей моделей (`parent`):**
   - Обнаружено 4 неразрешённых родительских пути: `blood_container/blood_container`, `totem_top`, `totem_top_crafted`, `totem_base`.
   - Путь в `models/item/blood_container.json` исправлен на `neorism:block/blood_container`.
   - Созданы необходимые родительские базовые модели `totem_top.json`, `totem_top_crafted.json`, `totem_base.json`.
   - По итогам: **0 неразрешённых родителей**.

---

## 3. Выполненные адаптационные работы

1. **Синхронизация каталогов текстур Neorism (Dual Path):**
   - Все файлы текстур блоков синхронизированы между `assets/neorism/textures/block/` (для современных JSON-моделей) и `assets/neorism/textures/blocks/` (для ванильного 1.7.10 `registerIcon`).
   - Добавлены алиасы:
     - `cursed_grass_top.png` &rarr; `cursed_grass_block_top.png`
     - `cursed_grass_side_snowy.png` &rarr; `cursed_grass_block_snowy.png`
     - `garlic_stage0.png`..`3.png` &rarr; `garlic_stage_0.png`..`3.png`
     - `vampire_soul_lantern.png` &rarr; `spirit_lantern.png`
2. **Зеркалирование ванильных текстур (`assets/minecraft/textures/`):**
   - Извлечены текстуры из чистого архива 1.7.10 и привязаны к современным путям: `coal_block`, `stone_bricks`, `cracked_stone_bricks`, `mossy_stone_bricks`, `chiseled_stone_bricks`, `gold_block`, брёвна всех типов древесины, шерсть всех цветов, части наковальни и котла, текстуры пламени `fire_0`, `fire_1`.
   - Сгенерированы текстуры для всех 16 цветов свечей (в обычном и зажжённом `_lit` состоянии).
   - Созданы фоллбэк-текстуры для современных блоков (`basalt_top`, `gilded_blackstone`, `copper_block`).
3. **Методологическая документация:**
   - Создано фундаментальное руководство: [`docs/MODEL_TEXTURE_PORTING_GUIDE.md`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/docs/MODEL_TEXTURE_PORTING_GUIDE.md).
   - Интегрирован раздел правил моделей и текстур в специализированный агентский навык [`.agents/skills/port-vampirism-feature/SKILL.md`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/.agents/skills/port-vampirism-feature/SKILL.md).
   - Обновлена дорожная карта [`docs/ROADMAP.md`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/docs/ROADMAP.md) (веха 8.0 отмечена как выполненная).

---

## 4. Верификация сборки

- Команда: `.\gradlew assemble`
- Результат: `BUILD SUCCESSFUL in 19s`
- Ошибки сборки, парсинга моделей и путей: **0**.
