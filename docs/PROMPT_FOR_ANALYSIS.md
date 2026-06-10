# Architecture Analysis Prompt

Use this prompt when sending the contents of the `generated/` folder to an AI to review another Kotlin repository.

## What You’re Receiving
- `model.json`: Full serialized model of the parsed Kotlin sources. Root fields: `directory` and `files`. Each file entry contains:
  - `filePath`, `packageName`
  - `imports`: list of fully-qualified import names
  - `declarations`: serialized domain objects (`ClassSignature`, `FunctionSignature`, `Property`, `TypeAlias`, etc.) including modifiers, parameters, and nested declarations
- `class-statistics.json`: Array of class-like declarations with `className`, `packageName`, optional modifiers, and `metrics` (parameters, superTypes, declarations, classes, functions, properties, aliases, superClasses, subClasses).
- `file-statistics.json`: Array of file summaries with `filePath`, `imports`, `declarations` (name + optional `visibilityModifier`), `metrics` (imports/declarations/classes/functions/properties/aliases), and `coupling` (afferentCoupling, efferentCoupling, instability).
- `package-statistics.json`: Array of package rollups containing `packageName`, `importedElements`, `importStatistics` (total/distinct plus sub/super/side/other package counts), and `declarationStatistics` (files, functions, properties, classes, typeAliases).
- `package-coupling.json`: Array of packages with their dependent `imports` (package names) and `coupling` (afferentCoupling `Ca`, efferentCoupling `Ce`, instability `I = Ce / (Ca + Ce)`).
- `packages.json`: Array of packages, each with `packageName` and `files`; every file repeats `filePath`, `packageName`, `imports`, and fully expanded `declarations` (including nested declarations).
- `search.json`: Object capturing a class-name search result with `classes`, `superClasses`, and `subClasses` arrays, each containing serialized class signatures.
- Mermaid diagrams:
  - `mermaid-class-diagram.mermaid`: Classes, properties, and relationships.
  - `mermaid-import-diagram.mermaid`: Package import graph (project-only).
  - `mermaid-import-all-diagram.mermaid`: Package import graph including external/libraries.
  - `mermaid-coupling-diagram.mermaid`: Package coupling graph for the project.
  - `mermaid-coupling-all-diagram.mermaid`: Coupling graph including external/libraries.

## How to Analyze
1. **Package coupling:** Use `package-coupling.json` (`Ca`, `Ce`, `instability`) to find unstable packages (instability near 1.0) that many others depend on, or very stable packages (instability near 0) with many dependents that might be hard to change.
2. **Imports and dependencies:** Use `package-statistics.json` and `packages.json` to see cross-package import patterns and hotspots. Look for heavy side-package or other-package imports.
3. **File-level surface area:** Use `file-statistics.json` to find files with many declarations/imports and high efferent coupling (likely god files).
4. **Class design:** Use `class-statistics.json` to spot deep inheritance counts or classes with many members/parameters.
5. **Diagrams:** Use the Mermaid files to visualize coupling/import flows and class structure. Convert with `mermaid-cli` or any Mermaid renderer if needed.
6. **Model completeness:** If parsing errors were reported separately, note that affected files may be missing or partial; interpret metrics accordingly.

## What to Return
Provide an architecture-oriented assessment: identify risky dependencies, unstable packages, potential layering issues, god files/classes, and notable hotspots. Base observations on the data structures above; cite specific packages/classes/files and the relevant metrics or diagram observations.***
