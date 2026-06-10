package net.dinkla.arclens.report

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.analysis.CircularDependenciesReport
import net.dinkla.arclens.analysis.ComplexMethodReport
import net.dinkla.arclens.analysis.Cycle
import net.dinkla.arclens.analysis.DeepInheritanceReport
import net.dinkla.arclens.analysis.LargeClass
import net.dinkla.arclens.analysis.LargeClassReport
import net.dinkla.arclens.analysis.LongMethodReport
import net.dinkla.arclens.analysis.PackageCouplingItem
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.FunctionSignature
import net.dinkla.arclens.domain.kotlinlang.Import
import net.dinkla.arclens.domain.kotlinlang.ImportedElement
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.domain.kotlinlang.Property
import net.dinkla.arclens.domain.kotlinlang.Type
import net.dinkla.arclens.domain.statistics.Coupling

private val pkgA = PackageName("com.example.a")
private val pkgB = PackageName("com.example.b")

private val fileA =
    KotlinFile(
        filePath = FilePath("/src/A.kt"),
        packageName = pkgA,
        declarations =
            listOf(
                FunctionSignature("foo"),
                Property("bar", Type("String")),
                ClassSignature("MyClass"),
            ),
        imports = listOf(Import(ImportedElement("com.example.b.Beta"))),
    )

private val fileB =
    KotlinFile(
        filePath = FilePath("/src/B.kt"),
        packageName = pkgB,
    )

private val sampleProject = Project(directory = "/src", files = listOf(fileA, fileB), parseTimestamp = 0L)

private fun emptyLargeClassReport(threshold: Int = 10) =
    LargeClassReport(threshold = threshold, largeClasses = emptyList(), totalLargeClasses = 0)

private fun emptyLongMethodReport(threshold: Int = 60) =
    LongMethodReport(threshold = threshold, longMethods = emptyList(), totalLongMethods = 0)

private fun emptyDeepInheritanceReport(threshold: Int = 3) =
    DeepInheritanceReport(threshold = threshold, deeplyInheritedClasses = emptyList(), totalDeeplyInherited = 0)

private fun emptyComplexMethodReport(threshold: Int = 15) =
    ComplexMethodReport(threshold = threshold, complexMethods = emptyList(), totalComplexMethods = 0)

private fun cleanCircularDeps() =
    CircularDependenciesReport(
        cycles = emptyList(),
        hasCycles = false,
        totalCycles = 0,
        packagesInCycles = emptySet(),
    )

private data class SmellReports(
    val largeClasses: LargeClassReport = emptyLargeClassReport(),
    val longMethods: LongMethodReport = emptyLongMethodReport(),
    val deepInheritance: DeepInheritanceReport = emptyDeepInheritanceReport(),
    val complexMethods: ComplexMethodReport = emptyComplexMethodReport(),
)

private fun reportData(
    project: Project = sampleProject,
    smells: SmellReports = SmellReports(),
    circularDependencies: CircularDependenciesReport = cleanCircularDeps(),
    couplingData: List<PackageCouplingItem> = emptyList(),
) = ReportData(
    project = project,
    packageStatistics = emptyList(),
    classStatistics = emptyList(),
    fileStatistics = emptyList(),
    couplingData = couplingData,
    circularDependencies = circularDependencies,
    mermaidClassDiagram = "",
    mermaidImportDiagram = "",
    mermaidCouplingDiagram = "",
    largeClasses = smells.largeClasses,
    longMethods = smells.longMethods,
    deepInheritance = smells.deepInheritance,
    complexMethods = smells.complexMethods,
)

