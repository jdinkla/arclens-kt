package net.dinkla.arclens.extract

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.Project
import net.dinkla.arclens.parser.PsiParser
import java.io.File

fun parseFiles(
    filePaths: List<String>,
    prefix: String,
    parser: PsiParser,
): List<Result<KotlinFile>> =
    runBlocking(Dispatchers.IO) {
        filePaths
            .map { filePath ->
                async {
                    val startTime = System.currentTimeMillis()
                    val result = parser.parseFile(filePath, prefix)
                    val elapsed = System.currentTimeMillis() - startTime
                    logger.debug { "$filePath: total=${elapsed}ms" }
                    result
                }
            }.map {
                it.await()
            }
    }

fun buildProject(
    directories: List<File>,
    kotlinFiles: List<KotlinFile>,
): Project {
    val absolutePaths = directories.map { it.absolutePath }
    return Project(
        directory = absolutePaths.first(),
        files = kotlinFiles,
        directories = absolutePaths,
        parseTimestamp = System.currentTimeMillis(),
    )
}

private val logger = KotlinLogging.logger {}
