package net.dinkla.arclens.commands

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.ComplexMethodReport

class ComplexMethodCommand :
    AbstractCommand(
        "Detect functions with high cyclomatic complexity",
        "complex-methods",
    ) {
    private val threshold by option("-t", "--threshold", help = "Cyclomatic complexity threshold")
        .int()
        .default(DEFAULT_THRESHOLD)

    companion object {
        const val DEFAULT_THRESHOLD = 15
    }

    override fun run() {
        val project = loadProject()
        val report = ComplexMethodReport.from(project, threshold)
        echo(Json.encodeToString(report))
    }
}
