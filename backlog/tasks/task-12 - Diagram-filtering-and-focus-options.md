---
id: TASK-12
title: Diagram filtering and focus options
status: To Do
assignee: []
created_date: '2026-06-10 12:33'
labels:
  - 'model:sonnet'
  - roadmap
dependencies: []
priority: medium
ordinal: 12000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Class diagram is unreadable beyond demo size and Mermaid chokes. Add --package <prefix> filter (all three diagram commands) and --around <Class> --depth <n> neighborhood focus (class diagram). See specs/roadmap.md, Tooling & UX.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 --package filters class, import, and coupling diagrams to a package prefix
- [ ] #2 --around with --depth renders only the neighborhood of a class
- [ ] #3 Tests cover filtered output; README examples updated
<!-- AC:END -->
