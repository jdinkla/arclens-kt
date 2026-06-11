---
id: TASK-15
title: >-
  Close parser coverage gaps: companion objects, object expressions, delegated
  properties, reified, annotations
status: To Do
assignee: []
created_date: '2026-06-11 13:44'
labels:
  - 'model:sonnet'
dependencies: []
ordinal: 15000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The PSI extractor (src/main/kotlin/net/dinkla/arclens/parser/PsiExtract.kt) does not capture several Kotlin constructs. Origin: 'Bugs' section of pre-arclens working notes (NKP.md, triaged 2026-06-11). Gaps verified against current code: (1) companion objects are extracted as plain OBJECT with no companion distinction; (2) anonymous objects / object expressions are not captured at all; (3) delegated properties (by lazy, custom delegates) carry no delegation info in the Property model; (4) reified type parameters are not recorded on functions; (5) annotations are not extracted anywhere (no domain model). Also re-verify the old note 'a property called property has a PROPERTY node' with a targeted repro test. Each gap follows the existing extract pattern: extend domain model (@Serializable), extend PsiExtract, add PsiExtractTest cases.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Companion objects distinguishable from regular objects in ClassSignature (e.g. isCompanion flag or element type)
- [ ] #2 Object expressions are captured or an explicit documented decision is made to skip them
- [ ] #3 Delegated properties record their delegation (at minimum a flag, ideally the delegate expression type)
- [ ] #4 Reified type parameters recorded on function signatures
- [ ] #5 Annotations on classes/functions/properties extracted into the domain model
- [ ] #6 Repro test for 'property named property' added; bug fixed if it reproduces
- [ ] #7 PsiExtractTest covers every new construct; ./gradlew check passes
<!-- AC:END -->
