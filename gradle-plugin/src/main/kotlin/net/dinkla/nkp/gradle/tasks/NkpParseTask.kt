package net.dinkla.nkp.gradle.tasks

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import net.dinkla.nkp.domain.kotlinlang.KotlinFile
import net.dinkla.nkp.domain.kotlinlang.Project
import net.dinkla.nkp.parser.PsiParser
import net.dinkla.nkp.utilities.getAllKotlinFiles
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Task that parses Kotlin source files and generates an analysis model.
 */
abstract class NkpParseTask : DefaultTask() {
    private val parser = PsiParser()

    @get:Input
    abstract val sourceDirs: ListProperty<File>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun parse() {
        val dirs = sourceDirs.get()
        if (dirs.isEmpty()) {
            logger.warn("No source directories configured for NKP analysis")
            return
        }

        val allFiles = dirs.flatMap { readFiles(it) }
        val successCount = allFiles.count { it.isSuccess }
        val failureCount = allFiles.count { it.isFailure }

        logger.lifecycle("NKP: Parsed $successCount files, $failureCount failures")

        if (failureCount > 0) {
            allFiles.filter { it.isFailure }.forEach {
                logger.warn("NKP parse error: ${it.exceptionOrNull()?.message}")
            }
        }

        val project = toProject(dirs, allFiles)
        val json = Json.encodeToString(project)

        val output = outputFile.get().asFile
        output.parentFile.mkdirs()
        output.writeText(json)

        logger.lifecycle("NKP: Model written to ${output.absolutePath}")
    }

    private fun readFiles(directory: File): List<Result<KotlinFile>> {
        val files = getAllKotlinFiles(directory)
        return runBlocking(Dispatchers.Default) {
            files
                .map { fileName ->
                    async {
                        extractFileInfo(fileName, directory.absolutePath)
                    }
                }.map { it.await() }
        }
    }

    private fun extractFileInfo(
        fileName: String,
        prefix: String,
    ): Result<KotlinFile> =
        try {
            parser.parseFile(fileName, prefix)
        } catch (e: Exception) {
            val message = "parsing '$fileName' yields ${e.message}"
            logger.debug("NKP: $message")
            Result.failure(Error(message, e))
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
        )
    }
}
