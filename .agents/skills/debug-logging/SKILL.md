---
name: debug-logging
description: Mandate to log all observable gameplay events via ModLogger gated by Config debug flags.
---

# Debug Logging

1. **Тотальное логгирование:** Все события (урон, ИИ, прокачка, спавн, торговля, ритуалы) логгировать через `ModLogger` (`combat`, `ai`, `leveling`, `spawn`, `trade`, `ritual`, `debug`) с указанием сущностей, координат и численных значений.
2. **Конфиг-гейтинг:** Все вызовы обязаны проверяться флагами `Config.debugLogging` и соответствующими подкатегориями для нулевого оверхеда в релизе.
