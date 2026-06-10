---
id: TASK-3
title: Dispose KotlinCoreEnvironment in PsiParser
status: Done
assignee: []
created_date: '2026-06-10 11:41'
updated_date: '2026-06-10 12:09'
labels:
  - bug
  - parser
dependencies: []
priority: medium
ordinal: 3000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
PsiParser creates a KotlinCoreEnvironment with Disposer.newDisposable() (PsiParser.kt:61) that is never disposed. Harmless for the CLI (process exits), but ArclensParseTask runs inside a long-lived Gradle daemon, so repeated builds accumulate undisposed environments. Make PsiParser Closeable (or dispose explicitly after parsing) and ensure call sites release it.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Disposable created in createKotlinCoreEnvironment is disposed when the parser is no longer needed
- [ ] #2 All call sites (CLI Parse command, ParallelParsing, Gradle tasks) release the parser
- [ ] #3 Existing tests still pass
- [ ] #4 1:true
<!-- AC:END -->
