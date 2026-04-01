package net.dinkla.arclens.commands

import net.dinkla.arclens.analysis.mermaidClassDiagram

class MermaidClassDiagram : AbstractCommand("Mermaid class diagram") {
    override fun run() {
        val project = loadProject()
        val diagram = mermaidClassDiagram(project)
        echo(diagram)
    }
}
