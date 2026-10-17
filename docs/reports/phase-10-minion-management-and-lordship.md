# Milestone Report: Phase 10 - Minion Management & Lordship System

> **Date:** 2026-10-17  
> **Status:** COMPLETED  
> **Phase:** Phase 10 - Minion Management & Lordship System  
> **Reference Codebase:** `reference/vampirism/` (TeamLapen / maxanier)

---

## 1. Scope & Objectives

Полный канонический перенос подсистемы Лордов и Миньонов из оригинального мода Vampirism / FactionAPI:
- Иерархия Лордов 1–5 уровней для фракций Вампиров и Охотников.
- Канонические титулы (Барон/Баронесса, Виконт/Виконтесса, Граф/Графиня, Герцог/Герцогиня, Принц/Принцесса, а также воинские звания охотников: Лейтенант, Капитан, Майор, Полковник, Бригадир) с разделением по полу (`TitleGender`).
- Отображение титулов в никнейме над головой и в чате через `PlayerEvent.NameFormat`, а также синхронизация со скорбордом (`ScoreboardUtil`).
- Персистентная модель данных миньонов (`MinionData`, `MinionInventory`, `PlayerMinionController`, `MinionWorldData`).
- Сущности миньонов (`EntityMinionBase`, `EntityVampireMinion`, `EntityHunterMinion`) с ИИ-поведением (`FollowLordGoal`, `DefendLordGoal`).
- Система приказов и задач (`StayTask`, `FollowLordTask`, `DefendAreaTask`, `ProtectLordTask`, `CollectResourcesTask` для сбора крови и ресурсов).
- Контейнер и клиентский графический интерфейс управления миньоном (`ContainerMinion`, `GuiMinion`) с синхронизацией пакетов (`SelectMinionTaskPacket`, `ToggleMinionTaskLockPacket`).

---

## 2. Эталонные классы оригинала

- `de.teamlapen.faction.api.factions.lord.ILordPlayer`
- `de.teamlapen.faction.api.factions.lord.ILordTitleProvider`
- `de.teamlapen.faction.api.factions.lord.LordTitles`
- `de.teamlapen.faction.common.factions.lord.LordPlayer`
- `de.teamlapen.faction.api.world.entities.minion.IMinionEntity`
- `de.teamlapen.faction.api.world.entities.minion.IMinionInventory`
- `de.teamlapen.faction.api.world.entities.minion.IMinionTask`
- `de.teamlapen.faction.common.factions.minions.MinionData`
- `de.teamlapen.faction.common.factions.minions.MinionInventory`
- `de.teamlapen.faction.common.factions.minions.PlayerMinionController`
- `de.teamlapen.faction.common.factions.minions.MinionWorldData`
- `de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity`
- `de.teamlapen.vampirism.common.world.entity.minion.HunterMinionEntity`
- `de.teamlapen.faction.common.world.inventory.MinionContainer`
- `de.teamlapen.faction.client.gui.screens.MinionScreen`
- `de.teamlapen.vampirism.client.renderer.entities.VampireMinionRenderer`
- `de.teamlapen.vampirism.client.renderer.entities.HunterMinionRenderer`

---

## 3. Технические детали реализации

### 3.1. Созданные и модифицированные классы

1. **API Лордства и Миньонов:**
   - `src/main/java/de/teamlapen/faction/api/factions/lord/ILordPlayer.java`
   - `src/main/java/de/teamlapen/faction/api/factions/lord/ILordTitleProvider.java`
   - `src/main/java/de/teamlapen/faction/api/factions/IFactionEntity.java`
   - `src/main/java/de/teamlapen/faction/api/world/entities/minion/IMinionEntity.java`
   - `src/main/java/de/teamlapen/faction/api/world/entities/minion/IMinionInventory.java`
   - `src/main/java/de/teamlapen/faction/api/world/entities/minion/IMinionTask.java`
   - `src/main/java/de/teamlapen/faction/api/factions/IPlayableFaction.java` (добавлен `TitleGender`)

