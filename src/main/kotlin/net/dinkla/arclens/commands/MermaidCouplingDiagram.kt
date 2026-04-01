package net.dinkla.arclens.commands

import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import net.dinkla.arclens.analysis.MermaidCouplingDiagram
import net.dinkla.arclens.analysis.PackageImports
import net.dinkla.arclens.analysis.combinedReport

class MermaidCouplingDiagram : AbstractCommand("Generate a Mermaid coupling diagram from code analysis") {
    private val includeAllLibraries by option(help = "include all libraries").flag(default = false)

    override fun run() {
        val project = loadProject()
        val imports =
            if (!includeAllLibraries) {
                PackageImports.filteredImports(project)
            } else {
                PackageImports.allImports(project)
            }
        val reportItems = combinedReport(imports)
        val diagram = MermaidCouplingDiagram(items = reportItems)
        echo(diagram.generate())
    }
}
