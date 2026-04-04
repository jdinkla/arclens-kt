package net.dinkla.arclens.analysis

import kotlinx.serialization.Serializable
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.Project

@Serializable
data class LargeClass(
    val className: String,
    val packageName: PackageName,
    val declarations: Int,
)

@Serializable
data class LargeClassReport(
    val threshold: Int,
    val largeClasses: List<LargeClass>,
    val totalLargeClasses: Int,
) {
    companion object {
        fun from(
            project: Project,
            threshold: Int,
        ): LargeClassReport {
            val stats = ClassStatistics.from(project)
            val large =
                stats
                    .filter { it.metrics.declarations > threshold }
                    .map { LargeClass(it.className, it.packageName, it.metrics.declarations) }
                    .sortedByDescending { it.declarations }
            return LargeClassReport(
                threshold = threshold,
                largeClasses = large,
                totalLargeClasses = large.size,
            )
        }
    }
}
