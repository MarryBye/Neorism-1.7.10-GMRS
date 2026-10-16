---
name: run-client-test
description: Procedures for compiling Neorism, syncing jars to run/client/mods, user-driven test launching, and subsequent agent inspection of game runtime logs and crash reports.
---

# Test Client & Runtime Log Inspection Workflow

Follow this procedure whenever code is compiled, testing takes place, and runtime logs are analyzed.

## 1. Build & Dependency Verification
Whenever code or assets are added or modified:
1. **Compile and Package:**
   ```powershell
   .\gradlew assemble
   ```
   Verify that `assemble` finishes with `BUILD SUCCESSFUL`.
2. **Classpath Management (No manual copying needed):**
   In RetroFuturaGradle (RFG), all 9 integrated GTNH and user mods (`Angelica`, `GTNHLib`, `Hodgepodge`, `VillageNames`, `Et-Futurum-Requiem`, `lwjgl3ify`, `unimixins`, `combatbackport`, `villagertradesbackport`) as well as the active `Neorism` build are mounted **directly on the Java classpath** by Gradle.
   **DO NOT** copy any dependency jars or `neorism-*-dev.jar` into `run/client/mods/`, otherwise FML will detect duplicate mods and halt with a fatal duplicate error. Keep `run/client/mods/` clean.

---

## 2. Client Launch Policy (User-Driven Only)
- **The user launches the client themselves** whenever they want to test the game (e.g. via `./gradlew runClient25` or their launcher).
- **The agent NEVER launches the client.**
- While the user plays and tests, the agent waits for the user's report or command to inspect logs.

---

## 3. Post-Testing Log Inspection Workflow
When the user finishes testing and asks to review the logs (*"посмотри логи"*, *"проверь логи игры"*, *"что в логах"*):
1. **Locate and Read Logs:**
   - Primary Forge log: `run/client/logs/fml-client-latest.log`
   - Vanilla log: `run/client/logs/latest.log`
   - Crash reports (if any): `run/client/crash-reports/`
2. **Filter and Analyze Key Data:**
   - Diagnostic debug entries: `[DEBUG:COMBAT]`, `[DEBUG:AI]`, `[DEBUG:LEVEL]`, `[DEBUG:SPAWN]`, `[DEBUG:TRADE]`, `[DEBUG:RITUAL]`.
   - Mod errors, exceptions, missing textures, missing sound events, or OpenGL pipeline warnings.
   - Compatibility issues with Angelica, Hodgepodge, Et-Futurum-Requiem, or lwjgl3ify.
3. **Action on Findings:**
   - If errors or crashes are found: explain the cause and immediately fix the code.
   - If clean: summarize key events recorded during the session and provide recommendations for the next phase.
