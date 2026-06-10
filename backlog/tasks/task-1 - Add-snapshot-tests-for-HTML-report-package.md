---
id: TASK-1
title: Add snapshot tests for HTML report package
status: To Do
assignee: []
created_date: '2026-06-10 11:41'
labels:
  - testing
  - report
dependencies: []
priority: high
ordinal: 1000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The report package has 0% test coverage on 390 lines, and HtmlReportGenerator.kt (515 lines of string-built HTML) is the largest file in the codebase. Commit ba52ea3 (mermaid rendering broken in hidden tabs) is exactly the kind of regression snapshot tests would catch. Also listed in specs/roadmap.md under Test Coverage.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Snapshot/regression test covers HtmlReportGenerator.generate() output for a representative ReportData fixture
- [ ] #2 Tests cover all report sections incl. code smells and diagrams
- [ ] #3 report package line coverage substantially above 0% in jacoco report
<!-- AC:END -->
