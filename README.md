# arclens-kt

arclens-kt is a static analysis tool for Kotlin programs (formerly kotlin-nkp).

## Introduction

**arclens** helps you understand and assess the architecture and structure of your Kotlin codebase by analyzing package dependencies, class hierarchies, and import relationships. It generates metrics and visual diagrams that aid in architectural decision-making, identifying coupling issues, and planning refactoring efforts.

### How to Use arclens

1. **Parse your project** - Convert your Kotlin source code into an analyzable JSON model
2. **Generate metrics** - Calculate package coupling, class statistics, and file-level metrics
3. **Visualize dependencies** - Create Mermaid diagrams showing class hierarchies, import flows, and package coupling
4. **Analyze architecture** - Use the metrics to identify unstable packages, high coupling, and architectural issues

### Use Cases

- **Architectural Assessment**: Understand package structure and dependencies in your codebase
- **Refactoring Planning**: Identify unstable packages and high coupling that need attention
- **Documentation**: Generate visual diagrams of your code structure
- **Code Review**: Get insights into package organization and dependencies
- **Migration Planning**: Understand dependencies before large-scale refactoring

## Capabilities

| Capability | Supported | Not Supported |
|-----------|----------|---------------|
| **Architectural Analysis** |
| Package dependency analysis | ✅ | |
| Package coupling metrics (Ca, Ce, I) | ✅ | |
| Import dependency tracking | ✅ | |
| Class hierarchy analysis | ✅ | |
| **Code Quality Metrics** |
| Class-level statistics | ✅ | |
| File-level statistics | ✅ | |
| Lines of code metrics | ✅ | |
| Cyclomatic complexity | ✅ | |
| Code duplication detection | | ❌ |
| **Dependency Analysis** |
| Package import relationships | ✅ | |
| Class inheritance trees | ✅ | |
| Circular dependency detection | ✅ | |
| Dependency distance metrics | | ❌ |
| **Visualization** |
| Mermaid class diagrams | ✅ | |
| Mermaid import flow diagrams | ✅ | |
| Mermaid coupling diagrams | ✅ | |
| **Temporal Analysis** |
| Change frequency analysis | | ❌ |
| Historical metric trends | | ❌ |
| **Code Smells** |
| Large class detection | ✅ | |
| Long method detection | ✅ | |
| Deep inheritance detection | ✅ | |
| Complex method detection | ✅ | |

**Summary**: arclens excels at **architectural and structural analysis** (package coupling, dependencies, class hierarchies) and provides **code smell detection** (large classes, long methods, deep inheritance, cyclomatic complexity). Not yet supported: code duplication and temporal analysis.

## Features

You can run the program with `bin/arclens.sh` or with `just run`.

```shell
$ bin/arclens.sh -h 
Usage: arclens [<options>] <command> [<args>]...

Options:
  -v, --version  Show the version and exit
  -h, --help     Show this message and exit

Commands:
  parse                     Parse a source directory and generate a model file.
  circular-dependencies     Detect circular dependencies between packages
  class-statistics          Class statistics
  complex-methods           Detect functions with high cyclomatic complexity
  deep-inheritance          Detect classes with deep inheritance hierarchies
  file-statistics           File statistics and imports report
  large-classes             Detect classes with too many declarations
  long-methods              Detect functions with too many lines
  mermaid-class-diagram     Mermaid class diagram
  mermaid-coupling-diagram  Generate a Mermaid coupling diagram from code analysis
  mermaid-import-diagram    Mermaid import diagram
  package-coupling          Generate package coupling metrics
  package-statistics        Package statistics
  packages                  Packages report
  search                    Search for a class by name
```

## Installation

### Prerequisites

