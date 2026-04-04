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

class LongMethodDetectionTest :
    StringSpec({
        "should detect no long methods when all are below threshold" {
            // Given
            val project = projectWith(fileWithSmallFunction)
            // When
            val report = LongMethodReport.from(project, threshold = 10)
            // Then
            report.totalLongMethods shouldBe 0
            report.longMethods shouldHaveSize 0
        }

        "should detect a long method when lineCount exceeds threshold" {
            // Given
            val project = projectWith(fileWithLongFunction)
            // When
            val report = LongMethodReport.from(project, threshold = 10)
            // Then
            report.totalLongMethods shouldBe 1
            report.longMethods[0].functionName shouldBe "longFn"
            report.longMethods[0].lineCount shouldBe 50
        }

        "should not include functions at exactly the threshold" {
            // Given
            val file =
                KotlinFile(
                    filePath = fp,
                    packageName = pkg,
                    declarations = listOf(FunctionSignature("exact", lineCount = 10)),
                )
            val project = projectWith(file)
            // When
            val report = LongMethodReport.from(project, threshold = 10)
            // Then
            report.totalLongMethods shouldBe 0
        }

        "should detect long methods inside classes" {
            // Given
            val project = projectWith(fileWithClassContainingLongMethod)
            // When
            val report = LongMethodReport.from(project, threshold = 10)
            // Then
            report.totalLongMethods shouldBe 1
            report.longMethods[0].className shouldBe "MyClass"
        }

        "should set className to null for top-level functions" {
            // Given
            val project = projectWith(fileWithLongFunction)
            // When
            val report = LongMethodReport.from(project, threshold = 10)
            // Then
            report.longMethods[0].className shouldBe null
        }

        "should sort results by lineCount descending" {
            // Given
            val project = projectWith(fileWithMultipleLongFunctions)
            // When
            val report = LongMethodReport.from(project, threshold = 10)
            // Then
            report.longMethods[0].lineCount shouldBe 50
            report.longMethods[1].lineCount shouldBe 30
        }

        "should include the threshold in the report" {
            // Given
            val project = projectWith(fileWithSmallFunction)
            // When
            val report = LongMethodReport.from(project, threshold = 10)
            // Then
            report.threshold shouldBe 10
        }
    })

private val fp = FilePath("file.kt")
private val pkg = PackageName("pkg")

private val fileWithSmallFunction =
    KotlinFile(
        filePath = fp,
        packageName = pkg,
        declarations = listOf(FunctionSignature("small", lineCount = 3)),
    )

private val fileWithLongFunction =
    KotlinFile(
        filePath = fp,
        packageName = pkg,
        declarations = listOf(FunctionSignature("longFn", lineCount = 50)),
    )

private val fileWithClassContainingLongMethod =
    KotlinFile(
        filePath = fp,
        packageName = pkg,
        declarations =
            listOf(
                ClassSignature(
                    "MyClass",
                    declarations = listOf(FunctionSignature("classMethod", lineCount = 40)),
                ),
            ),
    )

private val fileWithMultipleLongFunctions =
    KotlinFile(
        filePath = fp,
        packageName = pkg,
        declarations =
            listOf(
                FunctionSignature("medium", lineCount = 30),
                FunctionSignature("long", lineCount = 50),
            ),
    )

private fun projectWith(vararg files: KotlinFile) = Project("dir", files.toList())
