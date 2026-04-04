package net.dinkla.arclens.analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.FunctionSignature
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.domain.kotlinlang.Property
import net.dinkla.arclens.domain.kotlinlang.Type

class LargeClassDetectionTest :
    StringSpec({
        "should detect no large classes when all are below threshold" {
            // Given
            val project = projectWith(smallClass)
            // When
            val report = LargeClassReport.from(project, threshold = 10)
            // Then
            report.totalLargeClasses shouldBe 0
            report.largeClasses shouldHaveSize 0
        }

        "should detect a large class when declarations exceed threshold" {
            // Given
            val project = projectWith(largeClass)
            // When
            val report = LargeClassReport.from(project, threshold = 2)
            // Then
            report.totalLargeClasses shouldBe 1
            report.largeClasses[0].className shouldBe "BigClass"
            report.largeClasses[0].declarations shouldBe 4
        }

        "should not include classes at exactly the threshold" {
            // Given
            val project = projectWith(smallClass)
            // When
            val report = LargeClassReport.from(project, threshold = 2)
            // Then
            report.totalLargeClasses shouldBe 0
        }

        "should sort results by declaration count descending" {
            // Given
            val project = projectWith(smallClass, largeClass)
            // When
            val report = LargeClassReport.from(project, threshold = 1)
            // Then
            report.totalLargeClasses shouldBe 2
            report.largeClasses[0].declarations shouldBe 4
            report.largeClasses[1].declarations shouldBe 2
        }

        "should include the threshold in the report" {
            // Given
            val project = projectWith(smallClass)
            // When
            val report = LargeClassReport.from(project, threshold = 3)
            // Then
            report.threshold shouldBe 3
        }

        "should handle project with no classes" {
            // Given
            val project =
                Project(
                    "dir",
                    listOf(KotlinFile(FilePath("file.kt"), PackageName("pkg"))),
                )
            // When
            val report = LargeClassReport.from(project, threshold = 1)
            // Then
            report.totalLargeClasses shouldBe 0
        }
    })

private val smallClass =
    ClassSignature(
        "SmallClass",
        declarations =
            listOf(
                FunctionSignature("foo"),
                Property("bar", Type("Int")),
            ),
    )

private val largeClass =
    ClassSignature(
        "BigClass",
        declarations =
            listOf(
                FunctionSignature("a"),
                FunctionSignature("b"),
                FunctionSignature("c"),
                Property("d", Type("Int")),
            ),
    )

private fun projectWith(vararg classes: ClassSignature): Project =
    Project(
        "dir",
        listOf(
            KotlinFile(
                filePath = FilePath("file.kt"),
                packageName = PackageName("pkg"),
                declarations = classes.toList(),
            ),
        ),
    )
