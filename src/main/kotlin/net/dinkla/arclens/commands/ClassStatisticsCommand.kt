package net.dinkla.arclens.commands

import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.ClassStatistics

class ClassStatisticsCommand : AbstractCommand("Class statistics", "class-statistics") {
    override fun run() {
        val project = loadProject()
        val stats = ClassStatistics.from(project)
        echo(Json.encodeToString(stats))
    }
}
