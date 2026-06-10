package net.dinkla.arclens.report

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import net.dinkla.arclens.analysis.AnalyzedPackage
import net.dinkla.arclens.analysis.CircularDependenciesReport
import net.dinkla.arclens.analysis.ClassStatistics
import net.dinkla.arclens.analysis.ComplexMethod
import net.dinkla.arclens.analysis.ComplexMethodReport
import net.dinkla.arclens.analysis.Cycle
import net.dinkla.arclens.analysis.DeclarationStatistics
import net.dinkla.arclens.analysis.DeepInheritanceReport
import net.dinkla.arclens.analysis.DeeplyInheritedClass
import net.dinkla.arclens.analysis.FileMetrics
import net.dinkla.arclens.analysis.FileStatistics
import net.dinkla.arclens.analysis.ImportStatistics
import net.dinkla.arclens.analysis.LargeClass
import net.dinkla.arclens.analysis.LargeClassReport
import net.dinkla.arclens.analysis.LongMethod
import net.dinkla.arclens.analysis.LongMethodReport
import net.dinkla.arclens.analysis.PackageCouplingItem
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.Import
import net.dinkla.arclens.domain.kotlinlang.ImportedElement
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.domain.statistics.Coupling

private val pkgAlpha = PackageName("com.example.alpha")
private val pkgBeta = PackageName("com.example.beta")

private val fileAlpha =
    KotlinFile(
        filePath = FilePath("/project/src/alpha/Alpha.kt"),
        packageName = pkgAlpha,
        imports = listOf(Import(ImportedElement("com.example.beta.Beta"))),
    )

private val fileBeta =
    KotlinFile(
        filePath = FilePath("/project/src/beta/Beta.kt"),
        packageName = pkgBeta,
    )

private val testProject =
    Project(
        directory = "/project/src",
        files = listOf(fileAlpha, fileBeta),
        parseTimestamp = 0L,
    )

private val analyzedPackageAlpha =
    AnalyzedPackage(
        packageName = pkgAlpha,
        importedElements = emptySet(),
        importStatistics =
            ImportStatistics(
                total = 1,
                distinct = 1,
                fromSubPackage = 0,
                fromSuperPackage = 0,
                fromSidePackage = 1,
                fromOtherPackage = 0,
            ),
        declarationStatistics =
            DeclarationStatistics(
                files = 1,
                functions = 0,
                properties = 0,
                classes = 0,
                typeAliases = 0,
            ),
    )

private val classStatAlpha =
    ClassStatistics(
        className = "Alpha",
        packageName = pkgAlpha,
        elementType = ClassSignature.Type.CLASS,
    )

private val fileStatAlpha =
    FileStatistics(
        filePath = FilePath("/project/src/alpha/Alpha.kt"),
        imports = emptyList(),
        declarations = emptyList(),
        metrics = FileMetrics(imports = 1, declarations = 0, classes = 0, functions = 0, properties = 0, aliases = 0),
        coupling = Coupling(afferentCoupling = 1, efferentCoupling = 1),
    )

private val couplingAlpha =
    PackageCouplingItem(
        packageName = pkgAlpha,
        imports = setOf(pkgBeta),
        coupling = Coupling(afferentCoupling = 0, efferentCoupling = 1),
    )

private val couplingBeta =
    PackageCouplingItem(
        packageName = pkgBeta,
        imports = emptySet(),
        coupling = Coupling(afferentCoupling = 1, efferentCoupling = 0),
    )

private fun noCircularDeps() =
    CircularDependenciesReport(
        cycles = emptyList(),
        hasCycles = false,
        totalCycles = 0,
        packagesInCycles = emptySet(),
    )

private fun baseReportData(
    largeClasses: LargeClassReport =
        LargeClassReport(threshold = 10, largeClasses = emptyList(), totalLargeClasses = 0),
    longMethods: LongMethodReport = LongMethodReport(threshold = 60, longMethods = emptyList(), totalLongMethods = 0),
    deepInheritance: DeepInheritanceReport =
        DeepInheritanceReport(threshold = 3, deeplyInheritedClasses = emptyList(), totalDeeplyInherited = 0),
    complexMethods: ComplexMethodReport =
        ComplexMethodReport(threshold = 15, complexMethods = emptyList(), totalComplexMethods = 0),
    circularDependencies: CircularDependenciesReport = noCircularDeps(),
    couplingData: List<PackageCouplingItem> = listOf(couplingAlpha, couplingBeta),
) = ReportData(
    project = testProject,
    packageStatistics = listOf(analyzedPackageAlpha),
    classStatistics = listOf(classStatAlpha),
    fileStatistics = listOf(fileStatAlpha),
    couplingData = couplingData,
    circularDependencies = circularDependencies,
    mermaidClassDiagram = "classDiagram\n  Alpha --|> Base",
    mermaidImportDiagram = "flowchart LR\n  alpha --> beta",
    mermaidCouplingDiagram = "flowchart LR\n  alpha -- 1 --> beta",
    largeClasses = largeClasses,
    longMethods = longMethods,
    deepInheritance = deepInheritance,
    complexMethods = complexMethods,
)

