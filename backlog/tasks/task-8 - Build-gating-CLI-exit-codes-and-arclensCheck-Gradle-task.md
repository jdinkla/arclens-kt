---
id: TASK-8
title: 'Build gating: CLI exit codes and arclensCheck Gradle task'
status: To Do
assignee: []
created_date: '2026-06-10 12:33'
labels:
  - 'model:opus'
  - roadmap
dependencies: []
priority: high
ordinal: 8000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
CLI: --fail-on-violation flag causing non-zero exit when cycles are detected or smell thresholds are exceeded. Gradle plugin: arclensCheck task that fails the build on violations, wired into the 'check' lifecycle. Without a gate, arclens is a one-time audit instrument. See specs/roadmap.md, CI Integration & Adoption.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 CLI exits non-zero with --fail-on-violation when violations exist, zero otherwise
- [ ] #2 arclensCheck Gradle task fails the build on violations and is wired into check
- [ ] #3 Functional test (TestKit) covers both pass and fail cases
<!-- AC:END -->
