# Neorism 1.7.10 — Update & Audit Handoff Report (`v0.7.0` → `v0.8.0`)

**Дата:** 13.11.2026 17:06  
**Базовый тег:** `v0.7.0` (`5e481a5`)  
**Охваченные коммиты:** `c041146` .. `6a3c490` (7 коммитов)  
**Назначение:** Подробный технический справочник изменений для последующего аудита (что изменено, где, зачем и из какого канонического источника `reference/vampirism/` перенесено под Forge 1.7.10).

---

## 1. Команды отладки и баланса (`c041146`)

- **Зачем:** Быстрое тестирование фракций, уровней, рангов лорда, умений, боя с Дракулой и очистка зависших тотемов.
- **Где реализовано / изменено:**
  - `src/main/java/com/marrybye/neorism/common/command/CommandVampirism.java`
  - `src/main/java/com/marrybye/neorism/common/command/CommandFactions.java`
  - `src/main/java/com/marrybye/neorism/common/command/CommandLevelShortcut.java`
  - `src/main/java/com/marrybye/neorism/common/command/CommandLordShortcut.java`
  - `src/main/java/com/marrybye/neorism/CommonProxy.java`
  - `src/main/java/com/marrybye/neorism/common/skills/SkillHandler.java` (`forceUnlockSkill`, `forceUnlockAllSkills`)
  - `src/main/java/com/marrybye/neorism/common/action/ActionHandler.java`
  - `src/main/java/com/marrybye/neorism/common/tileentity/TileEntityTotem.java`
- **Канонический источник и адаптация 1.7.10:**
  - Адаптировано из Brigadier-команд `VampirismCommand` / `FactionsCommand` под `net.minecraft.command.CommandBase` (`ICommandSender`, `func_152373_a`). Добавлены алиасы `/level` и `/lord`, а также ветки `/vampirism velmorra tp` и `/vampirism velmorra fight start|phase`.

---

## 2. Поддержка Alex/Slim (3px руки) для NPC и устранение прозрачных полос (`c15e42b`, `a96634f`)

- **Зачем:** Текстуры `*_slim.png` (`hunter5..8_slim`, `hunter12_slim`, `minion/vampire/vampire1..6_slim`, `baroness`) рассчитаны на 3px ширину рук модели Alex; при рендере 4px моделью Стива на краю рукава появлялись прозрачные дыры. После первого запуска `runClient25` выявился баг перегрузки Java в `RenderBiped64`.
- **Где реализовано / исправлено:**
  - `src/main/java/com/marrybye/neorism/client/render/entity/RenderBiped64.java` — общий рендер 64x64 гуманоидов с переключением `wideModel`/`slimModel` и `wideArmor*`/`slimArmor*`. В коммите `a96634f` исправлен вызов `this.getEntityTexture((net.minecraft.entity.Entity) entity)`: без явного приведения к `Entity` компилятор привязывал вызов к `RenderBiped.getEntityTexture(EntityLiving)`, всегда возвращающему `null` вместо переопределённых `getEntityTexture(Entity)` в подклассах.
  - `src/main/java/com/marrybye/neorism/client/model/ModelBiped64.java` — очистка `this.boxList.clear()` перед сборкой 64x64 геометрии (убирает оставшиеся от `super(...)` 4px коробки), 3px геометрия рук и оверлеи рукавов.
  - `src/main/java/com/marrybye/neorism/client/model/ModelBipedArmor.java` — удаление wide-рукавов из `boxList` при `slim = true` и 3px оверлей брони.
  - Обновлены рендеры и сущности: `RenderHunter`, `RenderHunterMinion`, `RenderVampire`, `RenderVampireBaron`, `RenderVampireMinion`, `RenderVampireMother`, `RenderAdvancedHunter`, `RenderAdvancedVampire`, `EntityHunter`, `EntityVampire`, `EntityAdvancedHunter`, `EntityAdvancedVampire`.
- **Канонический источник и адаптация 1.7.10:**
  - Соответствует `ModelPlayer` из `Et-Futurum-Requiem` / `Vampirism` (64x64 UV для Alex 3px и Steve 4px рук) в среде Forge 1.7.10 (где ванильный `ModelBiped` использует сетку 64x32).

---

## 3. Ассеты и исправление ошибок лога `latest.log` (`a96634f`)

- **Зачем:** Устранение `FileNotFoundException` и отсутствующего звука в логе клиента.
- **Где исправлено:**
  - `src/main/resources/assets/neorism/sounds.json` — звук `entity.remains_defender.death` переключён с несуществующего в 1.7.10 `minecraft:dig/glass1..3` на `minecraft:random/glass1..3`.
  - `src/main/java/com/marrybye/neorism/common/action/vampire/BloodVisionAction.java` — переопределён `getIcon()` с указанием `neorism:textures/skills/blood_vision.png` (иконка лежала в `textures/skills/`, а `ActionBase` по умолчанию искал `textures/actions/`).

