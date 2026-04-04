package net.dinkla.arclens.gradle

import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

/**
 * Extension for configuring the arclens plugin.
 *
 * Example usage:
 * ```kotlin
 * arclens {
 *     sourceDirs.set(listOf(file("src/main/kotlin"), file("src/commonMain/kotlin")))
 *     outputDir.set(layout.buildDirectory.dir("arclens"))
 *     reports {
 *         classStatistics.set(true)
 *         fileStatistics.set(true)
 *         packageCoupling.set(true)
 *         mermaidClassDiagram.set(true)
 *         mermaidImportDiagram.set(true)
 *         mermaidCouplingDiagram.set(true)
 *     }
 * }
 * ```
 */
abstract class ArclensExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        /**
         * Source directories to analyze.
         * Defaults to src/main/kotlin if not specified.
         */
        abstract val sourceDirs: ListProperty<java.io.File>

        /**
         * Output directory for generated reports and model.
         * Defaults to build/arclens.
         */
        abstract val outputDir: DirectoryProperty

        /**
         * Report configuration.
         */
        val reports: ArclensReportExtension = objects.newInstance(ArclensReportExtension::class.java)

        /**
         * Configure reports using a DSL block.
         */
        fun reports(action: Action<ArclensReportExtension>) {
            action.execute(reports)
        }
    }

/**
 * Configuration for which reports to generate.
 */
abstract class ArclensReportExtension {
    /** Generate class statistics report (JSON). */
    @get:Input
    abstract val classStatistics: Property<Boolean>

    /** Generate file statistics report (JSON). */
    @get:Input
    abstract val fileStatistics: Property<Boolean>

    /** Generate package statistics report (JSON). */
    @get:Input
    abstract val packageStatistics: Property<Boolean>

    /** Generate package coupling metrics (JSON). */
    @get:Input
    abstract val packageCoupling: Property<Boolean>

    /** Generate packages report (JSON). */
    @get:Input
    abstract val packages: Property<Boolean>

    /** Generate Mermaid class diagram. */
    @get:Input
    abstract val mermaidClassDiagram: Property<Boolean>

    /** Generate Mermaid import diagram. */
    @get:Input
    abstract val mermaidImportDiagram: Property<Boolean>

    /** Generate Mermaid coupling diagram. */
    @get:Input
    abstract val mermaidCouplingDiagram: Property<Boolean>

    /** Include external libraries in diagrams. */
    @get:Input
    abstract val includeAllLibraries: Property<Boolean>

    /** Include private declarations in file statistics. */
    @get:Input
    abstract val includePrivateDeclarations: Property<Boolean>

    /** Detect classes with too many declarations. */
    @get:Input
    abstract val largeClasses: Property<Boolean>

    /** Declaration count threshold for large class detection. */
    @get:Input
    abstract val largeClassThreshold: Property<Int>

    /** Detect functions with too many lines. */
    @get:Input
    abstract val longMethods: Property<Boolean>

    /** Line count threshold for long method detection. */
    @get:Input
    abstract val longMethodThreshold: Property<Int>

    /** Detect unused imports. */
    @get:Input
    abstract val unusedImports: Property<Boolean>

    /** Detect classes with deep inheritance hierarchies. */
    @get:Input
    abstract val deepInheritance: Property<Boolean>

    /** Inheritance depth threshold for deep inheritance detection. */
    @get:Input
    abstract val deepInheritanceThreshold: Property<Int>
}