class ReportDataTest :
    StringSpec({
        // totalFiles / totalPackages derived properties

        "totalFiles should reflect project file count" {
            // Given
            val data = reportData()
            // When / Then
            data.totalFiles shouldBe 2
        }

        "totalPackages should reflect distinct packages in project" {
            // Given
            val data = reportData()
            // When / Then
            data.totalPackages shouldBe 2
        }

        "totalFunctions should count top-level functions across files" {
            // Given
            val data = reportData()
            // When / Then
            data.totalFunctions shouldBe 1
        }

        "totalProperties should count top-level properties across files" {
            // Given
            val data = reportData()
            // When / Then
            data.totalProperties shouldBe 1
        }

        // totalCodeSmells

        "totalCodeSmells should be zero when all smell reports are empty" {
            // Given
            val data = reportData()
            // When / Then
            data.totalCodeSmells shouldBe 0
        }

        "totalCodeSmells should aggregate all four smell types" {
            // Given
            val lc = LargeClassReport(10, listOf(LargeClass("A", pkgA, 12)), 1)
            val lm = LongMethodReport(60, emptyList(), 0)
            val di = DeepInheritanceReport(3, emptyList(), 0)
            val cm = ComplexMethodReport(15, emptyList(), 0)
            val data =
                reportData(
                    smells =
                        SmellReports(
                            largeClasses = lc,
                            longMethods = lm,
                            deepInheritance = di,
                            complexMethods = cm,
                        ),
                )
            // When / Then
            data.totalCodeSmells shouldBe 1
        }

        // HealthScore — overallStatus

        "overallStatus should be GOOD when no issues" {
            // Given
            val data = reportData()
            // When / Then
            data.healthScore.overallStatus shouldBe HealthStatus.GOOD
        }

        "overallStatus should be WARNING when circular dependencies exist" {
            // Given
            val circDeps =
                CircularDependenciesReport(
                    cycles = listOf(Cycle(listOf(pkgA, pkgB))),
                    hasCycles = true,
                    totalCycles = 1,
                    packagesInCycles = setOf(pkgA, pkgB),
                )
            val data = reportData(circularDependencies = circDeps)
            // When / Then
            data.healthScore.overallStatus shouldBe HealthStatus.WARNING
        }

        "overallStatus should be CAUTION when code smells exist" {
            // Given
            val lc = LargeClassReport(10, listOf(LargeClass("BigClass", pkgA, 20)), 1)
            val data = reportData(smells = SmellReports(largeClasses = lc))
            // When / Then
            data.healthScore.overallStatus shouldBe HealthStatus.CAUTION
        }

        "overallStatus should be CAUTION when high instability packages exist" {
            // Given
            val highInstabilityCoupling =
                PackageCouplingItem(
                    packageName = pkgA,
                    imports = setOf(pkgB),
                    coupling = Coupling(afferentCoupling = 0, efferentCoupling = 1),
                )
            val data = reportData(couplingData = listOf(highInstabilityCoupling))
            // When / Then
            data.healthScore.overallStatus shouldBe HealthStatus.CAUTION
        }

        // HealthScore average instability

        "averageInstability should be 0.0 when no coupling data" {
            // Given
            val data = reportData(couplingData = emptyList())
            // When / Then
            data.healthScore.averageInstability shouldBe 0.0
        }

        "averageInstability should average coupling instability values" {
            // Given
            val coupling1 = PackageCouplingItem(pkgA, emptySet(), Coupling(1, 0))
            val coupling2 = PackageCouplingItem(pkgB, emptySet(), Coupling(0, 1))
            val data = reportData(couplingData = listOf(coupling1, coupling2))
            // When / Then
            data.healthScore.averageInstability shouldBe 0.5
        }

        // HealthStatus labels and CSS classes

        "HealthStatus.GOOD should have correct label and cssClass" {
            HealthStatus.GOOD.label shouldBe "Healthy"
            HealthStatus.GOOD.cssClass shouldBe "health-good"
        }

        "HealthStatus.CAUTION should have correct label and cssClass" {
            HealthStatus.CAUTION.label shouldBe "Caution"
            HealthStatus.CAUTION.cssClass shouldBe "health-caution"
        }

        "HealthStatus.WARNING should have correct label and cssClass" {
            HealthStatus.WARNING.label shouldBe "Warning"
            HealthStatus.WARNING.cssClass shouldBe "health-warning"
        }
    })
