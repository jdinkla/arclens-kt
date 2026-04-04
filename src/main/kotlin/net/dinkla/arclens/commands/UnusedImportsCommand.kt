package net.dinkla.arclens.commands

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.UnusedImportsReport

class UnusedImportsCommand :
    AbstractCommand(
        "Detect unused imports",
        "unused-imports",
    ) {
    override fun run() {
        val project = loadProject()
        val report = UnusedImportsReport.from(project)
        echo(Json.encodeToString(report))
    }
}
