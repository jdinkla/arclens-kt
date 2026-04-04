package net.dinkla.arclens.analysis

import kotlinx.serialization.Serializable
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.Project

@Serializable
data class ComplexMethod(
    val functionName: String,
    val className: String?,
    val filePath: FilePath,
    val cyclomaticComplexity: Int,
)

@Serializable
data class ComplexMethodReport(
    val threshold: Int,
    val complexMethods: List<ComplexMethod>,
    val totalComplexMethods: Int,
) {
    companion object {
        fun from(
            project: Project,
            threshold: Int,
        ): ComplexMethodReport {
            val methods =
                project
                    .flatMap { file -> collectFunctions(file) }
                    .filter { it.cyclomaticComplexity > threshold }
                    .sortedByDescending { it.cyclomaticComplexity }
            return ComplexMethodReport(
                threshold = threshold,
                complexMethods = methods,
                totalComplexMethods = methods.size,
            )
        }
    }
}

private fun collectFunctions(file: KotlinFile): List<ComplexMethod> {
    val topLevel =
        file.functions.map { fn ->
            ComplexMethod(fn.name, null, file.filePath, fn.cyclomaticComplexity)
        }
    val inClasses =
        file.classes.flatMap { cls ->
            collectClassFunctions(cls, file.filePath)
        }
    return topLevel + inClasses
}

private fun collectClassFunctions(
    cls: ClassSignature,
    filePath: FilePath,
): List<ComplexMethod> {
    val own =
        cls.functions.map { fn ->
            ComplexMethod(fn.name, cls.name, filePath, fn.cyclomaticComplexity)
        }
    val nested =
        cls.classes.flatMap { nestedCls ->
            collectClassFunctions(nestedCls, filePath)
        }
    return own + nested
}
