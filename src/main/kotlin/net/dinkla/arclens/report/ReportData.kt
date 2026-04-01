package net.dinkla.arclens.report

import net.dinkla.arclens.analysis.AnalyzedPackage
import net.dinkla.arclens.analysis.CircularDependenciesReport
import net.dinkla.arclens.analysis.ClassStatistics
import net.dinkla.arclens.analysis.DeclarationFilter
import net.dinkla.arclens.analysis.FileStatistics
import net.dinkla.arclens.analysis.ImportFilter
import net.dinkla.arclens.analysis.MermaidCouplingDiagram
import net.dinkla.arclens.analysis.PackageCouplingItem
import net.dinkla.arclens.analysis.PackageImports
import net.dinkla.arclens.analysis.combinedReport
import net.dinkla.arclens.analysis.findCircularDependencies
import net.dinkla.arclens.analysis.mermaidClassDiagram
import net.dinkla.arclens.analysis.mermaidImportsFlowDiagram
import net.dinkla.arclens.analysis.packagesStatistics
import net.dinkla.arclens.domain.kotlinlang.Project

private const val INSTABILITY_THRESHOLD_HIGH = 0.7

/**
 * Aggregates all analysis data for HTML report generation.
 */
data class ReportData(
    val project: Project,
    val packageStatistics: List<AnalyzedPackage>,
    val classStatistics: List<ClassStatistics>,
    val fileStatistics: List<FileStatistics>,
    val couplingData: List<PackageCouplingItem>,
    val circularDependencies: CircularDependenciesReport,
    val mermaidClassDiagram: String,
    val mermaidImportDiagram: String,
    val mermaidCouplingDiagram: String,
) {
    val totalFiles: Int get() = project.files.size
    val totalPackages: Int get() = project.packages().size
    val totalClasses: Int get() = classStatistics.size
    val totalFunctions: Int get() = project.flatMap { it.functions }.size
    val totalProperties: Int get() = project.flatMap { it.properties }.size

    val healthScore: HealthScore get() = calculateHealthScore()

    private fun calculateHealthScore(): HealthScore {
        val hasCircularDeps = circularDependencies.hasCycles
        val avgInstability =
            if (couplingData.isNotEmpty()) {
                couplingData.map { it.coupling.instability }.average()
            } else {
                0.0
            }
        val highInstabilityCount = couplingData.count { it.coupling.instability > INSTABILITY_THRESHOLD_HIGH }

        return HealthScore(
            hasCircularDependencies = hasCircularDeps,
            circularDependencyCount = circularDependencies.totalCycles,
            averageInstability = avgInstability,
            highInstabilityPackages = highInstabilityCount,
        )
    }

    companion object {
        fun from(
            project: Project,
            options: ReportOptions,
        ): ReportData {
            val imports =
                if (options.includeAllLibraries) {
                    PackageImports.allImports(project)
                } else {
                    PackageImports.filteredImports(project)
                }

            val declarationFilter = DeclarationFilter.select(options.includePrivateDeclarations)
            val importFilter = ImportFilter.select(options.includeAllLibraries)

            return ReportData(
                project = project,
                packageStatistics = packagesStatistics(project),
                classStatistics = ClassStatistics.from(project),
                fileStatistics = FileStatistics.from(project, declarationFilter, importFilter),
                couplingData = combinedReport(imports),
                circularDependencies = findCircularDependencies(imports),
                mermaidClassDiagram = mermaidClassDiagram(project),
                mermaidImportDiagram = mermaidImportsFlowDiagram(project, !options.includeAllLibraries),
                mermaidCouplingDiagram = MermaidCouplingDiagram(combinedReport(imports)).generate(),
            )
        }
    }
}

/**
 * Health score metrics for the project.
 */
data class HealthScore(
    val hasCircularDependencies: Boolean,
    val circularDependencyCount: Int,
    val averageInstability: Double,
    val highInstabilityPackages: Int,
) {
    val overallStatus: HealthStatus
        get() =
            when {
                hasCircularDependencies -> HealthStatus.WARNING
                highInstabilityPackages > 0 -> HealthStatus.CAUTION
                else -> HealthStatus.GOOD
            }
}

enum class HealthStatus(
    val label: String,
    val cssClass: String,
) {
    GOOD("Healthy", "health-good"),
    CAUTION("Caution", "health-caution"),
    WARNING("Warning", "health-warning"),
}
