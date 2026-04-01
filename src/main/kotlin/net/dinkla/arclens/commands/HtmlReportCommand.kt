package net.dinkla.arclens.commands

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import net.dinkla.arclens.report.HtmlReportGenerator
import net.dinkla.arclens.report.ReportData
import net.dinkla.arclens.report.ReportOptions
import java.io.File

class HtmlReportCommand : AbstractCommand("Generate a standalone HTML report with embedded diagrams") {
    private val output by option("-o", "--output", help = "Output HTML file")
        .file(canBeDir = false)
        .default(File("report.html"))

    private val includeAllLibraries by option(
        "--include-all-libraries",
        help = "Include external library imports in analysis",
    ).flag(default = false)

    private val includePrivateDeclarations by option(
        "--include-private-declarations",
        help = "Include private declarations in analysis",
    ).flag(default = false)

    private val title by option("-t", "--title", help = "Report title")
        .default("Arclens-kt Report")

    override fun run() {
        val project = loadProject()

        val options =
            ReportOptions(
                title = title,
                includeAllLibraries = includeAllLibraries,
                includePrivateDeclarations = includePrivateDeclarations,
            )

        val reportData = ReportData.from(project, options)
        val generator = HtmlReportGenerator(reportData, options)
        val html = generator.generate()

        output.parentFile?.mkdirs()
        output.writeText(html)

        echo("HTML report generated: ${output.absolutePath}")
    }
}
