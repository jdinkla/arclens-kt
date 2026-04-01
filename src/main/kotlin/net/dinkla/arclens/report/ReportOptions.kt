package net.dinkla.arclens.report

/**
 * Configuration options for HTML report generation.
 */
data class ReportOptions(
    val title: String = "Arclens-kt Report",
    val includeAllLibraries: Boolean = false,
    val includePrivateDeclarations: Boolean = false,
)
