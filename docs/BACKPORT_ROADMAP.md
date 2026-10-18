# Neorism 1.7.10 — Backport Roadmap

Актуальный статус портирования *Vampirism: Become a Vampire* & *FactionAPI* (`reference/vampirism/`) на Forge 1.7.10.

## Выполненные этапы
- [x] **Phase 0: Project Setup & Legal** — Gradle 9 / RFG / Java 25 + Jabel, пакет `com.marrybye.neorism`, лицензии и атрибуция `maxanier` / `TeamLapen`.
- [x] **Phase 1: Core Registries & Network** — `ModItems`, `ModBlocks`, `ModEntities`, `ModPotions`, `ModAttributes`, `ModSounds`, `ModDamageSource`, `PacketHandler` (`SimpleNetworkWrapper`), 3 вкладки (`tabVampirism`, `tabHunter`, `tabVampire`).
- [x] **Phase 2: Faction Framework & IEEP** — Интеграция `de.teamlapen.faction.api`, `FactionPlayerHandler` (`IExtendedEntityProperties`), синхронизация фракций (`VAMPIRE`, `HUNTER`, `NEUTRAL`), маскировка (`IDisguise`).
- [x] **Phase 3: Blood & Feeding** — `BloodStats` (замена голода, истощение 6.0 в биоме / 4.0 вне биома), `NO_BLOOD`, укусы сущностей (`IBiteableEntity`), бутылки и контейнеры крови, формула регенерации.
- [x] **Phase 4: Vampire Progression, Sun & Garlic** — Уровни 1–14 (`VampireLeveling`), расчёт солнца (`SundamageRegistry`), `SUNSCREEN`, `GarlicHandler` по чанкам, инфекция `Sanguinare Vampiris`.
- [x] **Phase 5: Hunter Progression & Basics** — `HunterPlayer`, уровни 1–14 (`HunterLeveling`), `HunterIntel` (0–9), святая вода, колья (мгновенное убийство со спины), диффузоры чеснока, распятия.
- [x] **Phase 6: Actions & Skill Trees** — `ActionHandler`, древа навыков вампиров и охотников (`ISkillTree` DAG), `SkillsScreen`, круговое меню способностей (`SelectActionRadialScreen` на `[R]`), `FactionMenuScreen` на `[P]`.
- [x] **Phase 7: Arsenal, Weapons & Armors** — Полный арсенал оружия, 6 арбалетов, 7 болтов, колчаны, 25 оружейных масел, плащи, броня охотников/вампиров, зонты, книги вампиров.
- [x] **Phase 8: Blocks, Altars & Models** — Нормализация моделей/текстур под GTNHLib (`pack_format: 4`), Алтарь Вдохновения, Алтарь Инфузии (мультиблок), 16-цветные гробы (TESR), палатки охотников, тотемы, проклятая земля, тёмная ель и каменные блоки.
- [x] **Phase 9: Entities, Mobs & Bosses** — ИИ (`EntityAISunFlee`, `EntityAIBiteLiving`, `EntityAICrossbowAttack`), вампиры, бароны, охотники, тренеры, обращённые мобы, Дракула и Мать Вампиров.
- [x] **Phase 10: Lordship & Minions** — Титулы лордов (1–5), миньоны вампиров и охотников, задачи (`Follow`, `Defend`, `CollectBlood`, `GatherLoot`), GUI управления и радиальное меню миньонов (`[M]`).
- [x] **Phase 11: Worldgen, Biome & Structures** — Вампирский лес (`BiomeGenVampireForest`), подземные склепы, лагеря и аванпосты охотников, руины алтарей, замок Дракулы.
- [x] **Phase 12: Client HUD, GUI & Shaders** — Оверлеи крови, солнца, ярости и кулдаунов, редактор внешности (`VampirePlayerAppearanceScreen`), `BloodVision` и ночное зрение с поддержкой `Angelica`.
- [x] **Phase 13: Villages & Territory Control** — Захват деревень (`FactionVillage`), мультиблок тотема, рейды, знамена и фракционная торговля.
- [x] **Phase 14: GTNH Stack Integrations** — Совместимость с `Et-Futurum-Requiem`, `Combat Backport`, `Villager Trades Backport`, `VillageNames`, `Hodgepodge`, `GTNHLib`, `NEI`.
- [x] **Phase 15: Client Testing Audit & Polish** — Исправление локализации, 64x64 скинов (`ModelBiped64`), рендера 2D/3D предметов, геометрии гробов, палаток и тотемов, 8 каноничных кейбиндингов.

## Текущий этап (Active)
- [ ] **Phase 16: Live Gameplay Verification & Canonical Parity Fixes**
  - [ ] Проверка логов и исправление багов по результатам пользовательских тестов (`runClient25`).
  - [ ] Точечный аудит и доводка механик/баланса до 100% соответствия `reference/vampirism/`.
