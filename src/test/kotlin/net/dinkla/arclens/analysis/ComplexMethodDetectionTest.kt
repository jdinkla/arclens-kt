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

class ComplexMethodDetectionTest :
    StringSpec({
        "should detect no complex methods when all are below threshold" {
            // Given
            val project = projectWith(fileWithSimpleFunction)
            // When
            val report = ComplexMethodReport.from(project, threshold = 10)
            // Then
            report.totalComplexMethods shouldBe 0
            report.complexMethods shouldHaveSize 0
        }

        "should detect a complex method when complexity exceeds threshold" {
            // Given
            val project = projectWith(fileWithComplexFunction)
            // When
            val report = ComplexMethodReport.from(project, threshold = 10)
            // Then
            report.totalComplexMethods shouldBe 1
            report.complexMethods[0].functionName shouldBe "complexFn"
            report.complexMethods[0].cyclomaticComplexity shouldBe 20
        }

        "should not include functions at exactly the threshold" {
            // Given
            val file =
                KotlinFile(
                    filePath = fp,
                    packageName = pkg,
                    declarations = listOf(FunctionSignature("exact", cyclomaticComplexity = 10)),
                )
            val project = projectWith(file)
            // When
            val report = ComplexMethodReport.from(project, threshold = 10)
            // Then
            report.totalComplexMethods shouldBe 0
        }

        "should detect complex methods inside classes" {
            // Given
            val project = projectWith(fileWithClassContainingComplexMethod)
            // When
            val report = ComplexMethodReport.from(project, threshold = 10)
            // Then
            report.totalComplexMethods shouldBe 1
            report.complexMethods[0].className shouldBe "MyClass"
        }

        "should set className to null for top-level functions" {
            // Given
            val project = projectWith(fileWithComplexFunction)
            // When
            val report = ComplexMethodReport.from(project, threshold = 10)
            // Then
            report.complexMethods[0].className shouldBe null
        }

        "should sort results by cyclomaticComplexity descending" {
            // Given
            val project = projectWith(fileWithMultipleComplexFunctions)
            // When
            val report = ComplexMethodReport.from(project, threshold = 10)
            // Then
            report.complexMethods[0].cyclomaticComplexity shouldBe 20
            report.complexMethods[1].cyclomaticComplexity shouldBe 15
        }

        "should include the threshold in the report" {
            // Given
            val project = projectWith(fileWithSimpleFunction)
            // When
            val report = ComplexMethodReport.from(project, threshold = 10)
            // Then
            report.threshold shouldBe 10
        }
    })

private val fp = FilePath("file.kt")
private val pkg = PackageName("pkg")

private val fileWithSimpleFunction =
    KotlinFile(
        filePath = fp,
        packageName = pkg,
        declarations = listOf(FunctionSignature("simple", cyclomaticComplexity = 3)),
    )

private val fileWithComplexFunction =
    KotlinFile(
        filePath = fp,
        packageName = pkg,
        declarations = listOf(FunctionSignature("complexFn", cyclomaticComplexity = 20)),
    )

private val fileWithClassContainingComplexMethod =
    KotlinFile(
        filePath = fp,
        packageName = pkg,
        declarations =
            listOf(
                ClassSignature(
                    "MyClass",
                    declarations = listOf(FunctionSignature("classMethod", cyclomaticComplexity = 15)),
                ),
            ),
    )

private val fileWithMultipleComplexFunctions =
    KotlinFile(
        filePath = fp,
        packageName = pkg,
        declarations =
            listOf(
                FunctionSignature("medium", cyclomaticComplexity = 15),
                FunctionSignature("high", cyclomaticComplexity = 20),
            ),
    )

private fun projectWith(vararg files: KotlinFile) = Project("dir", files.toList())
