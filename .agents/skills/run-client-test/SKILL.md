---
name: run-client-test
description: Rules for building the mod, keeping run/client/mods clean, waiting for user client launch, and analyzing runtime logs on request.
---

# Build & Log Inspection

1. **Сборка:** После изменений компилировать мод через `.\gradlew assemble`.
2. **Чистота `run/client/mods/`:** Запрещено копировать jar-файлы мода или Gradle-зависимостей в `run/client/mods/` (RFG монтирует их автоматически, дубли вызовут краш FML).
3. **Запуск клиента:** Клиент запускает ТОЛЬКО пользователь. Агенту запрещено запускать игру.
4. **Анализ логов:** По команде пользователя (*"посмотри логи"*) проверить `run/client/logs/fml-client-latest.log`, `latest.log` и `run/client/crash-reports/` на ошибки и `[DEBUG:*]` записи, после чего сразу исправить найденные баги.
