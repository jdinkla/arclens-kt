package net.dinkla.arclens.commands

import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.PackageImports
import net.dinkla.arclens.analysis.findCircularDependencies

class CircularDependenciesCommand :
    AbstractCommand(
        "Detect circular dependencies between packages",
        "circular-dependencies",
    ) {
    private val includeAllLibraries by option(help = "include all libraries").flag(default = false)

    override fun run() {
        val project = loadProject()
        val imports =
            if (!includeAllLibraries) {
                PackageImports.filteredImports(project)
            } else {
                PackageImports.allImports(project)
            }
        val report = findCircularDependencies(imports)
        echo(Json.encodeToString(report))
    }
}
