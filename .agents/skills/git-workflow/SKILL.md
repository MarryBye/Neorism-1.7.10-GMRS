---
name: git-workflow
description: Standard operating procedure for Git commits, local-only staging, and GitHub release creation upon explicit user trigger.
---

# Git Workflow & Release Management

Follow these instructions whenever committing changes, managing branches, or publishing releases for Neorism.

## 1. Conventional Commits (Mandatory)
Every git commit must follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>(<scope>): <short description in imperative present tense>

[optional body explaining rationale and non-obvious details]
```

### Allowed Types:
- `feat`: New feature or mechanic (e.g. `feat(vampire): implement blood drinking from entities`).
- `fix`: Bug fix (e.g. `fix(render): correct coffin TESR bounding box and rotation`).
- `refactor`: Code refactoring without changing observable behavior (e.g. `refactor(faction): extract faction capabilities to separate classes`).
- `perf`: Performance optimization (e.g. `perf(blood): cache sun exposure calculation`).
- `docs`: Documentation, agent rules, skills, README (e.g. `docs(skills): add debug logging instructions`).
- `chore`: Build files, dependencies, Gradle scripts, assets organization (e.g. `chore(deps): sync external mods to run directory`).
- `test`: Adding or updating test cases or diagnostic harnesses.

### Common Scopes:
- `vampire`: Vampire progression, blood, skills, weaknesses, bat form.
- `hunter`: Hunter progression, weapons, crossbows, stakes, infusions.
- `faction`: Shared faction logic, lord titles, territory, villages.
- `render`: TESR, ISBRH, Angelica shaders, HUD overlays, particle effects.
- `entity`: Mobs, minions, bosses, AI behavior, spawning.
- `world`: Biomes, crypts, generation, structures.
- `ritual`: Altar recipes, state machines, catalysts.
- `network`: Network packets, client-server sync.
- `config`: Configuration options, toggles.

---

## 2. Local-Only Commits During Active Development
- **Always commit atomically:** After completing each coherent modification, feature, or fix, make a local commit.
- **NEVER push to remote (`git push`) without explicit instruction.**
- Keep all commits in the local git repository. Do not push after every change to prevent cluttering git history with micro-versions.

---

## 3. GitHub Release Creation (On User Command Only)
Only when the user explicitly issues the command to publish to GitHub (e.g., *"заливаем обновление на гитхаб"*, *"заливай на гитхаб"*, *"создай релиз"*):

1. **Review Git Log:** Inspect all commits since the previous release tag:
   ```bash
   git log $(git describe --tags --abbrev=0)..HEAD --oneline
   ```
2. **Determine Semantic Version:**
   - Patch (`v0.1.x`): Bugfixes and minor adjustments.
   - Minor (`v0.x.0`): New features, mechanics, or significant additions.
   - Major (`v1.0.0`): Full feature parity milestone.
3. **Tag Version:**
   ```bash
   git tag -a v<version> -m "Release v<version>: <summary>"
   ```
4. **Push Commits and Tags:**
   ```bash
   git push origin master --tags
   ```
5. **Create GitHub Release:** Using GitHub CLI or release workflow summarizing all features, fixes, and changes included in this release.