---

## 4. Полнота мира, структур, спавнов, блоков и звуков (`4279fcb`)

- **Зачем:** Аудит и перенос недостающих структур, декора, культур и звуковых триггеров оригинала.
- **Где реализовано:**
  - `src/main/java/com/marrybye/neorism/common/world/gen/WorldGenGiantRoot.java` — перенесена генерация гигантских корней из `GiantRootFeature` / `giant_dark_stone_root.json`.
  - `src/main/java/com/marrybye/neorism/common/world/gen/WorldGenMotherTree.java` — перенесено дерево Матери (`MotherStructure` / `MotherPiece`, 18x14x18) со спавном `EntityVampireMother`.
  - `src/main/java/com/marrybye/neorism/common/world/gen/WorldGenVampireDungeon.java` и `WorldGenVampireHut.java` — перенесены канонические подземелья вампиров и хижины на сваях с таблицами лута.
  - `src/main/java/com/marrybye/neorism/common/world/gen/VillageHunterTrainerCreationHandler.java` & `ModWorldGen.java` — интеграция домика тренера охотников в деревнях и генерация структур в мире.
  - `src/main/java/com/marrybye/neorism/common/blocks/BlockGarlic.java`, `BlockGarlicDiffuser.java`, `TileEntityGarlicDiffuser.java`, `ItemGarlic.java`, `ItemPureSaltWater.java` — каноническая логика роста чеснока, диффузоров (обычный/усиленный/долгий) и очищенной солёной воды.
  - `src/main/java/com/marrybye/neorism/common/core/ModSounds.java`, `src/main/resources/assets/neorism/sounds.json`, `VampireAtmosphereHandler.java`, `EntityDracula.java`, `EntityVampireBaroness.java`, `EntityVampireMother.java` — дополнены звуки существ, котла, саженца и атмосферы вампирского леса.

---

## 5. Крафты, NBT-прочность, NEI и гайды (`32ea1f5`)

- **Зачем:** Проверка и восполнение недостающих рецептов, отображение всех станков в NEI, канонические подсказки по прогрессии и попаданию в Вельморру.
- **Где реализовано:**
  - `src/main/java/com/marrybye/neorism/common/core/ModRecipes.java` — полный аудит и добавление крафтов (в т.ч. рецепты предметов, блоков, масел, стрел/обойм, алтарей и алхимического котла).
  - `src/main/java/com/marrybye/neorism/common/items/DurabilityHelper.java` + обновлены предметы (`ItemHunterArmor`, `ItemSwiftnessArmor`, `ItemHunterAxe`, `ItemVampireCloak`, `ItemVampireSword`, `ItemCrossbow`, `ItemOilBottle`, `WeaponOilEventHandler`).
  - `src/main/java/com/marrybye/neorism/common/integration/nei/`:
    - Доработаны `AlchemicalCauldronRecipeHandler`, `AltarInfusionRecipeHandler`, `AltarInspirationRecipeHandler`, `HunterTableRecipeHandler`, `NEIConfig`.
    - Добавлены информационные вкладки `NeorismItemInfoHandler.java` (пояснения получения/применения особых предметов) и `NeorismStructureInfoHandler.java` (текстово-схематические руководства по алтарям, порталу Вельморры и мультикам/ритуалам).
  - `src/main/java/com/marrybye/neorism/client/events/TooltipEventHandler.java` + локализация `en_US.lang` / `ru_RU.lang`.

---

## 6. Окно умений: скроллинг, битые привязки и каноничная экономика очков (`2ede409`)

