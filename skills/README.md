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

## Планируемые скиллы для создания:
1. `port-vampirism-feature`: Стандартный пошаговый процесс портирования механики из современных версий Vampirism в Neorism 1.7.10.
2. `port-modern-entity`: Алгоритм портирования сущности (моба, босса, миньона) с современным ИИ (Goal/Brain) на `EntityAIBase` Forge 1.7.10.
3. `port-ritual-multiblock`: Процедура создания ритуалов и многоблочных структур алтарей.
4. `port-gui-and-hud`: Создание оверлеев и GUI контейнеров для 1.7.10.
5. `gtnh-nei-integration`: Добавление рецептов и интеграция с NEI (Not Enough Items) для GTNH.
