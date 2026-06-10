---
id: TASK-13
title: 'Multi-module awareness: design spike'
status: To Do
assignee: []
created_date: '2026-06-10 12:33'
labels:
  - 'model:fable'
  - roadmap
  - design
dependencies: []
priority: high
ordinal: 13000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Most real Kotlin projects express architecture through Gradle modules; --sources flattening blurs exactly that boundary. This task is the DESIGN PASS ONLY: write specs/multi-module.md covering model changes (module attribution per file), module dependency graph derivation, per-module vs aggregate reports, and Gradle plugin strategy for multi-project builds. Output: a design doc plus a proposed split into implementation subtasks. See specs/roadmap.md, Analysis Features.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 specs/multi-module.md exists with model, analysis, and plugin design decisions plus rationale
- [ ] #2 Implementation broken into concrete follow-up subtasks with effort estimates
<!-- AC:END -->
