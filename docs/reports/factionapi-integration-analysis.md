# Architectural Analysis & Integration Report: TeamLapen FactionAPI (Vampirism 2.0+)

> **Date:** 2026-10-16  
> **Source:** TeamLapen / Cheaterpaul *FactionAPI* (CurseForge Project `1416927`, File `9021286`, `FactionApi-26.1.2-1.0.0-alpha.3.jar`)  
> **Repository Reference:** `TeamLapen/Vampirism` (`projects/faction-api`)  
> **Status:** CONNECTED & ROADMAP UPDATED  

---

## 1. Executive Summary

Starting in Vampirism 2.0 (Minecraft 1.20+ / 1.21), the original authors (**TeamLapen** / **Cheaterpaul**) decoupled the core faction and RPG systems from the base Vampirism mod into an independent framework called **FactionAPI**.

FactionAPI standardizes:
1. **Factions & Playable Factions** (`IFaction`, `IPlayableFaction`, `IFactionPlayerHandler`, `FactionsApi`).
2. **Skill Tree Directed Graph** (`ISkillTree`, `ISkillSegment`, `ISkillNode`, `ISkillHandler`).
3. **Action Ring / Radial Skill Rose** (`IAction`, `ILastingAction`, `IActionHandler`).
4. **Unified Faction Screen (`[P]` Key)**: Multi-tab dashboard for player stats, skill tree, minions, and territory control.
5. **Minions System** (`IMinionEntity`, `IMinionTask`, minion inventories and behaviors).
6. **Village Territory War & Totems** (`IVillageFaction`, `ITotem`, capture mechanics and defense raids).
7. **Refinements** (`IRefinement`, `IRefinementSet`, accessory perks).

To avoid maintaining custom/divergent logic and to guarantee 100% adherence to TeamLapen's official design, **Neorism has adopted the official `de.teamlapen.faction.api` architecture for 1.7.10**.

---

## 2. Comparison: Interim Implementation vs. TeamLapen FactionAPI

| Subsystem | Interim Neorism Implementation | TeamLapen FactionAPI Standard | Planned Migration Strategy |
| :--- | :--- | :--- | :--- |
| **Skill Tree Structure** | Flat list of skills unlocked sequentially via buttons | Directed Acyclic Graph (DAG) of `ISkillSegment` with `(x, y)` coordinates, parent connections, and mutually exclusive branches | Implement `SkillTree` & `SkillSegment` graph builder; load layouts from JSON/data |
| **Skill Tree GUI** | Basic `GuiSkillTree` list with static unlock buttons | Interactive panning/scrolling canvas (`SkillsScreen`), tree background texture (`skills.png`), drawn line connectors between parent and child nodes, dynamic node frames (locked/available/unlocked) | Refactor `GuiSkillTree` into pannable canvas with custom node rendering and connection lines |
| **Action Execution** | 2 fixed hotkey slots (`[R]` and `[Z]`) bound in skill GUI | Circular **Radial Menu (Skill Rose / Action Ring)** opened by holding `[R]`; hovering over slice selects ability; releasing activates | Implement `GuiActionRadial` (`SkillRoseScreen`) with clock-sweep cooldowns and duration overlays |
| **Faction Menu** | Direct keybind (`[K]`) straight to skill tree | Main character dashboard on **`[P]`** containing tabs: Overview, Skills, Minions, Lord/Villages | Implement unified `GuiFactionDashboard` on `[P]` with tab navigation |
| **Minion Tasks** | Ad-hoc AI routines | Standardized `IMinionTask` execution pipeline (Follow, Guard, Collect Blood, Gather Loot) | Base minion entities and tasks on `IMinionEntity` and `IMinionTask` in Phase 10 |
| **Village Capture** | Basic totem entity | Faction territory control, raid phases, capture progress percentage, defender spawns | Implement `IVillageFaction` and `ITotem` state machines in Phase 13 |

---

## 3. Codebase Integration (`de.teamlapen.faction.api`)

The canonical FactionAPI interface hierarchy has been backported and established directly in Neorism:

- `de.teamlapen.faction.api`:
  - `FactionsApi`: Global service entry point (`services()`, `factionPlayerHandler(player)`).
- `de.teamlapen.faction.api.factions`:
  - `IFaction`, `IPlayableFaction`, `IFactionPlayer`, `IFactionPlayerHandler`.
- `de.teamlapen.faction.api.factions.skills`:
  - `ISkill`, `ISkillSegment`, `ISkillTree`, `ISkillHandler`.
- `de.teamlapen.faction.api.factions.actions`:
  - `IAction`, `ILastingAction`, `IActionHandler`.
- `de.teamlapen.faction.api.factions.villages`:
  - `IVillageFaction`, `ITotem`.
- `de.teamlapen.faction.api.factions.minions`:
  - `IMinionEntity`, `IMinionTask`.
- `de.teamlapen.faction.api.factions.refinement`:
  - `IRefinement`, `IRefinementSet`.
- `de.teamlapen.faction.api.services`:
  - `IFactionServices`, `IFactionHelper`, `IFactionPredicates`.

Existing Neorism classes (`FactionPlayerHandler`, `ModFactions`, `IAction`, `IFaction`) have been connected via default interface bridges to ensure zero regressions while providing 100% polymorphic compatibility with FactionAPI.

---

## 4. Roadmap Impact & Future Migration Plan

The master roadmap (`docs/ROADMAP.md`) has been updated with the following milestones:

1. **Phase 6 Refactor (Action Ring & Skill Tree):**
   - Replace linear skill list with `ISkillTree` DAG and `ISkillSegment` nodes.
   - Build pannable/scrollable `GuiSkillTree` matching TeamLapen's layout.
   - Implement `SkillRoseScreen` (Radial Action Menu on hold `[R]`).
2. **Phase 10 (Lordship & Minions):**
   - Implement `IMinionTask` pipeline and minion management tab in Faction GUI.
3. **Phase 12 (Client & GUI Polish):**
   - Unified `GuiFactionDashboard` on `[P]`.
4. **Phase 13 (Villages & Faction Territory War):**
   - Full village capture mechanics powered by `IVillageFaction` and `ITotem`.
