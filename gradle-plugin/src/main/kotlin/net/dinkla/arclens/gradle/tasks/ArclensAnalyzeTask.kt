package net.dinkla.arclens.gradle.tasks

import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.ClassStatistics
import net.dinkla.arclens.analysis.DeclarationFilter
import net.dinkla.arclens.analysis.FileStatistics
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
        if (reports.classStatistics.get()) {
            generateClassStatistics(project, output)
            generatedCount++
        }

        if (reports.fileStatistics.get()) {
            generateFileStatistics(project, output, reports.includePrivateDeclarations.get())
            generatedCount++
        }

        if (reports.packageStatistics.get()) {
            generatePackageStatistics(project, output)
            generatedCount++
        }

        if (reports.packageCoupling.get()) {
            generatePackageCoupling(project, output, reports.includeAllLibraries.get())
            generatedCount++
        }

        if (reports.packages.get()) {
            generatePackages(project, output)
            generatedCount++
        }

        // Mermaid Diagrams
        if (reports.mermaidClassDiagram.get()) {
            generateMermaidClassDiagram(project, output)
            generatedCount++
        }

        if (reports.mermaidImportDiagram.get()) {
            generateMermaidImportDiagram(project, output, reports.includeAllLibraries.get())
            generatedCount++
        }

        if (reports.mermaidCouplingDiagram.get()) {
            generateMermaidCouplingDiagram(project, output, reports.includeAllLibraries.get())
            generatedCount++
        }

        logger.lifecycle("Arclens: Generated $generatedCount reports in ${output.absolutePath}")
    }

    private fun generateClassStatistics(
        project: Project,
        output: File,
    ) {
        val stats = ClassStatistics.from(project)
        val file = File(output, "class-statistics.json")
        file.writeText(json.encodeToString(stats))
        logger.info("Arclens: Generated ${file.name}")
    }

    private fun generateFileStatistics(
        project: Project,
        output: File,
        includePrivate: Boolean,
    ) {
        val filter = DeclarationFilter.select(includePrivate)
        val stats = FileStatistics.from(project, filter)
        val file = File(output, "file-statistics.json")
        file.writeText(json.encodeToString(stats))
        logger.info("Arclens: Generated ${file.name}")
    }

    private fun generatePackageStatistics(
        project: Project,
        output: File,
    ) {
        val stats = packagesStatistics(project)
        val file = File(output, "package-statistics.json")
        file.writeText(json.encodeToString(stats))
        logger.info("Arclens: Generated ${file.name}")
    }

    private fun generatePackageCoupling(
        project: Project,
        output: File,
        includeAll: Boolean,
    ) {
        val imports =
            if (includeAll) {
                PackageImports.allImports(project)
            } else {
                PackageImports.filteredImports(project)
            }
        val report = combinedReport(imports)
        val file = File(output, "package-coupling.json")
        file.writeText(json.encodeToString(report))
        logger.info("Arclens: Generated ${file.name}")
    }

    private fun generatePackages(
        project: Project,
        output: File,
    ) {
        val packages = project.packages()
        val file = File(output, "packages.json")
        file.writeText(json.encodeToString(packages))
        logger.info("Arclens: Generated ${file.name}")
    }

    private fun generateMermaidClassDiagram(
        project: Project,
        output: File,
    ) {
        val diagram = mermaidClassDiagram(project)
        val file = File(output, "class-diagram.mermaid")
        file.writeText(diagram)
        logger.info("Arclens: Generated ${file.name}")
    }

    private fun generateMermaidImportDiagram(
        project: Project,
        output: File,
        includeAll: Boolean,
    ) {
        val diagram = mermaidImportsFlowDiagram(project, !includeAll)
        val fileName = if (includeAll) "import-diagram-all.mermaid" else "import-diagram.mermaid"
        val file = File(output, fileName)
        file.writeText(diagram)
        logger.info("Arclens: Generated ${file.name}")
    }

    private fun generateMermaidCouplingDiagram(
        project: Project,
        output: File,
        includeAll: Boolean,
    ) {
        val imports =
            if (includeAll) {
                PackageImports.allImports(project)
            } else {
                PackageImports.filteredImports(project)
            }
        val report = combinedReport(imports)
        val diagram = MermaidCouplingDiagram(report).generate()
        val fileName = if (includeAll) "coupling-diagram-all.mermaid" else "coupling-diagram.mermaid"
        val file = File(output, fileName)
        file.writeText(diagram)
        logger.info("Arclens: Generated ${file.name}")
    }
}
