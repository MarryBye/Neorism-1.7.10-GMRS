# Отчёт о реализации: FactionAPI DAG Древо Навыков, Радиальное Меню Действий и Меню Фракции

> **Дата:** 17 октября 2026  
> **Фаза:** 6.5 (ROADMAP.md)  
> **Статус сборки:** `BUILD SUCCESSFUL` (19s, Gradle RFG 1.7.10)  
> **Оригинал:** `reference/vampirism/src/main/java/de/teamlapen/faction/` и `de/teamlapen/vampirism/`

---

## 1. Обзор выполненной работы

В рамках согласованного плана приведения Neorism в точное соответствие 1:1 с архитектурой TeamLapen (*Vampirism* + *FactionAPI*), подсистема навыков и способностей была полностью перестроена:

1. **DAG Граф навыков (`ISkillSegment` & `ISkillTree`)**:
   - Реализована структура направленного ациклического графа (Directed Acyclic Graph), в которой узлы (`SkillSegment`) могут иметь несколько родителей и взаимоисключающие ветви (`lockingSegments`).
   - Портирован движок автоматического позиционирования `SkillTreeLayout` на основе изотонической регрессии (isotonic regression) и балансировки ветвей, рассчитывающий точные экранные координаты `(x, y)` для каждого узла.
   - Зарегистрированы канонические деревья навыков в `SkillRegistry`:
     - `vampire_level` (ветви Ярости, Укусов/Ловкости, Маскировки/Магии)
     - `vampire_lord` (усиление миньонов, сбор крови, скорость лорда)
     - `hunter_level` (ветви Алхимии/Чеснока/Распятия, Зельеварения, Оружейных технологий)
     - `hunter_lord` (усиление охотничьих миньонов, арбалеты, сбор трофеев)

2. **Радиальное Меню Действий (Action Rose / Роза Способностей)**:
   - Создана система `GuiRadialMenu` и `SelectActionRadialScreen`, активируемая при удержании клавиши `[R]`.
   - Отрисовка круговых секторов через OpenGL Quads и `Tessellator.instance` (без использования современных конвейеров, 100% совместимо с шейдерами Angelica).
   - Определение сектора по полярным координатам мыши (`Math.atan2(dy, dx)` и `Math.sqrt(dx^2 + dy^2)`).
   - Плавная анимация раскрытия (ease-out cubic), иконки действий 16x16, динамическая подсветка активных состояний (зелёное/золотое свечение) и круговой прогресс перезарядки (cooldown sweep).
   - Активация выбранного действия при отпускании клавиши `[R]` или по клику мыши с отправкой сетевого пакета `ToggleActionPacket`.

3. **Интерактивный экран навыков (`SkillsScreen`)**:
   - Отрисовка оригинального окна `textures/gui/skills/window.png` (252x219).
   - Вкладки сверху для каждого доступного древа с иконками предметов и всплывающими подсказками.
   - Холст с перемещением (drag-to-pan) и отсечением через `GL11.glScissor` (адаптировано под любой GUI Scale).
   - Ортогональные соединительные линии (`SkillSegmentConnection`) со статусной подсветкой (зелёный — изучено, жёлтый/белый — доступно, тёмный — заблокировано).
   - Удержание клика: удержание ЛКМ в течение 8 тиков для изучения, удержание ПКМ в течение 16 тиков для сброса отдельного навыка с отображением кругового прогресс-кольца.
   - Подробные всплывающие подсказки с названием, стоимостью в очках, требованиями и описанием.

4. **Дашборд игрока / Меню Фракции (`FactionMenuScreen` по клавише `[P]`)**:
   - Окно интерфейса `faction_menu.png` (234x205).
   - 3D-модель персонажа, поворачивающаяся за курсором мыши.
   - Отображение фракции, ранга и уровня.
   - Кнопка перехода в древо навыков (`widget/skills.png`), открывающая `SkillsScreen`.

---

## 2. Созданные и изменённые файлы

