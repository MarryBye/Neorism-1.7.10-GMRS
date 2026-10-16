# Отчёт: Исправление парсинга Altar Pillar и локализации вкладок креатива

> **Дата:** 16 октября 2026 г.  
> **Статус:** Выполнено (Passed)  
> **Коммит:** `fix(lang): add creative tabs translations and fix altar_pillar variant parsing`

---

## 1. Контекст проблемы

1. **Локализация вкладок креатива (`CreativeTabs`):**
   - Вкладки креатива мода отображались как сырые ключи: `itemGroup.neorism.main` и `itemGroup.neorism.books`.
   - Требовалось провести аудит текущего состояния файлов локализации и исправить несостыковки.
2. **Колонна алтаря (`Altar Pillar`):**
   - Блок продолжал отрисовываться как фиолетово-чёрный куб без текстуры.

---

## 2. Анализ причин по логам игры

1. **Altar Pillar (ошибка парсера GTNHLib):**
   - В логе `fml-client-latest.log` на строке 10185 было зафиксировано исключение:
     ```
     [Client thread/ERROR] [GTNHLib|Models/]: Failed to parse neorism:blockstates/altar_pillar.json
     com.google.gson.JsonParseException: Model variant predicate is missing an equals; expected it to be of the format 'property name=property value': 'inventory'!
         at com.gtnewhorizon.gtnhlib.client.model.state.MonopartState$StateMatch.<init>(MonopartState.java:78)
     ```
   - Загрузчик моделей `GTNHLib` в классе `MonopartState$StateMatch` принимает в блоке вариантов стейта только два вида ключей:
     - Пустые совпадения: `""` или `"normal"` (без знака равенства).
     - Свойства стейта со знаком равенства: `key=value` (например, `type=bone`).
   - Наличие ключа `"inventory"` приводило к `JsonParseException`, из-за чего весь файл `altar_pillar.json` признавался невалидным, и блок откатывался на `MISSING_MODEL`.
2. **Вкладки креатива:**
   - В `ModCreativeTabs.java` вкладки регистрировались как:
     - `new CreativeTabs("neorism.main")` &rarr; Minecraft запрашивает `itemGroup.neorism.main`.
     - `new CreativeTabs("neorism.books")` &rarr; Minecraft запрашивает `itemGroup.neorism.books`.
   - В файлах `.lang` и `.json` присутствовали только старые ключи `itemGroup.vampirism` и `itemGroup.neorism`, а специфичные ключи для подкатегорий `.main` и `.books` отсутствовали.
   - Также отсутствовал перевод нейтральной фракции человека (`faction.neorism.neutral`).

---

## 3. Выполненные изменения

1. **`altar_pillar.json`:**
   - Удалён невалидный ключ `"inventory"`, сохранены валидные ключи пустых стейтов `""` и `"normal"`, а также все типы `type=bone`, `type=gold`, `type=iron`, `type=none`, `type=stone`.
   - Исключение `JsonParseException` полностью устранено.
2. **`ModCreativeTabs.java`:**
   - Переопределён клиентский метод `getTranslatedTabLabel()` для обеих вкладок:
     - Для основной вкладки (`tabMain`): проверяется ключ `itemGroup.neorism.main`, с каскадным фоллбэком на `itemGroup.neorism` и стандартный ванильный механизм.
     - Для вкладки книг (`tabBooks`): проверяется ключ `itemGroup.neorism.books`, с фоллбэком на `itemGroup.vampirism.books`.
3. **Файлы локализации (`.lang` и `.json`):**
   - В `ru_RU.lang` и `ru_ru.json` добавлены:
     - `itemGroup.neorism.main=Вампиризм`
     - `itemGroup.neorism.books=Вампирские книги`
     - `itemGroup.vampirism.main=Вампиризм`
     - `itemGroup.vampirism.books=Вампирские книги`
     - `faction.neorism.neutral=Человек`
     - `faction.neorism.neutral.singular=Человек`
     - `faction.neorism.neutral.plural=Люди`
   - В `en_US.lang` и `en_us.json` добавлены:
     - `itemGroup.neorism.main=Vampirism`
     - `itemGroup.neorism.books=Vampire Books`
     - `itemGroup.vampirism.main=Vampirism`
     - `itemGroup.vampirism.books=Vampire Books`
     - `faction.neorism.neutral=Human`
     - `faction.neorism.neutral.singular=Human`
     - `faction.neorism.neutral.plural=Humans`
   - Добавлены соответствующие ключи `itemGroup.neorism.main` и `itemGroup.neorism.books` во все 18 остальных языковых файлов `.lang` (`de_DE`, `fr_FR`, `es_ES`, `zh_CN` и др.).
4. **Текстуры блоков:**
   - Добавлена ванильная текстура `stone.png` в `assets/minecraft/textures/blocks/stone.png` для абсолютной надёжности парсинга частицы камня у моделей алтарей.

---

## 4. Верификация сборки

- Команда: `.\gradlew assemble`
- Результат: `BUILD SUCCESSFUL in 19s`
- Ошибки компиляции/ресурсов: Отсутствуют.