private val defaultOptions = ReportOptions(title = "Test Report")

class HtmlReportGeneratorTest :
    StringSpec({
        // Basic document structure

        "generate should produce valid HTML5 document" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "<!DOCTYPE html>"
            html shouldContain "<html lang=\"en\">"
            html shouldContain "</html>"
            html shouldContain "<head>"
            html shouldContain "</head>"
            html shouldContain "<body>"
            html shouldContain "</body>"
        }

        "generate should embed the report title in head and navbar" {
            // Given
            val options = ReportOptions(title = "My Custom Report")
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, options).generate()
            // Then
            html shouldContain "<title>My Custom Report</title>"
            html shouldContain "My Custom Report"
        }

        "generate should escape special characters in title" {
            // Given
            val options = ReportOptions(title = "<Script> & \"Dangerous\"")
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, options).generate()
            // Then
            html shouldNotContain "<title><Script>"
            html shouldContain "&lt;Script&gt;"
            html shouldContain "&amp;"
            html shouldContain "&quot;"
        }

        // Navigation links

        "generate should include navigation links for all sections" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "href=\"#overview\""
            html shouldContain "href=\"#packages\""
            html shouldContain "href=\"#classes\""
            html shouldContain "href=\"#files\""
            html shouldContain "href=\"#coupling\""
            html shouldContain "href=\"#circular-deps\""
            html shouldContain "href=\"#code-smells\""
            html shouldContain "href=\"#diagrams\""
        }

        // Section headings and anchors

        "generate should include all section headings with correct IDs" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "id=\"overview\""
            html shouldContain "id=\"packages\""
            html shouldContain "id=\"classes\""
            html shouldContain "id=\"files\""
            html shouldContain "id=\"coupling\""
            html shouldContain "id=\"circular-deps\""
            html shouldContain "id=\"code-smells\""
            html shouldContain "id=\"diagrams\""
        }

        // Overview section

        "generate should render package name from fixture in packages table" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "com.example.alpha"
        }

        "generate should render class name from fixture in classes table" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "Alpha"
        }

        "generate should show health status Good when no issues" {
            // Given
            val data = baseReportData(couplingData = emptyList())
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "health-good"
            html shouldContain "Healthy"
        }

        "generate should show no circular dependencies message when clean" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "No circular dependencies detected"
        }

        // Circular dependencies section

        "generate should render circular dependency details when cycles exist" {
            // Given
            val cycle = Cycle(packages = listOf(pkgAlpha, pkgBeta))
            val circDeps =
                CircularDependenciesReport(
                    cycles = listOf(cycle),
                    hasCycles = true,
                    totalCycles = 1,
                    packagesInCycles = setOf(pkgAlpha, pkgBeta),
                )
            val data = baseReportData(circularDependencies = circDeps)
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "alert-warning"
            html shouldContain "1 circular dependency cycle"
            html shouldContain "com.example.alpha"
        }

        "generate should show health-warning status when circular deps exist" {
            // Given
            val circDeps =
                CircularDependenciesReport(
                    cycles = listOf(Cycle(packages = listOf(pkgAlpha, pkgBeta))),
                    hasCycles = true,
                    totalCycles = 1,
                    packagesInCycles = setOf(pkgAlpha, pkgBeta),
                )
            val data = baseReportData(circularDependencies = circDeps)
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "health-warning"
            html shouldContain "Warning"
        }

        // Code smells section

        "generate should show no code smells message when none present" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "No code smells detected"
        }

        "generate should render large class in code smells section" {
            // Given
            val largeClass = LargeClass(className = "BigClass", packageName = pkgAlpha, declarations = 20)
            val report = LargeClassReport(threshold = 10, largeClasses = listOf(largeClass), totalLargeClasses = 1)
            val data = baseReportData(largeClasses = report)
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "BigClass"
            html shouldContain "Large Classes (threshold: 10 declarations)"
        }

        "generate should render long method in code smells section" {
            // Given
            val longMethod =
                LongMethod(
                    functionName = "doStuff",
                    className = "MyClass",
                    filePath = FilePath("MyClass.kt"),
                    lineCount = 80,
                )
            val report = LongMethodReport(threshold = 60, longMethods = listOf(longMethod), totalLongMethods = 1)
            val data = baseReportData(longMethods = report)
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "doStuff"
            html shouldContain "Long Methods (threshold: 60 lines)"
        }

        "generate should render deep inheritance in code smells section" {
            // Given
            val deepClass = DeeplyInheritedClass(className = "DeepClass", packageName = pkgAlpha, inheritanceDepth = 5)
            val report =
                DeepInheritanceReport(
                    threshold = 3,
                    deeplyInheritedClasses = listOf(deepClass),
                    totalDeeplyInherited = 1,
                )
            val data = baseReportData(deepInheritance = report)
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "DeepClass"
            html shouldContain "Deep Inheritance (threshold: 3 levels)"
        }

        "generate should render complex methods in code smells section" {
            // Given
            val complexMethod =
                ComplexMethod(
                    functionName = "complexFun",
                    className = null,
                    filePath = FilePath("Util.kt"),
                    cyclomaticComplexity = 20,
                )
            val report =
                ComplexMethodReport(threshold = 15, complexMethods = listOf(complexMethod), totalComplexMethods = 1)
            val data = baseReportData(complexMethods = report)
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "complexFun"
            html shouldContain "Complex Methods (threshold: 15 complexity)"
        }

        "generate should show code smell count and health-caution when smells exist" {
            // Given
            val largeClass = LargeClass(className = "BigClass", packageName = pkgAlpha, declarations = 20)
            val report = LargeClassReport(threshold = 10, largeClasses = listOf(largeClass), totalLargeClasses = 1)
            val data = baseReportData(largeClasses = report)
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "health-caution"
            html shouldContain "Code smells detected"
        }

        // Diagrams section

        "generate should include diagram tab buttons" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "Class Diagram"
            html shouldContain "Import Flow"
            html shouldContain "Coupling"
        }

        "generate should include mermaid blocks for all three diagrams" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "id=\"class-diagram\""
            html shouldContain "id=\"import-diagram\""
            html shouldContain "id=\"coupling-diagram\""
            html shouldContain "class=\"mermaid\""
        }

        "generate should render mermaid diagram content" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "classDiagram"
            html shouldContain "flowchart LR"
        }

        "generate should escape mermaid content with HTML special chars" {
            // Given
            val data =
                baseReportData().copy(
                    mermaidClassDiagram = "classDiagram\n  A <|-- B",
                )
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "&lt;|--"
        }

        "generate should mark first diagram as active and others as inactive" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "id=\"class-diagram\" class=\"diagram-container active\""
            html shouldContain "id=\"import-diagram\" class=\"diagram-container\""
            html shouldContain "id=\"coupling-diagram\" class=\"diagram-container\""
        }

        // Coupling section instability colours

        "generate should use instability-high class for high instability value" {
            // Given
            val highInstability =
                PackageCouplingItem(
                    packageName = pkgAlpha,
                    imports = setOf(pkgBeta),
                    coupling = Coupling(afferentCoupling = 0, efferentCoupling = 1),
                )
            val data = baseReportData(couplingData = listOf(highInstability))
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "instability-high"
        }

        "generate should use instability-low class for zero instability" {
            // Given
            val lowInstability =
                PackageCouplingItem(
                    packageName = pkgBeta,
                    imports = emptySet(),
                    coupling = Coupling(afferentCoupling = 1, efferentCoupling = 0),
                )
            val data = baseReportData(couplingData = listOf(lowInstability))
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "instability-low"
        }

        // JavaScript and CSS presence

        "generate should include mermaid script tag" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "mermaid"
            html shouldContain "mermaid.initialize"
        }

        "generate should include CSS styles" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "<style>"
            html shouldContain "</style>"
            html shouldContain ".navbar"
        }

        // Footer

        "generate should include footer with generated-by text" {
            // Given
            val data = baseReportData()
            // When
            val html = HtmlReportGenerator(data, defaultOptions).generate()
            // Then
            html shouldContain "Generated by arclens-kt"
        }
    })
