---
name: task-reporting
description: On-demand write-only report generation based on git commit history since the last report. Never run automatically and never read or edit existing reports.
---

# On-Demand Reporting (Write-Only)

1. **Только по запросу:** Никогда не создавать репорты автоматически после задач. Создавать ТОЛЬКО по явной просьбе пользователя.
2. **Write-Only (не читать и не менять):** После создания репорта агенту ЗАПРЕЩЕНО перечитывать его или редактировать. Старые репорты также не нужно открывать и читать — для определения точки отсчёта смотреть только дату/имя последнего файла в `docs/reports/` или историю `git log`.
3. **Источник данных:** Собирать информацию исключительно по истории git-коммитов со времени последнего репорта (или с начала истории, если репортов нет).
4. **Формат файла:** Сохранять в `docs/reports/DD-MM-YYYY-HH-MM-TITLE.md` и коммитить (`docs(report): ...`).
