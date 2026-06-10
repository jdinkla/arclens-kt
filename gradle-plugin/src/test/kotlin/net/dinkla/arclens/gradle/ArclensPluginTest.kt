package net.dinkla.arclens.gradle

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.gradle.testfixtures.ProjectBuilder

/**
 * Unit tests for [ArclensPlugin] task wiring and [ArclensExtension] defaults
 * using Gradle's [ProjectBuilder].
 */
class ArclensPluginTest :
    StringSpec({
        "plugin registers the parse, analyze and aggregate tasks" {
            // Given
            val project = ProjectBuilder.builder().build()

            // When
            project.plugins.apply("net.dinkla.arclens")

            // Then
            project.tasks.findByName("arclensParse").shouldNotBeNull()
            project.tasks.findByName("arclensAnalyze").shouldNotBeNull()
            project.tasks.findByName("arclens").shouldNotBeNull()
        }

        "registered tasks belong to the arclens analysis group" {
            // Given
            val project = ProjectBuilder.builder().build()

            // When
            project.plugins.apply("net.dinkla.arclens")

            // Then
            project.tasks.getByName("arclensParse").group shouldBe ArclensPlugin.TASK_GROUP
            project.tasks.getByName("arclensAnalyze").group shouldBe ArclensPlugin.TASK_GROUP
            project.tasks.getByName("arclens").group shouldBe ArclensPlugin.TASK_GROUP
        }

        "plugin creates the arclens extension" {
            // Given
            val project = ProjectBuilder.builder().build()

            // When
            project.plugins.apply("net.dinkla.arclens")

            // Then
            project.extensions.findByName("arclens").shouldNotBeNull()
            project.extensions.findByType(ArclensExtension::class.java).shouldNotBeNull()
        }

        "extension applies the documented default conventions" {
            // Given
            val project = ProjectBuilder.builder().build()
            project.plugins.apply("net.dinkla.arclens")

            // When
            val extension = project.extensions.getByType(ArclensExtension::class.java)

            // Then
            // No src/main/kotlin in the synthetic project, so sourceDirs defaults to empty.
            extension.sourceDirs.get() shouldBe emptyList()
            val expectedOutput =
                project.layout.buildDirectory
                    .dir("arclens")
                    .get()
                    .asFile
            extension.outputDir.get().asFile shouldBe expectedOutput
        }

        "report defaults enable the standard reports and diagrams" {
            // Given
            val project = ProjectBuilder.builder().build()
            project.plugins.apply("net.dinkla.arclens")

            // When
            val reports = project.extensions.getByType(ArclensExtension::class.java).reports

            // Then
            reports.classStatistics.get().shouldBeTrue()
            reports.fileStatistics.get().shouldBeTrue()
            reports.packageStatistics.get().shouldBeTrue()
            reports.packageCoupling.get().shouldBeTrue()
            reports.mermaidClassDiagram.get().shouldBeTrue()
            reports.mermaidImportDiagram.get().shouldBeTrue()
            reports.mermaidCouplingDiagram.get().shouldBeTrue()
        }

        "report defaults disable opt-in reports" {
            // Given
            val project = ProjectBuilder.builder().build()
            project.plugins.apply("net.dinkla.arclens")

            // When
            val reports = project.extensions.getByType(ArclensExtension::class.java).reports

            // Then
            reports.packages.get().shouldBeFalse()
            reports.includeAllLibraries.get().shouldBeFalse()
            reports.includePrivateDeclarations.get().shouldBeFalse()
        }

        "code smell detection defaults match the plugin thresholds" {
            // Given
            val project = ProjectBuilder.builder().build()
            project.plugins.apply("net.dinkla.arclens")

            // When
            val reports = project.extensions.getByType(ArclensExtension::class.java).reports

            // Then
            reports.largeClasses.get().shouldBeTrue()
            reports.largeClassThreshold.get() shouldBe ArclensPlugin.DEFAULT_LARGE_CLASS_THRESHOLD
            reports.longMethods.get().shouldBeTrue()
            reports.longMethodThreshold.get() shouldBe ArclensPlugin.DEFAULT_LONG_METHOD_THRESHOLD
            reports.deepInheritance.get().shouldBeTrue()
            reports.deepInheritanceThreshold.get() shouldBe ArclensPlugin.DEFAULT_DEEP_INHERITANCE_THRESHOLD
            reports.complexMethods.get().shouldBeTrue()
            reports.complexMethodThreshold.get() shouldBe ArclensPlugin.DEFAULT_COMPLEX_METHOD_THRESHOLD
        }
    })
