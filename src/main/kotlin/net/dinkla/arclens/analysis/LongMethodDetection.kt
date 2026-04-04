package net.dinkla.arclens.analysis

import kotlinx.serialization.Serializable
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.ClassSignature
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.Project

@Serializable
data class LongMethod(
    val functionName: String,
    val className: String?,
    val filePath: FilePath,
    val lineCount: Int,
)

@Serializable
data class LongMethodReport(
    val threshold: Int,
    val longMethods: List<LongMethod>,
    val totalLongMethods: Int,
) {
    companion object {
        fun from(
            project: Project,
            threshold: Int,
        ): LongMethodReport {
            val methods =
                project
                    .flatMap { file -> collectFunctions(file) }
                    .filter { it.lineCount > threshold }
                    .sortedByDescending { it.lineCount }
            return LongMethodReport(
                threshold = threshold,
                longMethods = methods,
                totalLongMethods = methods.size,
            )
        }
    }
}

private fun collectFunctions(file: KotlinFile): List<LongMethod> {
    val topLevel =
        file.functions.map { fn ->
            LongMethod(fn.name, null, file.filePath, fn.lineCount)
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
): List<LongMethod> {
    val own =
        cls.functions.map { fn ->
            LongMethod(fn.name, cls.name, filePath, fn.lineCount)
        }
    val nested =
        cls.classes.flatMap { nestedCls ->
            collectClassFunctions(nestedCls, filePath)
        }
    return own + nested
}
