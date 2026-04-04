# arclens-kt History

## Performance Investigation (2026-01-17)

### Profiling Results

Timing instrumentation confirmed the bottleneck:

| Phase | Time | Percentage |
|-------|------|------------|
| Parse (kotlin-grammar-tools) | 500-2500ms per file | **~99%** |
| Extract (our code) | 0-16ms per file | ~1% |

**Conclusion:** The ANTLR-based kotlin-grammar-tools parser was the bottleneck.

### Optimizations Applied

- Added timing instrumentation (`logger.debug` with parse/extract times)
- Changed `Dispatchers.Default` → `Dispatchers.IO` (now uses 64+ worker threads)
- Refactored modifier extraction to single-pass (minor improvement)
- Made incremental parsing default (use `--full` to force full parse)

### PSI Parser Migration (2026-01-17)

Replaced kotlin-grammar-tools with PSI-based parser using `kotlin-compiler-embeddable`.

**Benchmark Results:**
| Parser | Time (65 files) | Speedup |
|--------|-----------------|---------|
| PSI (new default) | 71ms | **25.9x faster** |
| Grammar (ANTLR) | 1839ms | baseline |

## Completed Milestones

- Parser improvements (extension functions, modifiers)
- Multi-source directory support
- Gradle plugin
- Circular dependency detection
- Incremental parsing (now default, use `--full` to override)
- Performance profiling & parallelization improvements (2026-01-17)
- PSI-based parser with 25.9x speedup (2026-01-17)