2. **Ядро системы Миньонов:**
   - `src/main/java/com/marrybye/neorism/common/entity/minion/MinionInventory.java` — 21 слот (оружие, щит/еда, 4 слота брони, 9-15 слотов хранилища).
   - `src/main/java/com/marrybye/neorism/common/entity/minion/MinionData.java` — персистентные характеристики, статы (здоровье, урон, скорость, вместимость), активная задача, NBT.
   - `src/main/java/com/marrybye/neorism/common/entity/minion/PlayerMinionController.java` — распределение слотов миньонов по UUID лорда.
   - `src/main/java/com/marrybye/neorism/common/entity/minion/MinionWorldData.java` — глобальное хранилище данных `WorldSavedData`.

3. **Задачи и ИИ миньонов:**
   - `src/main/java/com/marrybye/neorism/common/entity/minion/tasks/FollowLordTask.java`
   - `src/main/java/com/marrybye/neorism/common/entity/minion/tasks/StayTask.java`
   - `src/main/java/com/marrybye/neorism/common/entity/minion/tasks/DefendAreaTask.java`
   - `src/main/java/com/marrybye/neorism/common/entity/minion/tasks/ProtectLordTask.java`
   - `src/main/java/com/marrybye/neorism/common/entity/minion/tasks/CollectResourcesTask.java` — сбор крови и предметов в фоновом режиме.
   - `src/main/java/com/marrybye/neorism/common/entity/minion/tasks/ModMinionTasks.java` — реестр задач.
   - `src/main/java/com/marrybye/neorism/common/entity/minion/ai/FollowLordGoal.java` — следование за лордом с телепортацией при отставании.
   - `src/main/java/com/marrybye/neorism/common/entity/minion/ai/DefendLordGoal.java` — защита лорда от атакующих.

4. **Сущности и Рендеринг:**
   - `src/main/java/com/marrybye/neorism/common/entity/minion/EntityMinionBase.java`
   - `src/main/java/com/marrybye/neorism/common/entity/minion/EntityVampireMinion.java`
   - `src/main/java/com/marrybye/neorism/common/entity/minion/EntityHunterMinion.java`
   - `src/main/java/com/marrybye/neorism/client/render/entity/RenderVampireMinion.java`
   - `src/main/java/com/marrybye/neorism/client/render/entity/RenderHunterMinion.java`
   - Регистрация в `ModEntities.java` и `ClientProxy.java`.

5. **GUI и Сеть:**
   - `src/main/java/com/marrybye/neorism/common/entity/minion/ContainerMinion.java`
   - `src/main/java/com/marrybye/neorism/client/gui/GuiMinion.java`
   - `src/main/java/com/marrybye/neorism/common/network/packets/SelectMinionTaskPacket.java`
   - `src/main/java/com/marrybye/neorism/common/network/packets/ToggleMinionTaskLockPacket.java`
   - `src/main/java/com/marrybye/neorism/common/network/ModGuiHandler.java` (ID 100).

6. **Интеграция Лордства в `FactionPlayerHandler`:**
   - Реализован `ILordPlayer`.
   - Поле и синхронизация `titleGender` через `SyncFactionPacket`.
   - Подключение `PlayerEvent.NameFormat` для отображения титулов в нике и чате.
   - Подключение `ScoreboardUtil`.

---

## 4. Верификация и Сборка

1. Проведена сборка проекта:
   ```bash
   .\gradlew assemble
   ```
   **Результат:** `BUILD SUCCESSFUL in 13s` (0 ошибок).
2. Полная изоляция клиентского кода: прокси-регистрация GUI и рендереров исключает падения на выделенном сервере.

---

## 5. Следующие шаги

Переход к **Phase 11: World Generation, Vampire Biome & Structures** согласно генеральной дорожной карте (`docs/ROADMAP.md`):
- `11.1. Vampire Forest Biome` (`BiomeGenVampireForest`, цвета неба/воды, растительность, спавны).
- `11.2. Underground Vampire Crypt Structure`.
- `11.3. Hunter Camps & Outposts`.
- `11.4. Altar Ruins in Overworld`.
- `11.5. Dracula Castle Boss Arena`.
