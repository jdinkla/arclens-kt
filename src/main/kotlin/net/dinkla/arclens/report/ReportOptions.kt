package net.dinkla.arclens.report

/**
 * Configuration options for HTML report generation.
 */
data class ReportOptions(
    val title: String = "Arclens-kt Report",
    val includeAllLibraries: Boolean = false,
    val includePrivateDeclarations: Boolean = false,
    val largeClassThreshold: Int = DEFAULT_LARGE_CLASS_THRESHOLD,
    val longMethodThreshold: Int = DEFAULT_LONG_METHOD_THRESHOLD,
    val deepInheritanceThreshold: Int = DEFAULT_DEEP_INHERITANCE_THRESHOLD,
) {
    companion object {
        const val DEFAULT_LARGE_CLASS_THRESHOLD = 10
        const val DEFAULT_LONG_METHOD_THRESHOLD = 60
        const val DEFAULT_DEEP_INHERITANCE_THRESHOLD = 3
    }
}
