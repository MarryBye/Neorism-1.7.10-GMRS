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
   - **Ориентироваться на совместимость с последними версиями этих модов и их будущими обновлениями.**

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

Перед началом сложной подзадачи портирования всегда сверяться с `.agents/PROJECT_CONTEXT.md` и дорожной картой `.agents/BACKPORT_ROADMAP.md`.