- Java 21+ (JVM 21 target runtime)
- Gradle (included via wrapper)
- [just](https://github.com/casey/just) (optional, for convenience commands)
- [ktlint](https://github.com/pinterest/ktlint) (optional, for code formatting)

### Optional Tools

- [mermaid-cli](https://github.com/mermaid-js/mermaid-cli) - useful for converting Mermaid diagrams to SVG/HTML

## Usage

The first step is to parse the files in a directory to a JSON file.

```sh
$ bin/arclens.sh parse /repositories/ray-tracer-challenge/src/main/kotlin generated/model.json
```

### Multi-Source Directory Support

For projects with multiple source directories (e.g., Kotlin Multiplatform), use the `--sources` option:

```sh
$ bin/arclens.sh parse src/main/kotlin --sources=src/commonMain/kotlin,src/jvmMain/kotlin generated/model.json
```

Use this JSON file in the analysis steps as input.

### Examples

Generate a Mermaid class diagram:
```sh
$ bin/arclens.sh mermaid-class-diagram generated/model.json > generated/class-diagram.mermaid
```

Generate statistics:
```sh
$ bin/arclens.sh class-statistics generated/model.json > generated/class-statistics.json
$ bin/arclens.sh file-statistics generated/model.json > generated/file-statistics.json
$ bin/arclens.sh file-statistics --include-private-declarations generated/model.json > generated/file-statistics-full.json
$ bin/arclens.sh package-statistics generated/model.json > generated/package-statistics.json
```

Generate diagrams:
```sh
$ bin/arclens.sh mermaid-import-diagram generated/model.json > generated/import-diagram.mermaid
$ bin/arclens.sh mermaid-import-diagram --include-all-libraries generated/model.json > generated/import-diagram-all.mermaid
$ bin/arclens.sh mermaid-coupling-diagram generated/model.json > generated/coupling-diagram.mermaid
$ bin/arclens.sh mermaid-coupling-diagram --include-all-libraries generated/model.json > generated/coupling-diagram-all.mermaid
```

Search for classes:
```sh
$ bin/arclens.sh search generated/model.json MyClass
```

Detect circular dependencies:
```sh
$ bin/arclens.sh circular-dependencies generated/model.json > generated/circular-dependencies.json
$ bin/arclens.sh circular-dependencies --include-all-libraries generated/model.json > generated/circular-dependencies-all.json
```

Detect code smells:
```sh
$ bin/arclens.sh large-classes generated/model.json > generated/large-classes.json
$ bin/arclens.sh large-classes -t 5 generated/model.json > generated/large-classes.json
$ bin/arclens.sh long-methods generated/model.json > generated/long-methods.json
$ bin/arclens.sh long-methods -t 30 generated/model.json > generated/long-methods.json
$ bin/arclens.sh deep-inheritance generated/model.json > generated/deep-inheritance.json
$ bin/arclens.sh deep-inheritance -t 2 generated/model.json > generated/deep-inheritance.json
$ bin/arclens.sh complex-methods generated/model.json > generated/complex-methods.json
$ bin/arclens.sh complex-methods -t 10 generated/model.json > generated/complex-methods.json
```

List packages:
```sh
$ bin/arclens.sh packages generated/model.json > generated/packages.json
```

### Examples of the mermaid diagrams

#### Coupling diagram

![mermaid-coupling-diagram.webp](docs/mermaid-coupling-diagram.webp)

#### Import diagram

![mermaid-import-diagram.webp](docs/mermaid-import-diagram.webp)

#### Class diagram

![mermaid-class-diagram.webp](docs/mermaid-class-diagram.webp)

## Gradle Plugin

arclens is also available as a Gradle plugin for easy integration into your build process.

### Installation

Add the plugin to your project's `build.gradle.kts`:

```kotlin
plugins {
    id("net.dinkla.arclens") version "0.1"
}
```

### Configuration

```kotlin
arclens {
    // Source directories to analyze (defaults to src/main/kotlin)
    sourceDirs.set(listOf(
        file("src/main/kotlin"),
        file("src/commonMain/kotlin")
    ))

    // Output directory (defaults to build/arclens)
    outputDir.set(layout.buildDirectory.dir("arclens"))

    // Configure which reports to generate
    reports {
        classStatistics.set(true)       // JSON class statistics
        fileStatistics.set(true)        // JSON file statistics
        packageStatistics.set(true)     // JSON package statistics
        packageCoupling.set(true)       // JSON coupling metrics
        packages.set(false)             // JSON packages report
        mermaidClassDiagram.set(true)   // Mermaid class diagram
        mermaidImportDiagram.set(true)  // Mermaid import diagram
        mermaidCouplingDiagram.set(true) // Mermaid coupling diagram
        includeAllLibraries.set(false)  // Include external libraries in diagrams
        includePrivateDeclarations.set(false) // Include private declarations

        // Code smell detection
        largeClasses.set(true)          // Detect large classes
        largeClassThreshold.set(10)     // Declaration count threshold
        longMethods.set(true)           // Detect long methods
        longMethodThreshold.set(60)     // Line count threshold
        deepInheritance.set(true)       // Detect deep inheritance
        deepInheritanceThreshold.set(3) // Inheritance depth threshold
        complexMethods.set(true)        // Detect complex methods
        complexMethodThreshold.set(15)  // Cyclomatic complexity threshold
    }
}
```

### Tasks

| Task | Description |
|------|-------------|
| `arclens` | Run all arclens tasks (parse + analyze) |
| `arclensParse` | Parse Kotlin source files and generate model.json |
| `arclensAnalyze` | Run all configured analyses on the parsed model |

### Example

```sh
# Run all arclens analysis
./gradlew arclens

# Output is in build/arclens/
ls build/arclens/
# model.json class-statistics.json file-statistics.json ...
```

## Building and Developing

### Build the project and run the tests

```sh
$ ./gradlew check
```

### View Test Coverage

```sh
$ ./gradlew jacocoTestReport
$ open build/reports/jacoco/test/html/index.html
```

### Dependencies

The project uses [refreshVersions](https://splitties.github.io/refreshVersions/) for dependency management.

To update dependency versions:

```sh
$ ./gradlew refreshVersions
```

## Appendix: Generated Outputs

### `generated/model.json`
- Root `directory` plus a `files` array. Each entry captures a Kotlin file with `filePath`, `packageName`, `imports`, and `declarations` (classes, functions, properties, type aliases). Declarations are serialized using the domain model (`ClassSignature`, `FunctionSignature`, etc.), matching the Kotlin AST produced by the parser.

### `generated/class-statistics.json`
- JSON array where each item summarizes a class-like declaration. Fields: `className`, `packageName`, optional `classModifier`/`inheritanceModifier`/`visibilityModifier`, and a `metrics` object with counts (parameters, superTypes, declarations, classes, functions, properties, aliases, superClasses, subClasses).

### `generated/file-statistics.json`
- JSON array of per-file summaries. Each item includes `filePath`, `imports` (list of fully qualified names), `declarations` with names and optional `visibilityModifier`, `metrics` (counts of imports, declarations, classes, functions, properties, aliases), and `coupling` (afferentCoupling, efferentCoupling, instability).

### `generated/package-statistics.json`
- JSON array of package-level rollups. Each entry has `packageName`, `importedElements` (distinct imports), `importStatistics` (total/distinct and per-relationship counts), and `declarationStatistics` (files, functions, properties, classes, typeAliases).

### `generated/package-coupling.json`
- JSON array listing each package with its `imports` (packages it depends on) and a `coupling` object (afferentCoupling, efferentCoupling, instability). Useful for spotting stable/unstable packages.

### `generated/packages.json`
- JSON array of packages. Each package contains `packageName` and its `files`; every file repeats `filePath`, `packageName`, `imports`, and fully expanded `declarations` (including nested declarations), mirroring the source structure.

### `generated/search.json`
- JSON object that captures a search result: `classes` (matches), `superClasses`, and `subClasses`, each expressed as serialized class signatures with parameters, supertypes, and modifiers.

### `generated/large-classes.json`
- JSON object with large class detection results: `threshold` (the declaration count threshold used), `largeClasses` (array of flagged classes with `className`, `packageName`, `declarations` count), and `totalLargeClasses`.

### `generated/long-methods.json`
- JSON object with long method detection results: `threshold` (the line count threshold used), `longMethods` (array of flagged functions with `functionName`, `className` (null for top-level), `filePath`, `lineCount`), and `totalLongMethods`.

### `generated/deep-inheritance.json`
- JSON object with deep inheritance detection results: `threshold` (the depth threshold used), `deeplyInheritedClasses` (array with `className`, `packageName`, `inheritanceDepth`), and `totalDeeplyInherited`.

### `generated/complex-methods.json`
- JSON object with complex method detection results: `threshold` (the cyclomatic complexity threshold used), `complexMethods` (array of flagged functions with `functionName`, `className` (null for top-level), `filePath`, `cyclomaticComplexity`), and `totalComplexMethods`.

### `generated/circular-dependencies.json`
- JSON object containing circular dependency analysis results:
  - `cycles`: Array of detected cycles, each containing a list of `packages` involved in the cycle
  - `hasCycles`: Boolean indicating if any cycles were found
  - `totalCycles`: Number of cycles detected
  - `packagesInCycles`: Set of all packages that participate in at least one cycle
- Uses Tarjan's algorithm to find strongly connected components (SCCs) in the package dependency graph

### `generated/mermaid-class-diagram.mermaid`
- Mermaid definition for the class diagram of the analyzed project. Shows classes, properties, and relationships; ready for `mermaid-cli` or any Mermaid renderer.

### `generated/mermaid-import-diagram.mermaid`
- Mermaid graph describing import relationships between packages in the project, excluding external libraries.

### `generated/mermaid-import-all-diagram.mermaid`
- Same structure as the import diagram but includes external/library packages to show full dependency edges.

### `generated/mermaid-coupling-diagram.mermaid`
- Mermaid graph showing package-level coupling within the project, annotated with instability ranges.

### `generated/mermaid-coupling-all-diagram.mermaid`
- Coupling diagram that also includes external/library packages, useful for seeing outbound dependencies beyond the codebase.

(c) 2023 - 2026 Jörn Dinkla https://www.dinkla.net
