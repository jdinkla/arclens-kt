package net.dinkla.arclens.commands

import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.serialization.json.Json
import net.dinkla.arclens.analysis.DeclarationFilter
import net.dinkla.arclens.analysis.FileStatistics
import net.dinkla.arclens.analysis.ImportFilter

class FileStatisticsCommand : AbstractCommand("File statistics and imports report") {
    private val includeAllLibraries by option(help = "include all libraries").flag(default = false)
    private val includePrivateDeclarations by option(help = "include private declarations").flag(default = false)

    override fun run() {
        val project = loadProject()
        val declarationFilter = DeclarationFilter.select(includePrivateDeclarations)
        val importFilter = ImportFilter.select(includeAllLibraries)
        val stats = FileStatistics.from(project, declarationFilter, importFilter)
        echo(Json.encodeToString(stats))
    }
}
