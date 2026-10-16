---
name: run-client-test
description: Procedures for building Neorism, syncing jars to run/client/mods, launching the test client, monitoring runtime logs, and post-session reporting.
---

# Test Client & Runtime Verification Workflow

Follow this procedure whenever verifying code changes, testing gameplay, or running the game client for Neorism.

## 1. Build & Sync Procedure (Mandatory after code changes)
Whenever code or assets are added or modified:
1. **Compile and Package:**
   ```powershell
   .\gradlew assemble
   ```
   Verify that `assemble` finishes with `BUILD SUCCESSFUL`.
2. **Synchronize Mod Jar to `run/client/mods/`:**
   Copy the newly produced dev jar from `build/libs/neorism-*-dev.jar` to `run/client/mods/`.
   ```powershell
   $latest = Get-ChildItem -Path "build/libs" -Filter "neorism-*-dev.jar" | Sort-Object LastWriteTime -Descending | Select-Object -First 1
   Copy-Item -Path $latest.FullName -Destination "run/client/mods" -Force
   ```
3. **Verify Supported Dependencies in `run/client/mods/`:**
   Ensure all 9 required GTNH / user mods are present:
   - `Angelica-*.jar`
   - `GTNHLib-*.jar`
   - `Hodgepodge-*.jar`
   - `VillageNames-*.jar`
   - `Et-Futurum-Requiem-*.jar`
   - `lwjgl3ify-*.jar`
   - `unimixins-*.jar`
   - `combatbackport-*.jar`
   - `villagertradesbackport-*.jar`

---

## 2. Launching the Test Client
**Launch only when the user explicitly requests it** (e.g., *"запусти тестовый клиент"*, *"запусти runClient"*).
Do not launch the client on your own during regular code edits.

Command to launch:
```powershell
.\gradlew runClient
```
(or `.\gradlew runClient25` if running with Java 25 task).

---

## 3. Real-Time Log Monitoring
During the game execution:
- Track console output and `run/client/logs/fml-client-latest.log`.
- Look out for:
  - `[DEBUG:COMBAT]`, `[DEBUG:AI]`, `[DEBUG:LEVEL]`, `[DEBUG:SPAWN]`, `[DEBUG:TRADE]`, `[DEBUG:RITUAL]` messages.
  - Any OpenGL warnings or Angelica shader pipeline warnings.
  - Exceptions, NPEs, stack traces, missing texture / missing model errors.

---

## 4. Post-Session Action
When the game client closes:
- **Case A: Closed with Crash / Exception / Error:**
  - Do NOT wait. Immediately analyze the crash report (`run/client/crash-reports/`) and the last log entries in `fml-client-latest.log`.
  - Locate the root cause and start fixing the issue immediately.
- **Case B: Closed cleanly without errors:**
  - Ask the user how the gameplay test went and solicit their feedback/report on what they observed.
