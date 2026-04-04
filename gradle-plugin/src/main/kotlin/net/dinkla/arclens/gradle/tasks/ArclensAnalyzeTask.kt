package net.dinkla.arclens.gradle.tasks

import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.ClassStatistics
import net.dinkla.arclens.analysis.ComplexMethodReport
import net.dinkla.arclens.analysis.DeclarationFilter
import net.dinkla.arclens.analysis.DeepInheritanceReport
import net.dinkla.arclens.analysis.FileStatistics
import net.dinkla.arclens.analysis.LargeClassReport
import net.dinkla.arclens.analysis.LongMethodReport
import net.dinkla.arclens.analysis.MermaidCouplingDiagram
import net.dinkla.arclens.analysis.PackageImports
import net.dinkla.arclens.analysis.combinedReport
import net.dinkla.arclens.analysis.mermaidClassDiagram
import net.dinkla.arclens.analysis.mermaidImportsFlowDiagram
import net.dinkla.arclens.analysis.packagesStatistics
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.gradle.ArclensReportExtension
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Task that runs all configured arclens analyses on a parsed model.
 */
abstract class ArclensAnalyzeTask : DefaultTask() {
    @get:InputFile
    abstract val modelFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Nested
    abstract val reports: ArclensReportExtension

    private val json = Json { prettyPrint = true }

    @TaskAction
    fun analyze() {
        val model = modelFile.get().asFile
        if (!model.exists()) {
            logger.error("Arclens: Model file not found: ${model.absolutePath}")
            return
        }

        val project = Json.decodeFromString<Project>(model.readText())
        val output = outputDir.get().asFile
        output.mkdirs()

        var generatedCount = 0

        // JSON Reports
        generatedCount += generateJsonReports(project, output)

        // Code smell detection
        generatedCount += generateCodeSmellReports(project, output)

        // Mermaid Diagrams
        generatedCount += generateMermaidDiagrams(project, output)

        logger.lifecycle("Arclens: Generated $generatedCount reports in ${output.absolutePath}")
    }

    private fun generateJsonReports(
        project: Project,
        output: File,
    ): Int {
        var count = 0
        if (reports.classStatistics.get()) {
            writeReport(output, "class-statistics.json", ClassStatistics.from(project))
            count++
        }
        if (reports.fileStatistics.get()) {
            val filter = DeclarationFilter.select(reports.includePrivateDeclarations.get())
            writeReport(output, "file-statistics.json", FileStatistics.from(project, filter))
            count++
        }
        if (reports.packageStatistics.get()) {
            writeReport(output, "package-statistics.json", packagesStatistics(project))
            count++
        }
        if (reports.packageCoupling.get()) {
            val imports = selectImports(project, reports.includeAllLibraries.get())
            writeReport(output, "package-coupling.json", combinedReport(imports))
            count++
        }
        if (reports.packages.get()) {
            writeReport(output, "packages.json", project.packages())
            count++
        }
        return count
    }

    private fun selectImports(
        project: Project,
        includeAll: Boolean,
    ) = if (includeAll) PackageImports.allImports(project) else PackageImports.filteredImports(project)

    private fun generateMermaidDiagrams(
        project: Project,
        output: File,
    ): Int {
        val includeAll = reports.includeAllLibraries.get()
        var count = 0
        if (reports.mermaidClassDiagram.get()) {
            writeDiagram(output, "class-diagram.mermaid", mermaidClassDiagram(project))
            count++
        }
        if (reports.mermaidImportDiagram.get()) {
            val suffix = if (includeAll) "-all" else ""
            writeDiagram(output, "import-diagram$suffix.mermaid", mermaidImportsFlowDiagram(project, !includeAll))
            count++
        }
        if (reports.mermaidCouplingDiagram.get()) {
            val imports = selectImports(project, includeAll)
            val diagram = MermaidCouplingDiagram(combinedReport(imports)).generate()
            val suffix = if (includeAll) "-all" else ""
            writeDiagram(output, "coupling-diagram$suffix.mermaid", diagram)
            count++
        }
        return count
    }

    private fun writeDiagram(
        output: File,
        fileName: String,
        content: String,
    ) {
        val file = File(output, fileName)
        file.writeText(content)
        logger.info("Arclens: Generated ${file.name}")
    }

    private fun generateCodeSmellReports(
        project: Project,
        output: File,
    ): Int {
        var count = 0
        if (reports.largeClasses.get()) {
            writeReport(output, "large-classes.json", LargeClassReport.from(project, reports.largeClassThreshold.get()))
            count++
        }
        if (reports.longMethods.get()) {
            writeReport(output, "long-methods.json", LongMethodReport.from(project, reports.longMethodThreshold.get()))
            count++
        }
        if (reports.deepInheritance.get()) {
            val threshold = reports.deepInheritanceThreshold.get()
            writeReport(output, "deep-inheritance.json", DeepInheritanceReport.from(project, threshold))
            count++
        }
        if (reports.complexMethods.get()) {
            val threshold = reports.complexMethodThreshold.get()
            writeReport(output, "complex-methods.json", ComplexMethodReport.from(project, threshold))
            count++
        }
        return count
    }

    private inline fun <reified T> writeReport(
        output: File,
        fileName: String,
        report: T,
    ) {
        val file = File(output, fileName)
        file.writeText(json.encodeToString(report))
        logger.info("Arclens: Generated ${file.name}")
    }
}
