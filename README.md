# Neorism: Become a Vampire (Minecraft 1.7.10)

[![Minecraft 1.7.10](https://img.shields.io/badge/Minecraft-1.7.10-blue.svg)](https://minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-10.13.4.1614-orange.svg)](https://files.minecraftforge.net/)
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL_v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)
[![Built with GTNH](https://img.shields.io/badge/GTNH-RetroFuturaGradle-purple.svg)](https://github.com/GTNewHorizons/RetroFuturaGradle)

**Neorism** — это амбициозный бэкпорт современных версий легендарного мода **[Vampirism: Become a Vampire](https://github.com/TeamLapen/Vampirism)** на платформу **Minecraft 1.7.10** в экосистеме **GT New Horizons (GTNH)**.

---

## ⚠️ ВАЖНЫЙ ДИСКЛЕЙМЕР / IMPORTANT DISCLAIMER

1. **Интеллектуальная собственность и авторство:**
   - Оригинальный мод: **[Vampirism: Become a Vampire](https://github.com/TeamLapen/Vampirism)**.
   - Автор и создатель оригинала: **[maxanier](https://github.com/maxanier)** и команда **[TeamLapen](https://github.com/TeamLapen)**.
   - Официальный сайт: [vampirism.co](https://vampirism.co)
   - Страница на CurseForge: [Vampirism CurseForge](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire)
   - Мы выражаем **огромную благодарность и уважение** автору оригинального мода maxanier и TeamLapen за колоссальный труд, продуманный геймдизайн и открытость для моддинг-сообщества.

2. **Ошибки и поддержка (НЕ ПИСАТЬ ОРИГИНАЛЬНОМУ АВТОРУ!):**
   - **Neorism** является независимым неофициальным форком/бэкпортом.
   - Любые баги, вылеты, несовместимости и ошибки в Neorism являются **исключительно** следствием разработки бэкпорта авторами **MarryBye + Gemini AI**.
   - **КАТЕГОРИЧЕСКИ ЗАПРЕЩЕНО** отправлять багрепорты, крашлоги или задавать вопросы по Neorism автору оригинального мода (maxanier или TeamLapen)! Они не имеют отношения к разработке данного бэкпорта и не оказывают по нему никакой поддержки. Все вопросы и сообщения об ошибках должны направляться только разработчикам Neorism.

---

## 👥 Авторы проекта

* **Оригинальный мод:** `maxanier` (TeamLapen) и контрибьюторы Vampirism.
* **Авторы бэкпорта на 1.7.10:** `MarryBye + Gemini AI`.

---

## 🎯 Цели и особенности проекта

- **Полный перенос современных механик:**
  - Система фракций: Вампиры (Vampires) и Охотники на вампиров (Vampire Hunters).
  - Уровни и прокачка навыков (Skill Trees).
  - Механика крови, насыщения, солнца и слабостей.
  - Ритуалы (на алтарях вдохновения и очищения).
  - Инфузия крови (Blood Infusion) и переработка алхимии.
  - Мобы, боссы и миньоны из актуальных версий мода.
  - Оружие, броня, колчаны, арбалеты, чесночные гранаты и аксессуары.
- **Современный стек GTNH:**
  - Построен на базе **RetroFuturaGradle (RFG)** и **gtnhgradle 2.0.33+**.
  - Поддержка запуска на современных Java (Java 17, Java 21, Java 25) с использованием оптимизаций LWJGL3ify и Hodgepodge.
  - Использование UniMixins при необходимости интеграции глубоких хуков в движок 1.7.10.

---

## 🧩 Совместимость с ключевыми модами GTNH

В проекте Neorism изначально заложена глубокая интеграция и поддержка актуальных версий ключевых модов экосистемы GTNH (с прицелом на обратную и будущую совместимость с их последующими релизами):

| Мод | Назначение и интеграция с Neorism | Репозиторий |
| :--- | :--- | :--- |
| **Et Futurum Requiem** | Бэкпорт современных предметов, блоков, арбалетов и сущностей. Совместимость со стрелковым оружием и эффектами. | [Et-Futurum-Requiem](https://github.com/GTNewHorizons/Et-Futurum-Requiem) |
| **GTNHLib** | Библиотека математики, рендеринга и работы с частицами для эффектов крови и ритуалов. | [GTNHLib](https://github.com/GTNewHorizons/GTNHLib) |
| **Hodgepodge** | Критические исправления движка 1.7.10, расчёт световых циклов дня/ночи и стабильность под современными JVM. | [Hodgepodge](https://github.com/GTNewHorizons/Hodgepodge) |
| **Angelica** | Совместимость с современным пайплайном рендеринга Sodium / Iris / OptiFine для 1.7.10 (шейдеры, оверлеи вампирского зрения). | [Angelica](https://github.com/GTNewHorizons/Angelica) |
| **VillageNames** | Совместимость со структурой деревень, генерацией поселений вампиров и охотников, именами NPC. | [VillageNames](https://github.com/GTNewHorizons/VillageNames) |
| **lwjgl3ify** | Полная поддержка современного стека LWJGL 3, ввода с клавиатуры/мыши и запуска на Java 17-25. | [lwjgl3ify](https://github.com/GTNewHorizons/lwjgl3ify) |
| **UniMixins** | Универсальный загрузчик миксинов (SpongePowered Mixin) для модификации логики сущностей и рендеринга. | [UniMixins](https://github.com/LegacyModdingMC/UniMixins) |

Все зависимости подключены напрямую через **GTNH Maven Nexus** без необходимости локальных jar-файлов в папке `libs/`.

---

## 📜 Лицензирование / Licenses

Проект Neorism уважает и сохраняет оригинальную структуру лицензирования Vampirism:
- **Исходный код:** [GNU Lesser General Public License v3.0 (LGPL-3.0)](licenses/CODE_LICENSE). Все модификации бэкпорта распространяются под той же лицензией.
- **Текстуры и 3D-модели:** [Vampirism Asset License v1.0](licenses/ASSET_LICENSE). Разрешено некоммерческое использование для форков и аддонов Vampirism при указании авторства.
- **Лор и тексты:** [Vampirism Narrative Content License v1.0 (VNCL)](licenses/NARRATIVE_LICENSE).
- **Звуки:** Сохраняются лицензии из оригинального репозитория Vampirism.
- **Коммерческое использование:** Категорически запрещено. Проект полностью бесплатен.

Подробности см. в файле [LICENSE](LICENSE).

---

## 🛠️ Сборка проекта

Для сборки требуется JDK 25 (рекомендуется **Eclipse Temurin 25**):

```bash
# Проверка версии и запуск задач
./gradlew tasks

# Сборка мода
./gradlew build

# Запуск клиента в среде разработки (на Java 25)
./gradlew runClient25
```
