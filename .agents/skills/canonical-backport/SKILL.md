---
name: canonical-backport
description: Strict rule to always locate and adapt original TeamLapen Vampirism & FactionAPI source code and assets from reference/vampirism/ instead of inventing code, models, or textures.
---

# Canonical Backport

1. **Запрет отсебятины в коде:** Никогда не писать код, формулы, константы или древа навыков по памяти/представлению.
2. **Запрет своих текстур и моделей:** НЕ создавать собственные текстуры и НЕ писать/генерировать свои модели — все ассеты уже есть в `src/main/resources/assets/neorism/` и `reference/vampirism/`. Если кастомная модель/текстура действительно необходима, агент обязан сначала предложить это как идею пользователю и ждать согласия («да» / «нет»).
3. **Поиск оригинала:** Перед реализацией или аудитом найти исходник в `reference/vampirism/` (`de.teamlapen.vampirism` или `de.teamlapen.faction`) и прочитать его целиком.
4. **100% соответствие:** Переносить оригинальные алгоритмы, проверки, константы, ID и ключи локализации один в один с поправкой на API Forge 1.7.10 (см. `docs/MODEL_TEXTURE_PORTING_GUIDE.md`).
5. **Аудит:** Построчно сверять код Neorism с оригиналом и устранять любые упрощения или расхождения.