- **Зачем:** Пользователь не мог прокрутить дерево вампира ниже средних веток (`scrollY` был жёстко зажат в `-300..60`, при высоте дерева до ~626px), а также оставались лишние непотраченные очки умений и у обычного вампира, и у лорда.
- **Где исправлено:**
  - `src/main/java/com/marrybye/neorism/client/gui/skills/SkillsTabComponent.java` — динамический расчёт нижней границы скролла по `layout.getHeight()` и `viewH` (`Math.min(20, viewH - layout.getHeight() - 40)`).
  - `src/main/java/com/marrybye/neorism/client/gui/skills/SkillsScreen.java` — добавлена прокрутка колёсиком мыши (`handleMouseInput()` + `Mouse.getEventDWheel()`).
  - `src/main/java/com/marrybye/neorism/common/skills/SkillRegistry.java`:
    - Исправлен ID действия невидимости: `ActionRegistry.getAction("vampire_invisibility")` вместо несуществующего `"invisibility"`.
    - Стоимости узлов приведены к каноническим значениям `VampireSkills.java`, `HunterSkills.java` и `LordSkills.java` (1..3 очка; суммарная цена ветки уровня вампира — 48 очков при 26 доступных с уровней).
    - В обе ветки лорда (`vampire_lord` и `hunter_lord`) добавлен недостающий канонический навык `lord_attack_speed` (`vampire_lord_attack_speed` / `hunter_lord_attack_speed`, 1 очко) и для охотников добавлен `hunter_lord_speed` (1 очко). Теперь дерево лорда стоит ровно 8 очков (3+1+1+2+1).
  - `src/main/java/com/marrybye/neorism/common/skills/SkillHandler.java` — добавлен подсчёт реально потраченных очков (`getSpentSkillPoints()`) и проверка навыков для `lord_attack_speed` и `lord_speed` у обеих фракций.
  - `src/main/java/com/marrybye/neorism/common/faction/FactionPlayerHandler.java` — переход на каноническую формулу `FactionSkillPointProvider`: `Math.max(0, currentLevel - 1) * 2 + Math.max(0, lordLevel - 1) * 2` (26 за 14 уровней + 8 за 5 уровней лорда = 34 всего; пересчёт остатка `total - spent` при изменении уровня/ранга/фракции).
  - `src/main/resources/assets/neorism/lang/en_US.lang` и `ru_RU.lang` — локализация новых ключей навыков лорда.

---

## 7. Переработка измерения Вельморра, Большой Замок Дракулы и спавн Босса (`6a3c490`)

- **Зачем:** Старая генерация состояла из круглой кирпичной платформы в пустоте и маленькой коробки `25x25x7`, а сам Дракула отсутствовал внутри замка до активации всех 4 чаш.
- **Где переработано:**
  - `src/main/java/com/marrybye/neorism/common/world/dimensions/velmorra/ChunkProviderVelmorra.java`:
    - Убрана пустота: генерируется непрерывный тёмный холмистый ландшафт (2D FBM-шум) с плавным переходом от центральной плато-площадки замка (`r <= 44`, `y = 65`) к лесным холмам и кровавым озёрам в низинах (`y <= 58`, `ModFluids.blockBlood`).
    - Явно заполняется `biomeArray` биомом `ModBiomes.velmorra`.
    - В `populate` за пределами цитадели генерируются густые леса (`WorldGenDarkSpruceTree` из `darkSpruceLog` и `cursedSpruceLog`), флора (`vampireOrchid`, `cursedRoots`), гигантские корни (`WorldGenGiantRoot`), а также руины алтарей (`WorldGenVampireAltarRuins`), хижины (`WorldGenVampireHut`), склепы (`WorldGenVampireCrypt`), кладбищенские руины и редкие Древа Матери (`WorldGenMotherTree`).
  - `src/main/java/com/marrybye/neorism/common/world/gen/WorldGenDraculaCastle.java`:
    - Расширен в крупную готическую крепость (`61x61`, `x = -30..30`, `z = -26..34`, шпили до `y = 97`): парадная аллея с фонарями душ, надвратные башни с решёткой, 4 угловых бастиона с лестницами и лутом, внешний двор с двумя кровавыми фонтанами, высокий кафедральный зал с витражным окном-розой, люстрами, троном и 4 ритуальными чашами, плюс западное (королевский склеп) и восточное (библиотека крови) крылья и стража (`EntityVampire` / `EntityAdvancedVampire`).
    - В тронном зале изначально размещается **спящий / пассивный Лорд Дракула** (`setDormant(true)`).
  - `src/main/java/com/marrybye/neorism/common/entity/vampire/EntityDracula.java` и `DraculaFightManager.java`:
    - Адаптирована логика `DraculaState.PASSIVE` из оригинала: Дракула стоит перед троном без полосы босса и агрессии до момента атаки игроком или заполнения 4 кровавых чаш (`awakenDracula`), после чего мгновенно начинается 3-фазный бой.
    - Расширены границы арены в `DraculaFightManager.tick()` до `(-45, 50, -45)..(45, 110, 45)` и зона поиска чаш; точка обратного портала перенесена на `(0, 65, -31)`.
  - `BlockVelmorraPortal.java`, `CommandVampirism.java`, `WorldProviderVelmorra.java` — точка прибытия игрока выровнена на `(0.5D, 65.0D, -34.0D)` прямо на парадную аллею перед главными воротами замка.

---

## Проверка сборки

- Выполнена полная компиляция и сборка артефактов командой `.\gradlew assemble`: `BUILD SUCCESSFUL`.
- Клиентский запуск пользователем (`./gradlew runClient25`) требуется после финальных правок (`a96634f`, `2ede409`, `6a3c490`) для визуального тестирования нового замка, леса Вельморры, прокрутки дерева умений и 3px рук NPC.
