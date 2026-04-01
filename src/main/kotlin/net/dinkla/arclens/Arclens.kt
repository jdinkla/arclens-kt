package net.dinkla.arclens

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption
import net.dinkla.arclens.commands.CircularDependenciesCommand
import net.dinkla.arclens.commands.ClassStatisticsCommand
import net.dinkla.arclens.commands.FileStatisticsCommand
import net.dinkla.arclens.commands.HtmlReportCommand
import net.dinkla.arclens.commands.MermaidClassDiagram
import net.dinkla.arclens.commands.MermaidCouplingDiagram
import net.dinkla.arclens.commands.MermaidImportDiagram
import net.dinkla.arclens.commands.PackageCouplingCommand
import net.dinkla.arclens.commands.PackageStatisticsCommand
import net.dinkla.arclens.commands.PackagesCommand
import net.dinkla.arclens.commands.Parse
import net.dinkla.arclens.commands.SearchCommand

class Arclens : CliktCommand(name = "arclens") {
    init {
        versionOption("0.1", names = setOf("-v", "--version"))
    }

    override fun run() = Unit
}

fun main(args: Array<String>) {
    Arclens()
        .subcommands(
            Parse(),
            CircularDependenciesCommand(),
            ClassStatisticsCommand(),
            FileStatisticsCommand(),
            HtmlReportCommand(),
            MermaidClassDiagram(),
            MermaidCouplingDiagram(),
            MermaidImportDiagram(),
            PackageCouplingCommand(),
            PackageStatisticsCommand(),
            PackagesCommand(),
            SearchCommand(),
        ).main(args)
}
