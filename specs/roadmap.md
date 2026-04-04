# arclens-kt Roadmap

## Analysis Features

| Feature | Priority | Effort | Notes |
|---------|----------|--------|-------|
| Cyclomatic complexity | Medium | Medium | Count branches per function |
| Code duplication detection | Low | High | Needs token/AST-based comparison |

Done: LOC metrics, unused import detection, large class detection, long method detection, deep inheritance detection.

## Parser Coverage

| Construct | Priority | Effort | Notes |
|-----------|----------|--------|-------|
| `suspend` function types as parameters | Medium | Low | e.g. `suspend () -> Unit` |
| Secondary constructors | Medium | Low | |
| Context receivers | Low | Medium | Kotlin feature is deprecated in favor of context parameters |

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
| Comparison/diff mode | Medium | Medium | Compare two model.json files, show changes for PR reviews |
| Better error recovery | Low | Medium | Partially done: `Result<KotlinFile>` error collection exists, but whole file is skipped on failure — needs partial results |
| Watch mode | Low | Medium | Monitor source dirs, incrementally update model |
