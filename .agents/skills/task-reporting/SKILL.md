---
name: task-reporting
description: Mandatory workflow for generating task completion reports and updating the roadmap after completing features.
---

# Task Completion & Progress Reporting Workflow

This skill is mandatory whenever completing any task, subtask, or milestone from the Master Roadmap (`docs/ROADMAP.md`).

## 1. Obligation
After completing any feature, bugfix, or subsystem implementation:
1. **Never close a task without updating the Roadmap:** Open `docs/ROADMAP.md` and check the corresponding `[x]` boxes for the completed subtasks.
2. **Generate a Structured Completion Report:** Create a markdown report in `docs/reports/` using the standardized naming format:
   `docs/reports/<phase-id>-<subtask-slug>.md` (e.g. `docs/reports/phase-01-core-registries.md`).

---

## 2. Report Structure Template
Every report must strictly follow this template:

```markdown
# Milestone Report: [Phase X.Y] - [Subsystem Name]

> **Date:** YYYY-MM-DD  
> **Status:** COMPLETED  
> **Git Commit:** [commit hash]  

---

## 1. Scope & Objectives
- Summary of what was implemented.
- Modern Vampirism source classes referenced (from `reference/vampirism/`).

## 2. Technical Implementation Details
- **Classes Created/Modified:** Absolute file paths and responsibilities.
- **Formulas & Algorithms:** Specific mathematical formulas, level gates, or balance values ported from the original mod.
- **Forge 1.7.10 Adaptations:** How modern constructs (Capabilities, Tags, Registries) were mapped to 1.7.10 (IEEP, ModRegistries, Packets).

## 3. Diagnostic Logging Integration
- Log categories utilized (`[DEBUG:COMBAT]`, `[DEBUG:AI]`, `[DEBUG:LEVEL]`, etc.).
- Example log outputs observed during execution.

## 4. Verification & Testing
- Build verification (`.\gradlew assemble`).
- Jar synchronization to `run/client/mods/`.
- Test client observations (if run).

## 5. Next Steps
- The immediate next task on the roadmap.
```

---

## 3. Git Synchronization
After writing the report and updating `docs/ROADMAP.md`:
1. Stage the report and roadmap:
   ```bash
   git add docs/
   ```
2. Commit with conventional commit format:
   ```bash
   git commit -m "docs(report): add completion report for phase X.Y <task name>"
   ```
