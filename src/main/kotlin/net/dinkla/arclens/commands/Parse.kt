package net.dinkla.arclens.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.file
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.extract.ChangeStatus
import net.dinkla.arclens.extract.buildProject
import net.dinkla.arclens.extract.detectChanges
import net.dinkla.arclens.extract.filterFilesToParse
import net.dinkla.arclens.extract.formatIncrementalStatus
import net.dinkla.arclens.extract.formatParseErrors
import net.dinkla.arclens.extract.formatParseSummary
import net.dinkla.arclens.extract.parseFiles
import net.dinkla.arclens.extract.reuseFromCache
import net.dinkla.arclens.parser.PsiParser
import net.dinkla.arclens.utilities.getAllKotlinFiles
import net.dinkla.arclens.utilities.loadJson
import net.dinkla.arclens.utilities.saveJson
import java.io.File

class Parse : CliktCommand(name = "parse") {
    override fun help(context: Context) =
        "Parse source directories and generate a model file. This is the necessary " +
            "first step before running any analysis. Multiple source directories can be " +
            "specified using --sources option."

    private val source by argument(
        help = "Path to the primary source directory to analyze",
    ).file(mustExist = true, canBeDir = true, canBeFile = false)

    private val additionalSources by option(
        "--sources",
        "-s",
        help = "Additional source directories (comma-separated)",
    ).file(mustExist = true, canBeDir = true, canBeFile = false).split(",")

    private val target by argument(
        help = "The output file",
    ).convert { File(it) }
        .optional()

    private val silent by option(help = "no output").flag(default = false)

    private val full by option(
        "--full",
        "-f",
        help = "Force full parse, ignoring cached results",
    ).flag(default = false)

    private val parser: PsiParser by lazy { PsiParser() }

    override fun run() {
        if (!full && target?.exists() == true) {
            runIncremental()
        } else {
            runFull()
        }
    }

    private fun runFull() {
        val allSources = listOf(source) + (additionalSources ?: emptyList())
        val allFiles =
            allSources.flatMap { dir ->
                parseFiles(getAllKotlinFiles(dir), dir.absolutePath, parser)
            }
        if (!silent) {
            formatParseSummary(allFiles)?.let { echo(it) }
            formatParseErrors(allFiles)?.let { echo(it) }
        }
        val successFiles = allFiles.filter { it.isSuccess }.map { it.getOrThrow() }
        writeOutput(buildProject(allSources, successFiles))
    }

    private fun runIncremental() {
        val allSources = listOf(source) + (additionalSources ?: emptyList())
        val cachedProject = loadCachedProject()

        val currentFiles = allSources.flatMap { getAllKotlinFiles(it).map { path -> File(path) } }
        val changes = detectChanges(cachedProject, currentFiles)

        val filesToParse = filterFilesToParse(changes)
        val unchangedCount = changes.count { it.status == ChangeStatus.UNCHANGED }
        val deletedCount = changes.count { it.status == ChangeStatus.DELETED }

        val parsedFiles =
            if (filesToParse.isNotEmpty()) {
                allSources.flatMap { dir ->
                    val filesInDir = filesToParse.filter { it.absolutePath.startsWith(dir.absolutePath) }
                    parseFiles(filesInDir.map { it.absolutePath }, dir.absolutePath, parser)
                }
            } else {
                emptyList()
            }

        val reusedFiles =
            if (cachedProject != null) {
                reuseFromCache(cachedProject, changes)
            } else {
                emptyList()
            }

        if (!silent) {
            echo(formatIncrementalStatus(parsedFiles, unchangedCount, deletedCount))
            if (parsedFiles.any { it.isFailure }) {
                formatParseSummary(parsedFiles)?.let { echo(it) }
                formatParseErrors(parsedFiles)?.let { echo(it) }
            }
        }

        val allKotlinFiles = reusedFiles + parsedFiles.filter { it.isSuccess }.map { it.getOrThrow() }
        writeOutput(buildProject(allSources, allKotlinFiles))
    }

    private fun loadCachedProject(): Project? =
        try {
            target?.let { file ->
                if (file.exists()) file.loadJson<Project>() else null
            }
        } catch (e: Exception) {
            logger.warn { "Failed to load cached project: ${e.message}, falling back to full parse" }
            null
        }

    private fun writeOutput(project: Project) {
        if (target != null) {
            target!!.saveJson(project)
        } else {
            echo(Json.encodeToString(project))
        }
    }
}

private val logger = KotlinLogging.logger {}
