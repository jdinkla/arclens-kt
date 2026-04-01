package net.dinkla.arclens.commands

import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import net.dinkla.arclens.analysis.mermaidImportsFlowDiagram

class MermaidImportDiagram : AbstractCommand("Mermaid import diagram") {
    private val includeAllLibraries by option(help = "include all libraries").flag(default = false)

    override fun run() {
        val project = loadProject()
        val diagram = mermaidImportsFlowDiagram(project, !includeAllLibraries)
        echo(diagram)
    }
}
