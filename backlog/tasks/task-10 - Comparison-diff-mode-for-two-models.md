---
id: TASK-10
title: Comparison/diff mode for two models
status: To Do
assignee: []
created_date: '2026-06-10 12:33'
labels:
  - 'model:opus'
  - roadmap
dependencies:
  - TASK-6
priority: high
ordinal: 10000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Compare two model.json files and report metric deltas (coupling A/I/D, smells, cycles, package add/remove) for PR review: 'did this PR make coupling worse?'. JSON output plus human-readable rendering. Design problem: matching packages/classes across versions (renames, moves). See specs/roadmap.md, CI Integration & Adoption.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 diff command takes two model files and reports per-package metric deltas and new/removed cycles
- [ ] #2 Output available as JSON and human-readable text
- [ ] #3 Unit tests cover added/removed/changed packages
<!-- AC:END -->
