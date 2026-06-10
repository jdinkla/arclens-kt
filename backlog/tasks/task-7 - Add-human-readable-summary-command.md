---
id: TASK-7
title: Add human-readable summary command
status: To Do
assignee: []
created_date: '2026-06-10 12:33'
labels:
  - 'model:sonnet'
  - roadmap
dependencies:
  - TASK-6
priority: medium
ordinal: 7000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
New 'summary' CLI command printing a terminal-friendly overview from model.json: cycle count, packages in the zone of pain (high D), worst offenders, smell counts. Today every command emits raw JSON; the terminal loop should not require jq. See specs/roadmap.md, Tooling & UX.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 summary command registered in Arclens.kt and produces readable terminal output
- [ ] #2 Covers cycles, coupling/zone-of-pain, and all four smell detections
- [ ] #3 Command test exists; README updated
<!-- AC:END -->
