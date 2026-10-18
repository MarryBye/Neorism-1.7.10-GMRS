---
name: canonical-backport
description: Strict rule to always locate and adapt original TeamLapen Vampirism & FactionAPI source code from reference/vampirism/ instead of inventing implementations.
---

# Canonical Backport

1. **Запрет отсебятины:** Никогда не писать код, формулы, константы или древа навыков по памяти/представлению.
2. **Поиск оригинала:** Перед реализацией или аудитом найти исходник в `reference/vampirism/` (`de.teamlapen.vampirism` или `de.teamlapen.faction`) и прочитать его целиком.
3. **100% соответствие:** Переносить оригинальные алгоритмы, проверки, константы, ID, ключи локализации и ассеты один в один с поправкой на API Forge 1.7.10 (см. `docs/MODEL_TEXTURE_PORTING_GUIDE.md` для моделей/текстур).
4. **Аудит:** При запросе аудита построчно сверять код Neorism с оригиналом и устранять любые упрощения или расхождения.
