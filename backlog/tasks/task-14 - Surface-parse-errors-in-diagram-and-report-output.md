---
id: TASK-14
title: Surface parse errors in diagram and report output
status: To Do
assignee: []
created_date: '2026-06-11 13:44'
labels:
  - 'model:sonnet'
dependencies: []
ordinal: 14000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Parse-error reporting exists in src/main/kotlin/net/dinkla/arclens/extract/ParseReporting.kt (formatParseErrors, formatParseSummary), but the diagram and report commands (MermaidImportDiagram, MermaidCouplingDiagram, HtmlReportCommand, detection reports) do not surface parse errors. A diagram generated from a partially failed parse looks complete, silently misrepresenting the architecture. Origin: long-standing note 'Print warning if there were parse errors!!! esp. in diagram!' from pre-arclens working notes (NKP.md, triaged 2026-06-11).
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Diagram commands print a warning to stderr when the loaded model contains parse errors
- [ ] #2 Mermaid output embeds a visible comment/annotation noting incomplete parse when errors exist
- [ ] #3 HTML report shows a parse-error warning banner or section when errors exist
- [ ] #4 Unit/command tests cover the warning path for at least one diagram command and the HTML report
- [ ] #5 ./gradlew check passes
<!-- AC:END -->
