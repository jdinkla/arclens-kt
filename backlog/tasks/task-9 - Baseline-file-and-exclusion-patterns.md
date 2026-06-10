---
id: TASK-9
title: Baseline file and exclusion patterns
status: To Do
assignee: []
created_date: '2026-06-10 12:33'
labels:
  - 'model:opus'
  - roadmap
dependencies:
  - TASK-8
priority: high
ordinal: 9000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
detekt-style baseline: freeze existing violations so only new ones fail gating. Exclusion globs for generated code (protobuf, SQLDelight, KSP output). Introduce minimal .arclens.yml as the config home (groundwork for the roadmap 'Configuration file support' item). Key design problem: stable violation identity across runs (package + element name, not line numbers). See specs/roadmap.md, CI Integration & Adoption.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Baseline file can be generated from current state and suppresses listed violations in gating
- [ ] #2 Violation identity is stable under unrelated edits (line shifts do not invalidate baseline)
- [ ] #3 Exclusion patterns filter files at parse or analysis stage; documented in README
<!-- AC:END -->
