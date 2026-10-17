# Neorism 1.7.10 - Agent Guidelines & Development Rules

> **Проект:** Neorism: Become a Vampire (Minecraft 1.7.10)  
> **Основа:** Бэкпорт современных версий мода *Vampirism: Become a Vampire* (TeamLapen / maxanier)  
> **Авторы:** MarryBye + Gemini AI (бэкпорт), maxanier + TeamLapen (оригинальный мод)  
> **Среда:** Minecraft Forge 1.7.10 (`10.13.4.1614`) в экосистеме GT New Horizons (GTNH Gradle / RFG)

---

## 1. Главные правила и ограничения разработки

1. **Интеллектуальная собственность и атрибуция:**
   - Оригинальный мод — *Vampirism: Become a Vampire* от `maxanier` и `TeamLapen`.
   - Никогда не удалять ссылки на автора и оригинальный репозиторий.
   - В логах, метаданных и документации всегда указывать дисклеймер: баги Neorism не имеют отношения к авторам оригинала.

2. **Лицензионная чистота:**
   - Код: LGPL-3.0.
   - Ассеты (текстуры, модели): Vampirism Asset License v1.0 (только некоммерческое использование для форков).
   - Лор и тексты: Vampirism Narrative Content License v1.0 (VNCL).
   - Любое коммерческое использование строго запрещено.

3. **Технологический стек GTNH:**
   - Сборка: `RetroFuturaGradle` + `gtnhgradle:2.0.33+`.
   - Версия сборки / запуск демона: Java 25.
   - Поддержка синтаксиса: `jabel` включен в `gradle.properties` (разрешает современный синтаксис Java, компилируя в J8-байткод).
   - Запуск клиента в dev-среде: таски `runClient25` (или `runClient17`, `runClient`).

4. **Ключевые внешние зависимости (GTNH Ecosystem):**
   Все зависимости подключаются через GTNH Maven в `dependencies.gradle`:
   - `Et-Futurum-Requiem` (`ganymedes01.etfuturum:Et-Futurum-Requiem`): бэкпортированные сущности, предметы, стрелковая механика.
   - `GTNHLib` (`com.github.GTNewHorizons:GTNHLib`): вспомогательные библиотеки рендеринга и математики.
   - `Hodgepodge` (`com.github.GTNewHorizons:Hodgepodge`): патчи движка, расчёт дневного света и времени.
   - `Angelica` (`com.github.GTNewHorizons:Angelica`): шейдеры, современный рендеринг (Sodium/Iris). При написании рендереров не использовать устаревшие вызовы OpenGL, ломающие пайплайн Angelica.
   - `VillageNames` (`com.github.GTNewHorizons:VillageNames`): интеграция с деревнями, NPC и именами.
   - `lwjgl3ify` (`com.github.GTNewHorizons:lwjgl3ify`): поддержка LWJGL3 и Java 17-25.
   - `UniMixins` (`io.github.legacymoddingmc:unimixins`): миксины в байткод для глубоких хуков.
   - `Combat Backport` (`com.marrybye.combatbackport:combatbackport`): поддержка механик боя 1.9+ (скорость атаки, щиты, Sweeping Edge).
   - `Villager Trades Backport` (`com.marrybye.villagertradesbackport:villagertradesbackport`): поддержка современной торговли 1.14+ и взаимодействия с жителями.
   - **Ориентироваться на совместимость с последними версиями этих модов и их будущими обновлениями.**

5. **Каноничность кода (Точное копирование и адаптация оригинала, запрет отсебятины):**
   - **СТРОГО ЗАПРЕЩЕНО** писать код «из головы» или пытаться повторить механику по своему представлению/памяти.
   - Агент **ОБЯЗАН ВСЕГДА** находить оригинальный исходный код в папке `reference/vampirism/` (`de.teamlapen.vampirism` и `de.teamlapen.faction`) и точно адаптировать его под Minecraft 1.7.10.
   - Задача Neorism — **создать точную каноничную копию мода Vampirism + FactionAPI на 1.7.10**, а не мод «по мотивам».
   - При реализации новых механик, а также при аудите существующих — агент обязан обращаться к оригинальным классам, сверять алгоритмы, константы, формулы и приводить кодовую базу в 100% соответствие оригиналу. См. скилл `.agents/skills/canonical-backport/SKILL.md`.

---

## 2. Архитектурные правила портирования (Modern MC -> 1.7.10)

| Подсистема в Modern MC (1.20+) | Аналог в Forge 1.7.10 | Примечания |
| :--- | :--- | :--- |
| `DeferredRegister<T>` / Registry | `GameRegistry.registerItem`, `GameRegistry.registerBlock` | Обернуть в типобезопасный `ModRegistries` |
| `Item` с `Item.Properties` | `Item` с `setTextureName`, `setUnlocalizedName`, `setCreativeTab` | Соблюдать правила регистрации 1.7.10 |
| `Block` с `BlockBehaviour.Properties` | `Block` с `Material`, `setStepSound`, `setHardness` | Для метаданных использовать подтипы/metadata |
| Modern Capabilities (`ICapabilityProvider`) | `IExtendedEntityProperties` (IEEP) | Синхронизация данных игрока через SimpleNetworkWrapper |
| Network: `SimpleChannel` | `SimpleNetworkWrapper` | `IMessage` и `IMessageHandler` |
| Block & Item Models (JSON blockstates) | `ISimpleBlockRenderingHandler`, `TileEntitySpecialRenderer`, `IItemRenderer` | Для сложных моделей: OBJ/Bobj или классические модели |
| NBT: `CompoundTag` | `NBTTagCompound` | Методы `getString`, `getInteger`, `setTag` |
| Text: `Component.translatable(...)` | `ChatComponentTranslation`, `StatCollector.translateToLocal` | Локализация через `.lang` файлы |
| Capabilities & Effects | Potion, AttributeModifier, Event Hooks | Forge EventBus и FMLCommonHandler |

