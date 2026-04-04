# arclens-kt Roadmap

## Medium Priority

| Item | Description | Effort |
|------|-------------|--------|
| **HTML reports** | Standalone HTML with embedded Mermaid diagrams, navigation, filtering | Medium |
| **Streaming JSON** | Write files to JSON as parsed, reduce memory | Medium |
| **Chunked processing** | Process packages/files in batches | Medium |
| **Lazy loading** | Don't deserialize entire model for subset queries | Medium |

## Analysis Capabilities

| Feature | Effort |
|---------|--------|
| Cyclomatic complexity | Medium |
| LOC metrics | Low |
| Code duplication detection | High |
| Unused import detection | Low |
| Large class detection | Low |
| Long method detection | Low |
| Deep inheritance detection | Low |

## Other Ideas

- **Better error recovery** - Partial parse results instead of skipping entire file
- **Configuration file support** - `.arclens.yml` or `arclens.config.json` for project-specific rules
- **Watch mode** - Monitor source directories for changes, incrementally update model
- **Comparison/diff mode** - Compare two model.json files, show changes for PR reviews
