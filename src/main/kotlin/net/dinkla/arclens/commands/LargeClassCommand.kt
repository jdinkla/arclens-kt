package net.dinkla.arclens.commands

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.LargeClassReport

class LargeClassCommand :
    AbstractCommand(
        "Detect classes with too many declarations",
        "large-classes",
    ) {
    private val threshold by option("-t", "--threshold", help = "Declaration count threshold")
        .int()
        .default(DEFAULT_THRESHOLD)

    companion object {
        const val DEFAULT_THRESHOLD = 10
    }

    override fun run() {
        val project = loadProject()
        val report = LargeClassReport.from(project, threshold)
        echo(Json.encodeToString(report))
    }
}
