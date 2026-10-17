# Отчёт: Комплексный аудит и каноническое исправление всех существующих механик Neorism

> **Дата:** 2026-10-17  
> **Статус:** COMPLETED  
> **Фаза:** Комплексный аудит существующих механик (Phase 1–Phase 9)  
> **Сверка с оригиналом:** `reference/vampirism/` (TeamLapen / maxanier)

---

## 1. Цели аудита

Проведён полный аудит всех существующих механик Neorism 1.7.10 без добавления механик новых фаз. Все ранее созданные предметы, блоки, алтари, запреты и формулы приведены в 100% соответствие оригинальному коду `de.teamlapen.vampirism` и `de.teamlapen.faction`.

---

## 2. Использованные эталонные классы оригинала

- `de.teamlapen.vampirism.common.world.blocks.PedestalBlock` & `PedestalBlockEntity`
- `de.teamlapen.vampirism.common.world.blocks.AltarPillarBlock` & `AltarTipBlock`
- `de.teamlapen.vampirism.common.world.blocks.BloodContainerBlock` & `BloodContainerBlockEntity`
- `de.teamlapen.vampirism.common.world.blocks.CursedEarthBlock`
- `de.teamlapen.vampirism.common.items.HolyWaterBottleItem` & `HolyWaterSplashBottleEntity`
- `de.teamlapen.vampirism.common.items.GarlicItem` & `GarlicBreadItem`
- `de.teamlapen.vampirism.common.items.VampireFangItem`
- `de.teamlapen.vampirism.common.items.BloodBottleItem`
- `de.teamlapen.vampirism.common.items.VampireSwordItem`
- `de.teamlapen.vampirism.common.items.HeartSeekerItem` & `HeartStrikerItem`
- `de.teamlapen.vampirism.common.items.CrossbowItem`
- `de.teamlapen.vampirism.common.items.HunterAxeItem` & `StakeItem`
- `de.teamlapen.vampirism.common.items.IFactionExclusiveItem` & `IFactionLevelItem`
- `de.teamlapen.vampirism.common.world.blocks.AltarInspirationBlock` & `AltarInspirationBlockEntity`
- `de.teamlapen.vampirism.common.world.blocks.CoffinBlock` & `CoffinBlockEntity`

---

## 3. Детальные изменения и исправленные подсистемы

### 3.1. Кровавый пьедестал (`BlockBloodPedestal` & `TileEntityBloodPedestal`)
- **Ограничение предметов:** Пьедестал теперь принимает **исключительно** предметы, реализующие канонический интерфейс `IBloodChargeable` (`HeartSeeker`, `HeartStriker` и наследники `ItemVampireSword`). Случайные предметы положить нельзя.
- **Именование оружия:** При извлечении полностью заряженного меча вызывается метод `sword.tryName(stack, player)`, присваивающий мечу каноническое случайное вампирское имя из NBT-пула.
- **Поддержка компаратора:** Реализованы `hasComparatorInputOverride` и `getComparatorInputOverride`, возвращающие прогресс зарядки меча: `(int)(chargePercentage * 10)`.

### 3.2. Колонны и наконечники алтаря (`BlockAltarPillar` & `BlockAltarTip`)
- **Сигналы компаратора по типам наполнения:**
  - Stone (Камень) $\rightarrow$ сигнал 1.
  - Bone / Quartz (Кварц/Кость) $\rightarrow$ сигнал 2.
  - Iron (Железо) $\rightarrow$ сигнал 4.
  - Gold (Золото) $\rightarrow$ сигнал 5.
- **Механика слизи и воска (Gluing / Waxing):**
  - При клике сгустком слизи (`Items.slime_ball`) по колонне с блоком выставляется бит закрепления (`meta | 8`). Закреплённый блок нельзя случайно вынуть пустой рукой.
  - При клике топором (`ItemAxe`) фиксация снимается (`meta & 7`), позволяя извлечь блок.
- **Валидация выживания наконечника (`BlockAltarTip`):**
  - Добавлены проверки `canPlaceBlockAt`, `canBlockStay` и `onNeighborBlockChange`.
  - Наконечник может стоять только на колонне алтаря или твердой грани блока. При разрушении опоры наконечник корректно дропается в мир.

### 3.3. Резервуар крови (`BlockBloodContainer` & `TileEntityBloodContainer`)
- **Сохранение жидкости при разрушении и установке:** При ломании блока резервуара в выпадающий `ItemStack` записывается NBT-тег `FluidAmount`. При установке сохранённая кровь восстанавливается в `TileEntity`.
- **Компараторный сигнал:** Реализован вывод сигнала компаратора по формуле оригинала:
  - $0$, если резервуар пуст.
  - $1 + \lfloor \text{ratio} \times 14.0 \rfloor$, если внутри есть кровь (сигнал от 1 до 15).

### 3.4. Проклятая земля и святая вода (`BlockCursedEarth`, `ItemHolyWater`, `EntityHolyWaterBottle`)
- **Очищение проклятой земли:**
  - При клике ПКМ пузырьком `ItemHolyWater` проклятая земля превращается в обычную землю (`Blocks.dirt`). Бутылочка тратится с шансом в зависимости от тира ($1/25$ для обычной, $1/50$ для очищенной, $1/100$ для абсолютной).
  - При взрыве метательного флакона святой воды (`EntityHolyWaterBottle`) в радиусе поражения вся проклятая земля автоматически очищается до обычной земли.

### 3.5. Чесночный хлеб и запреты на ношение предметов (`ItemGarlicBread`, `Helper`)
- **Запрет на чесночный хлеб для вампиров:** В `ItemGarlicBread.onUpdate` подключен `Helper.handleHeldNonVampireItem(stack, entity, isSelected)`. Вампиры не могут держать чесночный хлеб в руках: получают чесночную слабость и роняют предмет.
- **Единые сообщения об ограничениях:** Для предметов охотников и вампиров (`ItemCrossbow`, мечи, броня) вывод сообщений унифицирован под `message.vampirism.restriction.faction.<faction>`.

### 3.6. Клык вампира и инфицирование (`ItemVampireFang`)
- **Каноническая логика заражения:**
  - Проверка возможности стать вампиром через `Helper.canBecomeVampire(player)`.
  - Наложение эффектов `Sanguinare Vampiris` и `Potion.poison (60 ticks)`.
  - Воспроизведение звуков и вывод канонических сообщений (`message.vampirism.already_vampire`, `message.vampirism.immune_to_sanguinare`).

### 3.7. Стакание бутылочек с кровью (`ItemBloodBottle`)
- **Размер стака:** Реализован `getItemStackLimit(ItemStack stack)`:
  - Полные бутылочки (9/9) и пустые (0/9) стакаются до **16**.
  - Частично заполненные бутылочки имеют лимит стака **1**, исключая потерю данных жидкости.

---

## 4. Верификация и сборка

1. Проведена полная компиляция и сборка проекта:
   ```bash
   .\gradlew assemble
   ```
   **Результат:** `BUILD SUCCESSFUL` (0 ошибок, 0 предупреждений).
2. Все языковые ключи добавлены в `en_US.lang` и `ru_RU.lang`.

---

## 5. Следующие шаги
- Передать готовность пользователю для клиентского тестирования через `.\gradlew runClient25`.
