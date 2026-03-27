package net.dinkla.nkp.commands

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import net.dinkla.nkp.domain.kotlinlang.KotlinFile
import net.dinkla.nkp.domain.kotlinlang.Project
import net.dinkla.nkp.extract.ChangeStatus
import net.dinkla.nkp.extract.detectChanges
import net.dinkla.nkp.extract.filterFilesToParse
import net.dinkla.nkp.extract.reuseFromCache
import net.dinkla.nkp.parser.PsiParser
import net.dinkla.nkp.utilities.getAllKotlinFiles
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
        val allFiles = allSources.flatMap { readFiles(it, parser) }
        if (!silent) {
            reportErrors(allFiles)
        }
        val project: Project = toProject(allSources, allFiles)
        writeOutput(project)
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
                allSources.flatMap { dir -> readFilesSelective(dir, filesToParse, parser) }
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
            reportIncrementalStatus(parsedFiles, unchangedCount, deletedCount)
        }

        val allKotlinFiles = reusedFiles + parsedFiles.filter { it.isSuccess }.map { it.getOrThrow() }
        val project =
            Project(
                directory = allSources.first().absolutePath,
                files = allKotlinFiles,
                directories = allSources.map { it.absolutePath },
                parseTimestamp = System.currentTimeMillis(),
            )
        writeOutput(project)
    }

    private fun loadCachedProject(): Project? =
        try {
            target?.let { file ->
                if (file.exists()) {
                    Json.decodeFromString<Project>(file.readText())
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            logger.warn { "Failed to load cached project: ${e.message}, falling back to full parse" }
            null
        }

    private fun reportIncrementalStatus(
        parsedFiles: List<Result<KotlinFile>>,
        unchangedCount: Int,
        deletedCount: Int,
    ) {
        val successCount = parsedFiles.count { it.isSuccess }
        val errorCount = parsedFiles.count { it.isFailure }
        val parts = mutableListOf<String>()
        if (successCount > 0) parts.add("$successCount parsed")
        if (errorCount > 0) parts.add("$errorCount exceptions")
        if (unchangedCount > 0) parts.add("$unchangedCount unchanged")
        if (deletedCount > 0) parts.add("$deletedCount deleted")
        echo(parts.joinToString(", "))

        if (errorCount > 0) {
            reportErrors(parsedFiles)
        }
    }

    private fun writeOutput(project: Project) {
        val json = Json.encodeToString(project)
        if (target != null) {
            target!!.writeText(json)
        } else {
            echo(json)
        }
    }

    private fun reportErrors(infos: List<Result<KotlinFile>>) {
        if (infos.isEmpty()) return
        val successCount = infos.count { it.isSuccess }
        val failureCount = infos.count { it.isFailure }
        if (successCount > 0 || failureCount > 0) {
            echo(
                infos
                    .groupBy { it.isSuccess }
                    .map {
                        "${it.value.size} ${if (it.key) "ok" else "exceptions"}"
                    }.joinToString(", "),
            )
        }
        val failures = infos.filter { it.isFailure }
        if (failures.isNotEmpty()) {
            echo("ERROR: The following exceptions occurred:")
            var count = 1
            echo("------------------------------------------------------------------------------")
            failures.forEach {
                val exception = it.exceptionOrNull()
                echo("${count++}. ${exception?.message}")
                exception?.cause?.printStackTrace(System.out)
                echo("------------------------------------------------------------------------------")
            }
        }
    }
}

private fun readFiles(
    directory: File,
    parser: PsiParser,
): List<Result<KotlinFile>> {
    val files = getAllKotlinFiles(directory)
    return runBlocking(Dispatchers.IO) {
        files
            .map { filePath ->
                async {
                    val startTime = System.currentTimeMillis()
                    val result = parser.parseFile(filePath, directory.absolutePath)
                    val elapsed = System.currentTimeMillis() - startTime
                    logger.debug { "$filePath: total=${elapsed}ms" }
                    result
                }
            }.map {
                it.await()
            }
    }
}

private fun readFilesSelective(
    directory: File,
    filesToParse: List<File>,
    parser: PsiParser,
): List<Result<KotlinFile>> {
    val directoryPath = directory.absolutePath
    val filesInDirectory = filesToParse.filter { it.absolutePath.startsWith(directoryPath) }
    return runBlocking(Dispatchers.IO) {
        filesInDirectory
            .map { file ->
                async {
                    val startTime = System.currentTimeMillis()
                    val result = parser.parseFile(file.absolutePath, directoryPath)
                    val elapsed = System.currentTimeMillis() - startTime
                    logger.debug { "${file.absolutePath}: total=${elapsed}ms" }
                    result
                }
            }.map {
                it.await()
            }
    }
}

private fun toProject(
    directories: List<File>,
    infos: List<Result<KotlinFile>>,
): Project {
    val absolutePaths = directories.map { it.absolutePath }
    return Project(
        directory = absolutePaths.first(),
        files = infos.filter { it.isSuccess }.map { it.getOrThrow() },
        directories = absolutePaths,
        parseTimestamp = System.currentTimeMillis(),
    )
}

private val logger = KotlinLogging.logger {}
