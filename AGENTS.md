# AGENTS.md

This document provides guidance for AI agents working on the **arclens-kt** repository.

## Project Overview

**arclens-kt** (formerly kotlin-nkp) is a static analysis tool for Kotlin programs. It analyzes package dependencies, class hierarchies, and import relationships to generate metrics and visual diagrams for architectural assessment.

## Tech Stack

### Core Technologies

- **Language**: Kotlin (version defined in `gradle/libs.versions.toml`)
- **JVM Target**: JVM 21 (configured in `build.gradle.kts`, requires Java 17+ for build dependencies)
- **Build System**: Gradle with Kotlin DSL
- **Code Style**: Official Kotlin code style (configured in `gradle.properties`)

### Key Dependencies

All dependency versions (including code quality tools) are managed via [refreshVersions](https://splitties.github.io/refreshVersions/) and defined in `gradle/libs.versions.toml`. Key dependencies include:

- **CLI Framework**: Clikt
- **Serialization**: Kotlinx Serialization JSON
- **Concurrency**: Kotlinx Coroutines
- **Parsing**: Kotlin Grammar Tools (custom library, must be built locally)
- **Logging**: 
  - Kotlin Logging JVM
  - SLF4J API
  - Logback Classic
- **Testing**:
  - Kotest (JUnit 5 runner, assertions)
  - ArchUnit (architecture testing)

See `gradle/libs.versions.toml` for current versions.

### Code Quality Tools

- **ktlint**: Code formatting and linting
- **detekt**: Static code analysis
- **jacoco**: Code coverage reporting

### Dependency Management

- Uses [refreshVersions](https://splitties.github.io/refreshVersions/) for dependency version management
- **All versions are defined in `gradle/libs.versions.toml`** - refer to this file for current versions
- Only stable versions are accepted (configured in `settings.gradle.kts`)
- To update dependency versions, run `./gradlew refreshVersions` or `just refresh`

## Project Structure

### Package Organization

The project follows a layered architecture with clear separation of concerns:

```
net.dinkla.arclens
├── commands/          # CLI command implementations
├── domain/            # Domain models
│   ├── kotlinlang/   # Kotlin language constructs (Project, Package, Class, etc.)
│   └── statistics/   # Statistical models (Coupling, etc.)
├── extract/          # Code extraction/parsing logic
├── analysis/         # Analysis algorithms and metrics
└── utilities/        # Utility functions and helpers
```

### Key Conventions

- **Domain Models**: Use `@Serializable` data classes for JSON serialization
- **Commands**: Extend `AbstractCommand` from `net.dinkla.arclens.commands`
- **Internal APIs**: Use `internal` visibility for implementation details
- **Test Structure**: Use Kotest's `StringSpec` style with Given/When/Then comments

## Coding Style & Conventions

### General Guidelines

1. **Follow Official Kotlin Style**: The project uses `kotlin.code.style=official` in `gradle.properties`
2. **Use Data Classes**: Prefer `data class` for domain models and value objects
3. **Extension Functions**: Use extension functions for utility operations
4. **Visibility Modifiers**: 
   - `internal` for module-internal APIs
   - `private` for class/file-private APIs
   - No modifier = `public` (only use when necessary)

### Code Quality Rules

The project has strict code quality requirements enforced by **detekt**:

- **Max Issues**: 0 (all issues must be resolved)
- **Active Checks Include**:
  - Complexity checks (cyclomatic complexity threshold: 15)
  - Large class detection (threshold: 600 lines)
  - Long method detection (threshold: 60 lines)
  - Long parameter list (function: 6, constructor: 7)
  - Nested block depth (threshold: 4)
  - Too many functions (files: 50, classes: 11)
  - Magic numbers (enabled, excludes tests)
  - Max line length: 120 characters

### Naming Conventions

- **Classes**: PascalCase (e.g., `KotlinFile`, `PackageStatistics`)
- **Functions**: camelCase (e.g., `extractIdentifier`, `loadProject`)
- **Properties**: camelCase (e.g., `packageName`, `filePath`)
- **Constants**: UPPER_SNAKE_CASE for top-level, camelCase for object properties
- **Packages**: lowercase, dot-separated (e.g., `net.dinkla.arclens.domain`)

### File Organization

- One public class/interface per file
- File name matches the primary public declaration
- Test files mirror source structure: `src/test/kotlin/...` matches `src/main/kotlin/...`

## Testing Guidelines

### Test Framework

- **Framework**: Kotest with JUnit 5 runner
- **Style**: Use `StringSpec` for descriptive test names
- **Structure**: Use Given/When/Then comments for clarity

### Example Test Structure

```kotlin
class ExtractTest : StringSpec({
    "extractSimpleIdentifier should return identifier when simpleIdentifier is found" {
        // Given
        val tree = fromText("fun myFunction() = 1")
        val functionTree = getDeclarations(tree).first { it.name == "functionDeclaration" }
        // When
        val identifier = extractSimpleIdentifier(functionTree)
        // Then
        identifier shouldBe "myFunction"
    }
})
```

### Testing Best Practices

1. **Descriptive Test Names**: Use clear, behavior-focused test names
2. **Given/When/Then**: Structure tests with comments
3. **Test Coverage**: Aim for high coverage (reports in `build/reports/jacoco/test/html/index.html`)
4. **Architecture Tests**: Use ArchUnit for architectural constraints
5. **Fixtures**: Reuse test fixtures from `src/test/kotlin/net/dinkla/arclens/Fixture.kt`

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew check  # Runs tests + jacocoTestReport

# View coverage report
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html

# Or use justfile
just test
just coverage-report
```

## Build & Development

### Prerequisites

- **Java 17+**: Required for building dependencies (kotlin-grammar-tools)
- **JVM 21**: Project targets JVM 21
- **Gradle**: Included via wrapper (`./gradlew`)
- **Optional Tools**:
  - [just](https://github.com/casey/just): Convenience commands
  - [ktlint](https://github.com/pinterest/ktlint): Code formatting
  - [mermaid-cli](https://github.com/mermaid-js/mermaid-cli): Diagram conversion

### Common Tasks

Use the `justfile` for common operations:

```bash
just build          # Build and run all checks
just clean          # Clean build artifacts
just lint           # Run ktlint formatting
just test           # Run tests
just coverage-report # Open coverage report
just run *args      # Run the application with arguments
just refresh        # Update dependency versions
```

Or use Gradle directly:

```bash
./gradlew check          # Build, test, and run all checks
./gradlew test           # Run tests
./gradlew jacocoTestReport # Generate coverage report
./gradlew refreshVersions # Update dependency versions
```

### Building Custom Dependencies

The project requires custom libraries that must be built locally:

1. **Kotlin Grammar Tools**: https://github.com/Kotlin/grammar-tools
2. **Kotlin Spec**: https://github.com/Kotlin/kotlin-spec

Run the installation script:

```bash
bin/install-libs.sh
```

**Note**: Requires Java 17 to build these dependencies.

## Code Review Checklist

When reviewing or contributing code:

- [ ] Code follows official Kotlin style guidelines
- [ ] All tests pass (`./gradlew test`)
- [ ] No detekt violations (`./gradlew detekt`)
- [ ] Code is properly formatted (`./gradlew ktlintFormat` or `just lint`)
- [ ] New code has appropriate test coverage
- [ ] Visibility modifiers are appropriate (use `internal` for module-private APIs)
- [ ] Code follows existing patterns and conventions
- [ ] Dependencies use refreshVersions (add to `gradle/libs.versions.toml`)

## Architecture Patterns

### Command Pattern

CLI commands extend `AbstractCommand`:

```kotlin
class MyCommand : AbstractCommand(
    help = "Description of what this command does",
    name = "my-command"
) {
    override fun run() {
        val project = loadProject()
        // Command logic here
    }
}
```

### Domain Models

Domain models are serializable data classes:

```kotlin
@Serializable
data class MyDomainObject(
    val property: String,
    val nested: NestedObject,
)
```

### Analysis Functions

Analysis functions are top-level functions that take domain objects:

```kotlin
fun analyzeSomething(project: Project): List<AnalyzedResult> {
    // Analysis logic
}
```

## Common Pitfalls

1. **Don't ignore detekt violations**: The build fails if `maxIssues > 0`
2. **Don't use wildcard imports** (except allowed patterns like `java.util.*`)
3. **Don't use magic numbers** in production code (exceptions: -1, 0, 1, 2, 3, 4)
4. **Don't skip tests**: Maintain high test coverage
5. **Don't forget visibility modifiers**: Use `internal` for module-private APIs

## Resources

- **README.md**: User-facing documentation and usage examples
- **detekt.yml**: Full detekt configuration with all rules
- **gradle/libs.versions.toml**: All dependency versions
- **justfile**: Common development tasks

## Getting Help

1. Check existing code patterns in similar files
2. Review test files for usage examples
3. Check detekt/ktlint outputs for style issues
4. Review the README for project context