### Новые классы (Core & GUI):
- [`Skill.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/skills/Skill.java) — модель навыка.
- [`SkillSegment.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/skills/SkillSegment.java) — узел графа с родителями и блокировками.
- [`SkillTree.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/skills/SkillTree.java) — контейнер древа навыков.
- [`SkillTreeGraph.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/skills/SkillTreeGraph.java) — построение DAG, расчёт глубины узлов.
- [`SkillTreeLayout.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/skills/SkillTreeLayout.java) — движок расчёта координат узлов.
- [`SkillRegistry.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/skills/SkillRegistry.java) — реестр всех деревьев Vampirism.
- [`IRadialMenuSlot.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/client/gui/radial/IRadialMenuSlot.java) — интерфейс слота радиального меню.
- [`RadialMenuSlot.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/client/gui/radial/RadialMenuSlot.java) — реализация слота с действием и текстурой.
- [`RadialMenu.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/client/gui/radial/RadialMenu.java) — логика радиального меню.
- [`GuiRadialMenu.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/client/gui/radial/GuiRadialMenu.java) — базовый OpenGL GUI экран кругового меню.
- [`SelectActionRadialScreen.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/client/gui/radial/SelectActionRadialScreen.java) — экран выбора способностей на `[R]`.
- [`SkillSegmentConnection.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/client/gui/skills/SkillSegmentConnection.java) — отрисовка соединительных линий между узлами.
- [`SkillSegmentComponent.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/client/gui/skills/SkillSegmentComponent.java) — компонент узла на холсте.
- [`SkillsTabComponent.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/client/gui/skills/SkillsTabComponent.java) — контроллер вкладки и холста.
- [`SkillsScreen.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/client/gui/skills/SkillsScreen.java) — полнофункциональный экран древа навыков на `[K]`.
- [`FactionMenuScreen.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/client/gui/FactionMenuScreen.java) — экран меню фракции на `[P]`.
- [`ToggleActionPacket.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/network/packets/ToggleActionPacket.java) — пакет вызова способности.
- [`ForgetSkillPacket.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/network/packets/ForgetSkillPacket.java) — пакет сброса отдельного навыка (ПКМ).
- [`ResetAllSkillsPacket.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/network/packets/ResetAllSkillsPacket.java) — пакет полного сброса навыков.

### Обновлённые классы:
- [`ISkill.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/de/teamlapen/faction/api/factions/skills/ISkill.java) — добавлены методы связи со способностями (`getAction()`).
- [`ISkillSegment.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/de/teamlapen/faction/api/factions/skills/ISkillSegment.java) — расширен методами для работы с графом родителей и детей.
- [`ISkillTree.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/de/teamlapen/faction/api/factions/skills/ISkillTree.java) — добавлены методы получения корневых и всех узлов.
- [`KeyBindings.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/client/input/KeyBindings.java) — подключены `keyAction1` (`[R]`), `keyFactionMenu` (`[P]`), `keySkills` (`[K]`).
- [`SkillHandler.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/skills/SkillHandler.java) — проверка разблокировки по графу и взаимоисключениям, метод `forgetSkill`.
- [`FactionPlayerHandler.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/faction/FactionPlayerHandler.java) — добавлен `getCurrentFaction()`.
- [`PacketHandler.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/network/PacketHandler.java) — зарегистрированы новые пакеты.
- [`ModRegistries.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/core/ModRegistries.java) — инициализация `SkillRegistry`.
- [`ModLogger.java`](file:///C:/Users/vluki/Desktop/Neorism-1.7.10-GMRS/src/main/java/com/marrybye/neorism/common/util/ModLogger.java) — добавлен метод `info(...)`.
- Ассеты текстур интерфейса: распакованы оригинальные текстуры `window.png`, `faction_menu.png`, спрайты узлов `skills_screen/*`, кнопок `widget/*` и иконки действий `textures/actions/*`.

---

## 3. Статус компиляции и сборки

Команда: `.\gradlew assemble`  
Результат: **BUILD SUCCESSFUL in 19s** (0 ошибок компиляции).
Сформирован финальный jar-файл мода в `build/libs/`.
