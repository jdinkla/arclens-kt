---
id: TASK-2
title: Add tests for the Gradle plugin
status: To Do
assignee: []
created_date: '2026-06-10 11:41'
labels:
  - testing
  - gradle-plugin
dependencies: []
priority: high
ordinal: 2000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The gradle-plugin subproject (~500 lines: ArclensExtension, ArclensPlugin, ArclensParseTask, ArclensAnalyzeTask) has zero tests, yet it is a published artifact (maven-publish, plugin ID net.dinkla.arclens) and the integration surface other projects consume. Use Gradle TestKit for functional tests and/or ProjectBuilder for unit tests of extension defaults and task wiring.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Unit tests verify ArclensExtension defaults and ArclensPlugin task registration (ProjectBuilder)
- [ ] #2 Functional test applies the plugin to a sample project and runs parse/analyze tasks (TestKit)
- [ ] #3 CLAUDE.md 'Adding a New Analysis Feature' checklist gains a Gradle plugin test step
<!-- AC:END -->
