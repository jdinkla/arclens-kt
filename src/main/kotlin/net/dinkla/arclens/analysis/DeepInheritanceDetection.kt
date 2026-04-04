package net.dinkla.arclens.analysis

import kotlinx.serialization.Serializable
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.Project

@Serializable
data class DeeplyInheritedClass(
    val className: String,
    val packageName: PackageName,
    val inheritanceDepth: Int,
)

@Serializable
data class DeepInheritanceReport(
    val threshold: Int,
    val deeplyInheritedClasses: List<DeeplyInheritedClass>,
    val totalDeeplyInherited: Int,
) {
    companion object {
        fun from(
            project: Project,
            threshold: Int,
        ): DeepInheritanceReport {
            val stats = ClassStatistics.from(project)
            val deep =
                stats
                    .filter { it.metrics.superClasses > threshold }
                    .map {
                        DeeplyInheritedClass(
                            it.className,
                            it.packageName,
                            it.metrics.superClasses,
                        )
                    }.sortedByDescending { it.inheritanceDepth }
            return DeepInheritanceReport(
                threshold = threshold,
                deeplyInheritedClasses = deep,
                totalDeeplyInherited = deep.size,
            )
        }
    }
}
