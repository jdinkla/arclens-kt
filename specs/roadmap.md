# arclens-kt Roadmap

## Positioning

Arclens' differentiated value is the **architectural layer**: coupling, cycles, module graphs, diffs, diagrams. Code smell detection (long methods, complexity, large classes) competes directly with detekt, which offers baselines, `@Suppress` support, and IDE integration — no further investment in smell detectors; effort goes into compounding the architectural strength.

## CI Integration & Adoption

These address the biggest practical gap: the tool *detects* but cannot *enforce*. Reports nobody is forced to look at decay into noise.

| Feature | Priority | Effort | Notes |
|---------|----------|--------|-------|
| Build gating / fail on violation | High | Medium | `arclensCheck` Gradle task that fails on `hasCycles == true` or threshold violations; proper exit codes on the CLI (`--fail-on-violation`). Without a gate, arclens is a one-time audit instrument, not something that lives in a project |
| Baseline & exclusions | High | Medium | detekt-style baseline file: freeze existing violations, fail only on new ones. Exclude patterns for generated code (protobuf, SQLDelight, KSP output). Adoption blocker for legacy codebases; natural home is `.arclens.yml` |
| Comparison/diff mode | High | Medium | Compare two model.json files, show delta for PR reviews; "did this PR make coupling worse?" is the question architecture tools get asked most. Natural CI gate together with build gating |
| SARIF output | Medium | Low | Lights up GitHub code-scanning annotations almost for free |

## Analysis Features

| Feature | Priority | Effort | Notes |
|---------|----------|--------|-------|
| Multi-module awareness | High | High | Most real Kotlin projects express architecture through Gradle modules, not packages; `--sources` flattening blurs exactly that boundary. Needs: module dependency view, per-module reports, aggregate task for the root project |
| Abstractness & distance from main sequence | Medium | Low | Have I (instability), missing A and D = \|A + I − 1\|. I alone doesn't say whether an unstable package is a problem; D gives the actionable "zone of pain / zone of uselessness" signal. Cheap: `abstract`/`interface` already parsed |
| Call graph / fan-in-fan-out | Medium | High | Cross-file method-level analysis: who calls what, method fan-in/fan-out; moves tool from class/package level to method level |
| Code duplication detection | Low | High | Needs token/AST-based comparison; overlaps with detekt territory (see Positioning) |

Done: LOC metrics, large class detection, long method detection, deep inheritance detection, cyclomatic complexity.

## Domain Model

| Feature | Priority | Effort | Notes |
|---------|----------|--------|-------|
| Annotation capture | Medium | Medium | Store annotations on classes, functions, properties; unlocks architectural queries (e.g. "which classes are `@Service`?", "are deprecated APIs still called?") |
| Structured generic types | Low | High | Replace string-based `Type` with AST; enables generic complexity analysis, variance tracking, type-safe dependency analysis |

## Parser Coverage

| Construct | Priority | Effort | Notes |
|-----------|----------|--------|-------|
| Context receivers | Low | Medium | Kotlin feature is deprecated in favor of context parameters |

Done: `suspend` function types as parameters, secondary constructors.

## Performance

| Feature | Priority | Effort | Notes |
|---------|----------|--------|-------|
| Streaming JSON | Low | Medium | Write parsed files to JSON incrementally, reduce memory |
| Chunked processing | Low | Medium | Process packages/files in batches |
| Lazy loading | Low | Medium | Don't deserialize entire model for subset queries |

## Tooling & UX

| Feature | Priority | Effort | Notes |
|---------|----------|--------|-------|
| Configuration file support | Medium | Medium | `.arclens.yml` for project-specific rules, thresholds, baselines, and exclusions |
| Human-readable summary output | Medium | Low | `summary` command for the terminal: "3 cycles, 5 packages in the zone of pain, worst offender: X". Today every command emits raw JSON to stdout; the CLI loop shouldn't require `jq`. HTML report covers some of this but not the terminal workflow |
| Diagram filtering & focus | Medium | Medium | Class diagram is unreadable beyond ~demo size (and Mermaid chokes). Needs `--package <prefix>`, `--around <Class> --depth <n>`, or per-package diagrams. Coupling diagram survives scale better |
| Better error recovery | Low | Medium | Partially done: `Result<KotlinFile>` error collection exists, but whole file is skipped on failure — needs partial results |
| Watch mode | Low | Medium | Monitor source dirs, incrementally update model |

## Test Coverage

| Feature | Priority | Effort | Notes |
|---------|----------|--------|-------|
| HTML report tests | Medium | Low | HtmlReportGenerator has 0% coverage; at minimum snapshot-based regression tests |
| Command layer coverage | Low | Medium | Currently 70% line / 44% branch; weakest tested area |
