# Neorism 1.7.10 — Project Context

## 1. Среда и Стек
- **Таргет:** Minecraft 1.7.10 (Forge `10.13.4.1614`), Gradle 9 + `gtnhgradle`, Java 25 + `jabel`.
- **Интеграции GTNH (`dependencies.gradle`):**
  - `Angelica`: шейдеры и рендер (избегать устаревшего immediate-mode GL и поломок GL-стейта).
  - `GTNHLib`: JSON-модели (`pack_format: 4`, `ModelISBRH`), математика, рендер.
  - `Hodgepodge`: фиксы движка и синхронизация солнечного цикла.
  - `Et-Futurum-Requiem`: бэкпорт ванильных механик, арбалетов, баннеров и звуков.
  - `Combat Backport` & `Villager Trades Backport`: бой 1.9+ (кулдауны, щиты) и торговля 1.14+.
  - `VillageNames`, `lwjgl3ify`, `UniMixins`.

## 2. Маппинг архитектуры (Modern Vampirism -> 1.7.10)
- **Capabilities -> IEEP:** `IExtendedEntityProperties` (`FactionPlayerHandler`, `VampirePlayer`, `HunterPlayer`), сохранение при смерти/смене измерения через `PlayerEvent.Clone`.
- **Network:** `SimpleNetworkWrapper` (канал `"neorism"`), пакеты `IMessage` / `IMessageHandler`.
- **Модели и Рендер:** Блоки со сложной геометрией используют `ModelISBRH` (GTNHLib JSON) или `TESR` (`TileEntityCoffinRenderer`, алтари, тотемы). Кастомные 3D-предметы — `IItemRenderer`.
- **ИИ и Достижения:** Modern `Brain`/`Goal` -> `EntityAIBase`; Advancements -> `AchievementPage` (`ModAchievements`).

## 3. Ресурсы и Эталонный код
- **Эталон (`reference/vampirism/`):** Полный исходный код `vampirism`, `vampirism-api`, `faction`, `faction-api`, датапаки (`data/`) и исходные модели (`raw_models/`).
- **Ассеты (`src/main/resources/assets/neorism/`):** Все оригинальные текстуры (дублированные в `block`/`blocks` и `item`/`items`), звуки (`sounds.json`), модели, шейдеры и 31 языковой `.lang` файл уже перенесены в проект.