---

## 3. Структура пакетов Neorism

- `com.marrybye.neorism`:
  - `api`: Публичное API для аддонов и других модов GTNH.
  - `client`: Рендеры, оверлеи (HUD крови/уровней), GUI, частицы, кейбиндинги.
  - `common`:
    - `blocks`: Блоки мода (алтари, гробы, тотемы, кровавые деревья).
    - `items`: Предметы, оружие, броня, флаконы крови, колчаны.
    - `entity`: Сущности (вампиры, охотники, летучие мыши, миньоны, боссы).
    - `faction`: Механика фракций (Vampire, Hunter, Neutral).
    - `vampire`: Механика вампиризма (кровь, солнце, навыки, уровни).
    - `hunter`: Механика охотников (снаряжение, уровни, инфузии, зелья).
    - `rituals`: Механика алтарей и ритуалов.
    - `network`: Сетевые пакеты (`SimpleNetworkWrapper`).
    - `world`: Биомы вампирского леса, структуры, генерация руд и склепов.

---

## 4. Папка со скиллами

Пользовательские и специализированные скиллы агента располагаются в:
- `.agents/skills/<skill_name>/SKILL.md`
  - `canonical-backport`: Железное правило поиска и адаптации оригинального кода TeamLapen Vampirism + FactionAPI.
  - `debug-logging`: Диагностическое логгирование.
  - `git-workflow`: Регламент коммитов и релизов.
  - `run-client-test`: Правила сборки и тестирования.
  - `task-reporting`: Ведение отчетов и обновление roadmap.
  - `port-vampirism-feature`: Процедура адаптации механик под 1.7.10.

Перед началом сложной подзадачи портирования всегда сверяться с `.agents/PROJECT_CONTEXT.md`, архитектурой `docs/ARCHITECTURE_OVERVIEW.md` и генеральной дорожной картой `docs/ROADMAP.md`.

---

## 5. Обязательные правила рабочего процесса разработки

1. **Форматирование Git коммитов (Conventional Commits):**
   - Каждый коммит должен строго следовать формату Conventional Commits: `<type>(<scope>): <описание>` (например: `feat(vampire): add blood drinking`, `fix(render): fix coffin TESR model`, `chore(deps): update mods in run directory`, `docs: ...`).
   - Фиксировать каждое логическое изменение в локальный git.

2. **Тотальное диагностическое логгирование (`ModLogger`):**
   - Логгировать все ключевые события игры в реальном времени: удары по мобам, расчёт урона, процесс торговли, изменение уровней/навыков, спавны сущностей (особенно боссов и элитных мобов), изменения в ИИ/поведении, координаты и состояние игрока.
   - Всё логгирование обязательно управляется через `Config.debugLogging` (и подкатегории `debugCombat`, `debugAI`, `debugLeveling`, `debugSpawning`, `debugTrading`, `debugRituals`), чтобы в релизной сборке не создавалось оверхеда.

3. **Контроль релизов и пушей на GitHub:**
   - **НИКОГДА не делать `git push` и не создавать релизы на GitHub**, пока пользователь явно не даст команду (*"заливаем на гитхаб"*, *"заливай обновление"*).
   - Все изменения коммитятся локально. При команде на релиз — объединять историю под новым тегом версии (`git tag`), пушить на GitHub и формировать структурированный Release.

4. **Жизненный цикл сборки и тестирования (`run-client-test`):**
   - После внесения изменений мод обязательно компилируется и собирается (`.\gradlew assemble`), чтобы обновить скомпилированные классы и проверить отсутствие ошибок сборки.
   - **Важно по `run/client/mods/`:** В среде RetroFuturaGradle все зависимости из `dependencies.gradle` (Angelica, GTNHLib, Hodgepodge, VillageNames, Et-Futurum-Requiem, lwjgl3ify, unimixins, combatbackport, villagertradesbackport) и сам мод Neorism монтируются на classpath автоматически при запуске таски `runClient25`/`runClient`. Копировать эти jar-файлы в `run/client/mods/` **НЕЛЬЗЯ**, иначе FML выдаст ошибку дублирования модов ("Found a duplicate mod"). Папка `run/client/mods/` предназначена только для сторонних модов, не объявленных в Gradle.
   - Клиент игры запускает **сам пользователь** лично, когда считает нужным протестировать изменения (через `.\gradlew runClient25`).
   - Агент **НЕ запускает клиент игры самостоятельно**.
   - После завершения тестирования пользователь даёт агенту команду изучить логи игры (*"посмотри логи"*, *"проверь логи игры"*).
   - Агент анализирует `run/client/logs/fml-client-latest.log`, `latest.log` и краш-репорты, на их основе делает диагностические выводы и при необходимости исправляет ошибки.

5. **Обязательное ведение отчётов и обновление дорожной карты (`task-reporting`):**
   - Движение по разработке мода осуществляется строго последовательно по фазам из `docs/ROADMAP.md`.
   - После выполнения каждой подзадачи/этапа агент обязан отметить соответствующий чекбокс `[x]` в `docs/ROADMAP.md` и составить структурированный отчёт в `docs/reports/` (с описанием изменений, портированных формул и статусом тестов).


