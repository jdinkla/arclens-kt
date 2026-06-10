---
id: TASK-6
title: Add abstractness (A) and distance from main sequence (D) metrics
status: To Do
assignee: []
created_date: '2026-06-10 12:33'
labels:
  - 'model:sonnet'
  - roadmap
dependencies: []
priority: medium
ordinal: 6000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Extend package coupling analysis with abstractness A (abstract classes + interfaces / total types per package) and distance D = |A + I - 1|. Data is already parsed (class/inheritance modifiers). Extend PackageCoupling output and coupling diagram annotations. Follow the 'Adding a New Analysis Feature' checklist in CLAUDE.md. See specs/roadmap.md, Analysis Features. First domino: feeds summary command and diff mode.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 package-coupling JSON output includes abstractness and distance per package
- [ ] #2 Edge cases tested: package with no types, all-abstract package
- [ ] #3 README appendix and capabilities table updated, roadmap item moved to done
<!-- AC:END -->
