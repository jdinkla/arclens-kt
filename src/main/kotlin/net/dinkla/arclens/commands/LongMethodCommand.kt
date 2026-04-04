package net.dinkla.arclens.commands

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.LongMethodReport

class LongMethodCommand :
    AbstractCommand(
        "Detect functions with too many lines",
        "long-methods",
    ) {
    private val threshold by option("-t", "--threshold", help = "Line count threshold")
        .int()
        .default(DEFAULT_THRESHOLD)

    companion object {
        const val DEFAULT_THRESHOLD = 60
    }

    override fun run() {
        val project = loadProject()
        val report = LongMethodReport.from(project, threshold)
        echo(Json.encodeToString(report))
    }
}
