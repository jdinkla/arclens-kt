package net.dinkla.arclens.extract

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import net.dinkla.arclens.domain.FilePath
import net.dinkla.arclens.domain.kotlinlang.KotlinFile
import net.dinkla.arclens.domain.kotlinlang.PackageName
import net.dinkla.arclens.domain.kotlinlang.Project
import java.io.File

class IncrementalParsingTest :
    StringSpec({
        "detectChanges should mark all files as NEW when cache is null" {
            // Given
            val currentFiles =
                listOf(
                    createTempFile("Test1.kt"),
                    createTempFile("Test2.kt"),
                )

            // When
            val changes = detectChanges(null, currentFiles)

            // Then
            changes shouldHaveSize 2
            changes.all { it.status == ChangeStatus.NEW } shouldBe true

            // Cleanup
            currentFiles.forEach { it.delete() }
        }

        "detectChanges should detect NEW files not in cache" {
            // Given
            val existingFile = createTempFile("Existing.kt")
            val newFile = createTempFile("New.kt")

            val cachedProject =
                Project(
                    directory = existingFile.parent,
                    files =
                        listOf(
                            KotlinFile(
                                FilePath("/${existingFile.name}"),
                                PackageName("test"),
                                lastModified = existingFile.lastModified(),
                                fileSize = existingFile.length(),
                            ),
                        ),
                    directories = listOf(existingFile.parent),
                )

            // When
            val changes = detectChanges(cachedProject, listOf(existingFile, newFile))

            // Then
            val newChange = changes.find { it.path == newFile.absolutePath }
            newChange?.status shouldBe ChangeStatus.NEW

            // Cleanup
            existingFile.delete()
            newFile.delete()
        }

        "detectChanges should detect MODIFIED files with different timestamp" {
            // Given
            val file = createTempFile("Modified.kt")
            val oldTimestamp = file.lastModified() - 1000

            val cachedProject =
                Project(
                    directory = file.parent,
                    files =
                        listOf(
                            KotlinFile(
                                FilePath("/${file.name}"),
                                PackageName("test"),
                                lastModified = oldTimestamp,
                                fileSize = file.length(),
                            ),
                        ),
                    directories = listOf(file.parent),
                )

            // When
            val changes = detectChanges(cachedProject, listOf(file))

            // Then
            changes shouldHaveSize 1
            changes.first().status shouldBe ChangeStatus.MODIFIED

            // Cleanup
            file.delete()
        }

        "detectChanges should detect MODIFIED files with different size" {
            // Given
            val file = createTempFile("Modified.kt")
            file.writeText("content")

            val cachedProject =
                Project(
                    directory = file.parent,
                    files =
                        listOf(
                            KotlinFile(
                                FilePath("/${file.name}"),
                                PackageName("test"),
                                lastModified = file.lastModified(),
                                fileSize = file.length() + 100,
                            ),
                        ),
                    directories = listOf(file.parent),
                )

            // When
            val changes = detectChanges(cachedProject, listOf(file))

            // Then
            changes shouldHaveSize 1
            changes.first().status shouldBe ChangeStatus.MODIFIED

            // Cleanup
            file.delete()
        }

        "detectChanges should detect UNCHANGED files" {
            // Given
            val file = createTempFile("Unchanged.kt")
            file.writeText("content")

            val cachedProject =
                Project(
                    directory = file.parent,
                    files =
                        listOf(
                            KotlinFile(
                                FilePath("/${file.name}"),
                                PackageName("test"),
                                lastModified = file.lastModified(),
                                fileSize = file.length(),
                            ),
                        ),
                    directories = listOf(file.parent),
                )

            // When
            val changes = detectChanges(cachedProject, listOf(file))

            // Then
            changes shouldHaveSize 1
            changes.first().status shouldBe ChangeStatus.UNCHANGED

            // Cleanup
            file.delete()
        }

        "detectChanges should detect DELETED files" {
            // Given
            val existingFile = createTempFile("Existing.kt")
            val deletedPath = "${existingFile.parent}/Deleted.kt"

            val cachedProject =
                Project(
                    directory = existingFile.parent,
                    files =
                        listOf(
                            KotlinFile(
                                FilePath("/${existingFile.name}"),
                                PackageName("test"),
                                lastModified = existingFile.lastModified(),
                                fileSize = existingFile.length(),
                            ),
                            KotlinFile(
                                FilePath("/Deleted.kt"),
                                PackageName("test"),
                                lastModified = 1000L,
                                fileSize = 100L,
                            ),
                        ),
                    directories = listOf(existingFile.parent),
                )

            // When
            val changes = detectChanges(cachedProject, listOf(existingFile))

            // Then
            val deletedChange = changes.find { it.path == deletedPath }
            deletedChange?.status shouldBe ChangeStatus.DELETED

            // Cleanup
            existingFile.delete()
        }

        "detectChanges should treat files with zero metadata as MODIFIED" {
            // Given
            val file = createTempFile("ZeroMeta.kt")

            val cachedProject =
                Project(
                    directory = file.parent,
                    files =
                        listOf(
                            KotlinFile(
                                FilePath("/${file.name}"),
                                PackageName("test"),
                                lastModified = 0L,
                                fileSize = 0L,
                            ),
                        ),
                    directories = listOf(file.parent),
                )

            // When
            val changes = detectChanges(cachedProject, listOf(file))

            // Then
            changes shouldHaveSize 1
            changes.first().status shouldBe ChangeStatus.MODIFIED

            // Cleanup
            file.delete()
        }

        "filterFilesToParse should return only NEW and MODIFIED files" {
            // Given
            val changes =
                listOf(
                    FileChange("/path/new.kt", ChangeStatus.NEW),
                    FileChange("/path/modified.kt", ChangeStatus.MODIFIED),
                    FileChange("/path/unchanged.kt", ChangeStatus.UNCHANGED),
                    FileChange("/path/deleted.kt", ChangeStatus.DELETED),
                )

            // When
            val filesToParse = filterFilesToParse(changes)

            // Then
            filesToParse shouldHaveSize 2
            filesToParse.map { it.path } shouldContainExactlyInAnyOrder listOf("/path/new.kt", "/path/modified.kt")
        }

        "filterFilesToParse should return empty list when no files need parsing" {
            // Given
            val changes =
                listOf(
                    FileChange("/path/unchanged.kt", ChangeStatus.UNCHANGED),
                    FileChange("/path/deleted.kt", ChangeStatus.DELETED),
                )

            // When
            val filesToParse = filterFilesToParse(changes)

            // Then
            filesToParse.shouldBeEmpty()
        }

        "reuseFromCache should return only UNCHANGED files" {
            // Given
            val unchangedFile =
                KotlinFile(
                    FilePath("/Unchanged.kt"),
                    PackageName("test"),
                    lastModified = 1000L,
                    fileSize = 100L,
                )
            val modifiedFile =
                KotlinFile(
                    FilePath("/Modified.kt"),
                    PackageName("test"),
                    lastModified = 2000L,
                    fileSize = 200L,
                )
            val tempDir = createTempDir()
            File(tempDir, "Unchanged.kt").createNewFile()

            val cachedProject =
                Project(
                    directory = tempDir.absolutePath,
                    files = listOf(unchangedFile, modifiedFile),
                    directories = listOf(tempDir.absolutePath),
                )

            val changes =
                listOf(
                    FileChange("${tempDir.absolutePath}/Unchanged.kt", ChangeStatus.UNCHANGED),
                    FileChange("${tempDir.absolutePath}/Modified.kt", ChangeStatus.MODIFIED),
                )

            // When
            val reusedFiles = reuseFromCache(cachedProject, changes)

            // Then
            reusedFiles shouldHaveSize 1
            reusedFiles.first().filePath.path shouldBe "/Unchanged.kt"

            // Cleanup
            tempDir.deleteRecursively()
        }

        "mixed scenario with new, modified, unchanged, and deleted files" {
            // Given
            val tempDir = createTempDir()
            val newFile = File(tempDir, "New.kt").apply { writeText("new content") }
            val modifiedFile = File(tempDir, "Modified.kt").apply { writeText("modified content") }
            val unchangedFile = File(tempDir, "Unchanged.kt").apply { writeText("unchanged content") }

            val cachedProject =
                Project(
                    directory = tempDir.absolutePath,
                    files =
                        listOf(
                            KotlinFile(
                                FilePath("/Modified.kt"),
                                PackageName("test"),
                                lastModified = modifiedFile.lastModified() - 1000,
                                fileSize = modifiedFile.length(),
                            ),
                            KotlinFile(
                                FilePath("/Unchanged.kt"),
                                PackageName("test"),
                                lastModified = unchangedFile.lastModified(),
                                fileSize = unchangedFile.length(),
                            ),
                            KotlinFile(
                                FilePath("/Deleted.kt"),
                                PackageName("test"),
                                lastModified = 1000L,
                                fileSize = 100L,
                            ),
                        ),
                    directories = listOf(tempDir.absolutePath),
                )

            // When
            val changes =
                detectChanges(
                    cachedProject,
                    listOf(newFile, modifiedFile, unchangedFile),
                )

            // Then
            changes shouldHaveSize 4
            changes.find { it.path == newFile.absolutePath }?.status shouldBe ChangeStatus.NEW
            changes.find { it.path == modifiedFile.absolutePath }?.status shouldBe ChangeStatus.MODIFIED
            changes.find { it.path == unchangedFile.absolutePath }?.status shouldBe ChangeStatus.UNCHANGED
            changes.find { "Deleted.kt" in it.path }?.status shouldBe ChangeStatus.DELETED

            val filesToParse = filterFilesToParse(changes)
            filesToParse shouldHaveSize 2

            val reusedFiles = reuseFromCache(cachedProject, changes)
            reusedFiles shouldHaveSize 1

            // Cleanup
            tempDir.deleteRecursively()
        }
    })

private fun createTempFile(name: String): File {
    val tempDir = System.getProperty("java.io.tmpdir")
    val file = File(tempDir, name)
    file.writeText("test content")
    return file
}

private fun createTempDir(): File =
    kotlin.io.path
        .createTempDirectory("incremental-test")
        .toFile()
