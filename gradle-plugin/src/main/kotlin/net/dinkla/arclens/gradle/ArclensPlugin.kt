package net.dinkla.arclens.gradle

import net.dinkla.arclens.gradle.tasks.ArclensAnalyzeTask
import net.dinkla.arclens.gradle.tasks.ArclensParseTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

/**
 * Gradle plugin for arclens-kt static analysis.
 *
 * Provides tasks for parsing Kotlin source code and generating
 * various analysis reports and diagrams.
 */
class ArclensPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Create main extension
        val extension = project.extensions.create<ArclensExtension>("arclens")

        // Configure defaults
        extension.sourceDirs.convention(
            project.provider {
                val defaultSrc = project.file("src/main/kotlin")
                if (defaultSrc.exists()) listOf(defaultSrc) else emptyList()
            },
        )
        extension.outputDir.convention(project.layout.buildDirectory.dir("arclens"))

        // Configure report defaults
        configureReportDefaults(extension.reports)

        // Register parse task
        val parseTask =
            project.tasks.register<ArclensParseTask>("arclensParse") {
                group = TASK_GROUP
                description = "Parse Kotlin source files and generate analysis model"
                sourceDirs.set(extension.sourceDirs)
                outputFile.set(extension.outputDir.file("model.json"))
            }

        // Register analyze task
        project.tasks.register<ArclensAnalyzeTask>("arclensAnalyze") {
            group = TASK_GROUP
            description = "Run all configured arclens analyses"
            dependsOn(parseTask)
            modelFile.set(parseTask.flatMap { it.outputFile })
            outputDir.set(extension.outputDir)
            // Connect report properties from extension to task
            reports.classStatistics.set(extension.reports.classStatistics)
            reports.fileStatistics.set(extension.reports.fileStatistics)
            reports.packageStatistics.set(extension.reports.packageStatistics)
            reports.packageCoupling.set(extension.reports.packageCoupling)
            reports.packages.set(extension.reports.packages)
            reports.mermaidClassDiagram.set(extension.reports.mermaidClassDiagram)
            reports.mermaidImportDiagram.set(extension.reports.mermaidImportDiagram)
            reports.mermaidCouplingDiagram.set(extension.reports.mermaidCouplingDiagram)
            reports.includeAllLibraries.set(extension.reports.includeAllLibraries)
            reports.includePrivateDeclarations.set(extension.reports.includePrivateDeclarations)
            reports.largeClasses.set(extension.reports.largeClasses)
            reports.largeClassThreshold.set(extension.reports.largeClassThreshold)
            reports.longMethods.set(extension.reports.longMethods)
            reports.longMethodThreshold.set(extension.reports.longMethodThreshold)
            reports.deepInheritance.set(extension.reports.deepInheritance)
            reports.deepInheritanceThreshold.set(extension.reports.deepInheritanceThreshold)
        }

        // Register aggregate task
        project.tasks.register("arclens") {
            group = TASK_GROUP
            description = "Run arclens parsing and analysis"
            dependsOn("arclensAnalyze")
        }
    }

    private fun configureReportDefaults(reports: ArclensReportExtension) {
        reports.classStatistics.convention(true)
        reports.fileStatistics.convention(true)
        reports.packageStatistics.convention(true)
        reports.packageCoupling.convention(true)
        reports.packages.convention(false)
        reports.mermaidClassDiagram.convention(true)
        reports.mermaidImportDiagram.convention(true)
        reports.mermaidCouplingDiagram.convention(true)
        reports.includeAllLibraries.convention(false)
        reports.includePrivateDeclarations.convention(false)
        reports.largeClasses.convention(true)
        reports.largeClassThreshold.convention(DEFAULT_LARGE_CLASS_THRESHOLD)
        reports.longMethods.convention(true)
        reports.longMethodThreshold.convention(DEFAULT_LONG_METHOD_THRESHOLD)
        reports.deepInheritance.convention(true)
        reports.deepInheritanceThreshold.convention(DEFAULT_DEEP_INHERITANCE_THRESHOLD)
    }

    companion object {
        const val TASK_GROUP = "arclens analysis"
        const val DEFAULT_LARGE_CLASS_THRESHOLD = 10
        const val DEFAULT_LONG_METHOD_THRESHOLD = 60
        const val DEFAULT_DEEP_INHERITANCE_THRESHOLD = 3
    }
}
