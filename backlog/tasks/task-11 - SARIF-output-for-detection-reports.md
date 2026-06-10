---
id: TASK-11
title: SARIF output for detection reports
status: To Do
assignee: []
created_date: '2026-06-10 12:33'
labels:
  - 'model:sonnet'
  - roadmap
dependencies: []
priority: medium
ordinal: 11000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Emit SARIF 2.1.0 for the four smell detections and circular dependencies so GitHub code scanning annotates PRs. Map filePath (and line where available) to SARIF locations. See specs/roadmap.md, CI Integration & Adoption.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 --format sarif (or sarif command) produces output valid against the SARIF 2.1.0 schema
- [ ] #2 Schema validity asserted in a test
- [ ] #3 README documents GitHub code-scanning usage
<!-- AC:END -->
