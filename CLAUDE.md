# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**arclens-kt** (formerly kotlin-nkp) is a static analysis tool for Kotlin programs. It analyzes package dependencies, class hierarchies, and import relationships to generate metrics and visual diagrams (Mermaid format) for architectural assessment.

## Build Commands

```bash
# Build and run all checks (tests, detekt, ktlint)
./gradlew check

# Run tests only
./gradlew test

# Run a single test class
./gradlew test --tests "net.dinkla.arclens.extract.ExtractTest"

# Run a single test method
./gradlew test --tests "net.dinkla.arclens.extract.ExtractTest.extractSimpleIdentifier*"

# Format code with ktlint
ktlint -F src/

# View coverage report
./gradlew jacocoTestReport && open build/reports/jacoco/test/html/index.html

# Run the application
./gradlew run --quiet --args="<command> <args>"

# Update dependencies
./gradlew refreshVersions
```

## Architecture

### Package Structure

```
net.dinkla.arclens
├── commands/          # CLI commands extending AbstractCommand
├── domain/
│   ├── kotlinlang/   # Domain models: Project, Package, KotlinFile, ClassSignature, etc.
│   └── statistics/   # Coupling metrics
├── extract/          # Incremental parsing and change detection
├── analysis/         # Metric calculations and diagram generation
└── utilities/        # File operations, JSON serialization
```

### Data Flow

1. **Parse**: Kotlin source → AST → Domain models → `model.json`
2. **Analyze**: Load `model.json` → Run analysis commands → Output metrics/diagrams

### Supported Kotlin Constructs

**Functions:**
- Regular and extension functions (including generic/nullable receiver types)
- Function modifiers: `suspend`, `inline`, `infix`, `tailrec`, `operator`, `external`
- Parameter modifiers: `vararg`, `noinline`, `crossinline`

**Types:**
- Simple types, generics, nullable types
- Function types as parameters/return types
### Key Patterns

- **Commands**: Extend `AbstractCommand` which provides `loadProject()` for model loading
- **Domain Models**: All are `@Serializable` data classes for JSON export
- **Analysis Functions**: Top-level functions that transform domain objects
- **Detection Reports**: `LargeClassReport`, `LongMethodReport`, `UnusedImportsReport`, `DeepInheritanceReport` — threshold-based code smell detection with `companion object { fun from() }` factories

## Code Quality Rules

Enforced by detekt (zero violations allowed):
- Max cyclomatic complexity: 15
- Max class size: 600 lines
- Max method length: 60 lines
- Max parameters: 6 (functions), 7 (constructors)
- Max nested depth: 4
- Max line length: 120 characters
- Magic numbers forbidden (except -1, 0, 1, 2, 3, 4)

## Testing

- **Framework**: Kotest with `StringSpec` style
- **Structure**: Given/When/Then comments
- **Fixtures**: Reuse from `src/test/kotlin/net/dinkla/arclens/Fixture.kt`

```kotlin
class MyTest : StringSpec({
    "function should do something" {
        // Given
        val input = ...
        // When
        val result = myFunction(input)
        // Then
        result shouldBe expected
    }
})
```

## Prerequisites

- **JVM 21** target runtime

## Key Conventions

- Use `internal` visibility for module-private APIs
- Use `data class` for domain models
- Use extension functions for utility operations
- Follow official Kotlin code style (`kotlin.code.style=official`)
- All dependency versions in `gradle/libs.versions.toml`
