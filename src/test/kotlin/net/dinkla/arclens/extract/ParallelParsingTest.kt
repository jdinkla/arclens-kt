package net.dinkla.arclens.extract

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.kotlinFile1
import net.dinkla.arclens.kotlinFile2
import net.dinkla.arclens.parser.PsiParser
import java.io.File

class ParallelParsingTest :
    StringSpec({
        "buildProject should create project from directories and files" {
            // Given
            val dirs = listOf(File("/base"))
            val files = listOf(kotlinFile1, kotlinFile2)

            // When
            val project = buildProject(dirs, files)

            // Then
            project.directory shouldBe "/base"
            project.files shouldHaveSize 2
            project.directories shouldBe listOf("/base")
        }

        "buildProject should use first directory as primary" {
            // Given
            val dirs = listOf(File("/primary"), File("/secondary"))
            val files = listOf(kotlinFile1)

            // When
            val project = buildProject(dirs, files)

            // Then
            project.directory shouldBe "/primary"
            project.directories shouldBe listOf("/primary", "/secondary")
        }

        "buildProject should handle empty file list" {
            // Given
            val dirs = listOf(File("/base"))

            // When
            val project = buildProject(dirs, emptyList())

            // Then
            project.files shouldHaveSize 0
        }

        "parseFiles should parse valid Kotlin files" {
            // Given
            val tempDir =
                kotlin.io.path
                    .createTempDirectory("parse-test")
                    .toFile()
            val file =
                File(tempDir, "Hello.kt").apply {
                    writeText("package test\n\nfun hello() = \"hello\"")
                }
            val parser = PsiParser()

            // When
            val results = parseFiles(listOf(file.absolutePath), tempDir.absolutePath, parser)

            // Then
            results shouldHaveSize 1
            results.first().isSuccess shouldBe true
            results
                .first()
                .getOrThrow()
                .packageName.name shouldBe "test"

            // Cleanup
            tempDir.deleteRecursively()
        }

        "parseFiles should return failure for non-existent files" {
            // Given
            val parser = PsiParser()

            // When
            val results = parseFiles(listOf("/nonexistent/Fake.kt"), "/nonexistent", parser)

            // Then
            results shouldHaveSize 1
            results.first().isFailure shouldBe true
        }

        "parseFiles should handle empty file list" {
            // Given
            val parser = PsiParser()

            // When
            val results = parseFiles(emptyList(), "/base", parser)

            // Then
            results shouldHaveSize 0
        }

        "parseFiles should parse multiple files in parallel" {
            // Given
            val tempDir =
                kotlin.io.path
                    .createTempDirectory("parse-test")
                    .toFile()
            val file1 =
                File(tempDir, "A.kt").apply {
                    writeText("package test\n\nfun a() = 1")
                }
            val file2 =
                File(tempDir, "B.kt").apply {
                    writeText("package test\n\nfun b() = 2")
                }
            val parser = PsiParser()

            // When
            val results =
                parseFiles(
                    listOf(file1.absolutePath, file2.absolutePath),
                    tempDir.absolutePath,
                    parser,
                )

            // Then
            results shouldHaveSize 2
            results.count { it.isSuccess } shouldBe 2

            // Cleanup
            tempDir.deleteRecursively()
        }
    })
