package net.dinkla.arclens.parser

import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/**
 * Parser implementation using JetBrains PSI (kotlin-compiler-embeddable).
 */
class PsiParser {
    private val environment: KotlinCoreEnvironment by lazy { createKotlinCoreEnvironment() }

    fun parseFile(
        filePath: String,
        prefix: String,
    ): Result<KotlinFile> =
        try {
            val file = File(filePath)
            val withoutPrefix = filePath.removePrefix(prefix)
            val content = file.readText()

            val ktFile = parseText(content, file.name)
            val kotlinFile =
                extractKotlinFile(
                    ktFile,
                    FilePath(withoutPrefix),
                    lastModified = file.lastModified(),
                    fileSize = file.length(),
                )
            Result.success(kotlinFile)
        } catch (e: Exception) {
            Result.failure(Error("parsing '$filePath' yields ${e.message}", e))
        }

    /**
     * Parse Kotlin source text into a KtFile PSI structure.
     */
    internal fun parseText(
        text: String,
        fileName: String = "temp.kt",
    ): KtFile {
        val factory = PsiFileFactory.getInstance(environment.project)
        return factory.createFileFromText(fileName, KotlinFileType.INSTANCE, text) as KtFile
    }

    companion object {
        private fun createKotlinCoreEnvironment(): KotlinCoreEnvironment {
            val configuration =
                CompilerConfiguration().apply {
                    put(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
                }
            val disposable = Disposer.newDisposable()
            return KotlinCoreEnvironment.createForProduction(
                disposable,
                configuration,
                EnvironmentConfigFiles.JVM_CONFIG_FILES,
            )
        }
    }
}
