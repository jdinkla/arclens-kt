package net.dinkla.arclens.analysis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.Project

class DeepInheritanceDetectionTest :
    StringSpec({
        "should detect no deeply inherited classes when all are below threshold" {
            // Given - A extends B (depth 1)
            val project = projectWithChain("A" to "B")
            // When
            val report = DeepInheritanceReport.from(project, threshold = 1)
            // Then
            report.totalDeeplyInherited shouldBe 0
            report.deeplyInheritedClasses shouldHaveSize 0
        }

        "should detect deeply inherited class when depth exceeds threshold" {
            // Given - C extends B, B extends A (C has depth 2)
            val project = projectWithChain("C" to "B", "B" to "A")
            // When
            val report = DeepInheritanceReport.from(project, threshold = 1)
            // Then
            report.totalDeeplyInherited shouldBe 1
            report.deeplyInheritedClasses[0].className shouldBe "C"
            report.deeplyInheritedClasses[0].inheritanceDepth shouldBe 2
        }

        "should not include classes at exactly the threshold" {
            // Given - B extends A (depth 1)
            val project = projectWithChain("B" to "A")
            // When
            val report = DeepInheritanceReport.from(project, threshold = 1)
            // Then
            report.totalDeeplyInherited shouldBe 0
        }

        "should sort by inheritance depth descending" {
            // Given - D->C->B->A (depth 3), C->B->A (depth 2)
            val project = projectWithChain("D" to "C", "C" to "B", "B" to "A")
            // When
            val report = DeepInheritanceReport.from(project, threshold = 1)
            // Then
            report.totalDeeplyInherited shouldBe 2
            report.deeplyInheritedClasses[0].inheritanceDepth shouldBe 3
            report.deeplyInheritedClasses[1].inheritanceDepth shouldBe 2
        }

        "should include the threshold in the report" {
            // Given
            val project = projectWithChain("A" to "B")
            // When
            val report = DeepInheritanceReport.from(project, threshold = 3)
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
            val report = DeepInheritanceReport.from(project, threshold = 1)
            // Then
            report.totalDeeplyInherited shouldBe 0
        }
    })

private val pkg = PackageName("pkg")

private fun projectWithChain(vararg relations: Pair<String, String>): Project {
    val allClasses = mutableMapOf<String, MutableList<String>>()
    for ((child, parent) in relations) {
        allClasses.getOrPut(child) { mutableListOf() }.add(parent)
    }
    for ((_, parent) in relations) {
        if (parent !in allClasses) {
            allClasses[parent] = mutableListOf()
        }
    }
    val classSignatures =
        allClasses.map { (name, superTypes) ->
            ClassSignature(name, superTypes = superTypes)
        }
    return Project(
        "dir",
        listOf(
            KotlinFile(
                filePath = FilePath("file.kt"),
                packageName = pkg,
                declarations = classSignatures,
            ),
        ),
    )
}
