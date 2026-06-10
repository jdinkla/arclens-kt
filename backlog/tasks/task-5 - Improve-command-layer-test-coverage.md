---
id: TASK-5
title: Improve command layer test coverage
status: To Do
assignee: []
created_date: '2026-06-10 11:41'
labels:
  - testing
  - commands
dependencies: []
priority: low
ordinal: 5000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The commands package is the weakest-tested production area: 78% line coverage and the overall branch coverage of 70% is dragged down here (roadmap notes 44% branch for commands). Listed in specs/roadmap.md under Test Coverage; tracked here as the actionable ticket.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Branch coverage of net.dinkla.arclens.commands meaningfully improved (target ~70%+)
- [ ] #2 Error paths (missing model.json, bad arguments) covered
<!-- AC:END -->
