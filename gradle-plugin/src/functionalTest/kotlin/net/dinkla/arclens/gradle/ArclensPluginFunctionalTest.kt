package net.dinkla.arclens.gradle

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File
import java.nio.file.Files

/**
 * Functional tests that apply the published plugin to a synthetic project and run
 * the parse/analyze tasks end-to-end via Gradle TestKit.
 *
 * The plugin depends on the root project through `implementation(project(":"))`.
 * TestKit resolves the plugin and that transitive dependency from the
 * plugin-under-test metadata exposed by `gradlePlugin { testSourceSets(...) }`,
 * which is wired up via [GradleRunner.withPluginClasspath].
 */
class ArclensPluginFunctionalTest :
    StringSpec({
        fun newProject(): File {
            val dir = Files.createTempDirectory("arclens-functional").toFile()
            File(dir, "settings.gradle.kts").writeText("""rootProject.name = "sample"""")
            File(dir, "build.gradle.kts").writeText(
                """
                plugins {
                    id("net.dinkla.arclens")
                }
                """.trimIndent(),
            )
            val srcDir = File(dir, "src/main/kotlin/com/example")
            srcDir.mkdirs()
            File(srcDir, "Greeter.kt").writeText(
                """
                package com.example

                import java.time.LocalDate

                class Greeter(private val name: String) {
                    fun greet(): String = "Hello, ${'$'}name on ${'$'}{LocalDate.now()}"
                }
                """.trimIndent(),
            )
            return dir
        }

        fun runner(
            projectDir: File,
            vararg args: String,
        ): GradleRunner =
            GradleRunner
                .create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(*args)
                .forwardOutput()

        "arclensParse parses sources and writes the model file" {
            // Given
            val projectDir = newProject()

            // When
            val result = runner(projectDir, "arclensParse").build()

            // Then
            result.task(":arclensParse")?.outcome shouldBe TaskOutcome.SUCCESS
            result.output shouldContain "Arclens: Parsed 1 files"
            File(projectDir, "build/arclens/model.json").shouldExist()
        }

        "arclensAnalyze depends on parse and generates the configured reports" {
            // Given
            val projectDir = newProject()

            // When
            val result = runner(projectDir, "arclensAnalyze").build()

            // Then
            result.task(":arclensParse")?.outcome shouldBe TaskOutcome.SUCCESS
            result.task(":arclensAnalyze")?.outcome shouldBe TaskOutcome.SUCCESS

            val output = File(projectDir, "build/arclens")
            File(output, "class-statistics.json").shouldExist()
            File(output, "file-statistics.json").shouldExist()
            File(output, "package-statistics.json").shouldExist()
            File(output, "package-coupling.json").shouldExist()
            File(output, "class-diagram.mermaid").shouldExist()
            File(output, "large-classes.json").shouldExist()
            File(output, "long-methods.json").shouldExist()
            File(output, "deep-inheritance.json").shouldExist()
            File(output, "complex-methods.json").shouldExist()
        }

        "aggregate arclens task runs the full pipeline" {
            // Given
            val projectDir = newProject()

            // When
            val result = runner(projectDir, "arclens").build()

            // Then
            result.task(":arclens")?.outcome shouldBe TaskOutcome.SUCCESS
            result.task(":arclensAnalyze")?.outcome shouldBe TaskOutcome.SUCCESS
            result.task(":arclensParse")?.outcome shouldBe TaskOutcome.SUCCESS
        }

        "report toggles in the arclens DSL disable opt-out reports" {
            // Given
            val projectDir = newProject()
            File(projectDir, "build.gradle.kts").writeText(
                """
                plugins {
                    id("net.dinkla.arclens")
                }

                arclens {
                    reports {
                        classStatistics.set(false)
                        mermaidClassDiagram.set(false)
                    }
                }
                """.trimIndent(),
            )

            // When
            val result = runner(projectDir, "arclensAnalyze").build()

            // Then
            result.task(":arclensAnalyze")?.outcome shouldBe TaskOutcome.SUCCESS
            val output = File(projectDir, "build/arclens")
            File(output, "class-statistics.json").exists() shouldBe false
            File(output, "class-diagram.mermaid").exists() shouldBe false
            // Reports left at their defaults are still produced.
            File(output, "file-statistics.json").shouldExist()
        }
    })
