---
name: canonical-backport
description: Strict workflow for porting mechanics, entities, items, blocks, and systems from original TeamLapen Vampirism & FactionAPI reference source code into Neorism Minecraft 1.7.10. Mandates copying and adapting canonical code instead of inventing custom implementations.
---

# Canonical Backport Workflow (Копирование и Адаптация Оригинала)

> **Главный закон проекта:**  
> В Neorism **СТРОГО ЗАПРЕЩЕНО** писать код «из головы», импровизировать или пытаться повторить механику по памяти/своему представлению.  
> Агент **ОБЯЗАН ВСЕГДА** находить оригинальный исходный код в репозитории `reference/vampirism/` (TeamLapen Vampirism + FactionAPI) и адаптировать его под Minecraft 1.7.10.  
> **Цель проекта — создать точную каноничную копию мода на 1.7.10, а не «мод по мотивам».**

---

## 1. Расположение оригинального кода (Reference Repository)

Вся кодовая база оригинального мода и его библиотек содержится в папке `reference/vampirism/`:
- **TeamLapen Vampirism (Core Mod):**  
  `reference/vampirism/src/main/java/de/teamlapen/vampirism/`
  - `common/core/`: Реестры (`ModItems.java`, `ModBlocks.java`, `ModEntities.java`, `ModEffects.java`, `ModRecipes.java`, `ModFactions.java`).
  - `common/world/`: Механики мира, блоки, предметы, инвентари, сущности.
  - `common/world/entity/player/`: Логика игрока-вампира (`vampire/`), охотника (`hunter/`), прокачка, навыки, кровь.
  - `client/`: Рендеры сущностей, блоков, предметов, GUI экраны, оверлеи HUD, частицы, модели (`client/model/`).
  - `data/vampirism/`: Рецепты, лут-таблицы, теги, предикаты, настройки генерации мира.
- **TeamLapen FactionAPI (Faction Subsystem & Skill Trees):**  
  `reference/vampirism/src/main/java/de/teamlapen/faction/`
  - `api/factions/`: Фракции, уровни, титулы лордов.
  - `api/factions/skills/`: Древо навыков (`ISkillTree`, `ISkillSegment`, `ISkill`, связи, разблокировка).
  - `client/gui/`: Круговое меню действий (Radial Menu), экраны прокачки навыков.

---

## 2. Пошаговый алгоритм реализации любой механики

### Шаг 1: Поиск каноничного источника (Search & Trace)
Перед написанием любого класса или метода агент обязан выполнить поиск по `reference/vampirism/`:
```powershell
git grep -i "<Keyword/Item/Mechanic>" "reference/vampirism"
```
1. Найти соответствующий класс(ы) в `de.teamlapen.vampirism` или `de.teamlapen.faction`.
2. Внимательно прочитать исходный класс целиком через `view_file`.
3. Зафиксировать:
   - Математические формулы и формулы урона/лечения/крови.
   - Числовые константы (кулдауны, длительность эффектов, радиусы, шансы).
   - Взаимодействие с фракциями (`canJoin`, `isVampire`, `isHunter`, уровни).
   - Звуки, частицы, сообщения в чат (`Component.translatable(...)`).

### Шаг 2: Адаптация API (Modern MC -> Forge 1.7.10)
Копировать исходную структуру класса и методы, заменяя современные вызовы на эквиваленты 1.7.10 по таблице адаптации:

| Подсистема в Modern MC | Эквивалент в Forge 1.7.10 (Neorism) | Особенности адаптации |
| :--- | :--- | :--- |
| `Level` / `ServerLevel` | `World` / `WorldServer` | `world.isRemote`, `worldObj` |
| `BlockPos` | `int x, int y, int z` | Или `ChunkCoordinates` при необходимости |
| `Direction` | `ForgeDirection` / `int side` | Конвертация сторон мира |
| `CompoundTag` | `NBTTagCompound` | `getInteger`, `getString`, `setTag` |
| `ResourceLocation` | `new ResourceLocation(Neorism.MODID, ...)` | Пространство имен `neorism` |
| `Holder<IPlayableFaction<?>>` | `IPlayableFaction` | Прямая ссылка на объект фракции |
| `IExtendedEntityProperties` | `FactionPlayerHandler` / `SkillHandler` | Современные Capabilities -> IEEP в 1.7.10 |
| `SimpleChannel` / Packets | `SimpleNetworkWrapper` (`IMessage` + `IMessageHandler`) | Синхронизация между клиентом и сервером |
| `LivingEntity.addEffect(...)` | `player.addPotionEffect(new PotionEffect(...))` | Стандартные зелья Forge 1.7.10 |
| `Item` с `Properties` | `Item` с `setUnlocalizedName`, `setTextureName` | Конструкторы 1.7.10 |
| `Block` с `Properties` | `Block` с `Material`, `setHardness`, `setStepSound` | Метаданные блоков |
| 3D Модели предметов/блоков | `TileEntitySpecialRenderer` (TESR) / `IItemRenderer` | Для сложных моделей не полагаться на сломанные JSON |
| GUI (`Screen`, `GuiGraphics`) | `GuiScreen`, `GL11`, `drawTexturedModalRect` | Обязательно сбрасывать GL стейт и освещение |
| `Component.translatable(...)` | `ChatComponentTranslation` / `StatCollector` | Локализация через `.lang` файлы |

### Шаг 3: Сохранение каноничных названий и ключей
- Идентификаторы предметов, блоков, навыков, действий и звуков должны строго совпадать с оригиналом.
- Все ключи локализации в `ru_RU.lang` и `en_US.lang` должны соответствовать оригиналу (`item.vampirism.<name>` -> `item.neorism.<name>`, `skill.neorism.<id>`, `text.neorism.<key>`).
- Текстуры и модели берутся строго из оригинальных ассетов (`reference/vampirism/src/main/resources/assets/vampirism/`).

---

## 3. Регламент проведения аудита существующих механик

Когда пользователь просит провести аудит или сравнение уже реализованной механики:
1. **Найти реализацию в проекте:** Найти файл(ы) в `src/main/java/com/marrybye/neorism/...`.
2. **Найти оригинальный файл:** Найти файл(ы) в `reference/vampirism/...`.
3. **Построчное сравнение:**
   - Проверить совпадение алгоритмов и логических веток.
   - Проверить наличие всех проверок (проверка на кулдауны, фракцию, уровень, креативный режим, иммунитеты).
   - Проверить формулы расчёта.
4. **Выявление расхождений:** Составить точный список отличий («в оригинале метод `X` делает `Y`, а у нас было упрощено до `Z`»).
5. **Приведение к канону:** Переписать неоригинальный код, внедрив точную адаптированную копию оригинальной логики.

---

## 4. Категорические запреты (Anti-Patterns)

- ❌ **Запрещено** выдумывать свои формулы прокачки, если они есть в `VampireLeveling.java` или `HunterLeveling.java`.
- ❌ **Запрещено** создавать произвольные узлы и связи в древе навыков, если они определены в FactionAPI / `SkillTree.java`.
- ❌ **Запрещено** изобретать собственные форматы NBT-данных игрока, если в оригинале уже определена структура хранения.
- ❌ **Запрещено** заменять оригинальные визуальные эффекты или звуки на «что-то похожее», если оригинальный ресурс доступен в `reference/vampirism`.
