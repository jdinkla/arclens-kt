# arclens-kt Remaining Work

**Last Updated:** 2026-01-17

## Performance Investigation Findings (2026-01-17)

### Profiling Results

Timing instrumentation confirmed the bottleneck:

| Phase | Time | Percentage |
|-------|------|------------|
| Parse (kotlin-grammar-tools) | 500-2500ms per file | **~99%** |
| Extract (our code) | 0-16ms per file | ~1% |

**Conclusion:** The ANTLR-based kotlin-grammar-tools parser is the bottleneck. Optimizing extraction code has negligible impact.

### Completed Optimizations

- [x] Added timing instrumentation (`logger.debug` with parse/extract times)
- [x] Changed `Dispatchers.Default` → `Dispatchers.IO` (now uses 64+ worker threads)
- [x] Refactored modifier extraction to single-pass (minor improvement)
- [x] Made incremental parsing default (use `--full` to force full parse)

### PSI Parser Implementation (2026-01-17)

Replaced kotlin-grammar-tools with PSI-based parser using `kotlin-compiler-embeddable`.

**Benchmark Results:**
| Parser | Time (65 files) | Speedup |
|--------|-----------------|---------|
| PSI (new default) | 71ms | **25.9x faster** |
| Grammar (ANTLR) | 1839ms | baseline |

**Usage:**
```bash
# PSI parser (default, faster)
./gradlew run --args="parse src/main/kotlin"

# ANTLR parser (original)
./gradlew run --args="parse --parser GRAMMAR src/main/kotlin"
```

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

## Completed

- [x] Parser improvements (extension functions, modifiers)
- [x] Multi-source directory support
- [x] Gradle plugin
- [x] Circular dependency detection
- [x] Incremental parsing (now default, use `--full` to override)
- [x] Performance profiling & parallelization improvements (2026-01-17)
- [x] **PSI-based parser (25.9x speedup)** - Replaced ANTLR with kotlin-compiler-embeddable (2026-01-17)
