package net.dinkla.arclens.commands

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.DeepInheritanceReport

class DeepInheritanceCommand :
    AbstractCommand(
        "Detect classes with deep inheritance hierarchies",
        "deep-inheritance",
    ) {
    private val threshold by option("-t", "--threshold", help = "Inheritance depth threshold")
        .int()
        .default(3)

    override fun run() {
        val project = loadProject()
        val report = DeepInheritanceReport.from(project, threshold)
        echo(Json.encodeToString(report))
    }
}
