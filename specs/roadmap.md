# arclens-kt Roadmap

## Analysis Features

| Feature | Priority | Effort | Notes |
|---------|----------|--------|-------|
| Call graph / fan-in-fan-out | Medium | High | Cross-file method-level analysis: who calls what, method fan-in/fan-out; moves tool from class/package level to method level |
| Code duplication detection | Low | High | Needs token/AST-based comparison |

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
| Configuration file support | Medium | Medium | `.arclens.yml` for project-specific rules and thresholds |
| Comparison/diff mode | Medium | Medium | Compare two model.json files, show delta for PR reviews; natural CI gate ("did coupling get worse?") |
| Better error recovery | Low | Medium | Partially done: `Result<KotlinFile>` error collection exists, but whole file is skipped on failure — needs partial results |
| Watch mode | Low | Medium | Monitor source dirs, incrementally update model |

## Test Coverage

| Feature | Priority | Effort | Notes |
|---------|----------|--------|-------|
| HTML report tests | Medium | Low | HtmlReportGenerator has 0% coverage; at minimum snapshot-based regression tests |
| Command layer coverage | Low | Medium | Currently 70% line / 44% branch; weakest tested area |
