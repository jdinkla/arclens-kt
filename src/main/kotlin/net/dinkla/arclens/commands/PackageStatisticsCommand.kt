package net.dinkla.arclens.commands

import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.packagesStatistics

class PackageStatisticsCommand : AbstractCommand("Package statistics", "package-statistics") {
    override fun run() {
        val project = loadProject()
        val stats = packagesStatistics(project)
        echo(Json.encodeToString(stats))
    }
}
