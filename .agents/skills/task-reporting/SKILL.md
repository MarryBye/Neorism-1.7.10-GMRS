---
name: task-reporting
description: On-demand report generation based on git commit history since the last report. Never run automatically.
---

# On-Demand Reporting

1. **Только по запросу:** Никогда не создавать репорты автоматически после задач. Создавать ТОЛЬКО по явной просьбе пользователя.
2. **Источник данных:** Собирать информацию по истории git-коммитов со времени последнего репорта в `docs/reports/` (или с первого коммита репозитория, если прошлых репортов нет).
3. **Формат файла:** Сохранять в `docs/reports/DD-MM-YYYY-HH-MM-TITLE.md` (текущие дата и время, `TITLE` — краткая тема заглавными буквами через дефис) и коммитить (`docs(report): ...`).
