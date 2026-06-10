---
id: TASK-4
title: 'Repo hygiene: relocate working notes and stray files'
status: To Do
assignee: []
created_date: '2026-06-10 11:41'
labels:
  - chore
dependencies: []
priority: low
ordinal: 4000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
PROMPT.md and PROMPT_FOR_ANALYSIS.md are tracked at the repo root but read like working notes — move to docs/ or specs/, or delete if stale. loc_timeline.png sits untracked at root — commit it somewhere meaningful (e.g. docs/) or remove it.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 PROMPT.md and PROMPT_FOR_ANALYSIS.md moved to an appropriate directory or deleted
- [ ] #2 loc_timeline.png either committed in a sensible location or removed
<!-- AC:END -->
