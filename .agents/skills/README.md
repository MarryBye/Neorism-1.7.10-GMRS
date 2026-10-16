# Neorism Development Skills Directory

В этой директории размещаются специализированные скиллы (skills) для агента Antigravity / Gemini AI.

## Структура скилла

Каждый скилл представляет собой отдельную подпапку с файлом `SKILL.md` и опциональными вспомогательными скриптами:

```
.agents/skills/
├── port-vampirism-feature/
│   └── SKILL.md
├── convert-modern-model/
│   └── SKILL.md
└── implement-entity/
    └── SKILL.md
```

## Формат `SKILL.md`

```markdown
---
name: skill-name
description: Краткое описание того, когда и как агент должен активировать этот скилл.
---

# Заголовок скилла
Инструкции, чеклисты, примеры кода и паттерны реализации.
```

## Активные скиллы проекта:
1. `git-workflow`: Правила Conventional Commits, локальных коммитов и публикация релизов на GitHub только по прямой команде.
2. `debug-logging`: Тотальное логгирование боя, ИИ, прокачки, торговли, спавнов и ритуалов через `ModLogger` и `Config.debugLogging`.
3. `run-client-test`: Сборка, синхронизация jar в `run/client/mods/`, запуск `runClient` по просьбе пользователя, мониторинг логов и устранение ошибок.
4. `task-reporting`: Обязательное ведение отчётов о завершении задач в `docs/reports/` и обновление чекбоксов в `docs/ROADMAP.md`.
5. `port-vampirism-feature`: Стандартный пошаговый процесс портирования механик, предметов и возможностей из современных версий Vampirism в Neorism 1.7.10.


## Планируемые скиллы для создания:
1. `port-modern-entity`: Алгоритм портирования сущности (моба, босса, миньона) с современным ИИ (Goal/Brain) на `EntityAIBase` Forge 1.7.10.
2. `port-ritual-multiblock`: Процедура создания ритуалов и многоблочных структур алтарей.
3. `port-gui-and-hud`: Создание оверлеев и GUI контейнеров для 1.7.10.
4. `gtnh-nei-integration`: Добавление рецептов и интеграция с NEI (Not Enough Items) для GTNH.

