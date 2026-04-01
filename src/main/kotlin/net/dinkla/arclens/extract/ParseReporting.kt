package net.dinkla.arclens.extract

import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import java.io.PrintWriter
import java.io.StringWriter

private const val SEPARATOR = "------------------------------------------------------------------------------"

fun formatParseSummary(results: List<Result<KotlinFile>>): String? {
    if (results.isEmpty()) return null
    return results
        .groupBy { it.isSuccess }
        .map { "${it.value.size} ${if (it.key) "ok" else "exceptions"}" }
        .joinToString(", ")
}

fun formatParseErrors(results: List<Result<KotlinFile>>): String? {
    val failures = results.filter { it.isFailure }
    if (failures.isEmpty()) return null
    val sb = StringBuilder()
    sb.appendLine("ERROR: The following exceptions occurred:")
    sb.appendLine(SEPARATOR)
    var count = 1
    failures.forEach {
        val exception = it.exceptionOrNull()
        sb.appendLine("${count++}. ${exception?.message}")
        exception?.cause?.let { cause ->
            val sw = StringWriter()
            cause.printStackTrace(PrintWriter(sw))
            sb.appendLine(sw.toString().trimEnd())
        }
        sb.appendLine(SEPARATOR)
    }
    return sb.toString().trimEnd()
}

fun formatIncrementalStatus(
    parsedFiles: List<Result<KotlinFile>>,
    unchangedCount: Int,
    deletedCount: Int,
): String {
    val successCount = parsedFiles.count { it.isSuccess }
    val errorCount = parsedFiles.count { it.isFailure }
    val parts = mutableListOf<String>()
    if (successCount > 0) parts.add("$successCount parsed")
    if (errorCount > 0) parts.add("$errorCount exceptions")
    if (unchangedCount > 0) parts.add("$unchangedCount unchanged")
    if (deletedCount > 0) parts.add("$deletedCount deleted")
    return parts.joinToString(", ")
}
